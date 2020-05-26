package com.ypf.feijibookserver.entity;

/**
 * VideoBean
 *
 * @author PengFei Yue
 * @date 2019/11/8
 * @description
 */
public class VideoBean {
    /**
     * 该视频的记录id
     */
   public String id;
    /**
     * 视频名称
     */
    public  String video_url;

    public VideoBean() {
    }

    public VideoBean(String id, String video_url) {
        this.id = id;
        this.video_url = video_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
