package top.omooo.audiovideotask.task_1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import top.omooo.audiovideotask.R;

/**
 * Created by SSC on 2018/6/29.
 */

public class DrawPictureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义View、SurfaceView
        setContentView(R.layout.activity_draw_picture);
        //自定义SurfaceView实现画板
//        setContentView(new DrawBoardView(this));
    }
}
