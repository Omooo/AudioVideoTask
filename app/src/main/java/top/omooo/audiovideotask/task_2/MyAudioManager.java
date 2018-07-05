package top.omooo.audiovideotask.task_2;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SSC on 2018/7/2.
 */

public class MyAudioManager implements Runnable{
    //指定音频源，这个和MediaRecorder是一样的，MIC指定为麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率（MediaRecorder的采样率通常是8000Hz AAC通常是44100，44100能兼容所有设置）
    private static final int mSampleRateInHz = 44100;
    //指定音频声道数，在AudioFormat类中指定用于此的常量，单声道
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    //指定音频量化位数
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小，调用AudioRecord.getMinBufferSize()方法获得
    private int mBufferSizeInBytes;

    private File mRecordingFile;
    private boolean isRecording = false;
    private boolean isPlaying;
    private AudioRecord mAudioRecord = null;
    private AudioTrack mAudioTrack = null;
    private File mFileRoot = null;

    //存放的目录路径
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecordFile";
    //保存的PCM文件名
    private static final String mFileNamePcm = "audio.pcm";

    private Thread mThread;
    private Thread mTrackThread;
    private DataOutputStream mDataOutputStream;
    private DataInputStream mDataInputStream;

    //音频流类型
    private static final int mStreamType = AudioManager.STREAM_MUSIC;
    private static int mMode = AudioTrack.MODE_STREAM;
    private int mMinBufferSizeTrack;

    private Context mContext;

    public MyAudioManager(Context context) {
        mContext = context;
        initDatas();
    }

    private void initDatas() {
        //计算最小缓冲区
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists()) {
            mFileRoot.mkdirs();
            Toast.makeText(mContext, "创建文件夹", Toast.LENGTH_SHORT).show();
        }

        //AudioTrack
        mMinBufferSizeTrack = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
        mAudioTrack = new AudioTrack(mStreamType, mSampleRateInHz, mChannelConfig, mAudioFormat, mMinBufferSizeTrack, mMode);


    }

    //开始录音
    public void startRecord() {
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(this);
                mThread.start();
            }
        }
        Toast.makeText(mContext, "开始录音", Toast.LENGTH_SHORT).show();
    }


    public void destroyThread() {
        isRecording = false;
        if (null != mThread && mThread.getState() == Thread.State.RUNNABLE) {
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
    }

    //停止录音
    public void stopRecord() {
        isRecording = false;
        //停止录音，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.stop();
            }
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
        destroyThread();
        Toast.makeText(mContext, "停止录音", Toast.LENGTH_SHORT).show();
    }

    public void playFile(String path) {
        File file = new File(path);
        try {
            mDataInputStream = new DataInputStream(new FileInputStream(file));
            isPlaying = true;
            if (mTrackThread == null) {
                mTrackThread = new Thread(this);
                mTrackThread.start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mAudioTrack != null) {
            if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioTrack.stop();//停止播放
            }
            if (mAudioTrack != null) {
                mAudioTrack.release();//释放audioTrack资源
            }
        }
        if (mDataInputStream != null) {
            try {
                mDataInputStream.close();//关闭数据输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        isRecording = true;
        mRecordingFile = new File(mFileRoot, mFileNamePcm);
        if (mRecordingFile.exists()) {
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "创建文件夹出错", Toast.LENGTH_SHORT).show();
        }
        try {
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
            byte[] buffer = new byte[mBufferSizeInBytes];
            if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                initDatas();
            }
            mAudioRecord.startRecording();
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);
                }
            }
            mDataOutputStream.close();
        } catch (IOException e) {
            stopRecord();
            Toast.makeText(mContext, "录音失败！", Toast.LENGTH_SHORT).show();
        }
    }
}
