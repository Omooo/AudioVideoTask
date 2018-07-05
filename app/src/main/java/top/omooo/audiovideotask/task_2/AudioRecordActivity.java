package top.omooo.audiovideotask.task_2;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.joker.api.Permissions4M;
import com.joker.api.wrapper.ListenerWrapper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.omooo.audiovideotask.R;

public class AudioRecordActivity extends AppCompatActivity implements Runnable {

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

    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecorder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz = 44100;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes;

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private File mWavFile;
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord = null;
    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecordFiles";
    //保存的音频文件名
    private static final String mFileName = "record.pcm";
    private static final String mWavFileName = "record.wav";
    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;
    private DataOutputStream mDataOutputStreamWav;
    private AudioTrackManager mAudioTrackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        ButterKnife.bind(this);

        initDatas();
        requestPer();
        mAudioTrackManager = new AudioTrackManager();
    }

    //初始化数据
    private void initDatas() {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//计算最小缓冲区
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);//创建AudioRecorder对象

        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists())
            mFileRoot.mkdirs();//创建文件夹

    }


    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_play, R.id.btn_pause, R.id.btn_convert, R.id.btn_play_wav})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startRecord();
                break;
            case R.id.btn_stop:
                stopRecord();
                break;
            case R.id.btn_play:
                mAudioTrackManager.startPlay(mPathName + "/" + mFileName);
                break;
            case R.id.btn_pause:
                mAudioTrackManager.stopPlay();
                break;
            case R.id.btn_convert:
                convertToWav();
                break;
            case R.id.btn_play_wav:
                mAudioTrackManager.startPlay(mPathName + "/" + mWavFileName);
                break;
        }
    }

    //pcm转wav
    private void convertToWav() {
        mWavFile = new File(mFileRoot, mWavFileName);
        mRecordingFile = new File(mFileRoot, mFileName);
        RandomAccessFile wavRaf = null;
        try {
            wavRaf = new RandomAccessFile(mWavFile, "rw");
            byte[] header = generateWavFileHeader(mRecordingFile.length(), mSampleRateInHz, mAudioRecord.getChannelCount());
            wavRaf.seek(0);
            wavRaf.write(header);
            wavRaf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //写入头文件 其实wav比pcm多的就是头文件信息
    private void writeWavFileHeader(FileOutputStream out, long totalAudioLen, long longSampleRate,
                                    int channels) throws IOException {
        byte[] header = generateWavFileHeader(totalAudioLen, longSampleRate, channels);
        out.write(header, 0, header.length);
    }

    private byte[] generateWavFileHeader(long pcmAudioByteCount, long longSampleRate, int channels) {
        long totalDataLen = pcmAudioByteCount + 36; // 不包含前8个字节的WAV文件总长度
        long byteRate = longSampleRate * 2 * channels;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (2 * channels);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (pcmAudioByteCount & 0xff);
        header[41] = (byte) ((pcmAudioByteCount >> 8) & 0xff);
        header[42] = (byte) ((pcmAudioByteCount >> 16) & 0xff);
        header[43] = (byte) ((pcmAudioByteCount >> 24) & 0xff);
        return header;
    }
    //开始录音

    public void startRecord() {

        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(this);
                mThread.start();//开启线程
            }
        }
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }
    //停止录音

    public void stopRecord() {
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioRecord.stop();
            }
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
    }

    @Override
    public void run() {

        //标记为开始采集状态
        isRecording = true;
        //创建一个流，存放从AudioRecord读取的数据
        mRecordingFile = new File(mFileRoot, mFileName);
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        mWavFile = new File(mFileRoot, mWavFileName);
        if (mWavFile.exists()) {
            mWavFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
            mWavFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //获取到文件的数据流
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
            mDataOutputStreamWav = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mWavFile)));
            byte[] buffer = new byte[mBufferSizeInBytes];


            //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
            if (mAudioRecord.getState() == mAudioRecord.STATE_UNINITIALIZED) {
                initDatas();
            }

            writeWavFileHeader(new FileOutputStream(mWavFile), mBufferSizeInBytes, mSampleRateInHz, mAudioRecord.getChannelCount());

            mAudioRecord.startRecording();//开始录音
            //getRecordingState获取当前AudioRecording是否正在采集数据的状态
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);
                    mDataOutputStreamWav.write(buffer[i]);
                }

            }
            mDataOutputStream.close();
            mDataOutputStreamWav.close();
        } catch (Throwable t) {
            stopRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyThread();
        stopRecord();
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
                                Toast.makeText(AudioRecordActivity.this, "录音权限申请失败！", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x02:
                                Toast.makeText(AudioRecordActivity.this, "读写SD卡权限申请失败！", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void permissionRationale(int i) {
                        switch (i) {
                            case 0x01:
                                Toast.makeText(AudioRecordActivity.this, "申请必要权限用于录音！", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x02:
                                Toast.makeText(AudioRecordActivity.this, "申请必要权限用于读写SD卡！", Toast.LENGTH_SHORT).show();
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
}

