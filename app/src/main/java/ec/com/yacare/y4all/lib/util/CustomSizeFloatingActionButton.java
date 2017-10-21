package ec.com.yacare.y4all.lib.util;

import android.content.Context;
import android.util.AttributeSet;

import com.github.clans.fab.FloatingActionButton;

public class CustomSizeFloatingActionButton extends FloatingActionButton {

    public CustomSizeFloatingActionButton(Context context) {
        super(context);
    }

    public CustomSizeFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSizeFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension((int) (width * 0.9f), (int) (height * 0.9f));

    }

}
