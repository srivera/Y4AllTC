package ec.com.yacare.y4all.lib.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import ec.com.yacare.y4all.lib.dto.Equipo;

public abstract class BaseConfigActivity extends AppCompatActivity {
	
	public abstract void verificarResultadoInstalacion(String resultado);

	public ProgressDialog progress;
	public abstract void verificarResultadoConfiguracion(String resultado);

	private Equipo equipo;

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public ProgressDialog getProgress() {
		return progress;
	}

	public void setProgress(ProgressDialog progress) {
		this.progress = progress;
	}
}
