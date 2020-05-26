package com.ypf.feijibookserver.entity;

/**
 * PhotosBean
 *
 * @author PengFei Yue
 * @date 2019/11/8
 * @description
 */
public class PhotosBean {
    public String id;
    public  String img_url;

    public PhotosBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
