package com.ypf.feijibookserver.dao;

import com.ypf.feijibookserver.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserMapper
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description MyBatis数据库映射操作
 */

@Repository
public interface UserMapper {
    /**
     * 注册账户
     *
     * @param account  注册的账户
     * @param password 账户密码
     */
    void register(@Param("account") String account, @Param("password") String password);

    /**
     * 设置类型编辑设置
     */
    void setTypeSetting(@Param("account") String account, @Param("list") List<RecordType> list);

    /**
     * 为该用户添加记录
     *
     * @param detailRecordBean 新添加的记录
     */
    void addRecord(DetailRecordBean detailRecordBean);

    /**
     * 删除用户单条记录
     *
     * @param id      记录的id
     * @param account 用户名
     */
    void deleteRecord(@Param("id") String id, @Param("account") String account);

    /**
     * 删除该用户的类型设置
     */
    void deleteTypeSetting(String account);

    /**
     * 通过用户名称查找该用户的信息
     *
     * @param account 用户名
     * @return 用户信息
     */
    UserInfoBean findAccount(String account);

    /**
     * 设置该用户的
     *
     * @param portrait
     */
    void setPortrait(@Param("account") String account, @Param("portrait") String portrait);

    /**
     * 更改该账户的密码
     *
     * @param account  账户名
     * @param password 该账户的密码
     */
    void setPassword(@Param("account") String account, @Param("password") String password);

    /**
     * 设置账户的昵称
     *
     * @param account  账户名
     * @param nickname 该账户的昵称
     */
    void setNickname(@Param("account") String account, @Param("nickname") String nickname);

    /**
     * 设置账户的性别
     *
     * @param account 用户名
     * @param sex     性别
     */
    void setSex(@Param("account") String account, @Param("sex") String sex);

    /**
     * 设置用户记录照片
     */
    void setImgUrl(@Param("account") String account, @Param("photos") List<PhotosBean> photos);

    /**
     * 设置用户记录视频
     */
    void setVideoUrl(@Param("account") String account, @Param("videos") List<VideoBean> list);

}
