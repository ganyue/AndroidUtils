package com.gy.xunfei;

import android.app.Application;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by ganyu on 2016/8/11.
 *
 * <p>讯飞sdk， 使用前要把本类的构造方法中的appid修改成你的app的appid</p>
 * <p></p>
 * <p></p>
 */
public class XunfeiUtils {

    private static XunfeiUtils mInstance;
    private Application mApp;

    public static XunfeiUtils getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new XunfeiUtils(application);
        }
        return mInstance;
    }

    private XunfeiUtils(Application application) {
        mApp = application;
        SpeechUtility.createUtility(application,
                SpeechConstant.APPID +"=57aad223," + SpeechConstant.FORCE_LOGIN +"=true");
    }

    /**************************************** 语音听写 *******************************************/
    /**听写主要指将连续语音快速识别为文字的过程，
     * 科大讯飞语音听写能识别通用常见的语句、词汇，而且不限制说法
     */
    private SpeechRecognizer speechRecognizer;
    private void initSpeechRecogizer () {
        speechRecognizer = SpeechRecognizer.createRecognizer(mApp, null);
        speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
    }

    public void startRecognizeSpeech (RecognizerListener listener) {
        if (speechRecognizer == null) initSpeechRecogizer();
        if (isRecognizingSpeech()) return;
        speechRecognizer.startListening(listener);
    }

    public void stopRecognizeSpeech () {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public boolean isRecognizingSpeech () {
        if (speechRecognizer != null) return speechRecognizer.isListening();
        return false;
    }

//    /***************************************** 语音识别 ******************************************/
//    /**即语法识别，主要指基于命令词的识别，识别指定关键词组合的词汇，或者固定说法的短句
//     * 这里是使用云端识别
//     */
//    private SparseArray<String> commandIds;
//    public int initCommands (final String command) {
//        if (commandIds == null) commandIds = new SparseArray<>();
//        String mCloudGrammar = "#ABNF 1.0 UTF-8;" +
//                "languagezh-CN;" +
//                "mode voice;" +
//                "root $main;" +
//                "$main = $command;" +
//                "$command = " + command + ";";
//
//        int result = speechRecognizer.buildGrammar("abnf", mCloudGrammar, new GrammarListener() {
//            @Override
//            public void onBuildFinish(String s, SpeechError speechError) {
//                commandIds.put(command.hashCode(), s);
//            }
//        });
//        return result;
//    }
//
//    public boolean recognizeCommand (String commands, RecognizerListener listener) {
//        if (speechRecognizer == null) initSpeechRecogizer();
//        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
//
//        if (commandIds == null || commandIds.indexOfKey(commands.hashCode()) < 0) {
//            int result = initCommands(commands);
//            if (result != ErrorCode.SUCCESS) {
//                return false;
//            }
//        }
//        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
//        speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, commandIds.get(commands.hashCode()));
//        speechRecognizer.startListening(listener);
//        return true;
//    }
//
//    public void stopRecognizeCommand () {
//        if (speechRecognizer != null) {
//            speechRecognizer.stopListening();
//        }
//    }

    /***************************************** 语音合成 ******************************************/
    /**合成是将文字信息转化为可听的声音信息，让机器像人一样开口说话*/
    private SpeechSynthesizer speechSynthesizer;
    private void initeSpeechSynthesizer () {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(mApp, null);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    public void textToVoice (String storePath, String word, SynthesizerListener listener) {
        if (speechSynthesizer == null) initeSpeechSynthesizer();
        speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, storePath);
        speechSynthesizer.startSpeaking(word, listener);
    }

}
