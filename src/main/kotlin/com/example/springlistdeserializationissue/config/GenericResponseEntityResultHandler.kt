//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.example.springlistdeserializationissue.config

import org.springframework.context.annotation.Bean
import org.springframework.core.KotlinDetector
import org.springframework.core.MethodParameter
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.http.*
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.util.Assert
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

open class GenericResponseEntityResultHandler @JvmOverloads constructor(
    writers: List<HttpMessageWriter<*>?>,
    resolver: RequestedContentTypeResolver,
    registry: ReactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance()
) : ResponseEntityResultHandler(writers, resolver, registry) {
    val COROUTINES_FLOW_CLASS_NAME: String = "kotlinx.coroutines.flow.Flow"

    private fun hasMoreThanTwoLevelGenerics(result: HandlerResult): Boolean {
        val returnType = result.returnType
        return returnType.hasGenerics() && returnType.getGeneric().hasGenerics()
    }

    override fun handleResult(exchange: ServerWebExchange, result: HandlerResult): Mono<Void> {
        val adapter = this.getAdapter(result)
        val actualParameter = result.returnTypeSource
        val returnValueMono: Mono<*>
        val bodyParameter: MethodParameter
        if (adapter != null) {
            Assert.isTrue(!adapter.isMultiValue, "Only a single ResponseEntity supported")
            returnValueMono = Mono.from(adapter.toPublisher<Any>(result.returnValue))
            val hasMoreThanTwoLevelGenerics = hasMoreThanTwoLevelGenerics(result)
            val isContinuation = (KotlinDetector.isSuspendingFunction(
                actualParameter.method!!
            ) &&
                    COROUTINES_FLOW_CLASS_NAME != actualParameter.parameterType.name)
            bodyParameter =
                if (isContinuation && !hasMoreThanTwoLevelGenerics)
                    actualParameter.nested()
                else
                    actualParameter.nested().nested()
        } else {
            returnValueMono = Mono.justOrEmpty(result.returnValue)
            bodyParameter = actualParameter.nested()
        }

        return returnValueMono.flatMap { returnValue: Any ->
            val httpEntity: Any
            if (returnValue is HttpEntity<*>) {
                httpEntity = returnValue
            } else {
                require(returnValue is HttpHeaders) { "HttpEntity or HttpHeaders expected but got: " + returnValue.javaClass }

                httpEntity = ResponseEntity<Any?>(
                    returnValue,
                    HttpStatus.OK
                )
            }

            if (httpEntity is ResponseEntity<*>) {
                exchange.response
                    .setRawStatusCode(httpEntity.statusCodeValue)
            }

            val entityHeaders =
                (httpEntity as HttpEntity<*>).headers
            val responseHeaders = exchange.response.headers
            if (!entityHeaders.isEmpty()) {
                entityHeaders.entries.stream()
                    .forEach { entry: Map.Entry<String?, List<String>> ->
                        responseHeaders[entry.key!!] = entry.value
                    }
            }
            if (httpEntity.body != null && returnValue !is HttpHeaders) {
                val etag = entityHeaders.eTag
                val lastModified = Instant.ofEpochMilli(entityHeaders.lastModified)
                val httpMethod = exchange.request.method
                return@flatMap if (SAFE_METHODS.contains(
                        httpMethod
                    ) && exchange.checkNotModified(etag, lastModified)
                ) exchange.response
                    .setComplete() else this.writeBody(
                    httpEntity.body,
                    bodyParameter,
                    actualParameter,
                    exchange
                )
            } else {
                return@flatMap exchange.response.setComplete()
            }
        }
    }

    init {
        this.order = 0
    }

    companion object {
        private val SAFE_METHODS: Set<HttpMethod> =
            EnumSet.of(HttpMethod.GET, HttpMethod.HEAD)
    }
}