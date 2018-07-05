package top.omooo.audiovideotask.task_3;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.joker.api.Permissions4M;
import com.joker.api.wrapper.ListenerWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.omooo.audiovideotask.R;

public class SurfacePreviewActivity extends AppCompatActivity {

    private static final String TAG = "SurfacePreviewActivity";
    @BindView(R.id.frame_layout)
    FrameLayout mFrameLayout;

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        requestPer();
        mCameraPreview = new CameraPreview(SurfacePreviewActivity.this, mCamera);
        new InitCameraThread().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mCamera) {
            if (safeCameraOpen()) {
                    mCameraPreview.setCamera(mCamera);
            }
        }
    }

    private boolean safeCameraOpen() {
        boolean open = false;
        try {
            releaseCamera();
            mCamera = Camera.open();
            open = (mCamera != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return open;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private class InitCameraThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (safeCameraOpen()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFrameLayout.removeAllViews();
                        mFrameLayout.addView(mCameraPreview);
                    }
                });
            }
        }
    }

    private void requestPer() {
        Permissions4M.get(this)
                .requestPermissions(Manifest.permission.CAMERA)
                .requestCodes(0x01)
                .requestListener(new ListenerWrapper.PermissionRequestListener() {
                    @Override
                    public void permissionGranted(int i) {

                    }

                    @Override
                    public void permissionDenied(int i) {
                        switch (i) {
                            case 0x01:
                                Toast.makeText(SurfacePreviewActivity.this, "照相机权限申请失败！", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void permissionRationale(int i) {
                        switch (i) {
                            case 0x01:
                                Toast.makeText(SurfacePreviewActivity.this, "申请必要权限用于预览相机！", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        Permissions4M.onRequestPermissionsResult(this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
