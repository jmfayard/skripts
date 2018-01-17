package statemachine

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import statemachine.BrokenEvent.MachineRepairDidComplete
import statemachine.LockedEvent.*
import statemachine.StateMachineStrategy.*
import statemachine.TurnStyle.Command.*
import statemachine.TurnStyle.Companion.FARE_PRICE
import statemachine.UnlockedEvent.AdmitPerson
import statemachine.UnlockedEvent.MachineDidFail

/***
 * Context: I highly recommend andymatuschak's  gist
 *
 * A composable pattern for pure state machines with effects
 * https://gist.github.com/andymatuschak/d5f0a8730ad601bcccae97e8398e25b2
 *
 * It's written in swift but nicely maps to Kotlin as demonstrated here
 *
 * See the schema of the TurnStyle here
 *
 * ![TurnStyle](https://camo.githubusercontent.com/a74ea94a7eab348f991fb22d6f70a92c5bef3740/68747470733a2f2f616e64796d617475736368616b2e6f72672f7374617465732f666967757265332e706e67)
 ***/


/** Generic State Machine **/
interface Event

abstract class NamedEvent(val name: String?) : Event {
    override fun toString(): String = name ?: toString()
}

interface StateCommand

data class Transition<State : StateType, Command : StateCommand>(
    val state: State,
    val command: Command?,
    val event: Event
) {
    fun emit(command: Command?) = this.copy(command = command)
    fun moveTo(state: State) = this.copy(state = state)
    fun moveAndEmit(state: State, command: Command) = this.copy(state = state, command = command)
}

enum class StateMachineStrategy { FIRE_NOW, DROP, STORE, CRASH }

object StartEvent : NamedEvent("StartEvent")


interface StateMachine<State : StateType, Command : StateCommand> {
    fun initialState(): State

    fun history(): List<Transition<State, Command>>

    fun currentState(): State

    fun handleEvent(event: Event): Command?

    fun enqueueEvent(event: Event): StateMachineStrategy

    /*** Utility Functions **/
    fun beginTransition(event: Event): Transition<State, Command> = Transition(currentState(), null, event)

    fun printHistory() {
        val history = history()
        val states = printList(history.map { it.state })
        val events = printList(history.map { it.event })
        val commands = printList(history.map { it.command })

        println(
            """
Events:   $events
States:   $states
Commands: $commands
    """
        )
    }


}


fun main(args: Array<String>) {

    /*** The functional core of the state machine is suepr trivial to test **/
    val events: List<Event> = listOf(
        InsertCoin(20), InsertCoin(20), InsertCoin(10),
        AdmitPerson,
        InsertCoin(1),
        MachineDidFailWhileLocked,
        MachineRepairDidComplete
    )

    val stateMachine = TurnStyle()
    events.forEach { event ->
        val strategy = stateMachine.enqueueEvent(event)
        check(strategy == FIRE_NOW) {
            stateMachine.printHistory()
            "Didn't expect strategy $strategy for event $event"
        }
        stateMachine.handleEvent(event)
    }

    stateMachine.printHistory()
    val history = stateMachine.history()


    val expectedStates = listOf(
        Locked(credit = 0), Locked(credit = 20), Locked(credit = 40),
        Unlocked,
        Locked(credit = 0), Locked(credit = 1),
        Broken(oldState = Locked(credit = 1)),
        Locked(credit = 1)
    )

    val expectedCommands: List<TurnStyle.Command?> =
        listOf(null, null, null, OpenDoors, CloseDoors, null, CallSomeone, null)

    history.map { it.state } shouldBe expectedStates
    history.map { it.command } shouldBe expectedCommands

    /** The imperative shell takes care of the side Effects **/

    runBlocking {
        val controller = runStateMachineWithSideEffects()
        controller.customerDidInsertCoin(10)
        delay(100)
        controller.customerDidInsertCoin(50)
        delay(100)
        controller.shitHappens()
        delay(5000)
        controller.stateMachine.printHistory()
        val expectedSideEffects =
            listOf("sendControlSignalToOpenDoors", "sendControlSignalToCloseDoors", "askSomeoneToRepair")
        controller.doorHardwareController.msgs shouldBe expectedSideEffects

    }


}


/***
 * Functional Core of our state machine.
 */


interface StateType

sealed class TurnStyleState(val name: String? = null) : StateType {

    abstract fun handleTransition(transition: TurnStyleTransition): TurnStyleTransition

    override fun toString(): String = name ?: super.toString()
}

typealias TurnStyleTransition = Transition<TurnStyleState, TurnStyle.Command>


sealed class LockedEvent(name: String? = null) : NamedEvent(name) {
    data class InsertCoin(val value: Int) : LockedEvent()
    object AdmitPersonWhileLocked : LockedEvent("AdmitPersonWhileLocked")
    object MachineDidFailWhileLocked : LockedEvent("MachineDidFailWhileLocked")
}

data class Locked(val credit: Int) : TurnStyleState() {

    override fun handleTransition(transition: TurnStyleTransition): TurnStyleTransition {
        val event = transition.event as LockedEvent

        return when (event) {
            MachineDidFailWhileLocked -> transition.moveAndEmit(Broken(oldState = this), CallSomeone)
            AdmitPersonWhileLocked -> transition.emit(SoundAlarm)
            is InsertCoin -> {
                val newCredit = credit + event.value
                if (newCredit >= FARE_PRICE)
                    transition.moveAndEmit(Unlocked, OpenDoors)
                else
                    transition.moveTo(Locked(newCredit))

            }
        }
    }
}

enum class UnlockedEvent : Event {
    AdmitPerson, MachineDidFail
}

object Unlocked : TurnStyleState("Unlocked") {
    override fun handleTransition(transition: TurnStyleTransition): TurnStyleTransition {
        val event = transition.event as UnlockedEvent
        return when (event) {
            AdmitPerson -> transition.moveAndEmit(Locked(credit = 0), CloseDoors)
            MachineDidFail -> transition.moveAndEmit(Broken(oldState = transition.state), CallSomeone)
        }
    }
}

enum class BrokenEvent : Event {
    MachineRepairDidComplete
}

data class Broken(val oldState: TurnStyleState) : TurnStyleState() {
    override fun handleTransition(transition: TurnStyleTransition): TurnStyleTransition {
        check(transition.event == MachineRepairDidComplete)
        return transition.moveTo(this.oldState)
    }

}

class TurnStyle : StateMachine<TurnStyleState, TurnStyle.Command> {
    enum class Command : StateCommand {
        SoundAlarm, CloseDoors, OpenDoors, CallSomeone
    }

    private val history: MutableList<TurnStyleTransition> = mutableListOf(
        Transition(initialState(), doNothing, StartEvent)
    )

    override fun initialState(): TurnStyleState =
        Locked(credit = 0)

    override fun history(): List<TurnStyleTransition> = history.toList()

    override fun currentState(): TurnStyleState = history.last().state

    override fun handleEvent(event: Event): TurnStyle.Command? {
        val newTransition = currentState().handleTransition(beginTransition(event))
        history += newTransition
        return newTransition.command
    }


    override fun enqueueEvent(event: Event): StateMachineStrategy {
        return when (event) {
            is LockedEvent -> when {
                currentState() is Locked -> FIRE_NOW
                event is InsertCoin -> STORE
                else -> DROP
            }
            is UnlockedEvent -> when {
                currentState() is Unlocked -> FIRE_NOW
                else -> DROP
            }
            is BrokenEvent -> when {
                currentState() is Broken -> FIRE_NOW
                else -> DROP
            }
            else -> CRASH
        }
    }


    companion object {
        private val doNothing: TurnStyle.Command? = null
        const val FARE_PRICE = 50
    }
}

private fun printList(list: List<Any?>) = list.joinToString(prefix = "listOf(", postfix = ")")
private infix fun <T> T?.shouldBe(expected: Any?) {
    if (this != expected) error("ShouldBe Failed!\nExpected: $expected\nGot:      $this")
}


/***
Now, an imperative shell that hides the enums and delegates to actuators.
Note that it has no domain knowledge: it just connects object interfaces.
 ***/

suspend fun runStateMachineWithSideEffects(): TurnStyleController {
    val controller = TurnStyleController(DoorHardwareController(), SpeakerController(), TurnStyle())
    launch { controller.consumeEvents() }
    return controller
}

class TurnStyleController(
    val doorHardwareController: DoorHardwareController,
    val speakerController: SpeakerController,
    val stateMachine: TurnStyle
) {

    val droppedEvents = mutableListOf<Event>()

    val storedEvents = mutableListOf<Event>()

    private val events = Channel<Event>(5)

    suspend fun enqueue(event: Event?) {
        if (event == null) return
        when (stateMachine.enqueueEvent(event)) {
            FIRE_NOW -> handleCommand(event)
            STORE -> storedEvents.add(event)
            DROP -> droppedEvents.add(event)
            CRASH -> {
                stateMachine.printHistory(); error("Unexpected event $event")
            }
        }
    }

    suspend fun consumeEvents() {
        var iteration = 0
        while (iteration++ < 100) {
            val event = searchSuitableStoreEvent() ?: events.receiveOrNull()

            enqueue(event)

            stateMachine.printHistory()
            delay(100)
        }
        stateMachine.printHistory()
    }

    suspend fun handleCommand(event: Event) {
        val command = stateMachine.handleEvent(event)
        val nextEvent: Event? = when (command) {
            OpenDoors -> doorHardwareController.sendControlSignalToOpenDoors()
            SoundAlarm -> speakerController.soundTheAlarm()
            CloseDoors -> doorHardwareController.sendControlSignalToCloseDoors()
            CallSomeone -> doorHardwareController.askSomeoneToRepair()
            null -> null
        }
        if (nextEvent != null) {
            events.send(nextEvent)
        }
    }

    private fun searchSuitableStoreEvent(): Event? {
        val event = storedEvents.firstOrNull {
            stateMachine.enqueueEvent(it) == FIRE_NOW
        }
        if (event == null) {
            return null
        } else {
            storedEvents.remove(event)
            return event
        }
    }

    suspend fun shitHappens() {
        enqueue(MachineDidFailWhileLocked)
    }


    suspend fun customerDidInsertCoin(value: Int) {
        enqueue(InsertCoin(value))
    }


}

class DoorHardwareController {
    val msgs = mutableListOf<String>()

    suspend fun sendControlSignalToOpenDoors(): Event? {
        delay(500)
        say("sendControlSignalToOpenDoors")
        return AdmitPerson
    }

    suspend fun sendControlSignalToCloseDoors(): Event? {
        delay(100)
        say("sendControlSignalToCloseDoors")
        return null
    }

    suspend fun askSomeoneToRepair(): Event? {
        delay(300)
        say("askSomeoneToRepair")
        return MachineRepairDidComplete
    }


    private fun say(msg: String) {
        msgs += msg
        println(msg)
    }


}

class SpeakerController {
    val msgs = mutableListOf<String>()
    suspend fun soundTheAlarm(): Event? {
        delay(50)
        say("soundTheAlarm")
        return MachineRepairDidComplete
    }

    private fun say(msg: String) {
        println(msg)
        msgs += msg
    }

}
