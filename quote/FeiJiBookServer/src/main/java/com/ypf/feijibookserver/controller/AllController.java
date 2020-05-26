package com.ypf.feijibookserver.controller;

import com.ypf.feijibookserver.entity.DetailRecordBean;
import com.ypf.feijibookserver.entity.RecordTypeBean;
import com.ypf.feijibookserver.entity.UserInfoBean;
import com.ypf.feijibookserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AllController
 *
 * @author PengFei Yue
 * @date 2019/10/10
 * @description
 */

@Controller
@RequestMapping("/user")
public class AllController {
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public String login(UserInfoBean userInfoBean) {
        String account = userInfoBean.getAccount();
        String password = userInfoBean.getPassword();
        return userService.login(account, password);
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(UserInfoBean userInfoBean) {
        String account = userInfoBean.getAccount();
        String password = userInfoBean.getPassword();
        return userService.register(account, password);
    }

    @PostMapping("/addDetailRecord")
    @ResponseBody
    public String addDetailRecord(DetailRecordBean recordBean) {
        return userService.addRecord(recordBean);
    }

    @PostMapping("/changeDetailRecord")
    @ResponseBody
    public String changeDetailRecord(DetailRecordBean recordBean) {
        return userService.changeRecord(recordBean);
    }

    @PostMapping("/deleteDetailRecord")
    @ResponseBody
    public String deleteDetailRecord(String account, String id) {
        return userService.deleteRecord(account, id);
    }

    @PostMapping("/getUserInfo")
    @ResponseBody
    public UserInfoBean getUserInfo(String account) {
        return userService.getUserInfo(account);
    }

    @RequestMapping("/uploadPortrait")
    @ResponseBody
    public String uploadPortrait(HttpServletRequest httpServletRequest) {
        return userService.uploadPortrait(httpServletRequest);
    }

    @PostMapping("/changeSex")
    @ResponseBody
    public String changeSex(String account, String sex) {
        return userService.changeSex(account, sex);
    }

    @PostMapping("/changeNickName")
    @ResponseBody
    public String changeNickName(String account, String nickname) {
        return userService.changeNickName(account, nickname);
    }

    @GetMapping(value = "/downLoadPortrait")
    public void downLoadPortrait(HttpServletRequest request, HttpServletResponse response) {
        userService.downloadPortrait(request, response);
    }

    @PostMapping("/upLoadTypeSetting")
    @ResponseBody
    public String upLoadTypeSetting
            (@RequestBody RecordTypeBean recordTypeBean) {
        return userService.upLoadTypeSetting(recordTypeBean.getAccount(),
                recordTypeBean.getRecordTypeList());
    }

    @RequestMapping("/uploadRecordPhotos")
    @ResponseBody
    public String uploadRecordPhotos(HttpServletRequest httpServletRequest) {
        return userService.uploadRecordPhotos(httpServletRequest);
    }

    @RequestMapping("/uploadRecordVideos")
    @ResponseBody
    public String uploadRecordVideos(HttpServletRequest httpServletRequest) {
        return userService.uploadRecordVideos(httpServletRequest);
    }

    @PostMapping("/changePassword")
    @ResponseBody
    public String changePassword(String account, String resPw, String newPw) {
        return userService.changePassword(account, resPw, newPw);
    }
}
