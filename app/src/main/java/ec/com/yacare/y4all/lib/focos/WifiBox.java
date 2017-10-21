package ec.com.yacare.y4all.lib.focos;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.xlink.wifi.pipe.manage.CmdManage;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.SendPipeListener;


public class WifiBox {
    public static void main(String[] args) {
        try {
            WifiBox wiFiBox = new WifiBox(InetAddress.getByName("255.255.255.255"));
            wiFiBox.offBox("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * The address of the WiFi box
     */
    private static InetAddress address;

    /**
     * The port of the WiFi box
     */
    private static int port;

    private Context context;
    /**
     * The default port for unconfigured boxes.
     */
    public static final int DEFAULT_PORT = 5987;
    public static byte[] CABECERA = { (byte) 0x80, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x11};

    public static byte[] CONEXION = { (byte) 0x20, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x16, (byte) 0x02,
            (byte) 0x62, (byte) 0x3A, (byte) 0xD5,
            (byte) 0xED, (byte) 0xA3, (byte) 0x01,
            (byte) 0xAE, (byte) 0x08, (byte) 0x2D,
            (byte) 0x46, (byte) 0x61, (byte) 0x41,
            (byte) 0xA7, (byte) 0xF6, (byte) 0xDC,
            (byte) 0xAF, (byte) 0xE9, (byte) 0x22,
            (byte) 0x00, (byte) 0x00, (byte) 0x64};
        public static byte[] RESETEAR = { (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x02};
    public static byte[] RESETEAR_WW = { (byte) 0x30, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x03};
    public static byte[] SECUENCIA = { (byte) 0x7b, (byte) 0x80, (byte) 0x00};
    public static byte[] TRAMA_ENCENDER = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_ENCENDER_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x01, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_ENCENDER_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x04,
            (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_ENCENDER_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x04,
            0x01, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_ENCENDER_6W = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x05, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_ENCENDER_6W_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x05, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_ENCENDER_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_ENCENDER_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x03,
            0x03, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_NIGHT = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x06, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_NIGHT_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x06, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_APAGAR = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_APAGAR_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x02, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_APAGAR_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x04,
            (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_APAGAR_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x04,
            0x02, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_APAGAR_6W = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_APAGAR_6W_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x02, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_APAGAR_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_APAGAR_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x03,
            0x04, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_WHITE = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x05, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_WHITE_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x05, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_WHITE_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x05,
            (byte) 0x64, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_WHITE_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x05,
            0x64, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_WHITE_CALID_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x05,
            (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_WHITE_CALID_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x05,
            0x00, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_WHITE_6W = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x05,
            (byte) 0x64, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_WHITE_6W_INT = { 0x31, 0x00,
            0x00, 0x08, 0x05,
            0x64, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_WHITE_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x05, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_WHITE_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x03,
            0x05, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_NIGHT_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x04,
            (byte) 0x05, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_NIGHT_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x04,
            0x05, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_COLOR = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x01};
    public static Integer[] TRAMA_COLOR_INT = { 0x31, 0x00,
            0x00, 0x07, 0x01};
    public static byte[] TRAMA_COLOR_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x01};
    public static Integer[] TRAMA_COLOR_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x01};
    public static byte[] TRAMA_COLOR_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01};
    public static Integer[] TRAMA_COLOR_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x01};
    public static byte[] TRAMA_BRIGHTNESS = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x02};
    public static Integer[] TRAMA_BRIGHTNESS_INT = { 0x31, 0x00,
            0x00, 0x07, 0x02};
    public static byte[] TRAMA_BRIGHTNESS_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x03};
    public static Integer[] TRAMA_BRIGHTNESS_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x03};
    public static byte[] TRAMA_BRIGHTNESS_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02};
    public static Integer[] TRAMA_BRIGHTNESS_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x02};

    public static byte[] TRAMA_SATURATION = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x02};
    public static Integer[] TRAMA_SATURATION_INT = { 0x31, 0x00,
            0x00, 0x08, 0x02};

    public static byte[] TRAMA_MODE = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x04};
    public static Integer[] TRAMA_MODE_INT = { 0x31, 0x00,
            0x00, 0x07, 0x04};
    public static byte[] TRAMA_MODE_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x06};
    public static Integer[] TRAMA_MODE_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x06};
    public static byte[] TRAMA_MODE_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x04};
    public static Integer[] TRAMA_MODE_BOX_INT = { 0x31, 0x00,
            0x00, 0x00, 0x04};
    public static byte[] TRAMA_DISCO_FASTER = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_FASTER_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x03, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_DISCO_FASTER_WW = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x04,
            (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_FASTER_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x04,
            0x03, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_DISCO_FASTER_BOX = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_FASTER_BOX_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x03, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_DISCO_SLOWLER  = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_SLOWLER_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x04, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_DISCO_SLOWLER_WW  = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x04,
            (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_SLOWLER_WW_INT = { 0x31, 0x00,
            0x00, 0x08, 0x04,
            0x04, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_DISCO_SLOWLER_BOX  = {(byte) 0x31, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x03,
            (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_DISCO_SLOWLER_BOX_INT = { 0x31, 0x00,
            0x00, 0x07, 0x03,
            0x04, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_UNLINK = {(byte) 0x3E, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_UNLINK_INT = {0x3E, 0x00,
            0x00, 0x07, 0x00,
            0x00, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_UNLINK_WW = {(byte) 0x3E, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_UNLINK_WW_INT = {0x3E, 0x00,
            0x00, 0x08, 0x00,
            0x00, 0x00, 0x00,
            0x00};
    public static byte[] TRAMA_UNLINK_DICROICO = {(byte) 0xCF, (byte) 0x00,
            (byte) 0x7D, (byte) 0x03, (byte) 0x00,
            (byte) 0x3E, (byte) 0x00, (byte) 0x00,
            (byte) 0x07};
    public static Integer[] TRAMA_UNLINK_DICROICO_INT = {0xCF, 0x00,
            0x7D, 0x03, 0x00,
            0x3E, 0x00, 0x00,
            0x07};
    public static byte[] TRAMA_LINK = {(byte) 0x3D, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_LINK_INT = {0x3D, 0x00,
            0x00, 0x07, 0x00,
            0x00, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_LINK_DICROICO = {(byte) 0x29, (byte) 0x00,
            (byte) 0x7C, (byte) 0xFB, (byte) 0x00,
            (byte) 0x33, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_LINK_DICROICO_INT = {0x29, 0x00,
            0x7C, 0xFB, 0x00,
            0x33, 0x00, 0x00,
            0x00};

    public static byte[] TRAMA_LINK_WW = {(byte) 0x3D, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    public static Integer[] TRAMA_LINK_WW_INT = {0x3D, 0x00,
            0x00, 0x08, 0x00,
            0x00, 0x00, 0x00,
            0x00};
    public static final int GROUP_ALL = 0x00;
    public static final int GROUP_1 = 0x01;
    public static final int GROUP_2 = 0x02;
    public static final int GROUP_3 = 0x03;
    public static final int GROUP_4 = 0x04;
    /**
     * The sleep time between both messages for switching lights to the white
     * mode.
     */
    public static final int DEFAULT_SLEEP_BETWEEN_MESSAGES = 100;

    /**
     * The command code for "DISCO SPEED FASTER".
     */
    public static final int COMMAND_DISCO_FASTER = 0x44;

    /**
     * The command code for "DISCO SPEED SLOWER".
     */
    public static final int COMMAND_DISCO_SLOWER = 0x43;

    /**
     * The maximum color value, starting at 0.
     */
    public static final int MAX_COLOR = 0xFF;

    /**
     * The maximum brightness value, starting at 0.
     */
    public static final int MAX_BRIGHTNESS = 0x64;

    /**
     * A constructor creating a new instance of the WiFi box class.
     *
     * @param address
     *            is the address of the WiFi box
     * @param port
     *            is the port of the WiFi box (omit this if unsure)
     */
    public WifiBox(InetAddress address, int port) {
        super();
        this.address = address;
        this.port = port;
    }

    /**
     * A constructor creating a new instance of the WiFi box class using the
     * default port number.
     *
     * @param address
     *            is the address of the WiFi box
     */
    public WifiBox(InetAddress address) {
        this(address, DEFAULT_PORT);
    }


    public WifiBox(Context context) {
       // this( InetAddress.getByName("255.255.255.255"), DEFAULT_PORT);
        this.context = context;
    }
    public WifiBox() throws UnknownHostException {
       // this( InetAddress.getByName("255.255.255.255"), DEFAULT_PORT);
        port = DEFAULT_PORT;
    }
    /**
     * A constructor creating a new instance of the WiFi box class. The address
     * is resolved from a hostname or ip address.
     *
     * @param host
     *            is the host given as hostname such as "domain.tld" or string
     *            repesentation of an ip address
     * @param port
     *            is the port of the WiFi box (omit this if unsure)
     * @throws UnknownHostException
     *             if the hostname could not be resolved
     */
    public WifiBox(String host, int port) throws UnknownHostException {
        this(InetAddress.getByName(host), port);
    }

    /**
     * A constructor creating a new instance of the WiFi box class using the
     * default port number. The address is resolved from a hostname or ip
     * address.
     *
     * @param host
     *            is the host given as hostname such as "domain.tld" or string
     *            repesentation of an ip address
     * @throws UnknownHostException
     *             if the hostname could not be resolved
     */
    public WifiBox(String host) throws UnknownHostException {
        this(host, DEFAULT_PORT);
    }

//    public static String IP_ROUTER_FOCOS = "10.10.100.254";

    public static String IP_ROUTER_FOCOS = "255.255.255.255";

    public String configurarRouter(String nombreWifi, String claveWifi) throws IOException {
        String resultadoConfigurar = "";
        String routerEncontrado = "";

        byte[] datosRecibir2 = new byte[1024];
        DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                datosRecibir2.length);

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);
        DatagramPacket packet2 = new DatagramPacket(("HF-A11ASSISTHREAD").getBytes(), ("HF-A11ASSISTHREAD").getBytes().length,  InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
        for (int i =0; i < 20; i++){
            datagramSocket.send(packet2);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Date hora = new Date();
        while(((new Date()).getTime() - hora.getTime()) < 1000){
            try {
                datagramSocket.setSoTimeout(10);
                datagramSocket.receive(receivePacket2);
                routerEncontrado = new String(receivePacket2.getData());
                System.out.println(routerEncontrado);
                break;
            } catch (SocketTimeoutException e) {
                continue;
            }
        }

        if(!routerEncontrado.equals("")){
            //Start admin
            DatagramPacket packet4 = new DatagramPacket("+ok".getBytes(), "+ok".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
            datagramSocket.send(packet4);

            //Set wifi
            String comandoWifi = "AT+WSSSID=" + nombreWifi;
            DatagramPacket packet3 = new DatagramPacket(comandoWifi.getBytes(), comandoWifi.getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);

            byte[] datosRecibir3 = new byte[1024];

            DatagramPacket receivePacket3 = new DatagramPacket(datosRecibir3,
                    datosRecibir3.length);

            datagramSocket.send(packet3);
            datagramSocket.setSoTimeout(30000);
            datagramSocket.receive(receivePacket3);

            System.out.println(new String(receivePacket3.getData()));

            //Set clave wifi
            comandoWifi = "AT+WSKEY=WPA2PSK,AES," + claveWifi + "\r";
            DatagramPacket packet5 = new DatagramPacket(comandoWifi.getBytes(), comandoWifi.getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
            byte[] datosRecibir5 = new byte[1024];
            DatagramPacket receivePacket5 = new DatagramPacket(datosRecibir5,
                    datosRecibir5.length);
            datagramSocket.send(packet5);
            datagramSocket.setSoTimeout(30000);
            datagramSocket.receive(receivePacket5);
            System.out.println(new String(receivePacket5.getData()));

            //Cambiar modo a STA
            DatagramPacket packet7 = new DatagramPacket("AT+WMODE=STA\r".getBytes(), "AT+WMODE=STA\n".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
            datagramSocket.send(packet7);

            //Reiniciar
            DatagramPacket packet8 = new DatagramPacket("AT+Z\r".getBytes(), "AT+Z\n".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
            datagramSocket.send(packet8);

            //Cerrar session
            DatagramPacket packet6 = new DatagramPacket("AT+Q\r".getBytes(), "AT+Q\r".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
            datagramSocket.send(packet6);

            //

        }else{
            return "ERR,Verifique que su celular este conectado a la red de su equipo o que su equipo este encendido";
        }



        return "OK," + routerEncontrado;
    }


    public String buscarRouter(Context context1) throws IOException {

        HashMap<String, String> routers = new HashMap<String, String>();
        String resultado =  "";
        try {
            byte[] datosRecibir2 = new byte[1024];
            DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                    datosRecibir2.length);

            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket packet2 = new DatagramPacket(("HF-A11ASSISTHREAD").getBytes(), ("HF-A11ASSISTHREAD").getBytes().length, InetAddress.getByName("255.255.255.255"), 48899);
            for (int i =0; i < 20; i++){
                datagramSocket.send(packet2);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Date hora = new Date();
            while(((new Date()).getTime() - hora.getTime()) < 1000){
                try {
                    datagramSocket.setSoTimeout(5);
                    datagramSocket.receive(receivePacket2);
                    routers.put((new String(receivePacket2.getData()).trim()), new String( receivePacket2.getData()).trim());
                } catch (SocketTimeoutException e) {
                    continue;
                }
            }
            datagramSocket.close();
            EquipoDataSource routerDataSource = new EquipoDataSource(context1);
            routerDataSource.open();
            Iterator it = routers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String[] valores = pair.getValue().toString().split(",");
                Equipo equipoBusqueda = new Equipo();
                equipoBusqueda.setNumeroSerie(valores[1]);
                Equipo routerLuces = routerDataSource.getEquipoNumSerie(equipoBusqueda);

                if(routerLuces != null){
                    routerLuces.setIpLocal(valores[0]);
                    routerDataSource.updateEquipo(routerLuces);
                    resultado = resultado + pair.getValue() + "," +  routerLuces.getNombreEquipo() + "," + routerLuces.getEstadoEquipo() + "#";
                    AudioQueu.imacRouter.put(valores[1], valores[0]);
                }else{
                    resultado = resultado + pair.getValue() + "#";
                }
                it.remove();
            }
            routerDataSource.close();
            System.out.println(resultado);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return resultado;
    }

    private SendPipeListener pipeListener = new SendPipeListener() {

        @Override
        public void onSendLocalPipeData(XDevice device, int code, int messageId) {
            // setDeviceStatus(false);
            switch (code) {
                case XlinkCode.SUCCEED:
                    break;
                case XlinkCode.TIMEOUT:
                    XlinkUtils.shortTips("Timeout");

                    break;
                case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                    XlinkUtils.shortTips("codigo de servidor no autorizado");
                  //  connectDevice();
                    break;
                case XlinkCode.SERVER_DEVICE_OFFLIEN:
                    XlinkUtils.shortTips("server device offline");
                    break;
                default:
                    XlinkUtils.shortTips("Otro error:" + code);
                    break;
            }

        }
    };

    public boolean sendData(final byte[] bs, String name) {
//		if (!isOnline) {
//			connectDevice();
//			return false;
//		}

        DatosAplicacion datosAplicacion = (DatosAplicacion) context;
        int ret = XlinkAgent.getInstance().sendPipeData(datosAplicacion.getEquipoSeleccionado().getDevice().getXDevice(), bs, pipeListener);
        if (ret < 0) {
            switch (ret) {
                case XlinkCode.NO_CONNECT_SERVER:
                    XlinkUtils.shortTips("No se conecto al servidor");
                    break;
                case XlinkCode.NETWORD_UNAVAILABLE:
                    XlinkUtils.shortTips("red no disponible");
                    break;
                case XlinkCode.NO_DEVICE:
                    XlinkUtils.shortTips("no encontro device");
                    XlinkAgent.getInstance().initDevice(datosAplicacion.getEquipoSeleccionado().getDevice().getXDevice());
                    break;
                default:
                    XlinkUtils.shortTips("Otro error：" + ret);
                    break;
            }

            return false;
        } else {
//			if (name != null) {
//				Log("发送数据,msgId:" + ret + " data:(" + name + ")"
//						+ bytesToHex(bs));
//				if(name.equals("开")) {
//					CmdManage.sesionFocos = new byte[2];
//					CmdManage.sesionFocos[0] = bs[19];
//					CmdManage.sesionFocos[1] = bs[20];
//				}
//			} else {
//				Log("发送数据,msgId:" + ret + " data:"
//						+ bytesToHex(bs));
//			}
        }
        return true;
    }


    public Boolean abrirSesionFocos(String imac) throws IOException {
        buscarRouterIp(imac.trim());
        if(AudioQueu.socketFocos != null && AudioQueu.socketFocos.isConnected()){
            AudioQueu.socketFocos.close();
        }
        address = InetAddress.getByName(AudioQueu.imacRouter.get(imac.trim()));
        DatagramPacket packet2 = new DatagramPacket(CONEXION, CONEXION.length, address, port);
        try {
            AudioQueu.socketFocos = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        byte[] datosRecibir2 = new byte[1024];
        System.out.println("CONEXION " + CONEXION);
        DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                datosRecibir2.length);
        for (int i =0; i < 5; i++){
            try{
                AudioQueu.socketFocos.send(packet2);
                System.out.println("ENVIADO: " + bytesToHex(packet2.getData()));
                AudioQueu.socketFocos.setSoTimeout(1000);
                AudioQueu.socketFocos.receive(receivePacket2);
    System.out.println("RECIBIDO: " + bytesToHex(receivePacket2.getData()));
                AudioQueu.sesionFocos = new byte[2];
                AudioQueu.sesionFocos[0] = datosRecibir2[19];
                AudioQueu.sesionFocos[1] = datosRecibir2[20];
                byte[] ultimo = {(byte) 0x00  };
                byte[] tramaReset = concat(RESETEAR, AudioQueu.sesionFocos, ultimo );
                DatagramPacket packet = new DatagramPacket(tramaReset, tramaReset.length,
                        address, port);
                AudioQueu.socketFocos.send(packet);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                tramaReset = concat(RESETEAR_WW, AudioQueu.sesionFocos, ultimo );
                packet = new DatagramPacket(tramaReset, tramaReset.length,
                        address, port);
                AudioQueu.socketFocos.send(packet);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return true;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String buscarRouterIp(String imac) throws IOException {

        HashMap<String, String> routers = new HashMap<String, String>();
        String resultado =  "";
        try {
            address = InetAddress.getByName(AudioQueu.imacRouter.get(imac));
            byte[] datosRecibir2 = new byte[1024];
            DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                    datosRecibir2.length);

            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket packet2 = new DatagramPacket(("HF-A11ASSISTHREAD").getBytes(), ("HF-A11ASSISTHREAD").getBytes().length, InetAddress.getByName("255.255.255.255"), 48899);
            for (int i =0; i < 20; i++){
                datagramSocket.send(packet2);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Date hora = new Date();
            while(((new Date()).getTime() - hora.getTime()) < 1000){
                try {
                    datagramSocket.setSoTimeout(5);
                    datagramSocket.receive(receivePacket2);
                    routers.put((new String(receivePacket2.getData()).trim()), new String( receivePacket2.getData()).trim());
                } catch (SocketTimeoutException e) {
                    continue;
                }
            }
            datagramSocket.close();
            Iterator it = routers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String[] valores = pair.getValue().toString().split(",");
                if(valores[1].equals(imac.trim())){
                    AudioQueu.imacRouter.put(valores[1].trim(), valores[0]);
                    break;
                }
                it.remove();
            }

        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return resultado;
    }

    private void sendMessage(byte[] trama, Integer[] sumar, int group, String imac) throws IOException {
//        ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                XlinkUtils.shortTips("antes del if");
//            }
//        });
        if (((DatosAplicacion)context).getEquipoSeleccionado().getLuzWifi().equals("WIFI")) {
//            ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    XlinkUtils.shortTips("send message wifi");
//                }
//            });
            Boolean abrirSesion = true;
            if (AudioQueu.socketFocos == null || !AudioQueu.socketFocos.isBound() || AudioQueu.sesionFocos == null) {

//                ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        XlinkUtils.shortTips("Abrio sesion focos");
//                    }
//                });
                abrirSesion = abrirSesionFocos(imac);
            }
            this.address = InetAddress.getByName(AudioQueu.imacRouter.get(imac));
            if (abrirSesion && AudioQueu.sesionFocos != null) {
                byte[] datosRecibir2 = new byte[1024];

                DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                        datosRecibir2.length);
                byte[] grupo = {(byte) group, (byte) 0x00, (byte) (sumHexa(sumar, group))};
                byte[] tramaCompleta = concat(CABECERA, AudioQueu.sesionFocos, SECUENCIA, trama, grupo);
                DatagramPacket packet = new DatagramPacket(tramaCompleta, tramaCompleta.length,
                        address, port);
//                ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        XlinkUtils.shortTips("Antes del for");
//                    }
//                });
                for (int i = 0; i < 3; i++) {
                    try {
                        System.out.println("ENVIADO: " + bytesToHex(packet.getData()));
                        AudioQueu.socketFocos.send(packet);
                        AudioQueu.socketFocos.setSoTimeout(1000);
                        AudioQueu.socketFocos.receive(receivePacket2);
                        String hex = bytesToHex(receivePacket2.getData());
                        System.out.println("RECIBIDO: " + hex);

                        if (!hex.substring(15, 16).equals("0")) {
                            System.out.println("Error" + hex);
                            abrirSesion = abrirSesionFocos(imac);
                        } else {
                            System.out.println("ok" + hex);
                            break;
                        }
                        Log.d("FOCOS WIFI", "FOCOS WIFI" + i);

                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }else{
            //Internet
            byte[] grupo = {(byte) group, (byte) 0x00, (byte) (sumHexa(sumar, group))};
            byte[] tramaCompleta = concat(CABECERA, CmdManage.sesionFocos, SECUENCIA, trama, grupo);
            sendData(tramaCompleta, "comando");
        }
    }
    private void sendMessageColor(byte[] trama, Integer[] sumar, int group, int color, String imac) throws IOException {
        if (((DatosAplicacion)context).getEquipoSeleccionado().getLuzWifi().equals("WIFI")) {
            Boolean abrirSesion = true;
            if(AudioQueu.socketFocos == null || !AudioQueu.socketFocos.isBound() || AudioQueu.sesionFocos == null){
                abrirSesion = abrirSesionFocos(imac);
            }

            this.address =  InetAddress.getByName(AudioQueu.imacRouter.get(imac));
            Log.d("this.address", "IP "  + " "+ this.address);
            if(abrirSesion && AudioQueu.sesionFocos != null){
                byte[] datosRecibir2 = new byte[1024];

                DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                        datosRecibir2.length);
                Integer[] coloresI = {color,  color,  color, color  };
                byte[] grupo = {(byte)group, (byte) 0x00, (byte) (sumHexa(sumar,  coloresI, group )) };
                byte[] colores = {(byte)color, (byte) color, (byte) color, (byte) color  };
                byte[] tramaCompleta = concat(CABECERA, AudioQueu.sesionFocos, SECUENCIA, trama, colores, grupo );
                DatagramPacket packet = new DatagramPacket(tramaCompleta, tramaCompleta.length,address, port);
                System.out.println("ENVIADO: " + bytesToHex(packet.getData()));
                for (int i =0; i < 2; i++){
                    try{
                        AudioQueu.socketFocos.send(packet);
                        AudioQueu.socketFocos.setSoTimeout(1000);
                        AudioQueu.socketFocos.receive(receivePacket2);
                        Log.d("FOCOS WIFI", "FOCOS WIFI" + i);
                        String hex = bytesToHex(receivePacket2.getData());
                        // System.out.println("RECIBIDO: " + hex);

                        if(!hex.substring(15,16).equals("0")){
                            // System.out.println("Error" + hex );
                            abrirSesion = abrirSesionFocos(imac);
                        }else{
                            // System.out.println("ok" + hex );
                            break;
                        }

                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            //Internet

            Integer[] coloresI = {color,  color,  color, color  };
            byte[] grupo = {(byte)group, (byte) 0x00, (byte) (sumHexa(sumar,  coloresI, group )) };
            byte[] colores = {(byte)color, (byte) color, (byte) color, (byte) color  };
            byte[] tramaCompleta = concat(CABECERA, CmdManage.sesionFocos, SECUENCIA, trama, colores, grupo );
            sendData(tramaCompleta, "comando");
        }
    }
    private void sendMessageConfigs(byte[] trama, Integer[] sumar, int group, int config, String imac) throws IOException {
        if (((DatosAplicacion)context).getEquipoSeleccionado().getLuzWifi().equals("WIFI")) {
            Boolean abrirSesion = true;
            if(AudioQueu.socketFocos == null || !AudioQueu.socketFocos.isBound() || AudioQueu.sesionFocos == null){
                abrirSesion = abrirSesionFocos(imac);
            }
            this.address =  InetAddress.getByName(AudioQueu.imacRouter.get(imac));
            if(abrirSesion && AudioQueu.sesionFocos != null){
                byte[] datosRecibir2 = new byte[1024];
                DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
                        datosRecibir2.length);
                Integer[] coloresI = {config,  0x00,  0x00, 0x00  };
                byte[] grupo = {(byte)group, (byte) 0x00, (byte) (sumHexa(sumar,  coloresI, group )) };
                byte[] colores = {(byte)config, (byte) 0x00, (byte) 0x00, (byte) 0x00  };

                byte[] tramaCompleta = concat(CABECERA,  AudioQueu.sesionFocos, SECUENCIA, trama, colores, grupo );

                DatagramPacket packet = new DatagramPacket(tramaCompleta, tramaCompleta.length,
                        address, port);
                System.out.println("ENVIADO: " + bytesToHex(packet.getData()));
                for (int i =0; i < 2; i++){
                    try{
                        AudioQueu.socketFocos.send(packet);
                        AudioQueu.socketFocos.setSoTimeout(1000);
                        AudioQueu.socketFocos.receive(receivePacket2);
                        Log.d("FOCOS WIFI", "FOCOS WIFI" + i);
                        String hex = bytesToHex(receivePacket2.getData());
     System.out.println("RECIBIDO: " + hex);

                        if(!hex.substring(15,16).equals("0")){
     System.out.println("Error" + hex );
                            abrirSesion = abrirSesionFocos(imac);
                        }else{
     System.out.println("ok" + hex );
                            break;
                        }
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            //Internet
            Integer[] coloresI = {config,  0x00,  0x00, 0x00  };
            byte[] grupo = {(byte)group, (byte) 0x00, (byte) (sumHexa(sumar,  coloresI, group )) };
            byte[] colores = {(byte)config, (byte) 0x00, (byte) 0x00, (byte) 0x00  };
            byte[] tramaCompleta = concat(CABECERA,  CmdManage.sesionFocos, SECUENCIA, trama, colores, grupo );
            sendData(tramaCompleta, "comando");
        }
    }


    public void off(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_APAGAR_6W, TRAMA_APAGAR_6W_INT, group,imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_APAGAR, TRAMA_APAGAR_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_APAGAR_WW, TRAMA_APAGAR_WW_INT, group, imac);
    }

    public void offBox(String imac) throws IOException, IllegalArgumentException {
        sendMessage(TRAMA_APAGAR_BOX, TRAMA_APAGAR_BOX_INT, GROUP_ALL, imac);
    }


    public void on(String grupo, String imac) throws IOException, IllegalArgumentException {
//        ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                XlinkUtils.shortTips("Ingreso a .on");
//            }
//        });

        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_ENCENDER_6W, TRAMA_ENCENDER_6W_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);


    }

    public void on(int group, String imac) throws IOException, IllegalArgumentException {
        sendMessage(TRAMA_ENCENDER_6W, TRAMA_ENCENDER_6W_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);


    }

    public void blink(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_6W, TRAMA_ENCENDER_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_APAGAR, TRAMA_APAGAR_INT, group, imac);
        sendMessage(TRAMA_APAGAR_WW, TRAMA_APAGAR_WW_INT, group, imac);
        sendMessage(TRAMA_APAGAR_6W, TRAMA_APAGAR_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_6W, TRAMA_ENCENDER_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_APAGAR, TRAMA_APAGAR_INT, group, imac);
        sendMessage(TRAMA_APAGAR_WW, TRAMA_APAGAR_WW_INT, group, imac);
        sendMessage(TRAMA_APAGAR_6W, TRAMA_APAGAR_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);
        sendMessage(TRAMA_ENCENDER_6W, TRAMA_ENCENDER_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_APAGAR, TRAMA_APAGAR_INT, group, imac);
        sendMessage(TRAMA_APAGAR_WW, TRAMA_APAGAR_WW_INT, group, imac);
        sendMessage(TRAMA_APAGAR_6W, TRAMA_APAGAR_6W_INT, group, imac);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onBox(String imac) throws IOException, IllegalArgumentException {
        sendMessage(TRAMA_ENCENDER_BOX, TRAMA_ENCENDER_BOX_INT, GROUP_ALL, imac);
    }

    public void unlink(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_UNLINK_WW, TRAMA_UNLINK_WW_INT, group, imac);
        sendMessage(TRAMA_UNLINK, TRAMA_UNLINK_INT, group, imac);
        sendMessage(TRAMA_UNLINK_DICROICO, TRAMA_UNLINK_DICROICO_INT, group, imac);
    }
    public void link(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_LINK_WW, TRAMA_LINK_WW_INT, group, imac);
        sendMessage(TRAMA_LINK, TRAMA_LINK_INT, group, imac);
        sendMessage(TRAMA_LINK_DICROICO, TRAMA_LINK_DICROICO_INT, group, imac);
    }
    /**
     * Switch all lights in all groups to the white mode. Note that the messages
     * are sent in a new thread. Therefore, you should not send other commands
     * directly after executing this one. Also, there are no exceptions when
     * sending messages fails since they occur in another thread.
     */
    public void white(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_WHITE_WW, TRAMA_WHITE_WW_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
// abrirSesionFocos(imac);
        sendMessage(TRAMA_WHITE, TRAMA_WHITE_INT, group, imac);
    }

    public void whiteCalid(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
        sendMessage(TRAMA_ENCENDER_WW, TRAMA_ENCENDER_WW_INT, group, imac);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendMessage(TRAMA_WHITE_CALID_WW, TRAMA_WHITE_CALID_WW_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_ENCENDER, TRAMA_ENCENDER_INT, group, imac);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
// abrirSesionFocos(imac);
        sendMessage(TRAMA_WHITE_CALID_WW, TRAMA_WHITE_CALID_WW_INT, group, imac);
    }

    public void whiteBox(String imac) throws IOException, IllegalArgumentException {
        sendMessage(TRAMA_WHITE_BOX, TRAMA_WHITE_BOX_INT, GROUP_ALL, imac);
    }
    public void night(String grupo, String imac) throws IOException, IllegalArgumentException {
        int group = convertirGrupo(grupo);
//        on(group, imac);
        sendMessage(TRAMA_NIGHT, TRAMA_NIGHT_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_NIGHT_WW, TRAMA_NIGHT_WW_INT, group, imac);
    }

    /**
     * Increase the disco mode's speed for the active group of lights (the last
     * one that was switched on).
     *
     * @throws IOException
     *             if the message could not be sent
     */
    public void discoModeFaster(int group, String imac) throws IOException {
        sendMessage(TRAMA_DISCO_FASTER, TRAMA_DISCO_FASTER_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_DISCO_FASTER_WW, TRAMA_DISCO_FASTER_WW_INT, group, imac);
    }

    public void discoModeFasterBox(String imac) throws IOException {
        sendMessage(TRAMA_DISCO_FASTER_BOX, TRAMA_DISCO_FASTER_BOX_INT, GROUP_ALL, imac);
    }

    /**
     * Decrease the disco mode's speed for the active group of lights (the last
     * one that was switched on).
     *
     * @throws IOException
     *             if the message could not be sent
     */
    public void discoModeSlower(int group, String imac) throws IOException {
        sendMessage(TRAMA_DISCO_SLOWLER, TRAMA_DISCO_SLOWLER_INT, group, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage(TRAMA_DISCO_SLOWLER_WW, TRAMA_DISCO_SLOWLER_WW_INT, group, imac);
    }

    public void discoModeSlowerBox(String imac) throws IOException {
        sendMessage(TRAMA_DISCO_SLOWLER_BOX, TRAMA_DISCO_SLOWLER_BOX_INT, GROUP_ALL, imac);
    }

    /**
     * Set the brightness value for a given group of lights.
     *
     * @param group
     *            is the number of the group to set the brightness for
     * @param value
     *            is the brightness value to set (between 0 and
     *            WiFiBox.MAX_BRIGHTNESS)
     * @throws IOException
     *             if the message could not be sent
     * @throws IllegalArgumentException
     *             if group is not between 1 and 4 or the brightness value is
     *             not between 0 and WiFiBox.MAX_BRIGHTNESS
     */
    public void brightness( int value, int group, String imac) throws IOException,
            IllegalArgumentException {
        int valorHex;
        if(value >= 0 && value < 24){
            valorHex = 0x00;
        }else if(value > 24 && value < 49){
            valorHex = 0x32;
        }else if(value > 50 && value < 74){
            valorHex = 0x4B;
        }else{
            valorHex = 0x64;
        }
        if (value < 0 || value > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "The brightness value should be between 0 and WiFiBox.MAX_BRIGHTNESS");
        }

        sendMessageConfigs(TRAMA_BRIGHTNESS, TRAMA_BRIGHTNESS_INT, group, valorHex, imac);
        sendMessageConfigs(TRAMA_BRIGHTNESS_WW, TRAMA_BRIGHTNESS_WW_INT, group, valorHex, imac);
    }

    public void saturation( int value, int group, String imac) throws IOException,
            IllegalArgumentException {
        int valorHex;
        if(value >= 0 && value < 24){
            valorHex = 0x64;
        }else if(value > 24 && value < 49){
            valorHex = 0x4B;
        }else if(value > 50 && value < 74){
            valorHex = 0x32;
        }else{
            valorHex = 0x00;
        }
        if (value < 0 || value > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "The brightness value should be between 0 and WiFiBox.MAX_BRIGHTNESS");
        }

        sendMessageConfigs(TRAMA_SATURATION, TRAMA_SATURATION_INT, group, valorHex, imac);
    }
    public void brightnessBox( int value, String imac) throws IOException,
            IllegalArgumentException {
// check arguments
        if (value < 0 || value > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "The brightness value should be between 0 and WiFiBox.MAX_BRIGHTNESS");
        }
        sendMessageConfigs(TRAMA_BRIGHTNESS_BOX, TRAMA_BRIGHTNESS_BOX_INT, GROUP_ALL, value, imac);
    }
    public void mode( int value, int group, String imac) throws IOException,
            IllegalArgumentException {
        on(group, imac);
        sendMessageConfigs(TRAMA_MODE, TRAMA_MODE_INT, group, value, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessageConfigs(TRAMA_MODE_WW, TRAMA_MODE_WW_INT, group, value, imac);
    }
    public void modeBox( int value, String imac) throws IOException,
            IllegalArgumentException {
        sendMessageConfigs(TRAMA_MODE_BOX, TRAMA_MODE_BOX_INT, GROUP_ALL, value, imac);
    }
    /**
     * Set the color value for the currently active group of lights (the
     * last one that was switched on).
     *
     * @param value
     *            is the color value to set (between 0 and
     *            WiFiBox.MAX_COLOR)
     * @throws IOException
     *             if the message could not be sent
     * @throws IllegalArgumentException
     *             if the color value is not between 0 and
     *             WiFiBox.MAX_COLOR
     */
    public void color(int value, Integer group, String imac) throws IOException,
            IllegalArgumentException {
// check argument
        if (value < 0 || value > MAX_COLOR) {
            throw new IllegalArgumentException(
                    "The color value should be between 0 and WiFiBox.MAX_COLOR");
        }
//        on(group, imac);
// send message to the WiFi box
        sendMessageColor(TRAMA_COLOR, TRAMA_COLOR_INT, group, value, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessageColor(TRAMA_COLOR, TRAMA_COLOR_INT, group, value, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendMessageColor(TRAMA_COLOR, TRAMA_COLOR_INT, group, value, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessageColor(TRAMA_COLOR, TRAMA_COLOR_INT, group, value, imac);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessageColor(TRAMA_COLOR_WW, TRAMA_COLOR_WW_INT, group, value, imac);
    }
    public void colorBox(int value, String imac) throws IOException,
            IllegalArgumentException {
// check argument
        if (value < 0 || value > MAX_COLOR) {
            throw new IllegalArgumentException(
                    "The color value should be between 0 and WiFiBox.MAX_COLOR");
        }
// send message to the WiFi box
        sendMessageColor(TRAMA_COLOR_BOX, TRAMA_COLOR_BOX_INT, GROUP_ALL, value, imac);
    }
    public static int sumHexa(Integer[] numeros, Integer grupo){
        Integer resultado = 0;
        for(int i = 0; i < numeros.length; i++){
            resultado = resultado + numeros[i];
        }
        resultado = resultado + grupo;
        return resultado;
    }
    public static int sumHexa(Integer[] numeros, Integer[] colores, Integer grupo){
        Integer resultado = 0;
        for(int i = 0; i < numeros.length; i++){
            resultado = resultado + numeros[i];
        }
        for(int i = 0; i < colores.length; i++){
            resultado = resultado + colores[i];
        }
        resultado = resultado + grupo;
        return resultado;
    }


    static public byte[] concat(byte[]... bufs) {
        if (bufs.length == 0)
            return null;
        if (bufs.length == 1)
            return bufs[0];
        for (int i = 0; i < bufs.length - 1; i++) {
            byte[] res = Arrays.copyOf(bufs[i], bufs[i].length+bufs[i + 1].length);
            System.arraycopy(bufs[i + 1], 0, res, bufs[i].length, bufs[i + 1].length);
            bufs[i + 1] = res;
        }
        return bufs[bufs.length - 1];
    }
    public int convertirGrupo(String grupo)  {
//        ((DatosAplicacion)context).getCurrentActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                XlinkUtils.shortTips("Ingreso a convertirGrupo");
//            }
//        });
        int grupoHex = GROUP_ALL;
        if(grupo.equals("1")){
            grupoHex = GROUP_1;
        }else if(grupo.equals("2")){
            grupoHex = GROUP_2;
        }else if(grupo.equals("3")){
            grupoHex = GROUP_3;
        }else if(grupo.equals("4")){
            grupoHex = GROUP_4;
        }
        return grupoHex;
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}