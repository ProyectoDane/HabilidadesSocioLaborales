package com.belatrix.habilidadessociolaborales.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    private static RenderScript sRenderScript = null;
    private static ImageUtils sInstance = null;
    private static Context sContext = null;

    private ImageUtils(Context context) {
        if (sRenderScript == null) {
            sRenderScript = RenderScript.create(context);
        }
    }

    public static ImageUtils getInstance(Context context) {
        sContext = context;
        if (sInstance == null) {
            sInstance = new ImageUtils(context);
        }
        return sInstance;
    }

    public BitmapDrawable getBlurredImage(int imageResource, float blurStyle) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap image = BitmapFactory.decodeResource(sContext.getResources(), imageResource, options);
        image = getBlurredBitmap(image, blurStyle);

        return new BitmapDrawable(sContext.getResources(), image);
    }

/*    public BitmapDrawable getBlurredImage(int imageResource) {
        return getBlurredImage(imageResource, 10f);
    }*/

    public Bitmap getBlurredBitmap(Bitmap bitmap, float blurStyle) {
        final Allocation input = Allocation.createFromBitmap(sRenderScript, bitmap);
        final Allocation output = Allocation.createTyped(sRenderScript, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(sRenderScript, Element.U8_4(sRenderScript));

        script.setRadius(blurStyle);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        return bitmap;
    }

    public Bitmap getBlurredBitmap(Bitmap bitmap) {
        return getBlurredBitmap(bitmap, 10f);
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
