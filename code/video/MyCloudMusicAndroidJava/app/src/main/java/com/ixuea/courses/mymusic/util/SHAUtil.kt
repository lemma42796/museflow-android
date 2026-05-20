package com.ixuea.courses.mymusic.util

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

/**
 * sha工具类
 */
object SHAUtil {
    /**
     * sha256 签名
     */
    @JvmStatic
    fun sha256(data: String): String {
        val salted = SaltUtil.wrap(data)
        return String(Hex.encodeHex(DigestUtils.sha256(salted)))
    }
}
