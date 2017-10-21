package ec.com.yacare.y4all.lib.asynctask.hotspot;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecibirComandoHotSpotAsyncTask extends AsyncTask<String, Float, Boolean> {

	private AgregarEquipoFragment activity;

	public RecibirComandoHotSpotAsyncTask(AgregarEquipoFragment activity) {
		this.activity = activity;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		MulticastSocket socketGrupo = null;

		try {
			socketGrupo = new MulticastSocket(YACSmartProperties.PUERTO_COMANDO_REMOTO_MULTICAST_INSTALACION);
			socketGrupo.joinGroup(InetAddress.getByName(YACSmartProperties.GRUPO_COMANDO_REMOTO_MULTICAST_INSTALACION));
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (AudioQueu.falloHotSpot && AudioQueu.verificarHotSpot) {
			byte[] receiveData = new byte[512];

			try {

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				socketGrupo.setSoTimeout(120000);
				socketGrupo.receive(receivePacket);

				String mensaje[] = (new String(receiveData)).split(";");
				Log.d("MENSAJE", "MENSAJE" + mensaje);

				if (mensaje[0].equals(YACSmartProperties.HOTSPOT_CONEXION_OK)) {
					DatagramPacket sendPacketDestino = new DatagramPacket(
							receiveData,
							receiveData.length,
							receivePacket.getAddress(),
							receivePacket.getPort());
					socketGrupo.send(sendPacketDestino);
					EquipoDataSource dataSource = new EquipoDataSource(activity.getActivity().getApplicationContext());
					dataSource.open();
					Equipo equipoBusqueda = new Equipo();
					equipoBusqueda.setNumeroSerie(mensaje[1]);
					equipoBusqueda = dataSource.getEquipoNumSerie(equipoBusqueda);
					equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
					equipoBusqueda.setIpLocal(mensaje[2]);
					dataSource.updateEquipo(equipoBusqueda);
					dataSource.close();

					AudioQueu.falloHotSpot = false;
					socketGrupo.close();
					return true;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				AudioQueu.verificarHotSpot = false;
				socketGrupo.close();
				return false;
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	@Override
	protected void onPostExecute(Boolean resultado) {
		super.onPostExecute(resultado);
//		activity.mostrarRespuestaConexion(resultado);
	}
}