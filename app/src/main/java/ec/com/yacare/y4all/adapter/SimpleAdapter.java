package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.evento.EventosActivity;
import ec.com.yacare.y4all.activities.evento.ImageSliderActivity;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private final Context mContext;
    private final List<Evento> mItems;
    private int mCurrentItemId = 0;
    private EventosActivity eventosActivity;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public TextView hora;
        public ImageView foto;
        public ImageView favorito;
        public ImageView check;


        public SimpleViewHolder(View view) {
            super(view);
            hora = (TextView) view.findViewById(R.id.hora);
            foto = (ImageView) view.findViewById(R.id.foto);
            favorito = (ImageView) view.findViewById(R.id.favorito);
            check = (ImageView) view.findViewById(R.id.check);
        }
    }

    public SimpleAdapter(Context context, List<Evento> mItems, EventosActivity eventosActivity) {
        mContext = context;
        this.mItems = mItems;
        this.eventosActivity = eventosActivity;
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.evento_list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Evento evento = (Evento)mItems.get(position);
//        holder.hora.setText(evento.getFecha().substring(11, 19) + evento.getTipoEvento());
        holder.hora.setText(evento.getId());
        File file = new File(evento.getFoto());

        if(evento.getEstado().equals("FAB")){
            holder.favorito.setVisibility(View.VISIBLE);
        }else {
            holder.favorito.setVisibility(View.GONE);
        }
        holder.favorito.bringToFront();


        if(file.exists()){
            Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+ evento.getId() + ".jpg");
            if(bmImg != null) {
                holder.foto.setImageBitmap(bmImg);
                holder.foto.setVisibility(View.VISIBLE);
                holder.foto.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.hora.setText("1");
                final Palette palette = Palette.from(bmImg).generate();

                final Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    holder.hora.setBackgroundColor(vibrant.getRgb());
                    holder.hora.setTextColor(vibrant.getTitleTextColor());
                    evento.setColorFondo(vibrant.getRgb());
                    evento.setColorLetra(vibrant.getTitleTextColor());
                } else {
                    final Palette.Swatch vibrant2 = palette.getLightVibrantSwatch();
                    if (vibrant2 != null) {
                        holder.hora.setBackgroundColor(vibrant2.getRgb());
                        holder.hora.setTextColor(vibrant2.getTitleTextColor());
                        evento.setColorFondo(vibrant2.getRgb());
                        evento.setColorLetra(vibrant2.getTitleTextColor());
                    } else {
                        final Palette.Swatch vibrant3 = palette.getDarkVibrantSwatch();
                        if (vibrant3 != null) {
                            holder.hora.setBackgroundColor(vibrant2.getRgb());
                            holder.hora.setTextColor(vibrant2.getTitleTextColor());
                            evento.setColorFondo(vibrant2.getRgb());
                            evento.setColorLetra(vibrant2.getTitleTextColor());
                        } else {
                            holder.hora.setBackgroundResource(R.color.colorsecundario400);
                            holder.hora.setTextColor(Color.WHITE);
                        }
                    }
                }
            }else{
                file.delete();
            }

        }else{
            mostrarImagenes(holder, evento);
        }
        holder.hora.setText(evento.getFecha().substring(11, 19));
            holder.foto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    eventosActivity.multipleSelection = true;
                    if(!evento.isSeleccionado()) {
                        evento.setSeleccionado(true);
                        holder.check.setVisibility(View.VISIBLE);
                        holder.check.bringToFront();
                        eventosActivity.eventoSeleccionadoCol.add(evento);
                        eventosActivity.fabEliminarSeleccion.setVisibility(View.VISIBLE);
                    }else{
                        evento.setSeleccionado(false);
                        eventosActivity.eventoSeleccionadoCol.remove(evento);
                        holder.check.setVisibility(View.GONE);
                        if(eventosActivity.eventoSeleccionadoCol.size() == 0){
                            eventosActivity.multipleSelection = false;
                            eventosActivity.fabEliminarSeleccion.setVisibility(View.GONE);
                        }
                    }
                    return false;
                }
            });

            holder.foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!eventosActivity.multipleSelection) {
                        if (evento.getTipoEvento().equals("TIMBRAR") || evento.getTipoEvento().equals("FOTO")) {
                            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
                            if (file.exists()) {
//                            Intent i = new Intent(eventosActivity, PlayVideoActivity.class);
                                Intent i = new Intent(eventosActivity, ImageSliderActivity.class);
                                i.putExtra("foto", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
                                i.putExtra("evento", evento);
                                i.putExtra("tabActivo", eventosActivity.tabActivo);
                                i.putExtra("eventos", eventosActivity.eventoCol);
                                i.putExtra("indice", String.valueOf(position));

                                String transitionName = eventosActivity.getString(R.string.transition_album_cover);
                                ActivityOptionsCompat options =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(eventosActivity,
                                                holder.foto,   // The view which starts the transition
                                                transitionName    // The transitionName of the view we’re transitioning to
                                        );

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA, options.toBundle());
                                }else{
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA);
                                }
                            } else {
                                RotateAnimation rotate = new RotateAnimation(0, 360,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                        0.5f);

                                rotate.setDuration(2000);
                                rotate.setRepeatCount(Animation.INFINITE);
                                holder.foto.setImageResource(R.drawable.refresh);
                                holder.foto.setEnabled(false);
                                holder.foto.setAnimation(rotate);
                                DatosAplicacion datosAplicacion = (DatosAplicacion) mContext.getApplicationContext();
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(eventosActivity.getApplicationContext());
                                String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
                                AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_FOTO_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";");
                                AudioQueu.contadorComandoEnviado++;
                                Toast.makeText(eventosActivity.getApplicationContext(), YACSmartProperties.intance.getMessageForKey("cargando.archivos.foto"), Toast.LENGTH_LONG).show();
                            }
                        } else if (evento.getTipoEvento().equals("BUZON")) {
                            final File fileVideo = new File(evento.getVideoBuzon());
                            if (fileVideo.exists()) {
//                            Intent i = new Intent(eventosActivity, PlayVideoActivity.class);
                                Intent i = new Intent(eventosActivity, ImageSliderActivity.class);
                                i.putExtra("videoBuzon", evento.getVideoBuzon());
                                i.putExtra("indice", String.valueOf(position));
                                i.putExtra("tabActivo", eventosActivity.tabActivo);
                                i.putExtra("evento", evento);
                                i.putExtra("eventos", eventosActivity.eventoCol);
                                String transitionName = eventosActivity.getString(R.string.transition_album_cover);
                                ActivityOptionsCompat options =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(eventosActivity,
                                                holder.foto,   // The view which starts the transition
                                                transitionName    // The transitionName of the view we’re transitioning to
                                        );

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA, options.toBundle());
                                }else{
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA);
                                }
                            } else {
                                DatosAplicacion datosAplicacion = (DatosAplicacion) mContext.getApplicationContext();
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(eventosActivity.getApplicationContext());
                                String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
                                AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_BUZON_MENSAJES + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";");
                                AudioQueu.contadorComandoEnviado++;
                                Toast.makeText(eventosActivity.getApplicationContext(), YACSmartProperties.intance.getMessageForKey("cargando.archivos.buzon"), Toast.LENGTH_LONG).show();
                            }
                        } else if (evento.getTipoEvento().equals("PUERTA")) {
                            final File fileVideo = new File(evento.getVideoPuerta());
                            if (fileVideo.exists()) {
                                Intent i = new Intent(eventosActivity, ImageSliderActivity.class);
                                i.putExtra("videoPuerta", evento.getVideoPuerta());
                                i.putExtra("indice", String.valueOf(position));
                                i.putExtra("evento", evento);
                                i.putExtra("tabActivo", eventosActivity.tabActivo);
                                i.putExtra("eventos", eventosActivity.eventoCol);
                                String transitionName = eventosActivity.getString(R.string.transition_album_cover);
                                ActivityOptionsCompat options =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(eventosActivity,
                                                holder.foto,   // The view which starts the transition
                                                transitionName    // The transitionName of the view we’re transitioning to
                                        );

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA, options.toBundle());
                                }else{
                                    eventosActivity.startActivityForResult(i, EventosActivity.REQUEST_DETALLE_VISITA);
                                }
                            } else {
                                RotateAnimation rotate = new RotateAnimation(0, 360,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                        0.5f);

                                rotate.setDuration(2000);
                                rotate.setRepeatCount(Animation.INFINITE);

                                holder.foto.setImageResource(R.drawable.refresh);
                                holder.foto.setEnabled(false);
                                holder.foto.setAnimation(rotate);
                                DatosAplicacion datosAplicacion = (DatosAplicacion) mContext.getApplicationContext();
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(eventosActivity.getApplicationContext());
                                String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
                                AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_VIDEO_SENSOR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";");
                                AudioQueu.contadorComandoEnviado++;
                                Toast.makeText(eventosActivity.getApplicationContext(), YACSmartProperties.intance.getMessageForKey("cargando.archivos.buzon"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
//                    if(!evento.isSeleccionado()) {
//                        evento.setSeleccionado(true);
//                        holder.check.setVisibility(View.VISIBLE);
//                        holder.check.bringToFront();
//                        eventosActivity.eventoSeleccionadoCol.add(evento);
//                    }else{
//                        evento.setSeleccionado(false);
//                        eventosActivity.eventoSeleccionadoCol.remove(evento);
//                        holder.check.setVisibility(View.GONE);
//                        if(eventosActivity.eventoSeleccionadoCol.size() == 0){
//                            eventosActivity.multipleSelection = false;
//                        }
//                    }
                    }
                }
            });
        }

    private void mostrarImagenes(SimpleViewHolder holder, Evento evento) {
        if(evento.getTipoEvento().equals("BUZON")) {
            File fileVideo = new File(evento.getVideoBuzon());
            if (fileVideo.exists()) {
                final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(evento.getVideoBuzon(), MediaStore.Video.Thumbnails.MICRO_KIND);
                holder.foto.setImageBitmap(bitmap);
                holder.foto.setVisibility(View.VISIBLE);
                holder.foto.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.hora.setText("2");
                final Palette palette = Palette.from(bitmap).generate();
                final Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null){
                    holder.hora.setBackgroundColor(vibrant.getRgb());
                    holder.hora.setTextColor(vibrant.getTitleTextColor());
                    evento.setColorFondo(vibrant.getRgb());
                    evento.setColorLetra(vibrant.getTitleTextColor());
                }else{
                    final Palette.Swatch vibrant2 = palette.getLightVibrantSwatch();
                    if (vibrant2 != null){
                        holder.hora.setBackgroundColor(vibrant2.getRgb());
                        holder.hora.setTextColor(vibrant2.getTitleTextColor());
                        evento.setColorFondo(vibrant2.getRgb());
                        evento.setColorLetra(vibrant2.getTitleTextColor());
                        final Palette.Swatch vibrant1 = palette.getMutedSwatch();
                    }else{
                        holder.hora.setBackgroundResource(R.color.colorsecundario400);
                        holder.hora.setTextColor(Color.WHITE);
                    }
                    }
                }else{
                    holder.hora.setText("7");
                    holder.foto.setImageResource(R.drawable.download);
                    holder.foto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }else if(evento.getTipoEvento().equals("TIMBRAR")) {
                File fileVideo = new File(evento.getVideoInicial());
                if (fileVideo.exists()) {
                    final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(evento.getVideoInicial(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    holder.foto.setImageBitmap(bitmap);
                    holder.foto.setVisibility(View.VISIBLE);
                    holder.foto.setScaleType(ImageView.ScaleType.FIT_XY);
                    holder.hora.setText("3");
                    final Palette palette = Palette.from(bitmap).generate();
                    final Palette.Swatch vibrant = palette.getVibrantSwatch();
                    if (vibrant != null){
                        holder.hora.setBackgroundColor(vibrant.getRgb());
                        holder.hora.setTextColor(vibrant.getTitleTextColor());
                        evento.setColorFondo(vibrant.getRgb());
                        evento.setColorLetra(vibrant.getTitleTextColor());
                    }else{
                        final Palette.Swatch vibrant2 = palette.getLightVibrantSwatch();
                        if (vibrant2 != null){
                            holder.hora.setBackgroundColor(vibrant2.getRgb());
                            holder.hora.setTextColor(vibrant2.getTitleTextColor());
                            evento.setColorFondo(vibrant2.getRgb());
                            evento.setColorLetra(vibrant2.getTitleTextColor());
                        }else{
                            holder.hora.setBackgroundResource(R.color.colorsecundario400);
                            holder.hora.setTextColor(Color.WHITE);
                        }
                    }
                }else{
                    holder.hora.setText("6");
                    holder.foto.setImageResource(R.drawable.download);
                    holder.foto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }else if(evento.getTipoEvento().equals("PUERTA")) {
                File fileVideo = new File(evento.getVideoPuerta());
                if (fileVideo.exists()) {
                    final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(evento.getVideoPuerta(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    holder.foto.setImageBitmap(bitmap);
                    holder.foto.setVisibility(View.VISIBLE);
                    holder.foto.setScaleType(ImageView.ScaleType.FIT_XY);
                    holder.hora.setText("4");
                    final Palette palette = Palette.from(bitmap).generate();
                    final Palette.Swatch vibrant = palette.getVibrantSwatch();
                    if (vibrant != null){
                        holder.hora.setBackgroundColor(vibrant.getRgb());
                        holder.hora.setTextColor(vibrant.getTitleTextColor());
                        evento.setColorFondo(vibrant.getRgb());
                        evento.setColorLetra(vibrant.getTitleTextColor());
                    }else{
                        Palette.Swatch vibrant2 = palette.getLightVibrantSwatch();
                        if (vibrant2 != null){
                            holder.hora.setBackgroundColor(vibrant2.getRgb());
                            holder.hora.setTextColor(vibrant2.getTitleTextColor());
                            evento.setColorFondo(vibrant2.getRgb());
                            evento.setColorLetra(vibrant2.getTitleTextColor());
                            final Palette.Swatch vibrant1 = palette.getMutedSwatch();
                        }else{
                            holder.hora.setBackgroundResource(R.color.colorsecundario400);
                            holder.hora.setTextColor(Color.WHITE);
                        }
                    }
                }else{
                    holder.hora.setText("5");
                    holder.foto.setImageResource(R.drawable.download);
                    holder.foto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }else{
                holder.hora.setText("8");
                holder.foto.setImageResource(R.drawable.download);
                holder.foto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }


}
