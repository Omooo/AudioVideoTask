package top.omooo.audiovideotask.task_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.omooo.audiovideotask.R;

public class PreviewActivity extends AppCompatActivity {
    @BindView(R.id.btn_surface)
    Button mBtnSurface;
    @BindView(R.id.btn_texture)
    Button mBtnTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_layout);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_surface, R.id.btn_texture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_surface:
                startActivity(new Intent(this,SurfacePreviewActivity.class));
                break;
            case R.id.btn_texture:
                startActivity(new Intent(this, TexturePreviewActivity.class));
                break;
        }
    }
}
