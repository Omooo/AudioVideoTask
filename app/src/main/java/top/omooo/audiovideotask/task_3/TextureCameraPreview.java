package top.omooo.audiovideotask.task_3;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import java.io.IOException;

public class TextureCameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    public TextureCameraPreview(Context context) {
        super(context);
    }

    public TextureCameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }
        try {
            mCamera.setPreviewCallback(mCameraPreviewCallback);
            mCamera.setPreviewTexture(surface); //使用SurfaceTexture
            mCamera.startPreview();
        } catch (IOException ioe) {

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private Camera.PreviewCallback mCameraPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    };
}
