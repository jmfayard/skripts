package jmfayard

import ru.gildor.coroutines.retrofit.Result


fun <T : Any> Result<T>.checkOk(): T {
    if (this is Result.Ok) {
        return value
    } else {
        error("Http call failed: ${this}")
    }
}



