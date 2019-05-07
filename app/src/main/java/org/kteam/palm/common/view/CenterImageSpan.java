package org.kteam.palm.common.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * @Project Palm
 *
 * @Description 居中显示图片的ImageSpan
 *
 * @Author MickyLiu
 * @Email mickyliu@126.com
 * @Date 2015年12月8日 下午3:44:01
 *
 */
public class CenterImageSpan extends ImageSpan {
	
	public CenterImageSpan(Drawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
