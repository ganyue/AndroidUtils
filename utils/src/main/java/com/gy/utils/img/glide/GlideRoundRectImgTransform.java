package com.gy.utils.img.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by ganyu on 2016/12/13.
 *
 */
public class GlideRoundRectImgTransform extends BitmapTransformation{
    public GlideRoundRectImgTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
        return roundRect(bitmapPool, bitmap);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    private Bitmap roundRect (BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap square = Bitmap.createBitmap(source, 0, 0, height, height);
        Bitmap result = pool.get(height, height, Bitmap.Config.RGB_565);
        if (result == null) result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(square, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        canvas.drawColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(0, 0, width, height), 20f, 20f, paint);
        return result;
    }
}
