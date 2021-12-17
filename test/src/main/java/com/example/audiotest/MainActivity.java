/***********************************************/
/**** 使用AndioRecord录音和使用AudioTrack回放  ********/
/***********************************************/
/******** 针对资料【Android实时录音回放】第二篇 **********/
package com.example.audiotest;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btnRecord,btnCall;
	SeekBar skbVolume;//调节音量
	boolean isRecording = false;//是否录放的标记
	//private boolean say;
	//采样率：音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用8000等低采样率。
	static final int frequency = 44100;//44100/8000; //采样率越大录放延时越小，越低延时越大
	//声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	//编码制式和采样大小：采集来的数据当然使用PCM编码(脉冲代码调制编码，即PCM编码。PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。)
	//android支持的采样大小16bit 或者8bit。当然采样大小越大，那么信息量越多，音质也越高，现在主流的采样大小都是16bit，在低质量的语音传输的时候8bit 足够了。
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBufSize,playBufSize;
	AudioRecord audioRecord;
	AudioTrack audioTrack;
	private boolean call_on=false;   //通话开关判断值
	private AudioManager audioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("语音实时录放");

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);        //通过该函数获取的最小buffer size，保证成功地创建一个AudioRecord对象

		playBufSize=AudioTrack.getMinBufferSize(frequency,   //通过该函数获取的最小buffer size，保证成功地创建一个AudioTrack对象
				channelConfiguration, audioEncoding);
		// -----------------------------------------
		//MediaRecorder.AudioSource.MIC：设置音频源：指的是从哪里采集音频。这里我们是从麦克风采集音频，所以此参数的值为MIC。可以参考MediaRecorder.AudioSource类，查看其他音频源
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);  //recBufSize*10

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration,audioEncoding,playBufSize,AudioTrack.MODE_STREAM);
		//------------------------------------------
		btnRecord = (Button) this.findViewById(R.id.btnRecord);
		btnCall = (Button) this.findViewById(R.id.btnCall);
		btnCall.setText("通话已关");
		btnCall.setEnabled(false); //初始令开关按钮无法使用

		skbVolume=(SeekBar)this.findViewById(R.id.skbVolume);
		skbVolume.setMax(100);//音量调节的极限
		skbVolume.setProgress(50);//设置seekbar的位置值
		audioTrack.setStereoVolume(0.5f, 0.5f);//设置当前音量大小

		/*******************************************************/
		btnRecord.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View vrc, MotionEvent eventrc){

				if(eventrc.getAction()==MotionEvent.ACTION_DOWN)//检测到手指触摸
				{
					/***** 下面这段是蓝牙耳机录音程序移植过来的，能用，但跟不用它的效果差不多。。直接开关SCO就可以了【也就是说这段程序是废话。。】
					 audioManager.startBluetoothSco(); //开蓝牙SCO
					 registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
					int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE,-1);

					if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state)
					{
					audioManager.setBluetoothScoOn(true); // 打开SCO
					audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
					if (audioManager.isBluetoothScoOn())
					{
					isRecording = true;  // 开始录音
					new RecordPlayThread().start();  // 开一条线程边录边放
					unregisterReceiver(this);  //加上这个录音效果好很多，不要去掉
					}
					}
					else       // 等待一秒后再尝试启动SCO
					{
					try {Thread.sleep(1000);}
					catch (InterruptedException e) {e.printStackTrace();}
					audioManager.startBluetoothSco();
					}
					} }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
					 */

					/******** 下面这一小段跟上面的废话效果差不多（测试过）***************************
					 audioManager.startBluetoothSco();      //启动蓝牙 SCO 音频连接。
					 try {Thread.currentThread(); Thread.sleep(150);}     //延时   ；单位:ms
					 catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					 audioManager.setBluetoothScoOn(true);  //要求使用蓝牙 SCO 耳机进行通讯。
					 /*****************************************************************/

					//防闪退测试
					try {Thread.currentThread(); Thread.sleep(200);}     //延时   ；单位:ms
					catch (InterruptedException e) {e.printStackTrace();}  //稍作延时

					//下面这个就是去杂音的关键！！release！记得release之后要重新赋值
					isRecording = false;
					//必须延时
					try {Thread.currentThread(); Thread.sleep(200);}     //延时   ；单位:ms
					catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					audioRecord.stop();
					audioRecord.release();
					audioRecord=null; //可有可无
					audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
							channelConfiguration, audioEncoding, recBufSize);  //recBufSize*10

					//【实测无需要将audioTrack.release()】
					//audioTrack.stop();
					//audioTrack.release();
					//audioTrack=null; //可有可无
					//audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					//         channelConfiguration,audioEncoding,playBufSize,AudioTrack.MODE_STREAM);

					//关SCO,手机录音到蓝牙耳机
					audioManager.setBluetoothScoOn(false);
					audioManager.stopBluetoothSco();

					//say=true;

					// new RecordPlayThread2().interrupt();
					//Thread RecordPlayThread2=new RecordPlayThread2();  //新建线程
					// RecordPlayThread2.interrupt();            //挂起线程
					// RecordPlayThread2=null;

					isRecording = true;
					new RecordPlayThread().start();             // 开一条线程边录边放
					//try {Thread.currentThread(); Thread.sleep(100);}     //延时   ；单位:ms
					// catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					btnRecord.setText("长按讲话");
					btnCall.setEnabled(true); //打开后令开关按钮可以使用
					call_on=true;
					btnCall.setText("通话已开");
				}
				if(eventrc.getAction()==MotionEvent.ACTION_UP)  //检测到手指离开屏幕
				{

					//防闪退测试
					try {Thread.currentThread(); Thread.sleep(200);}     //延时   ；单位:ms
					catch (InterruptedException e) {e.printStackTrace();}  //稍作延时

					//下面这个就是去杂音的关键！！release！记得release之后要重新赋值
					isRecording = false;
					//必须延时
					try {Thread.currentThread(); Thread.sleep(200);}     //延时   ；单位:ms
					catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					audioRecord.stop();
					audioRecord.release();
					audioRecord=null; //可有可无
					audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
							channelConfiguration, audioEncoding, recBufSize);  //recBufSize*10

					//【实测无需要将audioTrack.release()】
					//audioTrack.stop();
					//audioTrack.release();
					//audioTrack=null; //可有可无
					//audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					//         channelConfiguration,audioEncoding,playBufSize,AudioTrack.MODE_STREAM);

					//开SCO,蓝牙耳机录音到手机
					audioManager.startBluetoothSco();      //启动蓝牙 SCO 音频连接。
					audioManager.setBluetoothScoOn(true);  //要求使用蓝牙 SCO 耳机进行通讯。

					isRecording = true;
					new RecordPlayThread().start();             // 开一条线程边录边放
					//say=false;

					//new RecordPlayThread().interrupt();
					//Thread RecordPlayThread=new RecordPlayThread();  //新建线程
					//RecordPlayThread.interrupt();            //挂起线程
					//RecordPlayThread=null;

					//new RecordPlayThread2().start();
					//isRecording = true;
					btnRecord.setText("收听模式");
				}

				return true;
			}
		});


		btnCall.setOnClickListener(new OnClickListener() {
			public void onClick(View vcall) {

				//AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

				if(call_on)
				{
					call_on=false;    //关通话
					isRecording = false;
					btnRecord.setEnabled(false); //令录音按钮无法使用

					//下面这个就是去杂音的关键！！release！记得release之后要重新赋值
					//isRecording = false;
					//	 try {Thread.currentThread(); Thread.sleep(500);}     //延时   ；单位:ms
					//     catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					//		audioRecord.stop();
					//		audioRecord.release();
					//		audioRecord=null; //可有可无
					//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
					//                channelConfiguration, audioEncoding, recBufSize);  //recBufSize*10

					//audioTrack.stop();
					//audioTrack.release();
					//audioTrack = null;

					//audioManager.setSpeakerphoneOn(true);//开扬声器
					//audioManager.setMode(AudioManager.MODE_NORMAL); //扬声器模式

					btnCall.setText("通话已关");
				}
				else if(!call_on)
				{
					call_on=true;      //开通话
					isRecording = true;
					new RecordPlayThread().start();   // 开一条线程边录边放
					btnRecord.setEnabled(true);     //令录音按钮可以使用

					//	isRecording = false;
					//	 try {Thread.currentThread(); Thread.sleep(500);}     //延时   ；单位:ms
					//    catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					//		audioRecord.stop();
					//		audioRecord.release();
					//		audioRecord=null;  //可有可无
					//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
					//                channelConfiguration, audioEncoding, recBufSize);  //recBufSize*10

					//audioTrack.stop();
					//audioTrack.release();
					//audioTrack = null;

					//audioManager.setSpeakerphoneOn(false);//关闭扬声器
					//audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
					//setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
					//把声音设定成Earpiece（听筒）出来，设定为正在通话中

					//audioManager.setMode(AudioManager.MODE_IN_CALL); //听筒模式
					//audioManager.setMode(AudioManager.MODE_RINGTONE);  //铃声模式

					//Intent phoneIntent = new Intent("android.intent.action.CALL",Uri.parse("tel:" + "18933039950"));
					//startActivity(phoneIntent);

					//audioManager.startBluetoothSco();      //启动蓝牙 SCO 音频连接。
					//try {Thread.currentThread(); Thread.sleep(100);}     //延时   ；单位:ms
					//catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					//audioManager.setBluetoothScoOn(true);  //要求使用蓝牙 SCO 耳机进行通讯。

					btnCall.setText("通话已开");
				}
			}});

		/*******************************************************/

		skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float vol=(float)(seekBar.getProgress())/(float)(seekBar.getMax());
				audioTrack.setStereoVolume(vol, vol);     //设置音量
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}


	//收听音切换后很多杂音，试过用两个   audioRecord 和 audioTrack分别录音和播放，结果一样
	class RecordPlayThread extends Thread {
		public void run() {
			try {
				byte[] buffer1 = new byte[recBufSize];
				//byte[] buffer2 = new byte[recBufSize];

				audioRecord.startRecording();//开始录制
				audioTrack.play();//开始播放

				while (isRecording)  //isRecording
				{
					//if(say)   //这样分开放到不同缓存区也是一样的很多杂音，把audioRecord和audioTrack分别分成两个测试过也一样。。
					//{
					//从MIC(唛)保存数据到缓冲区
					int bufferReadResult1 = audioRecord.read(buffer1,0,recBufSize);
					byte[] tmpBuf1 = new byte[bufferReadResult1];
					System.arraycopy(buffer1, 0, tmpBuf1, 0, bufferReadResult1);
					//写入数据即播放
					audioTrack.write(tmpBuf1, 0, tmpBuf1.length);
					//}
					//else
					//{
					//从MIC(唛)保存数据到缓冲区
					//    int bufferReadResult2 = audioRecord.read(buffer2,0,recBufSize);
					//    byte[] tmpBuf2 = new byte[bufferReadResult2];
					//    System.arraycopy(buffer2, 0, tmpBuf2, 0, bufferReadResult2);
					//写入数据即播放
					//     audioTrack.write(tmpBuf2, 0, tmpBuf2.length);
					//}

					/******** 下面的时分复用法失败，完全没用 (所以不能同时对话，采用按下时手机说话，松开时蓝牙耳机回传) *******
					 //开SCO,蓝牙耳机录音到手机
					 audioManager.startBluetoothSco();      //启动蓝牙 SCO 音频连接。
					 try {Thread.currentThread(); Thread.sleep(50);}     //延时   ；单位:ms
					 catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					 audioManager.setBluetoothScoOn(true);  //要求使用蓝牙 SCO 耳机进行通讯。

					 //从MIC(唛)保存数据到缓冲区
					 int bufferReadResult1 = audioRecord.read(buffer,0,recBufSize);
					 byte[] tmpBuf1 = new byte[bufferReadResult1];
					 System.arraycopy(buffer, 0, tmpBuf1, 0, bufferReadResult1);
					 //写入数据即播放
					 audioTrack.write(tmpBuf1, 0, tmpBuf1.length);

					 try {Thread.currentThread(); Thread.sleep(1000);}     //延时   ；单位:ms
					 catch (InterruptedException e) {e.printStackTrace();}  //稍作延时

					 //关SCO,手机录音到蓝牙耳机
					 audioManager.setBluetoothScoOn(false);
					 audioManager.stopBluetoothSco();

					 //从MIC(唛)保存数据到缓冲区
					 int bufferReadResult2 = audioRecord.read(buffer,0,recBufSize);
					 byte[] tmpBuf2 = new byte[bufferReadResult2];
					 System.arraycopy(buffer, 0, tmpBuf2, 0, bufferReadResult2);
					 //写入数据即播放
					 audioTrack.write(tmpBuf2, 0, tmpBuf2.length);

					 try {Thread.currentThread(); Thread.sleep(1000);}     //延时   ；单位:ms
					 catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
					 /*************************************************************************/

				}
				audioTrack.stop();
				audioRecord.stop();

				//audioTrack.release();//释放资源,试过,会死【试试能不能去杂音】
				//audioRecord.release();
			} catch (Throwable t) {
				Toast.makeText(MainActivity.this, t.getMessage(), 1000);
			}
		}};


        /*
        //收听音切换后很多杂音，试过用两个   audioRecord 和 audioTrack分别录音和播放，结果一样
        class RecordPlayThread2 extends Thread {
            public void run2() {
                try {
    	                byte[] buffer2 = new byte[recBufSize];
    	                //byte[] buffer2 = new byte[recBufSize];

    	                audioRecord.startRecording();//开始录制
    	                audioTrack.play();//开始播放

    	                while (!say)  //isRecording
    	                {
    	                	//if(say)   //这样分开放到不同缓存区也是一样的很多杂音，把audioRecord和audioTrack分别分成两个测试过也一样。。
    	                	//{
    		                    //从MIC(唛)保存数据到缓冲区
    		                    int bufferReadResult2 = audioRecord.read(buffer2,0,recBufSize);
    		                    byte[] tmpBuf2 = new byte[bufferReadResult2];
    		                    System.arraycopy(buffer2, 0, tmpBuf2, 0, bufferReadResult2);
    		                    //写入数据即播放
    		                    audioTrack.write(tmpBuf2, 0, tmpBuf2.length);
    	                	//}
    	                	//else
    	                	//{
    		                    //从MIC(唛)保存数据到缓冲区
    		                //    int bufferReadResult2 = audioRecord.read(buffer2,0,recBufSize);
    		                //    byte[] tmpBuf2 = new byte[bufferReadResult2];
    		                //    System.arraycopy(buffer2, 0, tmpBuf2, 0, bufferReadResult2);
    		                    //写入数据即播放
    		               //     audioTrack.write(tmpBuf2, 0, tmpBuf2.length);
    	                	//}

    	             /******** 下面的时分复用法失败，完全没用 (所以不能同时对话，采用按下时手机说话，松开时蓝牙耳机回传) *******
    	                	//开SCO,蓝牙耳机录音到手机
    	                    audioManager.startBluetoothSco();      //启动蓝牙 SCO 音频连接。
    				    	try {Thread.currentThread(); Thread.sleep(50);}     //延时   ；单位:ms
    	                    catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
    				    	audioManager.setBluetoothScoOn(true);  //要求使用蓝牙 SCO 耳机进行通讯。

    	                    //从MIC(唛)保存数据到缓冲区
    	                    int bufferReadResult1 = audioRecord.read(buffer,0,recBufSize);
    	                    byte[] tmpBuf1 = new byte[bufferReadResult1];
    	                    System.arraycopy(buffer, 0, tmpBuf1, 0, bufferReadResult1);
    	                    //写入数据即播放
    	                    audioTrack.write(tmpBuf1, 0, tmpBuf1.length);

    				    	try {Thread.currentThread(); Thread.sleep(1000);}     //延时   ；单位:ms
    	                    catch (InterruptedException e) {e.printStackTrace();}  //稍作延时

    				    	//关SCO,手机录音到蓝牙耳机
    	        			audioManager.setBluetoothScoOn(false);
    	        			audioManager.stopBluetoothSco();

    	                    //从MIC(唛)保存数据到缓冲区
    	                    int bufferReadResult2 = audioRecord.read(buffer,0,recBufSize);
    	                    byte[] tmpBuf2 = new byte[bufferReadResult2];
    	                    System.arraycopy(buffer, 0, tmpBuf2, 0, bufferReadResult2);
    	                    //写入数据即播放
    	                    audioTrack.write(tmpBuf2, 0, tmpBuf2.length);

    				    	try {Thread.currentThread(); Thread.sleep(1000);}     //延时   ；单位:ms
    	                    catch (InterruptedException e) {e.printStackTrace();}  //稍作延时
    				/*************************************************************************/

/*   	                }
    	                audioTrack.stop();
    	                audioRecord.stop();

    	                //audioTrack.release();//释放资源,试过,会死
    	                //audioRecord.release();
                } catch (Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), 1000);
                }
            }};
 */

}