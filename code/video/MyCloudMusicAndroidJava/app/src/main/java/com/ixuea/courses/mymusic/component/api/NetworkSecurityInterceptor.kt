package com.ixuea.courses.mymusic.component.api

import com.ixuea.courses.mymusic.exception.ResponseDecryptException
import com.ixuea.courses.mymusic.exception.ResponseSignException
import com.ixuea.courses.mymusic.util.AESUtil
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.SHAUtil
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.nio.charset.Charset

/**
 * OkHttp 插件，用来处理部分接口的数据签名和加密。
 */
class NetworkSecurityInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            var request = chain.request()
            val url = request.url.toString()
            val method = request.method
            var requestBody = request.body

            if (url.endsWith("v2/addresses") && method == "POST") {
                val bodyString = getRequestBodyString(requestBody)
                val sign = SHAUtil.sha256(bodyString)
                request = request.newBuilder()
                    .headers(request.headers)
                    .addHeader(Constant.HEADER_SIGN, sign)
                    .method(method, requestBody)
                    .build()
            } else if (url.endsWith("v3/addresses") && method == "POST") {
                val bodyString = getRequestBodyString(requestBody)
                val encryptBodyString = AESUtil.encrypt(bodyString)
                requestBody = encryptBodyString.toRequestBody(JSON_MEDIA_TYPE)
                request = request.newBuilder()
                    .headers(request.headers)
                    .method(method, requestBody)
                    .build()
            }

            var response = chain.proceed(request)
            val sign = response.header(Constant.HEADER_SIGN)

            if (StringUtils.isNotBlank(sign)) {
                val dataString = getResponseString(response)
                val localSign = SHAUtil.sha256(dataString)
                if (localSign != sign) {
                    throw ResponseSignException()
                }
            }

            if (url.endsWith("v3/addresses") && method == "GET") {
                response = try {
                    val dataString = getResponseString(response)
                    val decryptString = AESUtil.decrypt(dataString)
                    val responseBody = decryptString.toResponseBody(JSON_MEDIA_TYPE)
                    response.newBuilder()
                        .body(responseBody)
                        .headers(response.headers)
                        .build()
                } catch (error: Exception) {
                    throw ResponseDecryptException()
                }
            }

            return response
        } catch (error: IOException) {
            throw error
        }
    }

    @Throws(IOException::class)
    private fun getRequestBodyString(requestBody: RequestBody?): String {
        if (requestBody == null) {
            return ""
        }

        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }

    @Throws(IOException::class)
    private fun getResponseString(response: Response): String {
        val responseBody = response.body ?: return ""
        val source = responseBody.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer
        return buffer.clone().readString(Charset.forName("UTF-8"))
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
