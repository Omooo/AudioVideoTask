package top.omooo.audiovideotask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.omooo.audiovideotask.task_1.DrawPictureActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_task_1)
    Button mBtnTask1;
    @BindView(R.id.btn_task_2)
    Button mBtnTask2;
    @BindView(R.id.btn_task_3)
    Button mBtnTask3;
    @BindView(R.id.btn_task_4)
    Button mBtnTask4;
    @BindView(R.id.btn_task_5)
    Button mBtnTask5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_task_1, R.id.btn_task_2, R.id.btn_task_3, R.id.btn_task_4, R.id.btn_task_5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_task_1:
                startActivity(new Intent(this, DrawPictureActivity.class));
                break;
            case R.id.btn_task_2:
                break;
            case R.id.btn_task_3:
                break;
            case R.id.btn_task_4:
                break;
            case R.id.btn_task_5:
                break;
        }
    }
}
