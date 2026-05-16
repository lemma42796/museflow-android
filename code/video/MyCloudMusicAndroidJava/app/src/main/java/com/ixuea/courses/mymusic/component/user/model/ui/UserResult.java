package com.ixuea.courses.mymusic.component.user.model.ui;

import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;

import java.util.List;

/**
 * 处理完用户数据模型
 */
public class UserResult {
    /**
     * 用来在列表中显示
     */
    private List<BaseMultiItemEntity> datum;

    /**
     * 字母
     */
    private List<String> letters;

    /**
     * 字母索引
     */
    private Integer[] indexes;

    public UserResult(List<BaseMultiItemEntity> datum, List<String> letters, Integer[] indexes) {
        this.datum = datum;
        this.letters = letters;
        this.indexes = indexes;
    }

    public List<String> getLetters() {
        return letters;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public List<BaseMultiItemEntity> getDatum() {
        return datum;
    }

    public void setDatum(List<BaseMultiItemEntity> datum) {
        this.datum = datum;
    }

    public Integer[] getIndexes() {
        return indexes;
    }

    public void setIndexes(Integer[] indexes) {
        this.indexes = indexes;
    }
}
