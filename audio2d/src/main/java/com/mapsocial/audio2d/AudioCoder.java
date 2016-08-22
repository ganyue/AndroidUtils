package com.mapsocial.audio2d;

import android.graphics.Paint;

import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class AudioCoder {

    public byte[] encode (int freq) {
//        freq = Audio2DConsts.Coodbook.B6;
        double dr = 2 * Math.PI * freq / Audio2DConsts.AudioInfo.Audio_Rate;
        double r = 0;
        byte[] buff = new byte[Audio2DConsts.AudioInfo.Sample_Count * 2];
        for (int i = 0; i < Audio2DConsts.AudioInfo.Sample_Count; i++) {
            int pcmData = (int) (Math.sin(r) * Audio2DConsts.AudioInfo.Max_Vol);
            r+=dr;

            buff[i * 2] = (byte) (pcmData & 0xff);
            buff[i * 2 + 1] = (byte) ((pcmData >> 8) & 0xff);
        }
        return buff;
    }

//    public static byte[] encode (int[] freqs) {
////        freqs = Audio2DConsts.Coodbook.F_Start;
////        freqs = Audio2DConsts.Coodbook.F_Divider;
////        freqs = Audio2DConsts.Coodbook.F_End;
//        freqs = new int[] {Audio2DConsts.Coodbook.B1, Audio2DConsts.Coodbook.B2, 0, 0, 0, 0, 0, 0, 0};
//        double[] drs = new double[freqs.length];
//        for (int i = 0; i < freqs.length; i++) {
//            switch (freqs[i]) {
//                case Audio2DConsts.Coodbook.B1:
//                    drs[i] = Audio2DConsts.Coodbook.DB1;
//                    break;
//                case Audio2DConsts.Coodbook.B2:
//                    drs[i] = Audio2DConsts.Coodbook.DB2;
//                    break;
//                case Audio2DConsts.Coodbook.B3:
//                    drs[i] = Audio2DConsts.Coodbook.DB3;
//                    break;
//                case Audio2DConsts.Coodbook.B4:
//                    drs[i] = Audio2DConsts.Coodbook.DB4;
//                    break;
//                case Audio2DConsts.Coodbook.B5:
//                    drs[i] = Audio2DConsts.Coodbook.DB5;
//                    break;
//                case Audio2DConsts.Coodbook.B6:
//                    drs[i] = Audio2DConsts.Coodbook.DB6;
//                    break;
//                case Audio2DConsts.Coodbook.B7:
//                    drs[i] = Audio2DConsts.Coodbook.DB7;
//                    break;
//                case Audio2DConsts.Coodbook.B8:
//                    drs[i] = Audio2DConsts.Coodbook.DB8;
//                    break;
//                case Audio2DConsts.Coodbook.B9:
//                    drs[i] = Audio2DConsts.Coodbook.DB9;
//                    break;
//                default:
//                    drs[i] = 0;
//                    break;
//            }
//        }
//
//        double[] rs = new double[freqs.length];
//        byte[] buff = new byte[Audio2DConsts.AudioInfo.Sample_Count * 2];
//        for (int i = 0; i < Audio2DConsts.AudioInfo.Sample_Count; i++) {
//            int pcmData = 0;
//
//            for (int j = 0; j < freqs.length; j++) {
//                if (drs[j] != 0) {
//                    pcmData += (int) (Math.sin(rs[j]) * Audio2DConsts.AudioInfo.Max_Vol/2);
//                    rs[j]+=drs[j];
//                }
//            }
//
//            if (pcmData >= Audio2DConsts.AudioInfo.Max_Vol) {
//                pcmData = Audio2DConsts.AudioInfo.Max_Vol;
//            } else if (pcmData <= Audio2DConsts.AudioInfo.Min_Vol) {
//                pcmData = Audio2DConsts.AudioInfo.Min_Vol;
//            }
//
//            buff[i * 2] = (byte) (pcmData & 0xff);
//            buff[i * 2 + 1] = (byte) ((pcmData >> 8) & 0xff);
//        }
//        return buff;
//    }

    private byte prevResult = 0x4f;

    public Complex[] decode (List<Byte> results, byte[] buff, int len) {
        if (len != 1024) return null;
        int sameCount = 0;
        len = len/2;
        Complex[] complices = new Complex[len];
        for (int i = 0; i < len; i++) {
            short lower = buff[i * 2];
            short uper = buff[i * 2 + 1];
            short temp = (short) ((uper << 8) | (lower & 0xff));

            complices[i] = new Complex(temp);
        }

        complices = Complex.fft(complices, len);

        byte recByte = recognize(complices);

        int resultSize = results.size();
        if (resultSize == 0 && recByte != 0x1f) {
            return complices;
        }

        if (recByte == 0x4f) {
            return complices;
        }

        if (prevResult == 0x4f) {
            prevResult = recByte;
            return  complices;
        }

        if (recByte == prevResult) {
            if (recByte == 0x1f) {
                if (resultSize == 0 || (resultSize > 0 && results.get(resultSize - 1) != 0x1f)) {
                    results.add(recByte);
                }
            } else if (resultSize > 0 && recByte == 0x2f) {
                if (results.get(resultSize - 1) == 0x2f) {
                    return complices;
                } else {
                    results.add(recByte);
                }
            } else if (resultSize > 0 && recByte != 0x2f) {
                if (results.get(resultSize - 1 ) == 0x2f) {
                    results.remove(resultSize - 1);
                    results.add(recByte);
                }
            }
            prevResult = 0x4f;
        } else {
            prevResult = recByte;
        }

        return complices;
    }

    private final int MIN_RECOG_NUM = 20000;
    private byte recognize (Complex[] complices) {
        Audio2DConsts consts = new Audio2DConsts();
        int[] values = {
                complices[Audio2DConsts.Coodbook.IB0].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB1].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB2].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB3].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB4].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB5].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB6].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB7].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB8].getIntValue(),
                complices[Audio2DConsts.Coodbook.IB9].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBa].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBb].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBc].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBd].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBe].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBf].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBStart].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBDivide].getIntValue(),
                complices[Audio2DConsts.Coodbook.IBEnd].getIntValue(),
        };

        int maxVal = 0;
        int maxValIndex = 0;
        for (int i = 0; i < values.length; i++) {
            if (maxVal < values[i]) {
                maxVal = values[i];
                maxValIndex = i;
            }
        }

        if (maxVal < MIN_RECOG_NUM) {
            return 0x4f;
        }

        switch (maxValIndex) {
            case 0:
                return 0x00;
            case 1:
                return 0x01;
            case 2:
                return 0x02;
            case 3:
                return 0x03;
            case 4:
                return 0x04;
            case 5:
                return 0x05;
            case 6:
                return 0x06;
            case 7:
                return 0x07;
            case 8:
                return 0x08;
            case 9:
                return 0x09;
            case 10:
                return 0x0a;
            case 11:
                return 0x0b;
            case 12:
                return 0x0c;
            case 13:
                return 0x0d;
            case 14:
                return 0x0e;
            case 15:
                return 0x0f;
            case 16:
                return 0x1f; //start
            case 17:
                return 0x2f; //divider
            case 18:
                return 0x3f; //end
        }
        return 0x4f;         //error value
    }

    public List<String> getRecognizeStrings (List<Byte> bResults) {
        List<String> results = new ArrayList<>();
        List<Byte> tempBArray = new ArrayList<>(bResults);

        List<Integer> startAndEndIndexes = new ArrayList<>();

        //find start and end flags
        for (int i = 0; i < bResults.size(); i++) {
            if (bResults.get(i) == 0x1f) {
                if (startAndEndIndexes.size() == 0) {
                    startAndEndIndexes.add(i);
                } else if (bResults.get(startAndEndIndexes.size() - 1) == 0x3f) {
                    startAndEndIndexes.add(i);
                } else {
                    startAndEndIndexes.set(startAndEndIndexes.size() - 1, i);
                }
            } else if (bResults.get(i) == 0x2f) {
                if (startAndEndIndexes.size() != 0 && bResults.get(startAndEndIndexes.size() - 1) == 0x1f) {
                    startAndEndIndexes.remove(startAndEndIndexes.size() - 1);
                }
            } else if (bResults.get(i) == 0x3f) {
                if (startAndEndIndexes.size() > 0
                        && bResults.get(startAndEndIndexes.size() - 1) == 0x1f) {
                    if ((i - startAndEndIndexes.get(startAndEndIndexes.size() - 1)) % 2 == 0) {
                        startAndEndIndexes.remove(startAndEndIndexes.size() - 1);
                    } else {
                        startAndEndIndexes.add(i);
                    }
                }
            }
        }

        int seg = 0;
        while ( (seg * 2 + 1) < startAndEndIndexes.size()) {
            int startIndex = startAndEndIndexes.get(seg);
            int endIndex = startAndEndIndexes.get(seg + 1);

            int buffLen = (endIndex - startIndex)/2;
            if (buffLen > 0) {
                byte[] buff = new byte[buffLen];
                for (int i = 0; i < buffLen; i++) {
                    byte low4 = bResults.get(seg * 2 + i * 2 + 1);
                    byte up4 = bResults.get(seg * 2 + i * 2 + 2);
                    buff[i] = (byte) ((up4 << 4) | low4);
                }
                results.add(new String(buff));
            }

            seg += 2;
        }

        return results;
    }
}
