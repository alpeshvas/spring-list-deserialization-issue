package com.example.springlistdeserializationissue.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utils.APIResponse

data class AMTemp(val name: String)

@RestController
@RequestMapping("/test")
class TestController {
    @GetMapping("/test-json")
    suspend fun testJson(): APIResponse<AMTemp> {
        return APIResponse.Success(AMTemp("test"))
    }

    @GetMapping("/test-json-fail")
    suspend fun testJsonFail(): APIResponse<List<String>> {
        return APIResponse.Success(listOf("test"))
    }
}
