package com.belatrix.habilidadessociolaborales.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.belatrix.habilidadessociolaborales.R;

public class ProgressImageView extends ImageView {

    private int color = 0xFF000000;

    private String text = "0 %";

    private final Rect textBounds = new Rect();

    public ProgressImageView(Context context) {
        super(context);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint mPaint = new Paint();

        mPaint.setColor(0xFFFFFFFF);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);

        mPaint.setColor(color);
        mPaint.setTextSize(getContext().getResources().getDimension(R.dimen.font_percentage_size));
        mPaint.setFakeBoldText(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.getTextBounds(text, 0, text.length(), textBounds);

        canvas.drawText(text, getWidth() / 2f, getHeight() / 2f - textBounds.exactCenterY(), mPaint);
    }

    public void setColor(int color) {
        this.color = color;
    }


    public void setPercentage(int percentage) {
        this.text = percentage + " %";
    }
}
