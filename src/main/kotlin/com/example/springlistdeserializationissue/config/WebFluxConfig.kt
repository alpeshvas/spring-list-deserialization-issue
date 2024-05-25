package com.example.springlistdeserializationissue.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler

@Configuration
class WebFluxConfig : WebFluxConfigurer {

    @Autowired
    private lateinit var requestedContentTypeResolver: RequestedContentTypeResolver

    @Autowired
    private lateinit var reactiveAdapterRegistry: ReactiveAdapterRegistry

    @Autowired
    private lateinit var serverCodecConfigurer: ServerCodecConfigurer

    @Bean
    @Primary
    fun genericResponseEntityResultHandler(): ResponseEntityResultHandler {
        return GenericResponseEntityResultHandler(serverCodecConfigurer.writers, requestedContentTypeResolver, reactiveAdapterRegistry)
    }
}