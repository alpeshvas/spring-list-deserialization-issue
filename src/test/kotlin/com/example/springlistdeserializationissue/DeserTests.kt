package com.example.springlistdeserializationissue

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.AMSummaryData
import utils.APIResponse

class DeserTests {
    @Test
    fun `jackson deserialization issue`() {
        val writer = jacksonObjectMapper().writer().forType(jacksonTypeRef<APIResponse<List<AMSummaryData>>>())
        val response = APIResponse.Success(listOf(AMSummaryData(name = "test", value = "test", change = "test")))
        val value = writer.writeValueAsString(response)
        // json assert
        assertEquals("{\"headers\":{},\"body\":[{\"name\":\"test\",\"value\":\"test\",\"change\":\"test\"}],\"statusCode\":\"OK\",\"statusCodeValue\":200}", value)
    }
}