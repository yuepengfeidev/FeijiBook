package com.example.feijibook.entity.record_type_bean;

/**
 * Created by 你是我的 on 2019/3/21
 */

// 可自定义添加的类型
public class CustomType {
    private String category;// 分类，如：娱乐，收入，饮食等，将图标分类开
    private int typeIconUrl;// 图标资源地址

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTypeIconUrl() {
        return typeIconUrl;
    }

    public void setTypeIconUrl(int typeIconUrl) {
        this.typeIconUrl = typeIconUrl;
    }
}
