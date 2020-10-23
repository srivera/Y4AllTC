package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.MensajeTexto;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class MensajeTextoArrayAdapter extends ArrayAdapter<MensajeTexto> {
		private final Context context;
		private final ArrayList<MensajeTexto> values;
		private MonitorIOActivity monitorIOActivity;
		private String nombreDispositivo;

	  public MensajeTextoArrayAdapter(Context context, ArrayList<MensajeTexto> values, MonitorIOActivity monitorIOActivity, String nombreDispositivo) {
	    super(context, R.layout.respuesta_list, values);
	    this.context = context;
	    this.values = values;
	    this.monitorIOActivity = monitorIOActivity;
		this.nombreDispositivo = nombreDispositivo;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.li_mensaje_texto, parent, false);
	    final MensajeTexto mensajeTexto = values.get(position);
	    TextView textView = (TextView) rowView.findViewById(R.id.nombreMensaje);
	    textView.setText(mensajeTexto.getTexto());
		ImageButton fabMensajeM = (ImageButton) rowView.findViewById(R.id.fabMensajeM);
		fabMensajeM.setColorFilter(Color.GRAY);
		ImageButton fabMensajeH = (ImageButton) rowView.findViewById(R.id.fabMensajeH);
		fabMensajeH.setColorFilter(Color.GRAY);
		fabMensajeM.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  DatosAplicacion datosAplicacion = (DatosAplicacion) monitorIOActivity.getApplicationContext();
				  String datosConfT = YACSmartProperties.COM_REPRODUCIR_TEXTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";"
						  + mensajeTexto.getTexto() + ";" + mensajeTexto.getId() + ";" +  YACSmartProperties.VOZ_MUJER1 + ";";
				  if (AudioQueu.esComunicacionDirecta) {
					  EnviarComandoThread enviarComandoThread = new EnviarComandoThread(monitorIOActivity, datosConfT, null,
							  monitorIOActivity, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					  enviarComandoThread.start();
				  } else {
					  monitorIOActivity.attemptComando(datosConfT);
				  }

			  }
		  });
		  fabMensajeH.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  DatosAplicacion datosAplicacion = (DatosAplicacion) monitorIOActivity.getApplicationContext();
				  String datosConfT = YACSmartProperties.COM_REPRODUCIR_TEXTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";"
						  + mensajeTexto.getTexto() + ";" + mensajeTexto.getId() + ";" +  YACSmartProperties.VOZ_HOMBRE1 + ";";
				  if (AudioQueu.esComunicacionDirecta) {
					  EnviarComandoThread enviarComandoThread = new EnviarComandoThread(monitorIOActivity, datosConfT, null,
							  monitorIOActivity, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					  enviarComandoThread.start();
				  } else {
					  monitorIOActivity.attemptComando(datosConfT);
				  }

			  }
		  });
	    

	    return rowView;
	  }
	  
	  

}
