package top.omooo.audiovideotask.task_3;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mFrameCount = 0;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启预览
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //若需要旋转、更改大小或者重新设置，停止预览
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (this.getResources().getConfiguration().orientation != -Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private Camera.PreviewCallback mPreviewCallback=new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            mFrameCount++;
        }
    };


}
