package com.mapsocial.audio2d;

import android.media.AudioFormat;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class Audio2DConsts {
    
    public static class AudioInfo {
        public static final int Audio_Rate = 44100;
        public static final int Audio_Config = AudioFormat.CHANNEL_IN_MONO;
        public static final int Audio_Format = AudioFormat.ENCODING_PCM_16BIT;

        public static final int Max_Vol = 32767;                //音量最大值
        public static final int Min_Vol = -32768;                //音量最小值
        public static final int Max_Multy_Vol = 32768 / 9;      //混音音量最大值
        public static final int Sample_Count = 4410;            //取样点数
        public static final int Sample_Analytic_Count = 512;    //每次傅里叶分析点数
    }

    public static class Coodbook {
//        public static final int B0 = 11500; //134
//        public static final int B1 = 12000; //140
//        public static final int B2 = 12500; //146
//        public static final int B3 = 13000; //151
//        public static final int B4 = 13500; //157
//        public static final int B5 = 14000; //163
//        public static final int B6 = 14500; //169
//        public static final int B7 = 15000; //175
//        public static final int B8 = 15500; //180
//        public static final int B9 = 16000; //186
//        public static final int Ba = 16500; //192
//        public static final int Bb = 17000; //198
//        public static final int Bc = 17500; //204
//        public static final int Bd = 18000; //209
//        public static final int Be = 18500; //215
//        public static final int Bf = 19000; //221
//        public static final int BStart = 19500; //227
//        public static final int BDivide = 20000;//233
//        public static final int BEnd = 20500;//239


        public static final int B0 = 1500; //134
        public static final int B1 = 2000; //140
        public static final int B2 = 2500; //146
        public static final int B3 = 3000; //151
        public static final int B4 = 3500; //157
        public static final int B5 = 4000; //163
        public static final int B6 = 4500; //169
        public static final int B7 = 5000; //175
        public static final int B8 = 5500; //180
        public static final int B9 = 6000; //186
        public static final int Ba = 6500; //192
        public static final int Bb = 7000; //198
        public static final int Bc = 7500; //204
        public static final int Bd = 8000; //209
        public static final int Be = 8500; //215
        public static final int Bf = 9000; //221
        public static final int BStart = 9500; //227
        public static final int BDivide = 10000;//233
        public static final int BEnd = 10500;//239

        //每种频率对应的每次取样的角度差
        public static final double DB0 = 2 * Math.PI * B0 / AudioInfo.Audio_Rate;
        public static final double DB1 = 2 * Math.PI * B1 / AudioInfo.Audio_Rate;
        public static final double DB2 = 2 * Math.PI * B2 / AudioInfo.Audio_Rate;
        public static final double DB3 = 2 * Math.PI * B3 / AudioInfo.Audio_Rate;
        public static final double DB4 = 2 * Math.PI * B4 / AudioInfo.Audio_Rate;
        public static final double DB5 = 2 * Math.PI * B5 / AudioInfo.Audio_Rate;
        public static final double DB6 = 2 * Math.PI * B6 / AudioInfo.Audio_Rate;
        public static final double DB7 = 2 * Math.PI * B7 / AudioInfo.Audio_Rate;
        public static final double DB8 = 2 * Math.PI * B8 / AudioInfo.Audio_Rate;
        public static final double DB9 = 2 * Math.PI * B9 / AudioInfo.Audio_Rate;
        public static final double DBa = 2 * Math.PI * Ba / AudioInfo.Audio_Rate;
        public static final double DBb = 2 * Math.PI * Bb / AudioInfo.Audio_Rate;
        public static final double DBc = 2 * Math.PI * Bc / AudioInfo.Audio_Rate;
        public static final double DBd = 2 * Math.PI * Bd / AudioInfo.Audio_Rate;
        public static final double DBe = 2 * Math.PI * Be / AudioInfo.Audio_Rate;
        public static final double DBf = 2 * Math.PI * Bf / AudioInfo.Audio_Rate;
        public static final double DBStart = 2 * Math.PI * BStart / AudioInfo.Audio_Rate;
        public static final double DBDivide = 2 * Math.PI * BDivide / AudioInfo.Audio_Rate;
        public static final double DBEnd = 2 * Math.PI * BEnd / AudioInfo.Audio_Rate;

        public static final int IB0 = (int) Math.ceil(1.0f * B0 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB1 = (int) Math.ceil(1.0f * B1 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB2 = (int) Math.ceil(1.0f * B2 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB3 = (int) Math.ceil(1.0f * B3 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB4 = (int) Math.ceil(1.0f * B4 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB5 = (int) Math.ceil(1.0f * B5 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB6 = (int) Math.ceil(1.0f * B6 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB7 = (int) Math.ceil(1.0f * B7 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB8 = (int) Math.ceil(1.0f * B8 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IB9 = (int) Math.ceil(1.0f * B9 * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBa = (int) Math.ceil(1.0f * Ba * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBb = (int) Math.ceil(1.0f * Bb * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBc = (int) Math.ceil(1.0f * Bc * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBd = (int) Math.ceil(1.0f * Bd * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBe = (int) Math.ceil(1.0f * Be * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBf = (int) Math.ceil(1.0f * Bf * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBStart = (int) Math.ceil(1.0f * BStart * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBDivide = (int) Math.ceil(1.0f * BDivide * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
        public static final int IBEnd = (int) Math.ceil(1.0f * BEnd * AudioInfo.Sample_Analytic_Count / AudioInfo.Audio_Rate);
    }

    private int[] test = {Coodbook.IB0,
            Coodbook.IB1,
            Coodbook.IB2,
            Coodbook.IB3,
            Coodbook.IB4,
            Coodbook.IB5,
            Coodbook.IB6,
            Coodbook.IB7,
            Coodbook.IB8,
            Coodbook.IB9,
            Coodbook.IBa,
            Coodbook.IBb,
            Coodbook.IBc,
            Coodbook.IBd,
            Coodbook.IBe,
            Coodbook.IBf,
            Coodbook.IBStart,
            Coodbook.IBDivide,
            Coodbook.IBEnd};
}
