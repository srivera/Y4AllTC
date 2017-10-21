package ec.com.yacare.y4all.activities.focos.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.weekview.DateTimeInterpreter;
import com.weekview.MonthLoader;
import com.weekview.WeekView;
import com.weekview.WeekViewEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.focos.ProgramacionActivity;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

/**
 * A basic example of how to use week view library.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public class CalendarioFragment extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.

        DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        ZonaDataSource zonaDataSource = new ZonaDataSource(getApplicationContext());
        zonaDataSource.open();
        ArrayList<ZonaLuces> zonas = zonaDataSource.getAllZonaRouter(datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
        zonaDataSource.close();
        HashMap<String, ZonaLuces> zonasHash = new HashMap<String, ZonaLuces>();
        for(ZonaLuces zonaLuces : zonas){
            zonasHash.put(zonaLuces.getId(), zonaLuces);
        }

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
//							System.out.printf("GMT offset is %s hours", TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));

        ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
        programacionDataSource.open();
        ArrayList<ProgramacionLuces> progs = programacionDataSource.getAllProgByRouter(datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
        Integer horaGMT;
        for(ProgramacionLuces programacionLuces : progs){
            char[] dias = programacionLuces.getDias().toCharArray();
            String[] hora = programacionLuces.getHoraInicio().split(":");
            Calendar hoy = Calendar.getInstance();
            hoy.setTime(new Date());

            Integer diaActual = hoy.get(Calendar.DAY_OF_WEEK);

            Integer offset = 7 - diaActual + 1;
            for(int i = 1; i < 8; i++){
                if(offset.equals(7)){
                    offset = 0;
                }
                if(dias[i - 1] =='1'){
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_WEEK, offset);
                    horaGMT = Integer.valueOf(hora[0]) - ((Long)TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)).intValue();
                    if(horaGMT >= 24){
                        horaGMT = horaGMT - 24;
                    }else if(horaGMT < 0){
                        horaGMT = horaGMT + 24;
                    }
                    startTime.set(Calendar.HOUR_OF_DAY, horaGMT);
                    startTime.set(Calendar.MINUTE, Integer.valueOf(hora[1]));
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);

                    String[] duracion = programacionLuces.getDuracion().split(":");

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR, Integer.valueOf(duracion[0]));
                    endTime.add(Calendar.MINUTE, Integer.valueOf(duracion[1]));
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    ZonaLuces zona = (ZonaLuces) zonasHash.get(programacionLuces.getIdZona());
                    WeekViewEvent event = new WeekViewEvent(1, zona.getNombreZona() + " " + programacionLuces.getNombre(), startTime, endTime);
                    if (zona.getNumeroZona().trim().equals("1")) {
                        event.setColor(getResources().getColor(R.color.event_color_01));
                    } else if (zona.getNumeroZona().trim().equals("1")) {
                        event.setColor(getResources().getColor(R.color.event_color_02));
                    } else if (zona.getNumeroZona().trim().equals("1")) {
                        event.setColor(getResources().getColor(R.color.event_color_03));
                    } else {
                        event.setColor(getResources().getColor(R.color.event_color_04));
                    }
                    event.setIdProgramacion(programacionLuces.getId());
                    events.add(event);

                }
                offset++;

            }

        }
        return events;
    }
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private ImageButton fabSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
         if (isScreenLarge()) {
            onConfigurationChanged(getResources().getConfiguration());
        } else {
            AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

         mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        Button btnHoy, btntresdias, btnSemana;
        btnHoy = (Button) findViewById(R.id.btnHoy);
        btnHoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeekViewType = TYPE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(1);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.goToToday();
            }
        });
        btntresdias = (Button) findViewById(R.id.btntresdias);
        btntresdias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
            }
        });
        btnSemana = (Button) findViewById(R.id.btnSemana);
        btnSemana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
            }
        });

        fabSalir = (ImageButton) findViewById(R.id.fabSalir);
        fabSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public void refresh(){
        mWeekView.notifyDatasetChanged();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("%02d:%02d:%s", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.DAY_OF_WEEK));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(CalendarioFragment.this, "Seleccionado " + event.getName(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CalendarioFragment.this, ProgramacionActivity.class);
        i.putExtra("zona", new ZonaLuces());
        startActivityForResult(i, 1);
    }

    String idBorrar;
    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
      //  Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CalendarioFragment.this);
        alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.confirmar"))
                .setMessage(YACSmartProperties.intance.getMessageForKey("confirmacion.eliminar.programacion"))
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        idBorrar = event.getIdProgramacion();
                      //  EliminarProgramacionAsyncTask eliminarProgramacionAsyncTask = new EliminarProgramacionAsyncTask(CalendarioFragment.this, event.getIdProgramacion());
                      //  eliminarProgramacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void verificarEliminarProgramacion(String respuesta) {
        //Nueva zona
        if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
            String resultado = null;
            Boolean status = null;
            JSONObject respuestaJSON = null;
            try {
                respuestaJSON = new JSONObject(respuesta);
                status = respuestaJSON.getBoolean("statusFlag");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status != null && status) {
                try {
                    resultado = respuestaJSON.getString("result");
                    if (resultado.equals("OK")) {
                        ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
                        programacionDataSource.open();
                        programacionDataSource.deleteProgramacionById(idBorrar);
                        programacionDataSource.close();
                        refresh();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                CalendarioFragment.this);
                        alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
                                .setMessage(YACSmartProperties.intance.getMessageForKey("exito.router"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        CalendarioFragment.this);
                alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
                        .setMessage(YACSmartProperties.intance.getMessageForKey("error.router"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    CalendarioFragment.this);
            alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
                    .setMessage(YACSmartProperties.intance.getMessageForKey("error.router"))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        //   Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CalendarioFragment.this, ProgramacionActivity.class);
        i.putExtra("tiempo", getEventTitle(time));
        i.putExtra("zona", new ZonaLuces());
        startActivityForResult(i, 1);
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
}
