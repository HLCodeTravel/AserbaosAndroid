package com.aserbao.aserbaosandroid.opengl.openGlCamera.simpleCameraOpengl.simpleCamera;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.aserbao.aserbaosandroid.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 使用SurfaceView预览Camera数据
 */
public class CameraSurfaceViewShowActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    //    @BindView(R.id.mSurface)
    SurfaceView mSurfaceView;

    public SurfaceHolder mHolder;
    @BindView(R.id.container_fl)
    FrameLayout mContainerFl;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_camera2);
        ButterKnife.bind(this);
        /*View view = LayoutInflater.from(this).inflate(R.layout.surface_view_layout, null);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface_view);*/
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.setBackgroundColor(Color.BLUE);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mContainerFl.post(new Runnable() {
            @Override
            public void run() {
                mContainerFl.addView(mSurfaceView);
                mContainerFl.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // Open the Camera in preview mode
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mParameters = mCamera.getParameters();
                    mParameters.setPictureFormat(PixelFormat.JPEG); //图片输出格式
//                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//预览持续发光
                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//持续对焦模式
                    mCamera.setParameters(mParameters);
                    mCamera.startPreview();
                    mCamera.cancelAutoFocus();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @OnClick(R.id.btn_change)
    public void onViewClicked() {
//        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("rotationX", 0.0f, 360.0f, 0.0F);
        PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 360.0f, 0.0F);
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f, 1.0f);
        PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mSurfaceView, valuesHolder, valuesHolder1, valuesHolder3);
        objectAnimator.setDuration(5000).start();
    }


}
