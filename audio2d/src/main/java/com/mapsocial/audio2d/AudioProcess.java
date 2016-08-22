package com.mapsocial.audio2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.util.Log;
import android.view.SurfaceView;

import com.gy.utils.log.LogUtils;

import java.util.ArrayList;

public class AudioProcess {
    public static final float pi= (float) 3.1415926;
    //应该把处理前后处理后的普线都显示出来
    private ArrayList<short[]> inBuf = new ArrayList<short[]>();//原始录入数据
    private ArrayList<int[]> outBuf = new ArrayList<int[]>();//处理后的数据
    private boolean isRecording = false;

    Context mContext;
    private int shift = 30;
    public int frequence = 0;

    private int length = 256;
    //y轴缩小的比例
    public int rateY = 21;
    //y轴基线
    public int baseLine = 0;
    //初始化画图的一些参数
    public void initDraw(int rateY, int baseLine,Context mContext, int frequence){
        this.mContext = mContext;
        this.rateY = rateY;
        this.baseLine = baseLine;
        this.frequence = frequence;
    }
    //启动程序
    public void start(AudioRecord audioRecord, int minBufferSize, SurfaceView sfvSurfaceView) {
        isRecording = true;
        new RecordThread(audioRecord, minBufferSize).start();
        new DrawThread(sfvSurfaceView).start();
    }
    //停止程序
    public void stop(SurfaceView sfvSurfaceView){
        isRecording = false;
        inBuf.clear();
    }

    //录音线程
    class RecordThread extends Thread{
        private AudioRecord audioRecord;
        private int minBufferSize;

        public RecordThread(AudioRecord audioRecord,int minBufferSize){
            this.audioRecord = audioRecord;
            this.minBufferSize = minBufferSize;
        }

        public void run(){
            try{
                short[] buffer = new short[minBufferSize];
                audioRecord.startRecording();
                while(isRecording){
                    int res = audioRecord.read(buffer, 0, minBufferSize);
                    synchronized (inBuf){
                        inBuf.add(buffer);
                    }
                    //保证长度为2的幂次数
                    length=up2int(res);
                    short[]tmpBuf = new short[length];
                    System.arraycopy(buffer, 0, tmpBuf, 0, length);

                    Complex[]complexs = new Complex[length];
                    int[]outInt = new int[length];
                    for(int i=0;i < length; i++){
                        Short short1 = tmpBuf[i];
                        complexs[i] = new Complex(short1.doubleValue());
                    }
                    Complex.fft(complexs,length);
                    for (int i = 0; i < length; i++) {
                        outInt[i] = complexs[i].getIntValue();
                    }
                    synchronized (outBuf) {
                        outBuf.add(outInt);
                    }
                }
                audioRecord.stop();
            }catch (Exception e) {
                // TODO: handle exception
                Log.i("Rec E",e.toString());
            }

        }
    }

    //绘图线程
    class DrawThread extends Thread{
        //画板
        private SurfaceView sfvSurfaceView;
        //当前画图所在屏幕x轴的坐标
        //画笔
        private Paint mPaint;
        private Paint tPaint;
        private Paint dashPaint;
        public DrawThread(SurfaceView sfvSurfaceView) {
            this.sfvSurfaceView = sfvSurfaceView;
            //设置画笔属性
            mPaint = new Paint();
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(2);
            mPaint.setAntiAlias(true);

            tPaint = new Paint();
            tPaint.setColor(Color.YELLOW);
            tPaint.setStrokeWidth(1);
            tPaint.setAntiAlias(true);
            tPaint.setTextSize(20);

            //画虚线
            dashPaint = new Paint();
            dashPaint.setStyle(Paint.Style.STROKE);
            dashPaint.setColor(Color.GRAY);
            Path path = new Path();
            path.moveTo(0, 10);
            path.lineTo(480,10);
            PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
            dashPaint.setPathEffect(effects);
        }

        @SuppressWarnings("unchecked")
        public void run() {
            while (isRecording) {
                ArrayList<int[]>buf = new ArrayList<int[]>();
                synchronized (outBuf) {
                    if (outBuf.size() == 0) {
                        continue;
                    }
                    buf = (ArrayList<int[]>)outBuf.clone();
                    outBuf.clear();
                }
                //根据ArrayList中的short数组开始绘图
                for(int i = 0; i < buf.size(); i++){
                    int[]tmpBuf = buf.get(i);
                    SimpleDraw(tmpBuf, rateY, baseLine);
                }

            }
        }

        /**
         * 绘制指定区域
         *
         *            X 轴开始的位置(全屏)
         * @param buffer
         *             缓冲区
         * @param rate
         *            Y 轴数据缩小的比例
         * @param baseLine
         *            Y 轴基线
         */

        private void SimpleDraw(final int[] buffer, int rate, int baseLine){
            Canvas canvas = sfvSurfaceView.getHolder().lockCanvas(
                    new Rect(0, 0, buffer.length,sfvSurfaceView.getHeight()));
            canvas.drawColor(Color.BLACK);
            canvas.drawText("幅度值", 0, 3, 2, 15, tPaint);
            canvas.drawText("原点(0,0)", 0, 7, 5, baseLine + 15, tPaint);
            canvas.drawText("频率(HZ)", 0, 6, sfvSurfaceView.getWidth() - 50, baseLine + 30, tPaint);
            canvas.drawLine(shift, 20, shift, baseLine, tPaint);
            canvas.drawLine(shift, baseLine, sfvSurfaceView.getWidth(), baseLine, tPaint);
            canvas.save();
            canvas.rotate(30, shift, 20);
            canvas.drawLine(shift, 20, shift, 30, tPaint);
            canvas.rotate(-60, shift, 20);
            canvas.drawLine(shift, 20, shift, 30, tPaint);
            canvas.rotate(30, shift, 20);
            canvas.rotate(30, sfvSurfaceView.getWidth()-1, baseLine);
            canvas.drawLine(sfvSurfaceView.getWidth() - 1, baseLine, sfvSurfaceView.getWidth() - 11, baseLine, tPaint);
            canvas.rotate(-60, sfvSurfaceView.getWidth()-1, baseLine);
            canvas.drawLine(sfvSurfaceView.getWidth() - 1, baseLine, sfvSurfaceView.getWidth() - 11, baseLine, tPaint);
            canvas.restore();
            //tPaint.setStyle(Style.STROKE);
            for(int index = 64; index <= 512; index = index + 64){
                canvas.drawLine(shift + index, baseLine, shift + index, 40, dashPaint);
                String str = String.valueOf(frequence / 512 * index);
                canvas.drawText( str, 0, str.length(), shift + index - 15, baseLine + 15, tPaint);
            }
            int y;
//            for(int i = 0; i < buffer.length; i = i + 1){
//                y = baseLine - buffer[i] / rateY ;
//                canvas.drawLine(i + shift, baseLine, i +shift, y, mPaint);
//            }

//            int lastloc = 200;
//            int minVal = 0;
//            for (double freq : Audio2DConsts.Coodbook.F_Start) {
//                int index = (int) Math.ceil(freq * 512 / 44100);
//                y = baseLine - buffer[index]/rate;
//                canvas.drawLine(lastloc, baseLine, lastloc, y, mPaint);
//                lastloc += 20;
//                if (minVal > buffer[index]) {
//                    minVal = buffer[index];
//                }
//                if (freq == Audio2DConsts.Coodbook.F_Start[4]) LogUtils.d("yue.gan", "" + buffer[index]);
////                canvas.drawText("" + buffer[index], index, baseLine - 20, tPaint);
//            }

//            int index = (int) Math.ceil(16000 * 512 / 44100);
//            y = baseLine - buffer[index]/rate;
//            canvas.drawLine(index + shift, baseLine, index +shift, y, mPaint);

            sfvSurfaceView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 向上取最接近iint的2的幂次数.比如iint=320时,返回256
     * @param iint
     * @return
     */
    private int up2int(int iint) {
        int ret = 1;
        while (ret<=iint) {
            ret = ret << 1;
        }

        ret >>= 1;
        if (ret >= 512) {
            ret = 512;
        }
        return ret;
    }
}