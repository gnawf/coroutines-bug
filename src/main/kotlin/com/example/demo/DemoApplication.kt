package com.example.demo

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.supervisorScope
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@EnableWebFlux
@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    Thread.setDefaultUncaughtExceptionHandler(MainThreadExceptionHandler(Thread.currentThread()))

    runApplication<DemoApplication>(*args)
}

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

@RestController
class Test {
    private val mockWebServer = MockWebServer()

    init {
        mockWebServer.start()
    }

    @GetMapping
    suspend fun test(): String {
        mockWebServer.enqueue(MockResponse(code = 400))

        supervisorScope {
            launch {
                try {
                    WebClient.builder().build()
                        .get()
                        .uri(mockWebServer.url("").toUri())
                        .retrieve()
                        .bodyToMono<String>()
                        .awaitSingle()
                } catch (e: Exception) {
                    Exception("wtf", e).printStackTrace()
                    throw e
                }
            }
        }

        return "Ok"
    }
}
