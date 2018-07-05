package top.omooo.audiovideotask.task_2;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.joker.api.Permissions4M;
import com.joker.api.wrapper.ListenerWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.omooo.audiovideotask.R;

/**
 * Created by SSC on 2018/7/1.
 */

public class AudioActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button mBtnStart;
    @BindView(R.id.btn_stop)
    Button mBtnStop;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.btn_pause)
    Button mBtnPause;
    @BindView(R.id.btn_convert)
    Button mBtnConvert;
    @BindView(R.id.btn_play_wav)
    Button mBtnPlayWav;

    private MyAudioManager mMyAudioManager;

    private String fileRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecordFile";

    private AudioTrackManager mAudioTrackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        ButterKnife.bind(this);

        requestPer();
        init();
    }

    private void init() {
        mMyAudioManager = new MyAudioManager(this);
        mAudioTrackManager = AudioTrackManager.getInstance();
    }

    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_play, R.id.btn_pause, R.id.btn_convert,R.id.btn_play_wav})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mMyAudioManager.startRecord();
                break;
            case R.id.btn_stop:
                mMyAudioManager.stopRecord();
                break;
            case R.id.btn_play:
//                mMyAudioManager.playFile(fileRoot + "/audio.pcm");
                mAudioTrackManager.startPlay(fileRoot + "/audio.pcm");
                break;
            case R.id.btn_pause:
//                mMyAudioManager.stopPlay();
                mAudioTrackManager.stopPlay();
                break;
            case R.id.btn_convert:

                break;
            case R.id.btn_play_wav:
                break;
        }
    }

    private void requestPer() {
        Permissions4M.get(this)
                .requestPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestCodes(0x01, 0x02)
                .requestListener(new ListenerWrapper.PermissionRequestListener() {
                    @Override
                    public void permissionGranted(int i) {

                    }

                    @Override
                    public void permissionDenied(int i) {
                        switch (i) {
                            case 0x01:
                                Toast.makeText(AudioActivity.this, "录音权限申请失败！", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x02:
                                Toast.makeText(AudioActivity.this, "读写SD卡权限申请失败！", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void permissionRationale(int i) {
                        switch (i) {
                            case 0x01:
                                Toast.makeText(AudioActivity.this, "申请必要权限用于录音！", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x02:
                                Toast.makeText(AudioActivity.this, "申请必要权限用于读写SD卡！", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyAudioManager.stopRecord();
        mMyAudioManager.destroyThread();
    }
}
