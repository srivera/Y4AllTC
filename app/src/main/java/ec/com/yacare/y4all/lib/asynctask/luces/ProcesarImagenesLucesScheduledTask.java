package ec.com.yacare.y4all.lib.asynctask.luces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.focos.ComandoFoco;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;


public class ProcesarImagenesLucesScheduledTask extends Thread {

	private ImageView iv;
	Palette palette;
	Palette.Swatch vibrant;
	Context context;
	String zona;

	static String AZUL = "BA";
	static String NARANJA = "23";
	static String AMARILLO = "3B";
	static String LIMA = "54";
	static String FUCSIA = "06";
	static String ROSADO = "FF";
	static String ROJO = "19";
	static String VERDE = "6E";
	static String AQUA = "85";
	static String MORADO = "D9";
	static String NEGRO = "00";
	static String BLANCO = "01";
	static String GRIS = "02";

	HashMap<String, String> colores = new HashMap<String, String>();

	public ProcesarImagenesLucesScheduledTask(ImageView iv, Context context, String zona) {
		super();
		this.iv = iv;
		this.context = context;
		this.zona = zona;
	}


	@Override
	public void run() {
		String datosConfT;
		Integer contador = 0;
		String ultimoColor = "";
		String color;
		DatosAplicacion datosAplicacion = (DatosAplicacion) context;


//		colores.put("000", NEGRO);
//		colores.put("255255255", BLANCO);
//		colores.put("25500", ROJO);
//		colores.put("02550", LIMA);
//		colores.put("00255", AZUL);
//		colores.put("2552550", AMARILLO);
//		colores.put("0255255", AQUA);
//		colores.put("2550255", FUCSIA);
//		colores.put("192192192", GRIS);
//		colores.put("128128128", GRIS);
//		colores.put("12800", ROJO);
//		colores.put("1281280", VERDE);
//		colores.put("01280", VERDE);
//		colores.put("1280128", MORADO);
//		colores.put("0128128", VERDE);
//		colores.put("00128", AZUL);

		colores.put("000",NEGRO);
		colores.put("00128",AZUL);
		colores.put("00192",AZUL);
		colores.put("00255",AZUL);
		colores.put("01280",VERDE);
		colores.put("0128128",VERDE);
		colores.put("0128192",AZUL);
		colores.put("0128255",AZUL);
		colores.put("01920",VERDE);
		colores.put("0192128",VERDE);
		colores.put("0192192",AQUA);
		colores.put("0192255",AQUA);
		colores.put("02550",VERDE);
		colores.put("0255128",VERDE);
		colores.put("0255192",LIMA);
		colores.put("0255255",AQUA);
		colores.put("12800",ROJO);
		colores.put("1280128",MORADO);
		colores.put("1280192",MORADO);
		colores.put("1280255",MORADO);
		colores.put("1281280",AMARILLO);
		colores.put("128128128",GRIS);
		colores.put("128128192",GRIS);
		colores.put("128128255",MORADO);
		colores.put("1281920",VERDE);
		colores.put("128192128",VERDE);
		colores.put("128192192",AQUA);
		colores.put("128192255",AQUA);
		colores.put("1282550",LIMA);
		colores.put("128255128",LIMA);
		colores.put("128255192",VERDE);
		colores.put("128255255",AQUA);
		colores.put("19200",ROJO);
		colores.put("1920128",MORADO);
		colores.put("1920192",MORADO);
		colores.put("1920255",MORADO);
		colores.put("1921280",NARANJA);
		colores.put("192128128",ROSADO);
		colores.put("192128192",MORADO);
		colores.put("192128255",MORADO);
		colores.put("1921920",AMARILLO);
		colores.put("192192128",AMARILLO);
		colores.put("192192192",GRIS);
		colores.put("192192255",MORADO);
		colores.put("1922550",LIMA);
		colores.put("192255128",LIMA);
		colores.put("192255192",LIMA);
		colores.put("192255255",AQUA);
		colores.put("25500",ROJO);
		colores.put("2550128",FUCSIA);
		colores.put("2550192",FUCSIA);
		colores.put("2550255",FUCSIA);
		colores.put("2551280",NARANJA);
		colores.put("255128128",ROSADO);
		colores.put("255128192",ROSADO);
		colores.put("255128255",ROSADO);
		colores.put("2551920",NARANJA);
		colores.put("255192128",NARANJA);
		colores.put("255192192",ROSADO);
		colores.put("255192255",MORADO);
		colores.put("2552550",AMARILLO);
		colores.put("255255128",AMARILLO);
		colores.put("255255192",AMARILLO);
		colores.put("255255255",BLANCO);



		while (AudioQueu.modoCamaraLuces) {
			if(AudioQueu.getVideoIntercom().containsKey(contador)){
				//Color
				Bitmap bitmap = BitmapFactory.decodeByteArray(AudioQueu.getVideoIntercom().get(contador), 0, AudioQueu.getVideoIntercom().get(contador).length);
				palette = Palette.from(bitmap).generate();
				vibrant = palette.getVibrantSwatch();

				if(vibrant != null) {

					color = buscarColor(Color.red(vibrant.getRgb()), Color.green(vibrant.getRgb()), Color.blue(vibrant.getRgb()));
					if (color != null && !ultimoColor.equals(color)) {
						ultimoColor = color;
						datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + zona + ";" + color + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
						ComandoFoco comandoFoco = new ComandoFoco(datosConfT, context);
						comandoFoco.start();
						iv.post(new Runnable() {
							@Override
							public void run() {
								iv.setBackgroundColor(vibrant.getRgb());
							}
						});

					}
				}
				AudioQueu.getVideoIntercom().remove(contador);
				contador++;

			}
		}

	}


	private String buscarColor(int red, int green, int blue){
		String color = "";
		int newRed, newGreen, newBlue;
		if(red <= 128){
			if(128 - red < red){
				newRed = 128;
			}else{
				newRed = 0;
			}
		}else if(red > 128 && red <= 192){
			if(192 - red < red){
				newRed = 192;
			}else{
				newRed = 128;
			}
		}else{
			if(255 - red < red){
				newRed = 255;
			}else{
				newRed = 192;
			}
		}

		if(green <= 128){
			if(128 - green < green){
				newGreen = 128;
			}else{
				newGreen = 0;
			}
		}else if(green > 128 && green <= 192){
			if(192 - green < green){
				newGreen = 192;
			}else{
				newGreen = 128;
			}
		}else{
			if(255 - green < green){
				newGreen = 255;
			}else{
				newGreen = 192;
			}
		}

		if(blue <= 128){
			if(128 - blue < blue){
				newBlue = 128;
			}else{
				newBlue = 0;
			}
		}else if(blue > 128 && blue <= 192){
			if(192 - blue < blue){
				newBlue = 192;
			}else{
				newBlue = 128;
			}
		}else{
			if(255 - blue < blue){
				newBlue = 255;
			}else{
				newBlue = 192;
			}
		}
		color = colores.get(String.valueOf(newRed) + String.valueOf(newGreen) + String.valueOf(newBlue));
		if(color == null){
			Log.d("Colores", "R: " + red + " G: " + green + " B: " + blue);
			Log.d("Colores", "R: " +newRed + " G: " + newGreen + " B: " + newBlue);
		}
		return color;

	}
}
