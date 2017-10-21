package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Respuesta;

public class SeleccionarRespuestaAdapter extends ArrayAdapter<Respuesta> {
		private final Context context;
		private final ArrayList<Respuesta> values;


	  public SeleccionarRespuestaAdapter(Context context,  ArrayList<Respuesta> values) {
	    super(context, R.layout.seleccionar_respuesta_item, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.seleccionar_respuesta_item, parent, false);
	    final Respuesta respuesta = values.get(position);
	    TextView textView = (TextView) rowView.findViewById(R.id.nombreRespuesta);
	    textView.setText(respuesta.getNombre());
	    textView.setTag(respuesta.getId());

		  if(respuesta.getEsSeleccionado() != null  && respuesta.getEsSeleccionado()){
			  textView.setTextSize(20);
		  }
	    return rowView;
	  }
	  
	  

}
