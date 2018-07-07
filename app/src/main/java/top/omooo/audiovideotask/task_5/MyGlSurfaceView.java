package top.omooo.audiovideotask.task_5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGlSurfaceView extends GLSurfaceView {

    private MyGlRenderer mMyGlRenderer;
    public MyGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        mMyGlRenderer = new MyGlRenderer();
        setRenderer(mMyGlRenderer);
    }
}
