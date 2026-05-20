package com.ixuea.courses.mymusic.util

import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES工具类
 */
object AESUtil {
    /**
     * AES128算法key
     */
    private const val AES128_KEY = "wqfrwOSH*gN%I2v6"

    /**
     * AES128算法IV
     */
    private const val AES128_IV = "VO*1sxQO5nDkcMyj"

    /**
     * 加密
     */
    @JvmStatic
    fun encrypt(data: String): String {
        val saltedData = SaltUtil.wrap(data)
        return aes(saltedData, Cipher.ENCRYPT_MODE)
    }

    /**
     * 解密
     */
    @JvmStatic
    fun decrypt(data: String): String {
        val result = aes(data, Cipher.DECRYPT_MODE)
        return SaltUtil.unwrap(result)
    }

    /**
     * aes实现
     *
     * @param mode 加密：[Cipher.ENCRYPT_MODE]，解密：[Cipher.DECRYPT_MODE]
     */
    private fun aes(data: String, mode: Int): String {
        try {
            val charset = Charset.defaultCharset()
            val key = SecretKeySpec(AES128_KEY.toByteArray(charset), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = IvParameterSpec(AES128_IV.toByteArray(charset))

            cipher.init(mode, key, iv)
            return if (mode == Cipher.ENCRYPT_MODE) {
                val result = cipher.doFinal(data.toByteArray(charset))
                Base64Util.encodeByte2String(result)
            } else {
                val dataBytes = Base64Util.decodeString2Byte(data)
                val result = cipher.doFinal(dataBytes)
                String(result, charset)
            }
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }
}
