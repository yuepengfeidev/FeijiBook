package com.ypf.feijibookserver.service;

import com.ypf.feijibookserver.entity.DetailRecordBean;
import com.ypf.feijibookserver.entity.RecordType;
import com.ypf.feijibookserver.entity.UserInfoBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * UserService
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description 客户端请求接口
 */


public interface UserService {

    /**
     * 登录接口
     *
     * @param account  账户
     * @param password 密码
     * @return 登录状态信息
     */
    String login(String account, String password);

    /**
     * 获取用户信息
     *
     * @param account 账户名
     * @return 用户信息
     */
    UserInfoBean getUserInfo(String account);

    /**
     * 注册接口
     *
     * @param account  注册的账户
     * @param password 注册的密码
     * @return 注册状态信息
     */
    String register(String account, String password);

    /**
     * 添加记录
     *
     * @return 添加记录的状态信息
     */
    String addRecord(DetailRecordBean detailRecordBean);

    /**
     * 修改记录（通过原纪录的id删除原纪录，然后添加新记录）
     *
     * @param detailRecordBean 新记录
     */
    String changeRecord(DetailRecordBean detailRecordBean);

    /**
     * 上传类型设置
     */
    String upLoadTypeSetting(String account, List<RecordType> list);


    /**
     * 删除记录
     *
     * @param account 删除的用户名
     * @param id      记录的id
     * @return 删除记录的状态信息
     */
    String deleteRecord(String account, String id);

    /**
     * 上传头像，同时改变数据库中的个人信息
     *
     * @param httpServletRequest 设置头像的请求
     * @return 设置头像的状态信息
     */
    String uploadPortrait(HttpServletRequest httpServletRequest);

    /**
     * 上传用户记录的视频
     */
    String uploadRecordVideos(HttpServletRequest httpServletRequest);

    /**
     * 上传用户记录的照片
     */
    String uploadRecordPhotos(HttpServletRequest httpServletRequest);

    /**
     * 下载用户头像
     */
    void downloadPortrait(HttpServletRequest httpServletRequest, HttpServletResponse response);

    /**
     * 改变该用户性别
     *
     * @param account 用户名
     * @param sex     性别
     * @return 改变用户性别的状态信息
     */
    String changeSex(String account, String sex);

    /**
     * 改变用户的昵称
     *
     * @param account  用户名
     * @param nickname 昵称
     * @return 改变用户昵称的状态
     */
    String changeNickName(String account, String nickname);


    /**
     * 修改用户密码
     *
     * @param account  用户名
     * @param resPw    原密码
     * @param changePw 新密码
     * @return 修改密码的状态
     */
    String changePassword(String account, String resPw, String changePw);
}
