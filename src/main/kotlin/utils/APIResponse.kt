package utils

import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class APIResponse<T>(httpStatus: HttpStatus) : ResponseEntity<APIResponse<T>>(httpStatus) {

    class Success<T>(
        private val response: T,
        val status: HttpStatus = HttpStatus.OK
    ) : APIResponse<T>(status) {
        override fun getBody(): APIResponse<T> = this
        @JsonValue fun value() = response
    }

    class Error<T>(
        private val httpStatus: HttpStatus,
        private val errorCode: String,
        private val message: String
    ) : APIResponse<T>(httpStatus) {
        override fun getBody() = this
        @JsonValue fun value() = mapOf(
            "errorCode" to errorCode,
            "message" to message
        )
    }
}
