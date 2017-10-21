package ec.com.yacare.y4all.lib.focos;

import android.content.Context;

import java.io.IOException;
import java.net.UnknownHostException;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class ComandoFoco extends  Thread{

    private String comando;
    private Context context;


    public ComandoFoco(String comando, Context context) {

        this.comando = comando;
        this.context = context;
    }

    @Override
    public void run() {
       // XlinkUtils.shortTips("Dentro  de Comando Foco");
        WifiBox WifiBox = new WifiBox(context);
//        ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                XlinkUtils.shortTips("valor de context1 " + context);
//            }
//        });
        String[] recibido = comando.split(";");

        if (YACSmartProperties.COM_ABRIR_SESION_LUCES.equals(recibido[0]) && recibido.length > 4) {
             try {

                WifiBox.abrirSesionFocos(recibido[4]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (YACSmartProperties.COM_ENCENDER_LUZ_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
//                ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        XlinkUtils.shortTips("Antes de entrar a WifiBox.on");
//                    }
//                });
                String grupo = recibido[4];
                WifiBox.on(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_ENCENDER_LUZ_BOX_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {

                WifiBox.onBox(recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_APAGAR_LUZ_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
                String grupo = recibido[4];

                WifiBox.off(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_APAGAR_LUZ_BOX_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {

                WifiBox.offBox(recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_WHITE_WIFI.equals(recibido[0])) {
            try {
                String grupo = recibido[4];

                WifiBox.white(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_WHITE_BOX_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {

                WifiBox.whiteBox(recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_CALIDA_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
                String grupo = recibido[4];

                WifiBox.whiteCalid(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_NIGHT_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
                String grupo = recibido[4];

                WifiBox.night(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_BRIGHTNESS_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);
                Integer valor = Integer.valueOf(recibido[5]);

                WifiBox.brightness(valor, grupo, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_BRIGHTNESS_BOX_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Integer valor = Integer.valueOf(recibido[5]);

                WifiBox.brightnessBox(valor, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_SATURATION_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);
                Integer valor = Integer.valueOf(recibido[5]);

                WifiBox.saturation(valor, grupo, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_COLOR_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);
                String bb = "0x" + recibido[5];
                int hex = Integer.decode(bb);


                WifiBox.color(hex, grupo, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                String bb = "0x" + recibido[5];
                int hex = Integer.decode(bb);

                WifiBox.colorBox(hex, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_MODE_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);
                Byte val = new Byte(recibido[5]);
                String a = String.format("%02X ", val);
                String bb = "0x" + a.trim();
                int hex = Integer.decode(bb);

                WifiBox.mode(hex, grupo, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_MODE_BOX_WIFI.equals(recibido[0]) && recibido.length > 6) {
            try {
                Byte val = new Byte(recibido[5]);
                String a = String.format("%02X ", val);
                String bb = "0x" + a.trim();
                int hex = Integer.decode(bb);

                WifiBox.modeBox(hex, recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_DISCO_FASTER_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);

                WifiBox.discoModeFaster(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_DISCO_FASTER_BOX_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
//                Integer grupo = Integer.valueOf(recibido[4]);

                WifiBox.discoModeFasterBox(recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_DISCO_SLOWLER_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {
                Integer grupo = Integer.valueOf(recibido[4]);

                WifiBox.discoModeSlower(grupo, recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LUZ_DISCO_SLOWLER_BOX_WIFI.equals(recibido[0]) && recibido.length > 5) {
            try {

                WifiBox.discoModeSlowerBox(recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_LINK_FOCOS.equals(recibido[0]) && recibido.length > 4) {
            try {
                //  Integer grupo = Integer.valueOf(recibido[4]);

                WifiBox.link(recibido[4], recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_UNLINK_FOCOS.equals(recibido[0]) && recibido.length > 4) {
            try {
                //  Integer grupo = Integer.valueOf(recibido[4]);

                WifiBox.unlink(recibido[4], recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//    }else if (YACSmartProperties.COM_BUSCAR_ROUTER.equals(recibido[0]) && recibido.length > 3) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            recibidoCopia = new String[]{recibido[0],recibido[1],recibido[2],recibido[3]};
//            Thread t1 = new Thread(new Runnable() {
//                public void run() {
//                    buscarRouter(recibidoCopia);
//                }
//            });
//            t1.start();
//        }
//    }else if (YACSmartProperties.COM_LINK_ROUTER.equals(recibido[0]) && recibido.length > 6) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            RouterDataSource routerDataSource = new RouterDataSource(activity.getApplicationContext());
//            routerDataSource.open();
//            RouterLuces routerLuces = routerDataSource.getRouterByImacAll(recibido[5]);
//            if(routerLuces == null){
//                routerLuces = new RouterLuces();
//                routerLuces.setId(recibido[5]);
//                routerLuces.setEstado("1");
//                routerLuces.setIdEquipo(datosAplicacion.getEquipo().getId());
//                routerLuces.setIpRouter(recibido[4]);
//                routerLuces.setImacRouter(recibido[5]);
//                routerLuces.setNombreRouter(recibido[6]);
//                routerDataSource.createRouter(routerLuces);
//            }else{
//                routerLuces.setEstado("1");
//                routerLuces.setIdEquipo(datosAplicacion.getEquipo().getId());
//                routerLuces.setIpRouter(recibido[4]);
//                routerLuces.setImacRouter(recibido[5]);
//                routerLuces.setNombreRouter(recibido[6]);
//                routerDataSource.updateRouter(routerLuces);
//            }
//            routerDataSource.close();
//            String resultado = YACSmartProperties.COM_LINK_ROUTER + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";" + recibido[5] + ";" + recibido[6]  + ";" + "OK" + ";";
//
//            emitComando(resultado);
//        }
//    }else if (YACSmartProperties.COM_UNLINK_ROUTER.equals(recibido[0]) && recibido.length > 5) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            RouterDataSource routerDataSource = new RouterDataSource(activity.getApplicationContext());
//            routerDataSource.open();
//            routerDataSource.deleteRouterImac(recibido[5]);
//            routerDataSource.close();
//            ZonaDataSource zonaDataSource = new ZonaDataSource(activity.getApplicationContext());
//            zonaDataSource.open();
//            zonaDataSource.deleteZonaImac(recibido[5]);
//            zonaDataSource.close();
//            String resultado = YACSmartProperties.COM_UNLINK_ROUTER + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";" + recibido[5] +";" + "OK" + ";";
//
//            emitComando(resultado);
//        }
//
//    }else if (YACSmartProperties.COM_SINCRONIZAR_ZONA_LUCES.equals(recibido[0]) && recibido.length > 4) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            String res  = " ";
//            ZonaDataSource zonaDataSource = new ZonaDataSource(activity.getApplicationContext());
//            zonaDataSource.open();
//            ArrayList<ZonaLuces> zonas = zonaDataSource.getZonaByRouter(recibido[4]);
//            zonaDataSource.close();
//            for(ZonaLuces zonaLuces : zonas){
//                res = res.trim() + zonaLuces.getId() + "," + zonaLuces.getNombreZona() + "," + zonaLuces.getEstado() + "#";
//            }
//            String resultado = YACSmartProperties.COM_SINCRONIZAR_ZONA_LUCES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4]  + ";" + res + ";";
//
//            emitComando(resultado);
//        }
//    }else if (YACSmartProperties.COM_ELIMINAR_ZONA_LUCES.equals(recibido[0]) && recibido.length > 5) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            ZonaDataSource dataSource = new ZonaDataSource(activity.getApplicationContext());
//            dataSource.open();
//            dataSource.deleteZonaImacId(recibido[4], recibido[5]);
//            dataSource.close();
//            String resultado = YACSmartProperties.COM_ELIMINAR_ZONA_LUCES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";" + recibido[5] + ";" + "OK" + ";";
//
//            emitComando(resultado);
//        }
//    }else if (YACSmartProperties.COM_CREAR_ZONA_LUCES.equals(recibido[0]) && recibido.length > 5) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            String res  = "ERR";
//            ZonaDataSource zonaDataSource = new ZonaDataSource(activity.getApplicationContext());
//            zonaDataSource.open();
//            ArrayList<ZonaLuces> zonas = zonaDataSource.getZonaByRouter(recibido[4]);
//
//            if(zonas.size() < 4){
//                res  = "OK";
//                ZonaLuces zonaLuces = new ZonaLuces();
//                zonaLuces.setId(String.valueOf(zonas.size() + 1));
//                zonaLuces.setEstado("0");
//                zonaLuces.setIdEquipo(datosAplicacion.getEquipo().getId());
//                zonaLuces.setIdRouter(recibido[4]);
//                zonaLuces.setNombreZona(recibido[5]);
//                zonaLuces.setEncenderTimbre("0");
//                zonaDataSource.createZona(zonaLuces);
//            }
//            zonaDataSource.close();
//            String resultado = YACSmartProperties.COM_CREAR_ZONA_LUCES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";" + recibido[5] + ";" + String.valueOf(zonas.size() + 1) + ";"+ res + ";" ;
//
//            emitComando(resultado);
//        }
//    } else if (YACSmartProperties.COM_CREAR_PROGRAMACION.equals(recibido[0]) && recibido.length > 10) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(activity.getApplicationContext());
//            programacionDataSource.open();
//            ProgramacionLuces programacionLuces = new ProgramacionLuces();
//            programacionLuces.setId(UUID.randomUUID().toString());
//            programacionLuces.setIdRouter(recibido[4] );
//            programacionLuces.setIdZona(recibido[5] );
//            programacionLuces.setNombre(recibido[6]);
//            programacionLuces.setAccion(recibido[7]);
//            programacionLuces.setHoraInicio(recibido[8]);
//            programacionLuces.setDuracion(recibido[9]);
//            programacionLuces.setDias(recibido[10]);
//            ProgramacionLuces progresult = programacionDataSource.createProgramacion(programacionLuces);
//
//            programacionDataSource.close();
//            String resultadoProg = "OK";
//            if(progresult == null){
//                resultadoProg = "ERR";
//            }
//            String resultado = YACSmartProperties.COM_CREAR_PROGRAMACION + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";" + recibido[5] + ";" + recibido[6] + ";" + recibido[7]+ ";" + recibido[8] + ";" + recibido[9]
//                    + ";" + recibido[10]  + ";" + programacionLuces.getId() + ";" + resultadoProg + ";";
//
//            emitComando(resultado);
//
//            if(progresult != null){
//                Intent intent = new Intent(activity.getApplicationContext(), AlarmaReceiver.class);
//                intent.putExtra("idProgramacion", programacionLuces.getId());
//                intent.putExtra("evento", "1");
//                intent.putExtra("dias", programacionLuces.getDias());
//                intent.putExtra("duracion", programacionLuces.getDuracion());
//                intent.putExtra("horaInicio", programacionLuces.getHoraInicio());
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                String[] hora = programacionLuces.getHoraInicio().split(":");
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hora[0]));
//                calendar.set(Calendar.MINUTE, Integer.valueOf(hora[1]));
//                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
//                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent );
////Apagar
//                String[] duracion = programacionLuces.getDuracion().split(":");
//                Integer horaFinal = Integer.valueOf(hora[0]) + Integer.valueOf(duracion[0]);
//                Integer minutoFinal = Integer.valueOf(hora[1]) + Integer.valueOf(duracion[1]);
//                if(minutoFinal > 60){
//                    horaFinal = horaFinal + 1;
//                    minutoFinal = minutoFinal - 60;
//                }
//                if(horaFinal > 24){
//                    horaFinal = horaFinal - 24;
//                }
//                Intent intent2 = new Intent(activity.getApplicationContext(), AlarmaReceiver.class);
//                intent2.putExtra("idProgramacion", programacionLuces.getId());
//                intent2.putExtra("evento", "0");
//                intent.putExtra("dias", programacionLuces.getDias());
//                intent.putExtra("duracion", programacionLuces.getDuracion());
//                intent.putExtra("horaInicio", programacionLuces.getHoraInicio());
//                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(activity.getApplicationContext(), 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//                Calendar calendar2 = Calendar.getInstance();
//                calendar2.setTimeInMillis(System.currentTimeMillis());
//                calendar2.set(Calendar.HOUR_OF_DAY, horaFinal);
//                calendar2.set(Calendar.MINUTE, minutoFinal);
//                alarmManager.set(AlarmManager.RTC, calendar2.getTimeInMillis(), pendingIntent2 );
//            }
//        }
//    } else if (YACSmartProperties.COM_ELIMINAR_PROGRAMACION.equals(recibido[0]) && recibido.length > 4) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(activity.getApplicationContext());
//            programacionDataSource.open();
//            programacionDataSource.deleteProgramacionById(recibido[4]);
//            programacionDataSource.close();
//            String resultado = YACSmartProperties.COM_ELIMINAR_PROGRAMACION + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + recibido[4] + ";";
//
//            emitComando(resultado);
//        }
//    } else if (YACSmartProperties.COM_SINCRONIZAR_PROGRAMACION.equals(recibido[0]) && recibido.length > 3) {
//        String numeroSerie = recibido[3];
//        if(datosAplicacion.getEquipo().getLuzWifi().equals("1")
//                && numeroSerie.equals(datosAplicacion.getEquipo().getNumeroSerie())
//                && !YACConfiguracion.comunicacionIniciada){
//            ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(activity.getApplicationContext());
//            programacionDataSource.open();
//            ArrayList<ProgramacionLuces> programaciones = programacionDataSource.getAllProgramacion();
//            programacionDataSource.close();
//            String programacionLista = " ";
//            for(ProgramacionLuces programacionLuces : programaciones){
//                programacionLista = programacionLista.trim() + programacionLuces.getId() + "," + programacionLuces.getIdRouter() + "," + programacionLuces.getIdZona() + ","
//                        + programacionLuces.getNombre() + "," + programacionLuces.getAccion() + "," + programacionLuces.getHoraInicio() + "," + programacionLuces.getDuracion()
//                        + "," + programacionLuces.getDias() + "#";
//            }
//            String resultado = YACSmartProperties.COM_SINCRONIZAR_PROGRAMACION + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO  + ";"+ recibido[1] + ";"+ recibido[2] + ";"+ recibido[3] + ";"
//                    + programacionLista + ";";
//
//            emitComando(resultado);
//        }
        }

    }
}