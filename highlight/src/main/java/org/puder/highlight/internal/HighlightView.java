package org.puder.highlight.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.puder.highlight.R;

public class HighlightView extends RelativeLayout {

    private static final int ALPHA_60_PERCENT           = 153;
    private final float      innerRadiusScaleMultiplier = 1.2f;
    private final float      outerRadiusScaleMultiplier = 1.8f;

    private Paint            eraserPaint;
    private Paint            basicPaint;

    private HighlightItem    item;


    public HighlightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        basicPaint = new Paint();
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
    }

    public void setHighlightItem(HighlightItem item) {
        this.item = item;
        if (item.titleId != -1) {
            TextView title = (TextView) findViewById(R.id.highlight_title);
            title.setText(item.titleId);
        }
        if (item.descriptionId != -1) {
            TextView descr = (TextView) findViewById(R.id.highlight_description);
            descr.setText(item.descriptionId);
        }
        invalidate();
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        // Delegate the click listener to the button
        findViewById(R.id.highlight_button).setOnClickListener(listener);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int width = item.screenRight - item.screenLeft;
        int height = item.screenBottom - item.screenTop;
        int cx = item.screenLeft + width / 2 - location[0];
        int cy = item.screenTop + height / 2 - location[1];
        float radius = width > height ? width / 2 : height / 2;
        Bitmap overlay = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas overlayCanvas = new Canvas(overlay);
        overlayCanvas.drawColor(0xcc3d4353);// 0x3333B5E5);
        eraserPaint.setAlpha(ALPHA_60_PERCENT);
        overlayCanvas.drawCircle(cx, cy, radius * outerRadiusScaleMultiplier, eraserPaint);
        eraserPaint.setAlpha(0);
        overlayCanvas.drawCircle(cx, cy, radius * innerRadiusScaleMultiplier, eraserPaint);
        canvas.drawBitmap(overlay, 0, 0, basicPaint);
        super.dispatchDraw(canvas);
    }
}
