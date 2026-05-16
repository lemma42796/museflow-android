package com.tencent.liteav.demo.superplayer.model.protocol;

public class PlayInfoConstant {

    public enum EncryptedURLType {

        SIMPLEAES("SimpleAES"),
        WIDEVINE("widevine");

        private String value;

        EncryptedURLType(String type) {
            value = type;
        }

        public String getValue() {
            return value;
        }
    }
}
