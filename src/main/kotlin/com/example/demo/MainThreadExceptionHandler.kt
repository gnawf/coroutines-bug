package com.example.demo

/**
 * Replicates what was in Statsig SDK prior to 2.0.8
 *
 * [GitHub Release](https://github.com/statsig-io/java-server-sdk/compare/2.0.7...2.0.8)
 */
class MainThreadExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        throw e
    }
}
