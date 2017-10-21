package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.MensajeTexto;

public class MensajeTextoArrayAdapter extends ArrayAdapter<MensajeTexto> {
		private final Context context;
		private final ArrayList<MensajeTexto> values;
		private MonitorIOActivity monitorIOActivity;

	  public MensajeTextoArrayAdapter(Context context, ArrayList<MensajeTexto> values, MonitorIOActivity monitorIOActivity) {
	    super(context, R.layout.respuesta_list, values);
	    this.context = context;
	    this.values = values;
	    this.monitorIOActivity = monitorIOActivity;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.li_mensaje_texto, parent, false);
	    final MensajeTexto mensajeTexto = values.get(position);
	    TextView textView = (TextView) rowView.findViewById(R.id.nombreMensaje);
	    textView.setText(mensajeTexto.getTexto());
		if(mensajeTexto.getEsSeleccionado() != null  && mensajeTexto.getEsSeleccionado()){
			textView.setTextSize(20);
		}
	    

	    return rowView;
	  }
	  
	  

}
