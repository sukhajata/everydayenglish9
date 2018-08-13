package com.sukhajata.everydayenglish;


import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import com.sukhajata.everydayenglish.interfaces.AudioSetupCallback;

/**
 * Created by Administrator on 18/8/2560.
 */

public class MyApplication extends Application {

    TextToSpeech textToSpeech;
    boolean initialized;
    MediaPlayer mediaPlayer;
    boolean missingData;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setupAudio(final AudioSetupCallback callback) {

        textToSpeech = new TextToSpeech(
                this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = textToSpeech.setLanguage(Locale.US);
                            if (result == TextToSpeech.LANG_MISSING_DATA ) {
                                callback.onAudioSetupComplete(AudioSetupCallback.AUDIO_SETUP_MISSING_LANGUAGE);
                            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                callback.onAudioSetupComplete(AudioSetupCallback.AUDIO_SETUP_FAILURE);
                            } else {
                                textToSpeech.setSpeechRate(0.8f);
                                callback.onAudioSetupComplete(AudioSetupCallback.AUDIO_SETUP_SUCCESS);
                                initialized = true;
                            }
                        } else {
                            callback.onAudioSetupComplete(AudioSetupCallback.AUDIO_SETUP_FAILURE);
                        }
                    }
                },
                "com.google.android.tts");
    }


    public void playAudio(String text, String audioFileName) {
        if (audioFileName != null && audioFileName.length() > 1) {
            audioFileName = audioFileName.substring(0, audioFileName.indexOf('.'));
            String path = "raw/" + audioFileName;
            try {
                int resId = getResources().getIdentifier(audioFileName, "raw", getPackageName());
                Log.d("AUDIO", audioFileName + ", resId: " + String.valueOf(resId));

                //Class res = R.raw.class;
                //Field field = res.getField(audioFileName);
                //int resId = field.getInt(null);

                //cancel anything currently playing
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(this, resId);
                mediaPlayer.start();
            } catch(Exception e) {
                Log.e("Audio", e.getMessage());
            }
        } else if (initialized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.v("TTS", "Speak new API");
                Bundle bundle = new Bundle();
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null);
            } else {
                Log.v("TTS", "Speak old API");
                HashMap<String, String> param = new HashMap<>();
                param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, param);
            }
        }

    }

}
