package com.ixuea.courses.mymusic.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * sha工具类
 */
public class SHAUtil {
    /**
     * sha256 签名
     *
     * @param data
     * @return
     */
    public static String sha256(String data) {
        //加盐
        data = SaltUtil.wrap(data);
        return new String(Hex.encodeHex(DigestUtils.sha256(data)));
    }
}
