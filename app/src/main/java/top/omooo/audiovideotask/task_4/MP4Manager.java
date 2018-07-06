package top.omooo.audiovideotask.task_4;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MP4Manager {

    private static final String TAG = "MP4Manager";

    public static void extractVideo(String inputFilePath,String outputFilePath) {
        int videoIndex = -1;
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;
        try {
            mediaExtractor.setDataSource(inputFilePath);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    videoIndex = i; //得到具体轨道
                    break;
                }
            }
            mediaExtractor.selectTrack(videoIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);
            mediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int i = mediaMuxer.addTrack(trackFormat);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            long videoSampleTime;
            //获取每一帧的时间
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();

                mediaExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = mediaExtractor.getSampleTime();
                videoSampleTime = Math.abs(sampleTime - sampleTime1);
            }
            mediaExtractor.unselectTrack(videoIndex);
            mediaExtractor.selectTrack(videoIndex);

            while (true) {
                int data = mediaExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }

                bufferInfo.size = data;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSampleTime;

                mediaMuxer.writeSampleData(i, byteBuffer, bufferInfo);

                mediaExtractor.advance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mediaExtractor.release();
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
        }
    }

    public static void extractAudio(String inputFilePath,String outputFilePath) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;
        int audioIndex = -1;
        try {
            mediaExtractor.setDataSource(inputFilePath);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioIndex = i;
                }
            }
            mediaExtractor.selectTrack(audioIndex);
            MediaFormat format = mediaExtractor.getTrackFormat(audioIndex);
            mediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int i = mediaMuxer.addTrack(format);
            mediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            long time;

            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();

                mediaExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = mediaExtractor.getSampleTime();
                mediaExtractor.advance();

                time = Math.abs(sampleTime - sampleTime1);
            }

            mediaExtractor.unselectTrack(audioIndex);
            mediaExtractor.selectTrack(audioIndex);
            while (true) {
                int data = mediaExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }
                bufferInfo.size = data;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += time;

                mediaMuxer.writeSampleData(i, byteBuffer, bufferInfo);
                mediaExtractor.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mediaExtractor.release();
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
        }
    }

    public static void combine(String inputVideoFilePath, String inputAudioFilePath, String outputVideoFilePath) {
        MediaExtractor videoExtractor;
        MediaExtractor audioExtractor = null;
        MediaMuxer mediaMuxer = null;
        videoExtractor = new MediaExtractor();
        try {
            videoExtractor.setDataSource(inputVideoFilePath);
            int videoIndex = -1;
            MediaFormat videoTrackFormat = null;
            int trackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                videoTrackFormat = videoExtractor.getTrackFormat(i);
                if (videoTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                    videoIndex = i;
                }
            }
            audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(inputAudioFilePath);
            int audioIndex = -1;
            MediaFormat audioTrackFormat = null;
            trackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                audioTrackFormat = audioExtractor.getTrackFormat(i);
                if (audioTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioIndex = i;
                }
            }

            videoExtractor.selectTrack(videoIndex);
            audioExtractor.selectTrack(audioIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer = new MediaMuxer(outputVideoFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            assert videoTrackFormat != null;
            int videoTrackIndex = mediaMuxer.addTrack(videoTrackFormat);
            assert audioTrackFormat != null;
            int audioTrackIndex = mediaMuxer.addTrack(audioTrackFormat);
            mediaMuxer.start();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            long videoTime;
            long audioTime;
            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                videoExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = videoExtractor.getSampleTime();
                videoExtractor.advance();

                videoTime = Math.abs(sampleTime - sampleTime1);
            }

            {
                audioExtractor.readSampleData(byteBuffer, 0);
                if (audioExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    audioExtractor.advance();
                }
                audioExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = audioExtractor.getSampleTime();
                audioExtractor.advance();
                audioExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = audioExtractor.getSampleTime();
                audioExtractor.advance();

                audioTime = Math.abs(sampleTime - sampleTime1);
            }

            videoExtractor.unselectTrack(videoIndex);
            videoExtractor.selectTrack(videoIndex);

            while (true) {
                int data = videoExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }
                videoBufferInfo.size = data;
                videoBufferInfo.presentationTimeUs += videoTime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();

                mediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            while (true) {
                int data = audioExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }
                audioBufferInfo.size = data;
                audioBufferInfo.presentationTimeUs += audioTime;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();

                mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
            videoExtractor.release();
            if (audioExtractor != null) {
                audioExtractor.release();
            }
        }
    }
}
