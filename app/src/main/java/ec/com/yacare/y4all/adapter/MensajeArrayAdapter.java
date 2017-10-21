package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class MensajeArrayAdapter extends ArrayAdapter<Mensaje> {
    private final Context context;
    private final ArrayList<Mensaje> values;



    public MensajeArrayAdapter(Context context, ArrayList<Mensaje> values) {
        super(context, R.layout.mensaje_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        final Mensaje mensajeOffLine = (Mensaje) values.get(position);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf" );


        if(mensajeOffLine.getTipo() != null && mensajeOffLine.getTipo().equals(YACSmartProperties.COM_MENSAJE_VOZ)) {
            if(mensajeOffLine.getDireccion() != null && mensajeOffLine.getDireccion().equals("REC")) {
                rowView = inflater.inflate(R.layout.mensaje_voz_left_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.txtHora);
                textView.setText(mensajeOffLine.getHora());

                Button reproducir = (Button) rowView.findViewById(R.id.btnReproducirMensaje);
                reproducir.setTypeface(font);
                reproducir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File("/sdcard/" + mensajeOffLine.getId() + ".txt");
                        if(file.exists()) {
                            FileInputStream fileInputStream = null;
                            byte[] bFile = new byte[(int) file.length()];
                            try {
                                fileInputStream = new FileInputStream(file);
                                fileInputStream.read(bFile);
                                fileInputStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            AudioTrack track;
                            track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
                                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT, bFile.length,
                                    AudioTrack.MODE_STREAM);
                            track.setNotificationMarkerPosition(bFile.length / 2);
                            track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                                @Override
                                public void onPeriodicNotification(AudioTrack track) {
                                    // nothing to do
                                }

                                @Override
                                public void onMarkerReached(AudioTrack track) {
                                    track.stop();
                                    track.release();
                                }
                            });

                            track.play();
                            track.write(bFile, 0, bFile.length);
                        }
                    }
                });
            }else{
                rowView = inflater.inflate(R.layout.mensaje_voz_right_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.txtHora);
                textView.setText(mensajeOffLine.getHora());

                Button reproducir = (Button) rowView.findViewById(R.id.btnReproducirMensaje);
                reproducir.setTypeface(font);
                reproducir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File("/sdcard/" + mensajeOffLine.getId() + ".txt");
                        if(file.exists()) {
                            FileInputStream fileInputStream = null;
                            byte[] bFile = new byte[(int) file.length()];
                            try {
                                fileInputStream = new FileInputStream(file);
                                fileInputStream.read(bFile);
                                fileInputStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            AudioTrack track;
                            track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
                                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT, bFile.length,
                                    AudioTrack.MODE_STREAM);
                            track.setNotificationMarkerPosition(bFile.length / 2);
                            track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                                @Override
                                public void onPeriodicNotification(AudioTrack track) {
                                    // nothing to do
                                }

                                @Override
                                public void onMarkerReached(AudioTrack track) {
                                    track.stop();
                                    track.release();
                                }
                            });

                            track.play();
                            track.write(bFile, 0, bFile.length);
                        }
                    }
                });
            }


        }else{
            if(mensajeOffLine.getDireccion() != null && mensajeOffLine.getDireccion().equals("REC")) {
                rowView = inflater.inflate(R.layout.mensaje_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.txtMensaje);
                textView.setText(mensajeOffLine.getHora() + " " + mensajeOffLine.getMensaje());
                textView.setTag(mensajeOffLine);
            }else{
                rowView = inflater.inflate(R.layout.mensaje_right_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.txtMensaje);
                textView.setText(mensajeOffLine.getHora() + " " + mensajeOffLine.getMensaje());
                textView.setTag(mensajeOffLine);
            }
        }

//        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
//        Typeface fontBlack = Typeface.createFromAsset(context.getAssets(), "Lato-Black.ttf");
//        textView.setTypeface(fontBlack);
//        textView.setTypeface(fontRegular);

        return rowView;
    }


}
