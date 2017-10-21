package ec.com.yacare.y4all.lib.asynctask.dispositivo;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;
import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class ComandoConfigurarScheduledTask extends AsyncTask<String, Float, String> {

    private String comando;
    private ValidarCuentaFragment validarCuentaFragment;
    private AgregarEquipoFragment agregarEquipoActivity;
    private  Equipo equipo;

    public ComandoConfigurarScheduledTask(String comando, ValidarCuentaFragment validarCuentaFragment, AgregarEquipoFragment agregarEquipoActivity) {
        super();
        this.comando = comando;
        this.validarCuentaFragment = validarCuentaFragment;
        this.agregarEquipoActivity = agregarEquipoActivity;
    }

    String comandoInicial;
    boolean vaciar = false;

    @Override
    protected String doInBackground(String... arg0) {

        try {
            DatosAplicacion datosAplicacion;


            if(validarCuentaFragment != null) {
                datosAplicacion = ((DatosAplicacion) validarCuentaFragment.getActivity().getApplicationContext());
                equipo = datosAplicacion.getEquipoSeleccionado();
            }else{
                equipo = agregarEquipoActivity.getEquipo();
            }


            DatagramSocket clientSocket = new DatagramSocket();


            //"codigo;id;tipo;nombre;totalpaquetes;"
            String datosConfS = comando + ";";

            byte[] datosConfB = datosConfS.getBytes();
            byte[] datosComando = new byte[512];

            System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

            DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
                    datosComando.length,  InetAddress.getByName(equipo.getIpLocal()),
                    YACSmartProperties.PUERTO_COMANDO_DEFECTO);
            clientSocket.send(sendPacketConf);
            clientSocket.send(sendPacketConf);
            clientSocket.send(sendPacketConf);
//            Log.d("comando / tamano", "comando  " + datosConfS + "/" + datosComando.length);
//
//            comandoInicial = comando.substring(0, 3);
//
//
//            if (comandoInicial.equals(YACSmartProperties.COM_CREAR_CUENTA) || comandoInicial.equals(YACSmartProperties.COM_FINALIZAR_CONFIGURACION) ) {
//                String resp = "ERR";
//                for(int i =0; i < 5; i++) {
//                    byte[] recibido = new byte[1024];
//                    try {
//                        clientSocket.send(sendPacketConf);
//
//                        DatagramPacket receivePacket = new DatagramPacket(recibido,
//                                recibido.length);
//                        clientSocket.setSoTimeout(10000);
//                        clientSocket.receive(receivePacket);
//
//                        String recibidoS[] = new String(receivePacket.getData()).split(";");
//                        Log.d("ComandoConfigurarScheduledTask",  new String(receivePacket.getData()));
//                        resp = "OK";
//                        break;
//                    } catch (SocketTimeoutException e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
                clientSocket.close();
                return "";

//            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void onPostExecute(String respuesta) {
//        Log.d("RESULTADO ComandoConfigurarScheduledTask",respuesta );
//        if(respuesta.equals("OK")){
//            if (validarCuentaFragment != null) {
//                //cambiar estado a instalado
//                FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(validarCuentaFragment, validarCuentaFragment.getEquipoSeleccionado(), null);
//                instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }else{
//                FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, equipo, agregarEquipoActivity);
//                instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//            }
//
//        }else{
//            if(validarCuentaFragment != null) {
//                validarCuentaFragment.reportarError(respuesta);
//            }
//        }

    }

    /**
     * Obtiene el array de bytes descomprimido a partir de otro array de bytes
     * comprimido
     *
     * @param file
     *            los datos comprimidos
     * @return los datos descomprimidos.
     * @throws IOException
     *             de vez en cuando
     */
//    public byte[] descomprimirGZIP(byte[] file, Integer paquete)  {
//        ByteArrayInputStream gzdata = new ByteArrayInputStream(file);
//        GZIPInputStream gunzipper;
//        try {
//            gunzipper = new GZIPInputStream(gzdata, file.length);
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            byte[] readed = new byte[paquete];
//            int actual = 1;
//            while ((actual = gunzipper.read(readed)) > 0) {
//                data.write(readed, 0, actual);
//
//            }
//            gzdata.close();
//            gunzipper.close();
//            byte[] returndata = data.toByteArray();
//            return returndata;
//        } catch (IOException e) {
//        }
//        return new byte[paquete];
//    }
}