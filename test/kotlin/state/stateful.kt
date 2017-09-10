package org.softpark.stateful4k.demo

import org.junit.Test
import org.softpark.stateful4k.*
import org.softpark.stateful4k.action.IExecutor
import org.softpark.stateful4k.extensions.createExecutor
import org.softpark.stateful4k.extensions.event
import org.softpark.stateful4k.extensions.state

interface Emitter {
    fun sound(text: String)
}
class ConsoleEmitter : Emitter {
        override fun sound(text: String) = println(text)
}

abstract class DoorState(locked: Boolean) {
    var locked: Boolean = locked; get private set

    fun lock() {
        locked = true
    }

    fun unlock() {
        locked = false
    }
}

class ClosedState(locked: Boolean) : DoorState(locked) {}
class OpenState(locked: Boolean) : DoorState(locked) {}

interface DoorEvent
class LockEvent : DoorEvent
class UnlockEvent : DoorEvent
class CloseEvent : DoorEvent
class OpenEvent : DoorEvent

open class DoorStateMachine {


    val config = StateMachine
            .createConfigurator<Emitter, DoorState, DoorEvent>()
            .apply {
                event(DoorState::class, UnlockEvent::class)
                        .filter { state.locked }
                        .loop { state.unlock(); context.sound("Click!") }
                event(DoorState::class, LockEvent::class)
                        .filter { !state.locked }
                        .loop { state.lock(); context.sound("Clack!") }

                event(ClosedState::class, OpenEvent::class)
                        .filter { state.locked }
                        .loop { context.sound("Click! Click!") }
                event(ClosedState::class, OpenEvent::class)
                        .goto { context.sound("Click! Squeak!"); OpenState(false) }

                event(OpenState::class, CloseEvent::class)
                        .filter { state.locked }
                        .loop { context.sound("Squeak! Bang!") }
                event(OpenState::class, CloseEvent::class)
                        .goto { context.sound("Squeak! Click!"); ClosedState(false) }

                event(DoorState::class, DoorEvent::class).loop()
            }

    fun start(emitter: Emitter, state: DoorState? = null)
            : IExecutor<Emitter, DoorState, DoorEvent> =
            config.createExecutor(emitter, state ?: ClosedState(true))

    @Test fun stateMachine() {
        val executor: IExecutor<Emitter, DoorState, DoorEvent> = start(ConsoleEmitter(), OpenState(locked = false))
        executor.fire(UnlockEvent())
        executor.fire(CloseEvent())
        executor.fire(OpenEvent())
        executor.fire(CloseEvent())
        executor.fire(LockEvent())
        executor.fire(UnlockEvent())
        executor.fire(LockEvent())
    }
}