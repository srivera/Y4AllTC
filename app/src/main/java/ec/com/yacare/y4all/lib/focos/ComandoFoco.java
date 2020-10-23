package ec.com.yacare.y4all.lib.focos;

import android.content.Context;
import android.util.Log;

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
        WifiBox WifiBox = new WifiBox(context);
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
        } else if (YACSmartProperties.COM_LUZ_MODE_BOX_WIFI_CONEXION.equals(recibido[0]) && recibido.length > 6) {
            try {
                Byte val = new Byte(recibido[5]);
                String a = String.format("%02X ", val);
                String bb = "0x" + a.trim();
                int hex = Integer.decode(bb);
                Log.d("estado focos", "Enviando comando ..");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WifiBox.modeBoxConexion(hex, recibido[6]);
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
               WifiBox.link(recibido[4], recibido[5], recibido[6]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (YACSmartProperties.COM_UNLINK_FOCOS.equals(recibido[0]) && recibido.length > 4) {
            try {
                WifiBox.unlink(recibido[4], recibido[5]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}