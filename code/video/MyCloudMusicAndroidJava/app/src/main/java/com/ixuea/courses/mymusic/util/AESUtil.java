package com.ixuea.courses.mymusic.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES工具类
 */
public class AESUtil {
    /**
     * AES128算法key
     */
    private static final String AES128_KEY = "wqfrwOSH*gN%I2v6";

    /**
     * AES128算法IV
     */
    private static final String AES128_IV = "VO*1sxQO5nDkcMyj";

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        //加盐
        data = SaltUtil.wrap(data);
        return aes(data, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        String r = aes(data, Cipher.DECRYPT_MODE);
        return SaltUtil.unwrap(r);
    }

    /**
     * aes实现
     *
     * @param data
     * @param mode 加密：[Cipher.ENCRYPT_MODE]，解密：[Cipher.DECRYPT_MODE]
     * @return
     */
    private static String aes(String data, int mode) {
        try {
            //随机生成key
            //但防止其他客户端不好实现同等算法
            //所以这里不使用
            //虽然key后
            //相同的输入数据，结果不相同
            //KeyGenerator generator = KeyGenerator.getInstance("AES");
            //SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            //random.setSeed(AES128_KEY.getBytes());
            //generator.init(128, random);
            //SecretKey secretKey = generator.generateKey();
            //byte[] keyBytes = secretKey.getEncoded();

            //创建key
            SecretKeySpec key = new SecretKeySpec(AES128_KEY.getBytes(), "AES");

            //算法
            //算法/模式/补码方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            //使用CBC模式
            //需要一个向量iv
            //可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(AES128_IV.getBytes());

            //初始化，并且传递key,iv
            cipher.init(mode, key, iv);
            if (mode == Cipher.ENCRYPT_MODE) {
                //加密
                byte[] result = cipher.doFinal(data.getBytes());

                //base64编码后返回
                return Base64Util.encodeByte2String(result);
            } else {
                //解密
                byte[] dataBytes = Base64Util.decodeString2Byte(data);

                //解密
                byte[] result = cipher.doFinal(dataBytes);

                //转为字符串
                return new String(result);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
