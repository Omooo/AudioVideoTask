package top.omooo.audiovideotask.task_5;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.omooo.audiovideotask.R;

public class GLActivity extends AppCompatActivity {
    @BindView(R.id.frame_layout)
    FrameLayout mFrameLayout;


    private GLSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl);
        ButterKnife.bind(this);

        mGLSurfaceView = new MyGlSurfaceView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(500, 500);
        addContentView(mGLSurfaceView,layoutParams);
    }
}
