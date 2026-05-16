package com.ixuea.courses.mymusic.model;

/**
 * 资源
 * 将资源放到单独的对象中
 * 好处是后面还可扩展更多的字段
 * 例如：资源类型；资源大小；资源备注
 */
public class Resource extends Base {
    /**
     * 相对地址
     */
    private String uri;

    /**
     * 类型，0：图片；10：视频
     */
    private int style;

    public Resource(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
