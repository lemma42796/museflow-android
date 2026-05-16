package com.ixuea.courses.mymusic.component.search.model;

import java.util.List;

/**
 * 搜索建议模型
 */
public class Suggest {
    /**
     * 歌单搜索建议
     */
    private List<SuggestItem> sheets;

    /**
     * 用户搜索建议
     */
    private List<SuggestItem> users;

    public List<SuggestItem> getSheets() {
        return sheets;
    }

    public void setSheets(List<SuggestItem> sheets) {
        this.sheets = sheets;
    }

    public List<SuggestItem> getUsers() {
        return users;
    }

    public void setUsers(List<SuggestItem> users) {
        this.users = users;
    }
}
