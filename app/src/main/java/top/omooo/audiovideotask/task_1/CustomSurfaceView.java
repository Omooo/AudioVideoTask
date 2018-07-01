package top.omooo.audiovideotask.task_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import top.omooo.audiovideotask.R;

/**
 * Created by SSC on 2018/6/29.
 */

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Bitmap mBitmap;

    public CustomSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //得到控制器
        mSurfaceHolder = getHolder();
        //对SurfaceView进行操作
        mSurfaceHolder.addCallback(this);
        mPaint = new Paint();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.picture);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawBitmap(mBitmap, new Matrix(), mPaint);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //SurfaceView发生变化时候回调
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //SurfaceView销毁时回调
    }
}
