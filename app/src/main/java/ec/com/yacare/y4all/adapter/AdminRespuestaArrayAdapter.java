package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.respuesta.AdministrarRespuestasActivity;
import ec.com.yacare.y4all.lib.dto.Respuesta;
import ec.com.yacare.y4all.lib.enumer.TipoRespuestaEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class AdminRespuestaArrayAdapter extends ArrayAdapter<Respuesta> {
		private final Context context;
		private final ArrayList<Respuesta> values;
		private EditText nombreRespuesta;
		private Spinner tipoRespuesta;
		private  AdministrarRespuestasActivity administrarRespuestasActivity;

	  public AdminRespuestaArrayAdapter(Context context, ArrayList<Respuesta> values, AdministrarRespuestasActivity administrarRespuestasActivity) {
	    super(context, R.layout.respuesta_list, values);
	    this.context = context;
	    this.values = values;
	    this.administrarRespuestasActivity = administrarRespuestasActivity;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.li_respuesta, parent, false);
	    final Respuesta respuesta = values.get(position);
	    TextView textView = (TextView) rowView.findViewById(R.id.nombreRespuesta);
	    textView.setText(respuesta.getNombre());
	    
	    TextView tipo = (TextView) rowView.findViewById(R.id.tipoRespuesta);
	  
	    String tipoVoz = YACSmartProperties.getInstance().getMessageForKey("tipo.voz.hombre.descripcion");
	    if(respuesta.getTipoVoz().equals(YACSmartProperties.getInstance().getMessageForKey("tipo.voz.mujer"))){
	    	tipoVoz = YACSmartProperties.getInstance().getMessageForKey("tipo.voz.mujer.descripcion");
	    }
	    
		if(respuesta.getTipo().equals(TipoRespuestaEnum.TR02.getCodigo())){
	    	tipo.setText(TipoRespuestaEnum.TR02.getDescripcion() + " - " + tipoVoz);
		}else if(respuesta.getTipo().equals(TipoRespuestaEnum.TR03.getCodigo())){
	    	tipo.setText(TipoRespuestaEnum.TR03.getDescripcion() + " - " + tipoVoz);
		}else{
	    	tipo.setText(TipoRespuestaEnum.TR04.getDescripcion() + " - " + tipoVoz);
		}
	    
//	    respuesta.setEditar((Button) rowView.findViewById(R.id.btneditar));
//	    respuesta.setReproducir((Button) rowView.findViewById(R.id.btnreproducir));
//	    respuesta.setBorrar((Button) rowView.findViewById(R.id.btnborrar));
//	    textView.setTag(respuesta);
//
//	    respuesta.getEditar().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//    			List<String> tipos;
//    			ArrayAdapter<String> adapterTipos;
//            	nombreRespuesta = new EditText(administrarRespuestasActivity.getActivity());
//            	nombreRespuesta.setHint("Ingrese el nombre de la respuesta");
//            	nombreRespuesta.setText(respuesta.getNombre());
//
//            	tipoRespuesta = new Spinner(administrarRespuestasActivity.getActivity());
//            	tipos = new ArrayList<String>();
//
//            	tipos.add(TipoRespuestaEnum.TR02.getDescripcion());
//            	tipos.add(TipoRespuestaEnum.TR03.getDescripcion());
//            	tipos.add(TipoRespuestaEnum.TR04.getDescripcion());
//
//            	adapterTipos = new ArrayAdapter<String>(administrarRespuestasActivity.getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, tipos);
//            	adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            	tipoRespuesta.setAdapter(adapterTipos);
//
//             	int pos = adapterTipos.getPosition(respuesta.getTipo());
//            	tipoRespuesta.setSelection(pos);
//
//                TableLayout ll = new TableLayout(administrarRespuestasActivity.getActivity());
//                ll.addView(nombreRespuesta,  new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//                ll.addView(tipoRespuesta,  new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//
//    			new AlertDialog.Builder(administrarRespuestasActivity.getActivity())
//    			.setTitle(YACSmartProperties.intance.getMessageForKey("editar.respuesta"))
//    			.setView(ll)
//    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//    				public void onClick(DialogInterface dialog, int whichButton) {
//    					String tipo = tipoRespuesta.getItemAtPosition(tipoRespuesta.getSelectedItemPosition()).toString();
//    					if(tipo.equals(TipoRespuestaEnum.TR02.getDescripcion())){
//    						respuesta.setTipo(TipoRespuestaEnum.TR02.getCodigo());
//    					}else if(tipo.equals(TipoRespuestaEnum.TR03.getDescripcion())){
//    						respuesta.setTipo(TipoRespuestaEnum.TR03.getCodigo());
//
//    					}else{
//    						respuesta.setTipo(TipoRespuestaEnum.TR04.getCodigo());
//    					}
//    					respuesta.setNombre(nombreRespuesta.getText().toString());
////						administrarRespuestasActivity.getDatasource().updateRespuesta(respuesta);
////						administrarRespuestasActivity.getAdapter().notifyDataSetChanged();
//
//    				}
//    			}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//    				public void onClick(DialogInterface dialog, int whichButton) {
//
//    				}
//    			}).show();
//
//            }
//        });
	    
	    
	    respuesta.getBorrar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				administrarRespuestasActivity.setRespuestaGrabada(respuesta);
//				DatosAplicacion datosAplicacion = (DatosAplicacion) administrarRespuestasActivity.getActivity().getApplicationContext();
//				EnviarComandoThread genericoAsyncTask = new EnviarComandoThread(administrarRespuestasActivity.getActivity().getApplicationContext(),
//						YACSmartProperties.COM_ELIMINAR_RESPUESTA +  ";" + respuesta.getId(),
//						null, null, administrarRespuestasActivity,
//						datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO);
//				genericoAsyncTask.start();

            }
        });
	    
	    respuesta.getReproducir().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				DatosAplicacion datosAplicacion = (DatosAplicacion) administrarRespuestasActivity.getActivity().getApplicationContext();
//            	administrarRespuestasActivity.setRespuestaGrabada(new Respuesta());
//            	administrarRespuestasActivity.getRespuestaGrabada().setId(respuesta.getId());
//				ComandoGenericoScheduledTask genericoAsyncTask = new ComandoGenericoScheduledTask(administrarRespuestasActivity.getActivity().getApplicationContext(), YACSmartProperties.COM_REPRODUCIR_RESPUESTA +  ";" + respuesta.getId(), null, null, null,
//						datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO);
//				genericoAsyncTask.start();
            }
        });
	    return rowView;
	  }
	  
	  

}
