package ec.com.yacare.y4all.lib.util;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;

/**
 * Created by yacare on 28/01/2017.
 */

public class DigitalClock extends TextView {

    Calendar mCalendar;
    private final static String m12 = "h:mm:ss aa";
    private final static String m24 = "dd/MM/yyyy k:mm:ss";
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
       // Resources r = mContext.getResources();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;

                if(Y4HomeActivity.circleProgressSegundo != null){
                    int porcentajeSegundos = mCalendar.get(Calendar.SECOND) * 100 / 60;
                    Y4HomeActivity.circleProgressSegundo.changePercentage(porcentajeSegundos,  mCalendar.get(Calendar.HOUR_OF_DAY));
                    mCalendar.setTimeInMillis(System.currentTimeMillis());
                    setText(DateFormat.format(mFormat, mCalendar));
                    int porcentajeMinuto = mCalendar.get(Calendar.MINUTE) * 100 / 60;
                    Y4HomeActivity.circleProgressMinuto.changePercentage(porcentajeMinuto);
                    int porcentajeHora = mCalendar.get(Calendar.HOUR) * 100 / 12;
                    Y4HomeActivity.circleProgressHora.changePercentage(porcentajeHora, porcentajeSegundos);

                    if(mCalendar.get(Calendar.HOUR_OF_DAY) > 12 && mCalendar.get(Calendar.HOUR_OF_DAY) < 19){
                        Y4HomeActivity.layoutReloj.setBackgroundResource(R.drawable.fondo_reloj_tarde);
                        setTextColor(Color.BLACK);
                        Y4HomeActivity.textYacare.setTextColor(Color.BLACK);
                    }else if(mCalendar.get(Calendar.HOUR_OF_DAY) >= 19){
                        Y4HomeActivity.layoutReloj.setBackgroundResource(R.drawable.fondo_reloj_noche);
                        setTextColor(Color.WHITE);
                        Y4HomeActivity.textYacare.setTextColor(Color.WHITE);
                    }else{
                        Y4HomeActivity.layoutReloj.setBackgroundResource(R.drawable.fondo_reloj_dia);
                        setTextColor(Color.BLACK);
                        Y4HomeActivity.textYacare.setTextColor(Color.BLACK);
                    }

                }

                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
//        if (get24HourMode()) {
            mFormat = m24;
//        } else {
//            mFormat = m12;
//        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }
}
