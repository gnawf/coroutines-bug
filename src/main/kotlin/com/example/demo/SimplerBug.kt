package com.example.demo

import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.supervisorScope
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import reactor.core.publisher.Mono
import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    Thread.setDefaultUncaughtExceptionHandler(MainThreadExceptionHandler())

    val mockWebServer = MockWebServer()
    mockWebServer.start()

    mockWebServer.enqueue(MockResponse(code = 400))

    supervisorScope {
        launch {
            Mono.error<Any>(UnsupportedOperationException())
                .awaitSingle()
        }
    }

    exitProcess(1)
}
