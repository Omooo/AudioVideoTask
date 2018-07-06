package top.omooo.audiovideotask.task_4;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.omooo.audiovideotask.R;

public class ExtractMuxerActivity extends AppCompatActivity {
    @BindView(R.id.btn_extract_video)
    Button mBtnExtractVideo;
    @BindView(R.id.btn_extract_audio)
    Button mBtnExtractAudio;
    @BindView(R.id.btn_combine)
    Button mBtnCombine;

    private static final String inputFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVideoTask/input.mp4";
    private static final String outputFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVideoTask/outVideo.mp4";
    private static final String outputFileAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVideoTask/outAudio.mp3";
    private static final String outputFileVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVideoTask/outCombineFile.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_muxer);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_extract_video, R.id.btn_extract_audio, R.id.btn_combine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_extract_video:
                MP4Manager.extractVideo(inputFilePath, outputFilePath);
                break;
            case R.id.btn_extract_audio:
                MP4Manager.extractAudio(inputFilePath, outputFileAudioPath);
                break;
            case R.id.btn_combine:
                MP4Manager.combine(outputFilePath, outputFileAudioPath, outputFileVideoPath);
                break;
        }
    }
}
