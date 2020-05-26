package com.example.feijibook.activity.user_info_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.feijibook.R;
import com.example.feijibook.activity.login_in_act.LoginInActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.FileUtils;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * UIPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/21
 * @description
 */
public class UIPresenter implements UIContract.Presenter {
    private UIContract.View mView;
    private UIContract.Model mModel;
    /**
     * 相册请求码
     */
    private static final int ALBUM_REQUEST_CODE = 1;
    /**
     * 相册请求码
     */
    private static final int CAMERA_REQUEST_CODE = 2;
    /**
     * 剪裁请求码
     */
    private static final int CROP_SMALL_PICTURE = 3;
    private static final String MAN = "男";
    private static final String WOMEN = "女";

    /**
     * 调用照相机返回图片文件
     */
    private File tempFile;

    UIPresenter(Activity activity, UIContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setShowPortraitSettingDialog() {
        mView.showPortraitSettingDialog();
    }

    @Override
    public void setShowSexSettingDialog() {
        mView.showSexSettingDialog();
    }

    @Override
    public void setShowNicknameDialog() {
        mView.showNicknameDialog();
    }

    @Override
    public void setSex(int itemIndex) {
        String sex = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.SEX);
        if (itemIndex == 0) {
            // 初始化显示性别
            mView.showSex(sex);
            return;
        } else if (itemIndex == 1 && !WOMEN.equals(sex)
                || itemIndex == 2 && !MAN.equals(sex)) {
            // 如果修改性别没变，则不执行任何操作
            return;
        } else if (itemIndex == 1 && WOMEN.equals(sex)) {
            // 当选择的性别不同时，修改
            mView.showSex(MAN);
            sex = MAN;
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.SEX, MAN);
        } else if (itemIndex == 2 && MAN.equals(sex)) {
            mView.showSex(WOMEN);
            sex = WOMEN;
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.SEX, WOMEN);
        }

        mModel.requestSetSex(SharedPreferencesUtils.
                getStrFromSp(SharedPreferencesUtils.ACCOUNT), sex)
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals(Constants.CHANGE_SUCCESS)) {
                            mView.showToast("修改性别成功！");
                        } else {
                            mView.showToast("修改性别失败！");
                        }
                    }

                    @Override
                    public void onHttpError(ExceptionHandle.ResponseException exception) {
                        mView.showToast(exception.message);
                    }
                });
    }

    @Override
    public void setNickName(String nickName) {
        String nn = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.NICKNAME);
        if (nickName == null) {
            // 如果传入昵称为空，则为初始化昵称
            nickName = nn;
        } else if (!nn.equals(nickName)) {
            // 当传入昵称和原先昵称不相同时，则修改
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.NICKNAME, nickName);
            NoticeUpdateUtils.updateInfo = true;
            mModel.requestSetNickName(SharedPreferencesUtils.
                    getStrFromSp(SharedPreferencesUtils.ACCOUNT), nickName)
                    .subscribe(new BaseObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s.equals(Constants.CHANGE_SUCCESS)) {
                                mView.showToast("修改昵称成功！");
                            } else {
                                mView.showToast("修改昵称失败！");
                            }
                        }

                        @Override
                        public void onHttpError(ExceptionHandle.ResponseException exception) {
                            mView.showToast(exception.message);
                        }
                    });
        }
        mView.showNickName(nickName);
    }

    @Override
    public void setShowAccount() {
        mView.showAccount(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT));
    }

    @Override
    public void setShowPortrait() {
        String portrait = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.PORTRAIT);
        if (portrait == null || "".equals(portrait)) {
            mView.showPortrait(R.drawable.portrait_default);
        } else {
            mView.showPortrait(portrait);
        }
    }

    @Override
    public void setClippingPortrait(Uri contentUri) {
        if (contentUri == null) {
            Log.i("tag", "The uri is not exist."); }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        // 设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 剪裁图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        // 图片大小不足自动拉伸
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        String url = getPath();
        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.PORTRAIT, url);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(FileUtils.createFile(url)));
        mView.startActForResult(intent, CROP_SMALL_PICTURE); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                // 调用相机后返回
                if (resultCode == RESULT_OK) {
                    // 用相机返回的照片取调用剪裁也需要对uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(MyApplication.sContext,
                                MyApplication.sContext.getPackageName() +
                                        ".provider", tempFile);
                        setClippingPortrait(contentUri);
                    } else {
                        setClippingPortrait(Uri.fromFile(tempFile));
                    }
                }
                break;
            case ALBUM_REQUEST_CODE:
                // 调用相册后返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    // 调用裁剪
                    setClippingPortrait(uri);
                }
                break;
            case CROP_SMALL_PICTURE:
                // 调用裁剪后返回
                if (intent != null) {
                    NoticeUpdateUtils.updateInfo = true;
                    File file = new File(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.PORTRAIT));
                    String account = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT);
                    RequestBody requestPortrait = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                    MultipartBody.Part portrait = MultipartBody.Part.
                            createFormData(account, "portrait.png", requestPortrait);
                    mModel.requestUploadPortrait(portrait)
                            .subscribe(new BaseObserver<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    if (s.equals(Constants.UPLOAD_SUCCESS)) {
                                        setShowPortrait();
                                        mView.showToast("上传头像成功！");

                                    } else {
                                        mView.showToast("上传头像失败！");
                                    }
                                }

                                @Override
                                public void onHttpError(ExceptionHandle.ResponseException exception) {
                                    mView.showToast(exception.message);
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getPortraitFromCamera() {
        // 用于保存调用相机拍照后生成的图片
        tempFile = FileUtils.createFile(MyApplication.BASE_PATH + "/tempPortrait.png");
        // 跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断版本，在7.0以上使用fileprovider 获取uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MyApplication.sContext,
                    MyApplication.sContext.getPackageName() + ".provider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            Log.e("getPicFromAlbum", contentUri.toString());
        } else {
            // 否则使用Uri.fromFile(file)获取uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        mView.startActForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void getPortraitFromAlbum() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mView.startActForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }

    @Override
    public void setSignOut(Activity activity) {
        SharedPreferencesUtils.saveBoolToSp(SharedPreferencesUtils.FIRST_LOGIN, true);
        Intent intent = new Intent(activity, LoginInActivity.class);
        setStartActivity(intent);
       /* // 销毁主界面活动
        BaseActivity.getBindAdjacentLayer().finishAct();*/
       sOnDestroyActListener.onDestroyAct();
        // 销毁当前活动
        setFinishAct();
    }

    public static OnDestroyActListener sOnDestroyActListener;

    public static void setOnDestroyActListener(OnDestroyActListener onDestroyActListener) {
        sOnDestroyActListener = onDestroyActListener;
    }

    public interface OnDestroyActListener {
        void onDestroyAct();
    }


    /**
     * 裁剪后的地址
     */
    private String getPath() {
        return MyApplication.BASE_PATH + "/portrait.png";
    }

    @Override
    public void start() {
        mModel = new UIModel();
    }

    @Override
    public void setInit() {
        mView.initListener();
        setNickName(null);
        setSex(0);
        setShowAccount();
        setShowPortrait();
    }

}
