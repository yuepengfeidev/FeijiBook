package com.example.feijibook.widget.my_camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.example.feijibook.activity.camera_act.CameraContract;
import com.example.feijibook.app.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * CameraController
 *
 * @author PengFei Yue
 * @date 2019/9/10
 * @description 自定义Camera2配置
 */
public class CameraController {
    private static final String TAG = "CameraController";
    private static Activity mActivity;
    /**
     * 保存视频、照片的临时文件夹路径
     */
    private String mTempFolderPath;
    private ImageReader mImageReader;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private AutoFitTextureView mTextureView;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private File mFile;
    private Integer mSensorOrientation;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest.Builder mPreviewBuilder;

    /**
     * Camera2 API 保证的最大预览宽高
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private boolean mFlashSupported;
    private int mState = STATE_PREVIEW;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    /**
     * 相机状态：显示相机预览。
     */
    private static final int STATE_PREVIEW = 0;
    /**
     * 相机状态：等待焦点被锁定。
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * 等待曝光被PreCapture状态
     */
    private static final int STATE_WAITING_PRE_CAPTURE = 2;
    /**
     * 相机状态：等待曝光的状态是不是PreCapture。
     */
    private static final int STATE_WAITING_NON_PRE_CAPTURE = 3;
    /**
     * 相机状态：拍照。
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    private MediaRecorder mMediaRecorder;
    private String mNextVideoAbsolutePath;

    private CameraContract.Presenter mPresenter;

    private static class ClassHolder {
        static CameraController mInstance = new CameraController();
    }

    public static CameraController getInstance(Activity activity) {
        mActivity = activity;
        return ClassHolder.mInstance;
    }

    public void initCamera(AutoFitTextureView textureView, CameraContract.Presenter presenter) {
        this.mTextureView = textureView;
        this.mPresenter = presenter;
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        setTempFolderPath(MyApplication.BASE_PATH);
    }

    /**
     * 设置照片视频的临时保存文件
     */
    private void setTempFolderPath(String path) {
        mTempFolderPath = path + "/Temp";
        File tempFolderFile = new File(mTempFolderPath);
        if (!tempFolderFile.exists()) {
            tempFolderFile.mkdirs();
            Log.d(TAG, "临时文件夹创建完毕");
        } else {
            Log.d(TAG, "临时文件夹已存在");
        }
    }

    /**
     * 开始录像
     */
    public void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            mMediaRecorder = new MediaRecorder();
            setUpMediaRecorder();
            closePreviewSession();


            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // 为相机预览设置曲面
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // 设置MediaRecorder的表面
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // 启动捕捉动画,一旦开始，则跟新UI开始录制
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 开启录像
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录像
     */
    public void stopRecordingVideo() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mNextVideoAbsolutePath = null;
        startPreview();
    }

    /**
     * 录制视频时更新预览
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpMediaRecorder() throws IOException {
        // 设置用于录制的音源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 开始捕捉和编码数据到setOutputFile(指定的文件)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        // 设置在录制过程中产生的输出文件的格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getTempVideoFilePath();
        }
        // 设置输出文件的路径
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        // 设置最长事件为13秒
        mMediaRecorder.setMaxDuration(13000);
        // 设置录制的视频编码比特率
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        // 设置要捕捉的视频帧速率
        mMediaRecorder.setVideoFrameRate(25);
        // 设置要捕捉视频的高度和宽度
        mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        // 设置用于录制的视频编码器
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置Audio的编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(270);
                break;
            default:
                break;
        }
        mMediaRecorder.prepare();
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    /**
     * 开启相机
     */
    private void openCamera(int width, int height) {
        // 设置相机输出
        setUpCameraOutputs(width, height);
        // 配置变换
        configureTransform(width, height);
        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // 打开相机预览
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (InterruptedException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // 打开相机时调用此方法，开始相机预览
            mCameraOpenCloseLock.release();
            mCameraDevice = camera;
            // 创建CameraPreviewSession
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraOpenCloseLock.release();
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mCameraOpenCloseLock.release();
            mCameraDevice.close();
            mCameraDevice = null;
            mActivity.finish();
        }
    };

    /**
     * 为相机预览创建新的CameraCaptureSession
     */
    private void startPreview() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // 将默认缓冲区的大小配置为我们想要的相机预览大小，设置分辨率
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // 预览的输出Surface
            Surface surface = new Surface(texture);

            // 设置一个具有输出Surface的CaptureRequest.Builder
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // 创建一个CaptureRequestSession来进行相机预览
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (null == mCameraDevice) {
                                return;
                            }

                            // 显示预览画面
                            mCaptureSession = session;
                            // 自动对焦
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                            // 开启相机预览并添加事件
                            mPreviewRequest = mPreviewRequestBuilder.build();
                            try {
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理与JPEG捕捉有关的事件
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {

                /**
                 * 处理过程
                 */
                private void process(CaptureResult result) {
                    switch (mState) {
                        case STATE_PREVIEW:
                            // 预览状态
                            break;
                        case STATE_WAITING_LOCK: {
                            // 获取自动聚焦状态
                            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                            if (afState == null) {
                                // 不聚焦则 拍摄静态图片
                                captureStillPicture();
                            } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                                if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                                    mState = STATE_PICTURE_TAKEN;
                                    captureStillPicture();
                                } else {
                                    runPreCaptureSequence();
                                }
                            }
                            break;
                        }
                        case STATE_WAITING_PRE_CAPTURE: {
                            // 等待曝光状态
                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            // 曝光完毕后进入等待取消预捕捉状态
                            if (aeState == null ||
                                    aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                                    aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                                mState = STATE_WAITING_NON_PRE_CAPTURE;
                            }
                            break;
                        }
                        case STATE_WAITING_NON_PRE_CAPTURE: {
                            // 不预捕捉状态
                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            // 确认曝光状态结束，则进入拍照状态捕捉静态图像
                            if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                                mState = STATE_PICTURE_TAKEN;
                                captureStillPicture();
                            }
                            break;
                        }
                        default:
                    }
                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                    process(partialResult);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    process(result);
                }
            };

    /**
     * 运行preCapture序列来捕获静止图像
     */
    private void runPreCaptureSequence() {
        try {
            // 设置拍照参数请求,预捕捉
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // 当前状态设置为等待预捕捉
            mState = STATE_WAITING_PRE_CAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍摄静态图片
     */
    private void captureStillPicture() {
        try {
            if (null == mCameraDevice) {
                return;
            }
            // 创建捕捉静态图片的的CaptureRequest.Builder
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // 使用相同的AE和AF模式作为预览。
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // 方向
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                           /* unlockFocus();*/
                        }
                    };

            // 停止连续取景
            mCaptureSession.stopRepeating();
            // 捕捉图片
            mCaptureSession.capture(captureBuilder.build(), captureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照完后解锁焦点
     */
    private void unlockFocus() {
        try {
            // 重置自动对焦
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // 相机恢复正常的预览状态
            mState = STATE_PREVIEW;
            // 打开连续取景模式
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Integer getOrientation(int rotation) {
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * 设置自动打开闪光灯
     */
    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    /**
     * 在确定相机预览大小后应调用此方法
     *
     * @param viewWidth  宽
     * @param viewHeight 高
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        // 以竖屏向上方向为0°，后置相机采集到的图像是90°，前置相机采集到的图像时270°，需要旋转视图
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            // 旋转前的矩阵转换到旋转后的矩形，缩放大小为填充屏幕
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }


    /**
     * 设置与相机相关的成员变量。
     *
     * @param width  相机预览的可用尺寸宽度
     * @param height 相机预览的可用尺寸的高度
     */
    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        // 获取摄像头列表
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                // 不适用前置摄像头
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                // 获取可用的配置流
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // 获取手机旋转的角度以调整图片的方向
                int displayRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
                // Sensor安装角度
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;

                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                mActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthPixels = displayMetrics.widthPixels;
                int heightPixels = displayMetrics.heightPixels;

                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        maxPreviewWidth,
                        maxPreviewHeight);

                mImageReader = ImageReader.newInstance(maxPreviewWidth, maxPreviewHeight,
                        ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                int orientation = mActivity.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // 横屏
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(widthPixels, heightPixels);
                }

                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个合适的相机预览尺寸
     *
     * @param choices 支持的预览尺寸列表
     * @param width   本机分辨率宽度
     * @param height  本机分辨率高度
     * @return 最佳预览尺寸
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        // 挑选分辨率相近的
        for (Size option : choices) {
            int deviationWidth = Math.abs(option.getWidth() - width);
            int deviationHeight = Math.abs(option.getHeight() - height);
            if ((deviationHeight < 300) && (deviationWidth < 300)) {
                return option;
            }
        }
        float reqRatio = (float) width / height;
        Size retSize = null;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        // 没有分辨率相近的则挑选 宽高比相近的
        for (Size size : choices) {
            curRatio = (float) size.getWidth() / size.getHeight();
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    /**
     * 拍照
     */
    public void takePicture() {
        lockFocus();
    }

    private String getTempVideoFilePath() {
        return mTempFolderPath + "/tempVideo.mp4";
    }

    private void lockFocus() {
        // 创建临时照片文件
        mFile = new File(mTempFolderPath + "/tempPhoto.jpg");
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * ImageReader的回调对象
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // 等图片可以得到的时候获取图片并保存
                    mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile, mPresenter));
                    unlockFocus();
                }
            };

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * 将JPG保存到指定的文件中。
     */
    private static class ImageSaver implements Runnable {
        /**
         * JPEG图像
         */
        private final Image mImage;
        /**
         * 保存图像的文件
         */
        private final File mFile;

        private CameraContract.Presenter mPresenter;

        ImageSaver(Image image, File file, CameraContract.Presenter presenter) {
            mImage = image;
            mFile = file;
            mPresenter = presenter;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);

                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 回到主线程显示刚拍的照片
                        mPresenter.setShowConfirmPhoto(bitmap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
