package com.example.demo

/**
 * Replicates what was in Statsig SDK prior to 2.0.8
 *
 * [Github Release](https://github.com/statsig-io/java-server-sdk/compare/2.0.7...2.0.8)
 */
class MainThreadExceptionHandler(val currentThread: Thread) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (!t.name.equals(currentThread.name)) {
            throw e
        }
        println("Shutting down Statsig")
        throw e
    }
}
