package ec.com.yacare.y4all.lib.activities;

import java.util.Locale;

import ec.com.yacare.y4all.lib.dto.Dispositivo;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public abstract class BaseTextSpeechActivity extends Activity implements  android.speech.tts.TextToSpeech.OnInitListener{
	
	private TextToSpeech textToSpeech;
	
	private Dispositivo dispositivoActual;
	
	private String ultimoMensaje;
	
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Locale locSpanish = new Locale("es");
			int result = textToSpeech.setLanguage(locSpanish);
//			textToSpeech.setSpeechRate(0.95f); 
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			} else {
				
			}
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}
	
	public void convertTextToSpeech(String textoLeer) {
		if(dispositivoActual != null){
			textoLeer = dispositivoActual.getNombreDispositivo() + " dice .. " + textoLeer;
			textToSpeech.speak(textoLeer, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	public TextToSpeech getTextToSpeech() {
		return textToSpeech;
	}

	public void setTextToSpeech(TextToSpeech textToSpeech) {
		this.textToSpeech = textToSpeech;
	}

	public Dispositivo getDispositivoActual() {
		return dispositivoActual;
	}

	public void setDispositivoActual(Dispositivo dispositivoActual) {
		this.dispositivoActual = dispositivoActual;
	}

	public String getUltimoMensaje() {
		return ultimoMensaje;
	}

	public void setUltimoMensaje(String ultimoMensaje) {
		this.ultimoMensaje = ultimoMensaje;
	}
	
	
}
