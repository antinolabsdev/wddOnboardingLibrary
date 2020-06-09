package com.library.mylibrary.camera.p2v;


import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.library.mylibrary.R;
import com.library.mylibrary.camera.widget.AutoFitTextureView;
import com.library.mylibrary.camera.widget.CaptureButton;


@SuppressLint("ValidFragment")
public class CameraCaptureRecordFragment extends BaseFragment implements OnCameraCaptureListener {


    private int mode;
    private long duration;

    private CameraOrientationListener cameraOrientationListener;

    private AutoFitTextureView surfaceView;
    private View viewBack;
    private CaptureButton captureButton;
    private ImageView viewSplashMode, viewSwitch, viewFocusView;
    private TextView viewTextInfo;
    private View viewNavigation;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public static CameraCaptureRecordFragment newInstance(int mode, long duration) {
        CameraCaptureRecordFragment fragment = new CameraCaptureRecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, mode);
        args.putLong(ARG_PARAM2, duration);
        fragment.setArguments(args);
        return fragment;
    }


//    public CameraCaptureRecordFragment(int mode, long duration) {
//
//        this.mode = mode;
//        this.duration = duration;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mode = getArguments().getInt(ARG_PARAM1);
            this.duration = getArguments().getInt(ARG_PARAM2);
        }

        cameraOrientationListener = new CameraOrientationListener(getContext());
        cameraOrientationListener.enable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.camera_fragment_capture_record, container, false);
        surfaceView = view.findViewById(R.id.camera_capture_record_surface_view);
        viewBack = view.findViewById(R.id.camera_capture_record_btn_back);
        captureButton = view.findViewById(R.id.camera_capture_record_capture_button);
        viewSplashMode = view.findViewById(R.id.camera_capture_record_iv_splash);
        viewSwitch = view.findViewById(R.id.camera_capture_record_iv_switch);
        viewFocusView = view.findViewById(R.id.camera_capture_record_focus_view);
        viewTextInfo = view.findViewById(R.id.camera_capture_record_tv_info);
        viewNavigation = view.findViewById(R.id.camera_capture_record_view_navigation);

        // ——————————————————————————————————————初始化——————————————————————————————————————————
        final Capture capture = new Capture(surfaceView);
        capture.setOnCameraCaptureListener(this);

        captureButton.setMode(mode);
        captureButton.setDuration(duration);
        if (mode == CaptureButton.Mode.MODE_CAPTURE) {
            viewTextInfo.setText(""); //Tap to take a photo
        } else if (mode == CaptureButton.Mode.MODE_RECORD) {
            viewTextInfo.setText(""); //Long press the camera
        } else if (mode == CaptureButton.Mode.MODE_CAPTURE_RECORD) {
            viewTextInfo.setText(""); //Tap to take a photo
        }
        // ——————————————————————————————————————点击事件——————————————————————————————————————————
        // 返回
        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 切换闪光灯
        viewSplashMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture.enableFlashLight();
            }
        });

        // 切换摄像头
        viewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture.switchCamera();
            }
        });

        // 点击拍摄
        captureButton.setOnProgressTouchListener(new CaptureButton.OnProgressTouchListener() {
            @Override
            public void onCapture() {
                capture.capturePhoto(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordStart() {
                capture.captureRecordStart(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordEnd() {
                capture.captureRecordEnd();
            }

            @Override
            public void onCaptureError(String message) {
                capture.captureRecordFailed();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

        });

        // 对焦
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        float X = event.getRawX();
                        float Y = event.getRawY();
                        Util.log("click    X：" + X + "    Y：" + Y);
                        capture.focus(X, Y);
                        break;
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.setFullScreen(getActivity());
        // 导航栏区域
        if (Util.isNavigationBarShow(getActivity())) {
            ViewGroup.LayoutParams layoutParams = viewNavigation.getLayoutParams();
            layoutParams.height = Util.getNavigationBarHeight(getActivity());
            viewNavigation.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onDestroy() {
        cameraOrientationListener.disable();
        cameraOrientationListener = null;
        super.onDestroy();
    }

    // —————————————————————————————————VIEW———————————————————————————————————————

    @Override
    public void onToggleSplash(String flashMode) {
        if (flashMode == null) {
            // 说明不支持闪光灯
            viewSplashMode.setVisibility(View.GONE);
            return;
        }
        viewSplashMode.setVisibility(View.VISIBLE);
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_off_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_camera_flash_auto_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_on_24dp);
        }
    }

    @Override
    public void onFocusSuccess(float x, float y) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewFocusView.getLayoutParams();
        layoutParams.leftMargin = (int) x - Util.dip2px(getContext(), 35);
        layoutParams.topMargin = (int) y - Util.dip2px(getContext(), 35);
        viewFocusView.setLayoutParams(layoutParams);
        Util.scale(viewFocusView);
    }

    @Override
    public void onCapturePhoto(String photoPath) {
        // 拍照成功
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.camera_capture_main_framelayout,
                        CameraCapturePreviewFragment.newInstance(Util.Const.类型_照片, photoPath),
                        CameraCapturePreviewFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCaptureRecord(String filePath) {
        // 录像成功
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.camera_capture_main_framelayout,
                        CameraCapturePreviewFragment.newInstance(Util.Const.类型_视频, filePath),
                        CameraCapturePreviewFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
