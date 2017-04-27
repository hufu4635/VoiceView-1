package com.winsion.voiceview;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wyl on 2017/4/26.
 */

public class VoicePlayer {
    private static VoicePlayer mInstance;
    private boolean isPlaying;
    // 记录当前正在播放的音频路径
    private String currentPath;
    private MediaPlayer mediaPlayer;

    private OnEndListener mListener;

    private VoicePlayer() {
    }

    public static VoicePlayer getInstance() {
        if (mInstance == null) {
            synchronized (VoicePlayer.class) {
                if (mInstance == null) {
                    mInstance = new VoicePlayer();
                }
            }
        }
        return mInstance;
    }

    public int getDuration(String mUri) {
        int duration = 0;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = ((int) Math.ceil(Integer.valueOf(durationStr) / 1000f));
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    public void playRecord(String path, final OnEndListener listener) {
        if (mListener != null) {
            if (mListener.isPlaying()) {
                mListener.onEnd();
            }
        }
        this.mListener = listener;
        if (TextUtils.equals(path, currentPath)) {
            if (!isPlaying) {
                startPlay(path, listener);
            } else {
                stopPlay();
            }
        } else {
            currentPath = path;
            startPlay(path, listener);
        }
    }

    private void startPlay(String path, final OnEndListener listener) {
        isPlaying = true;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    mediaPlayer.release();
                    mediaPlayer = null;
                    if (listener != null) {
                        listener.onEnd();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null && isPlaying) {
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public interface OnEndListener {
        void onEnd();

        boolean isPlaying();
    }
}
