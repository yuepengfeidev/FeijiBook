package com.example.feijibook.entity.gson_bean;

import java.util.List;

/**
 * RecordTypeBean
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description 向服务器传输记录设置的json
 */
public class RecordTypeBean {
    String account;
    List<RecordType> list;

    public RecordTypeBean(String account, List<RecordType> recordTypeList) {
        this.account = account;
        list = recordTypeList;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<RecordType> getList() {
        return list;
    }

    public void setList(List<RecordType> list) {
        this.list = list;
    }
}
