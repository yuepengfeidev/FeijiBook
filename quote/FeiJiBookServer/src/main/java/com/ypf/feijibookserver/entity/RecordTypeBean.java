package com.ypf.feijibookserver.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * RecordTypeBean
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description 向服务器传输记录设置的json
 */
public class RecordTypeBean {
    public String account;
    public List<RecordType> list;

    public RecordTypeBean() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<RecordType> getRecordTypeList() {
        return list;
    }

    public void setRecordTypeList(List<RecordType> recordTypeList) {
        list = recordTypeList;
    }
}
