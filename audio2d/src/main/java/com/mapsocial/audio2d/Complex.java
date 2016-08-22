package com.mapsocial.audio2d;

import android.graphics.Canvas;

/**
 * Created by ganyu on 2016/8/18.
 *
 */
public class Complex {
    public double real;
    public double image;

    public Complex() {
        this.real = 0;
        this.image = 0;
    }

    public Complex(double real, double image){
        this.real = real;
        this.image = image;
    }

    public Complex(int real, int image) {
        Integer integer = real;
        this.real = integer.floatValue();
        integer = image;
        this.image = integer.floatValue();
    }

    public Complex(double real) {
        this.real = real;
        this.image = 0;
    }
    /**乘法*/
    public Complex multiply(Complex complex) {
        Complex tmpComplex = new Complex();
        tmpComplex.real = this.real * complex.real - this.image * complex.image;
        tmpComplex.image = this.real * complex.image + this.image * complex.real;
        return tmpComplex;
    }

    /**加法*/
    public Complex sum(Complex complex) {
        Complex tmpComplex = new Complex();
        tmpComplex.real = this.real + complex.real;
        tmpComplex.image = this.image + complex.image;
        return tmpComplex;
    }

    /**减法*/
    public Complex subtract(Complex complex) {
        Complex result = new Complex();
        result.real = this.real - complex.real;
        result.image = this.image - complex.image;
        return result;
    }

    /**获得一个复数的模值*/
    public int getIntValue(){
        int ret = 0;
        ret = (int) Math.round(Math.sqrt(this.real*this.real - this.image*this.image));
        return ret;
    }

    /**快速傅里叶变换*/
    public static Complex[] fft(Complex[] xin,int len)
    {
        int f,m,N2,nm,i,k,j,L;//L:运算级数
        double p;
        int e2,le,B,ip;
        Complex w = new Complex();
        Complex t = new Complex();
        N2 = len / 2;//每一级中蝶形的个数,同时也代表m位二进制数最高位的十进制权值
        f = len;//f是为了求流程的级数而设立的
        for(m = 1; (f = f / 2) != 1; m++);                             //得到流程图的共几级
        nm = len - 2;
        j = N2;
        /******倒序运算——雷德算法******/
        for(i = 1; i <= nm; i++)
        {
            if(i < j)//防止重复交换
            {
                t = xin[j];
                xin[j] = xin[i];
                xin[i] = t;
            }
            k = N2;
            while(j >= k)
            {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }
        /******蝶形图计算部分******/
        for(L=1; L<=m; L++)                                    //从第1级到第m级
        {
            e2 = (int) Math.pow(2, L);
            //e2=(int)2.pow(L);
            le=e2+1;
            B=e2/2;
            for(j=0;j<B;j++)                                    //j从0到2^(L-1)-1
            {
                p=2*Math.PI/e2;
                w.real = Math.cos(p * j);
                //w.real=Math.cos((double)p*j);                                   //系数W
                w.image = Math.sin(p*j) * -1;
                //w.imag = -sin(p*j);
                for(i=j;i<len;i=i+e2)                                //计算具有相同系数的数据
                {
                    ip=i+B;                                           //对应蝶形的数据间隔为2^(L-1)
                    t=xin[ip].multiply(w);
                    xin[ip] = xin[i].subtract(t);
                    xin[i] = xin[i].sum(t);
                }
            }
        }

        return xin;
    }
}
