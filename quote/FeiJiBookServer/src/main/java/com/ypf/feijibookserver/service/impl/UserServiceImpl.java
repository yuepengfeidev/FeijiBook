package com.ypf.feijibookserver.service.impl;

import com.ypf.feijibookserver.dao.UserMapper;
import com.ypf.feijibookserver.entity.*;
import com.ypf.feijibookserver.service.UserService;
import com.ypf.feijibookserver.util.FileUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * UserServiceImpl
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    private static String LOGIN_SUCCESS = "LoginSuccess";
    private static String ACCOUNT_ERROR = "AccountError";
    private static String PASSWORD_ERROR = "PasswordError";
    private static String REGISTER_FAILED = "RegisterFailed";
    private static String REGISTER_SUCCESS = "RegisterSuccess";
    private static String UPLOAD_FAILED = "UpLoadFailed";
    private static String UPLOAD_SUCCESS = "UpLoadSuccess";
    private static String CHANGE_SUCCESS = "ChangeSuccess";
    private static String DELETE_SUCCESS = "DeleteSuccess";
    /**
     * 无界线程池
     */
    private ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>());

    @Override
    public String login(String account, String password) {
        UserInfoBean userInfo = userMapper.findAccount(account);
        if (userInfo == null) {
            // 没有找到该账户名
            return ACCOUNT_ERROR;
        } else {
            if (userInfo.getPassword().equals(password)) {
                // 密码正确，登录成功
                return LOGIN_SUCCESS;
            } else {
                return PASSWORD_ERROR;
            }
        }
    }

    @Override
    public UserInfoBean getUserInfo(String account) {
        return userMapper.findAccount(account);
    }

    @Override
    public String register(String account, String password) {
        UserInfoBean userInfo = userMapper.findAccount(account);
        if (userInfo != null) {
            // 已存在该账户，注册失败
            return REGISTER_FAILED;
        } else {
            // 否则注册成功，添加账户到数据库
            userMapper.register(account, password);
            return REGISTER_SUCCESS;
        }
    }

    @Override
    public String addRecord(DetailRecordBean detailRecordBean) {
        userMapper.addRecord(detailRecordBean);
        return UPLOAD_SUCCESS;
    }

    @Override
    public String changeRecord(DetailRecordBean detailRecordBean) {
        userMapper.deleteRecord(detailRecordBean.getResId(), detailRecordBean.getAccount());
        userMapper.addRecord(detailRecordBean);
        return CHANGE_SUCCESS;
    }

    @Override
    public String upLoadTypeSetting(String account, List<RecordType> list) {
        userMapper.deleteTypeSetting(account);
        userMapper.setTypeSetting(account, list);
        return UPLOAD_SUCCESS;
    }

    @Override
    public String deleteRecord(String account, String id) {
        userMapper.deleteRecord(id, account);
        return DELETE_SUCCESS;
    }

    @Override
    public String uploadPortrait(HttpServletRequest httpServletRequest) {
        try { for (FileItem fileItem : FileUtils.
                    parseFileUpload(httpServletRequest)) {
                // 这是传入的name
                String account = fileItem.getFieldName();
                // 这是传入的文件名
                String portrait = fileItem.getName();
                String realPath = httpServletRequest.
                        getServletContext().getRealPath("\\");
                String saveUrl = FileUtils.getFileName
                        (realPath, account, "portrait",
                        account + portrait);
                try { InputStream inputStream = fileItem.getInputStream();
                    FileUtils.inputStreamToFile(inputStream, saveUrl);
                    userMapper.setPortrait(account, saveUrl);
                } catch (IOException e) { e.printStackTrace(); } }
            return UPLOAD_SUCCESS;
        } catch (FileUploadException e) { e.printStackTrace(); }
        return UPLOAD_FAILED; }

    @Override
    public String uploadRecordVideos(HttpServletRequest httpServletRequest) {
        String account = httpServletRequest.getParameter("account");
        String realPath = httpServletRequest.getServletContext().getRealPath("\\");
        try {
            List<FileItem> fileItems = FileUtils.parseFileUpload(httpServletRequest);
            UploadVideosThread thread = new
                    UploadVideosThread(fileItems, account, realPath, userMapper);
            threadPoolExecutor.execute(thread);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }return UPLOAD_SUCCESS; }

    @Override
    public String uploadRecordPhotos(HttpServletRequest httpServletRequest) {
        String account = httpServletRequest.getParameter("account");
        String realPath = httpServletRequest.getServletContext().getRealPath("\\");
        try {
            List<FileItem> fileItems = FileUtils.parseFileUpload(httpServletRequest);
            UploadPhotosThread thread =
                    new UploadPhotosThread(fileItems, account, realPath, userMapper);
            threadPoolExecutor.execute(thread);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        return UPLOAD_SUCCESS;
    }

    @Override
    public void downloadPortrait(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getParameter("url");
        InputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(path)));
            //转码，免得文件名中文乱码
            path = URLEncoder.encode(path, "UTF-8");
            //设置文件下载头
            response.addHeader("Content-Disposition", "attachment;filename=" + path);
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            int len;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public String changeSex(String account, String sex) {
        userMapper.setSex(account, sex);
        return CHANGE_SUCCESS;
    }

    @Override
    public String changeNickName(String account, String nickName) {
        userMapper.setNickname(account, nickName);
        return CHANGE_SUCCESS;
    }


    @Override
    public String changePassword(String account, String redPw, String changePw) {
        UserInfoBean userInfoBean = userMapper.findAccount(account);
        if (userInfoBean.getPassword().equals(redPw)) {
            userMapper.setPassword(account, changePw);
            return CHANGE_SUCCESS;
        } else {
            return PASSWORD_ERROR;
        }
    }

    public class UploadPhotosThread implements Runnable {
        List<FileItem> fileItems;
        String account;
        String realPath;
        private UserMapper userMapper;

        UploadPhotosThread(List<FileItem> fileItems, String account, String realPath, UserMapper userMapper) {
            this.fileItems = fileItems;
            this.account = account;
            this.realPath = realPath;
            this.userMapper = userMapper;
        }

        @Override
        public void run() {
            try {
                String lastId = null;
                List<PhotosBean> photosBeans = new ArrayList<>();
                // 这是传入的文件名
                String imgName;
                StringBuilder stringBuilder = new StringBuilder();
                String saveUrl;
                for (FileItem fileItem : fileItems) {
                    String id = fileItem.getFieldName();

                    imgName = fileItem.getName();
                    InputStream inputStream = fileItem.getInputStream();
                    saveUrl = FileUtils.getFileName(realPath, account, "photo",
                            imgName);
                    FileUtils.inputStreamToFile(inputStream, saveUrl);
                    if (lastId != null && !id.equals(lastId)) {
                        // 如果上一个id和当前id不同e，表示不同记录，则添加上条记录到list
                        PhotosBean photosBean = new PhotosBean();
                        photosBean.setId(lastId);
                        photosBean.setImg_url(stringBuilder.toString());
                        photosBeans.add(photosBean);
                        stringBuilder = new StringBuilder();
                        lastId = id;
                    } else if (lastId == null) {
                        // 第一条记录则记下id
                        lastId = id;
                    }
                    stringBuilder.append(imgName);
                    stringBuilder.append("|");
                }
                PhotosBean photosBean = new PhotosBean();
                photosBean.setId(lastId);
                photosBean.setImg_url(stringBuilder.toString().trim());
                // 添加最后一条记录（for循环中只添加上调记录）
                photosBeans.add(photosBean);
                if (photosBeans.size() > 0) {
                    userMapper.setImgUrl(account, photosBeans);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class UploadVideosThread implements Runnable {
        List<FileItem> fileItems;
        String account;
        String realPath;
        UserMapper userMapper;

        UploadVideosThread(List<FileItem> fileItems, String account, String realPath, UserMapper userMapper) {
            this.fileItems = fileItems;
            this.account = account;
            this.realPath = realPath;
            this.userMapper = userMapper;
        }

        @Override
        public void run() {
            try {
                String id;
                String video;
                List<VideoBean> videoBeans = new ArrayList<>();
                for (FileItem fileItem : fileItems) {
                    id = fileItem.getFieldName();
                    // 这是传入的文件名
                    video = fileItem.getName();
                    videoBeans.add(new VideoBean(id, video));
                    String saveUrl = FileUtils.getFileName(realPath, account, "video",
                            video);

                    InputStream inputStream = fileItem.getInputStream();
                    FileUtils.inputStreamToFile(inputStream, saveUrl);
                }
                if (videoBeans.size() > 0) {
                    userMapper.setVideoUrl(account, videoBeans);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
