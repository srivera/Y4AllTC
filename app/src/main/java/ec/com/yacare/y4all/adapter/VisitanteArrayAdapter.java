package ec.com.yacare.y4all.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.EtiquetarFotoAsyncTask;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Visitante;
import ec.com.yacare.y4all.lib.util.ImageHelper;

public class VisitanteArrayAdapter extends ArrayAdapter<Visitante> {
    // The detected faces.
    private final ArrayList<Visitante> faces;

    private final Context context;

    // The thumbnails of detected faces.
    private List<Bitmap> faceThumbnails;

    private View convertViewP;

     public VisitanteArrayAdapter(Context context, ArrayList<Visitante> values) {
         super(context, R.layout.respuesta_list, values);
        faces = values;
        this.context = context;
        faceThumbnails = new ArrayList<>();

        if (values != null) {
            for (Visitante face : faces) {
                try {
                    // Crop face thumbnail with five main landmarks drawn from original image.

                    face.foto = ImageHelper.generateFaceThumbnail(
                            face.bitmap, face);
                    faceThumbnails.add(face.foto);
                } catch (IOException e) {
                    // Show the exception when generating face thumbnail fails.

                }
            }
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return faces.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.visitante_item, parent, false);
        }
        convertViewP = convertView;
        convertView.setId(position);

        // Show the face thumbnail.
        ((ImageView) convertView.findViewById(R.id.face_thumbnail)).setImageBitmap(
                faceThumbnails.get(position));
        final Visitante visitante = faces.get(position);
        ((ImageView) convertView.findViewById(R.id.face_thumbnail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                final AlertDialog d = new AlertDialog.Builder(context)
                        .setTitle("Etiquete la persona")
                        .setCancelable(true)
                        .setView(input)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (!input.getText().toString().equals("")) {
                                            visitante.setEtiqueta(input.getText().toString());
                                            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                                            visitante.foto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                            byte [] b=baos.toByteArray();
                                            String fotoString = Base64.encodeToString(b, Base64.DEFAULT);
                                            TextView etiqueta = ((TextView) convertViewP.findViewById(R.id.text_detected_face));
                                            etiqueta.setText(input.getText().toString());
                                            EtiquetarFotoAsyncTask etiquetarVisitanteActivity = new EtiquetarFotoAsyncTask(((DatosAplicacion)context.getApplicationContext()).getEquipoSeleccionado().getNumeroSerie(),
                                                    context, input.getText().toString(), fotoString);
                                            etiquetarVisitanteActivity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            dialog.cancel();
                                        }
                                    }
                                }).create();

                d.show();
            }
        });

        // Show the face details.
//        DecimalFormat formatter = new DecimalFormat("#0.0");
//        String face_description = "Age: " + formatter.format(faces.get(position).faceAttributes.age) + "\n"
//                + "Gender: " + faces.get(position).faceAttributes.gender + "\n"
//                + "Head pose(in degree): roll(" + formatter.format(faces.get(position).faceAttributes.headPose.roll) + "), "
//                + "yaw(" + formatter.format(faces.get(position).faceAttributes.headPose.yaw) + ")\n"
//                + "Moustache: " + formatter.format(faces.get(position).faceAttributes.facialHair.moustache) + "\n"
//                + "Smile: " + formatter.format(faces.get(position).faceAttributes.smile);
//        ((TextView) convertView.findViewById(R.id.text_detected_face)).setText( faces.get(position).etiqueta);
        TextView etiqueta = ((TextView) convertView.findViewById(R.id.text_detected_face));
        etiqueta.setText("Etiquetar");
        return convertView;
    }
}



