package ec.com.yacare.y4all.activities.focos.calendar;

import com.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;

/**
 * A basic example of how to use week view library.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public class BasicActivity extends BaseActivity {

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

        ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
        programacionDataSource.open();
        ArrayList<ProgramacionLuces> progs = programacionDataSource.getAllProgByRouter(datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
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
                    startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hora[0]));
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
                    events.add(event);

                }
                offset++;

            }

        }
        return events;
    }

}
