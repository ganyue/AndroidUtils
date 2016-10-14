package com.gy.utils.audio.mpd;

import android.text.TextUtils;

import com.gy.utils.log.LogUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ganyu on 2016/10/12.
 *
 */
public class MpdTcpResponseMessageProcessor extends Thread{

    private ArrayBlockingQueue<MpdResponse> messages;
    private OnProcessListener onProcessListener;
    private boolean isRun;

    public MpdTcpResponseMessageProcessor() {
        messages = new ArrayBlockingQueue<>(64);
    }

    public void onReceive (String cmd, List<String> msg) {
        messages.offer(new MpdResponse(cmd, msg));
    }

    public void setOnProcessListener (OnProcessListener listener) {
        onProcessListener = listener;
    }

    @Override
    public void run() {
        isRun = true;
        while (isRun) {
            try {
                MpdResponse response = messages.take();

                if (response == null ||  onProcessListener == null) {
                    continue;
                }

                if (!TextUtils.isEmpty(response.cmd) && response.msg != null && response.msg.size() > 0) {
                    onProcessListener.onProcess(response.cmd, response.msg);
                }

            } catch (Exception e) {
                //nothing to do
                e.printStackTrace();
                LogUtils.e("yue.gan", "########## error to process recieve msg!");
            }
        }
    }

    public boolean isRun () {
        return isRun;
    }

    public void release() {
        isRun = false;
        messages.clear();
        onProcessListener = null;
        interrupt();
    }

    public interface OnProcessListener {
        void onProcess(String cmd, List<String> msg);
    }
}
