package top.omooo.audiovideotask.task_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import top.omooo.audiovideotask.R;

/**
 * Created by SSC on 2018/6/29.
 */

public class CustomImage extends android.support.v7.widget.AppCompatImageView {

    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.picture);
    private Paint mPaint = new Paint();

    public CustomImage(Context context) {
        super(context);
    }

    public CustomImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

}
