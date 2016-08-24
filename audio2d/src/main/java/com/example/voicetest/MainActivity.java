/************************************************************************ 
android/iphone/windows/linux����ͨѶ��
����ͨѶ�������� 
׼ȷ��95%���ϣ���ʵһ���ǲ������ġ� 
�ӿڷǳ��򵥣���������ʾ����3���ӾͿ��������Ӧ����������ͨѶ���� 
��������ǿ�����������������ô���ţ��źŶ���׼ȷ�� 
�����ı���Ϊ16���ƣ���ͨ������ɴ����κ��ַ� 
���ܷǳ�ǿ��û�����в��˵�ƽ̨������ͨ���ڴ���Ż�����ʱ����벻�ٷ������ڴ棬��7*24Сʱ���� 
��֧���κ�ƽ̨��������ƽ̨android, iphone, windows, linux, arm, mipsel����ʾ�� 
����ɲ鿴��http://blog.csdn.net/softlgh 
����: ҹ���� QQ:3116009971 �ʼ���3116009971@qq.com 
************************************************************************/  

package com.example.voicetest;

import voice.SSIDWiFiInfo;
import voice.decoder.DataDecoder;
import voice.decoder.VoiceRecognizer;
import voice.decoder.VoiceRecognizerListener;
import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import voice.encoder.VoicePlayerListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsocial.audio2d.R;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("voiceRecog");
    }

    private final static int MSG_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_PLAY_START = 3;
    private final static int MSG_PLAY_END = 4;
    class MyHandler extends Handler
    {
        private TextView mRecognisedTextView;
        private TextView playButtonTips;
        public MyHandler(TextView recogTextView, TextView _playTips) {
            mRecognisedTextView = recogTextView;
            playButtonTips = _playTips;
        }
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_RECG_TEXT)
            {
                String s = (String)msg.obj;
                mRecognisedTextView.setText(s);
                if(s != null && s.length() > 0)
                {
	                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
	                Toast toast = Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT);
	                toast.show();
                }
            }
            else if(msg.what == MSG_RECG_START)
            {
            	mRecognisedTextView.setText("receive : " + msg.obj);
            }
            else if(msg.what == MSG_PLAY_START)
            {
            	playButtonTips.setText("start play ...");
            }
            else if(msg.what == MSG_PLAY_END)
            {
            	playButtonTips.setText("");
            }
            super.handleMessage(msg);
        }
    }

    private static String TAG = "mainActivity";
    Handler handler;
    VoiceRecognizer recognizer;//����ʶ����
    VoicePlayer player;//����������
    ToneGenerator toneGenerator =
            new ToneGenerator(
                    AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        player = new VoicePlayer();
        player.setListener(new VoicePlayerListener(){
			@Override
			public void onPlayEnd(VoicePlayer arg0) {
				handler.sendMessage(handler.obtainMessage(MSG_PLAY_END));
				//recognizer.pause(0);//�����˫��ͨѶ����ָ�ʶ��
			}

			@Override
			public void onPlayStart(VoicePlayer arg0) {
				handler.sendMessage(handler.obtainMessage(MSG_PLAY_START));
				//recognizer.pause(10000);//�����˫��ͨѶ��Ϊ�˱����Լ�ʶ���Լ����ŵ��źţ���������ͣʶ����
			}        	
        });
        
        //��������ʶ����
        recognizer = new VoiceRecognizer();
        recognizer.setListener(new VoiceRecognizerListener() {
            @Override
            public void onRecognizeStart(float _soundTime) {
            	handler.sendMessage(handler.obtainMessage(MSG_RECG_START));
            }

            @Override
            public void onRecognizeEnd(float _soundTime, int _result, String _hexData) {
            	String data = "";
                if(_result == 0)
                {
                	byte[] hexData = _hexData.getBytes();
                	int infoType = DataDecoder.decodeInfoType(hexData);
                	if(infoType == DataDecoder.IT_STRING)
                	{
                		data = DataDecoder.decodeString(_result, hexData);
                	}
                	else if(infoType == DataDecoder.IT_SSID_WIFI)
                	{
                		SSIDWiFiInfo wifi = DataDecoder.decodeSSIDWiFi(_result,  hexData);
                		data = "ssid:" + wifi.ssid + ",pwd:" + wifi.pwd; 
                	}
                	else
                	{
                		data = "nothing";
                	}
                }
                handler.sendMessage(handler.obtainMessage(MSG_RECG_TEXT, data));               
            }
        });
        final TextView palyTextTips = ((TextView) findViewById(R.id.playTextTips));
        ((EditText) findViewById(R.id.playText)).addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				palyTextTips.setText("after text changed\n(" + s.toString().length() + ")");
			}
        	
        });
        handler = new MyHandler((TextView) findViewById(R.id.recognizeText), (TextView) findViewById(R.id.playButtonTips));

        ((Button) findViewById(R.id.voicePlay)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String data = ((EditText)findViewById(R.id.playText)).getText().toString();
            	String encodeData = DataEncoder.encodeString(data);
            	Log.i(TAG, data + " encode to :" + encodeData);
                player.play(encodeData);
            	//player.play(DataEncoder.encodeMacWiFi(new byte[]{(byte)0xff, 0x0e, 0x01, 0x02, 0x03, 0x04}, "0123456789012345678901234567890123456789"));
            }
        });
        
        autoSetAudioVolumn();

        //��ʼʶ��
        recognizer.start();
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    //��������Ϊ60%
    public void autoSetAudioVolumn()
    {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(max*0.6), 0);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        recognizer.stop();//ֹͣʶ��
    }

    @Override
    protected void onResume() {
        super.onResume();
        recognizer.start();//��������ʶ��
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
