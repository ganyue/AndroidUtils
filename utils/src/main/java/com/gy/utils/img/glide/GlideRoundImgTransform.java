package com.gy.utils.img.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by ganyu on 2016/12/13.
 */
public class GlideRoundImgTransform extends BitmapTransformation{
    public GlideRoundImgTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
        return circleCrop(bitmapPool, bitmap);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    private Bitmap circleCrop (BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap square = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.RGB_565);
        if (result == null) result = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(square, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        float r = size /2f;
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(r, r, r, paint);
        return result;
    }
}
