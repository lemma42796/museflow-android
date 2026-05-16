package com.ixuea.courses.mymusic.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * AES工具类测试
 */
@RunWith(AndroidJUnit4.class)
public class AESUtilTests {
    /**
     * 测试aes128加密
     */
    @Test
    public void testEncrypt() {
        //相等测试
        String data = AESUtil.encrypt("ixueaedu");
        Assert.assertEquals(
                "r4+kYpb2MAQ2JAcRuAmkruZutzNhrnF0sDBn3j1D9FxnDo7m9rF2fykSw7mJq6mi",
                data
        );

        //不相等
        Assert.assertNotEquals("ixueaedu", data);
    }

    /**
     * 测试aes128解密
     */
    @Test
    public void testDecrypt() {
        //相等测试
        String data =
                AESUtil.decrypt("r4+kYpb2MAQ2JAcRuAmkruZutzNhrnF0sDBn3j1D9FxnDo7m9rF2fykSw7mJq6mi");
        Assert.assertEquals("ixueaedu", data);

        //不相等测试
        Assert.assertNotEquals("ixuea", data);
    }
}
