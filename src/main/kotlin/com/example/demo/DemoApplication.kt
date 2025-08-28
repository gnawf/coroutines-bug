package com.example.demo

import kotlinx.coroutines.CoroutineScope
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

@RestController
class Test {
    private val mockWebServer = MockWebServer()

    init {
        mockWebServer.start()
    }

    /**
     * This request never finishes and hangs
     *
     * Throws a [kotlinx.coroutines.CoroutinesInternalError]
     */
    @GetMapping("supervisor-scope")
    suspend fun supervisorScope(): String {
        supervisorScope {
            launchBadCode()
        }

        return "Ok"
    }

    /**
     * This request finishes with a 500 Internal Server Error.
     *
     * Does NOT throw a [kotlinx.coroutines.CoroutinesInternalError]
     */
    @GetMapping("coroutine-scope")
    suspend fun coroutineScope(): String {
        coroutineScope {
            launchBadCode()
        }

        return "Ok"
    }

    suspend fun CoroutineScope.launchBadCode() {
        mockWebServer.enqueue(MockResponse(code = 400))

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
}
