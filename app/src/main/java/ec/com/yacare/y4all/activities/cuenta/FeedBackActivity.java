package ec.com.yacare.y4all.activities.cuenta;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ec.com.yacare.y4all.activities.R;

public class FeedBackActivity extends Fragment {

	private EditText editAsunto, editMensaje;
	private Button btnEnviar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_feedback, container, false);

	editAsunto = (EditText) view.findViewById(R.id.editAsunto);
		editMensaje = (EditText) view.findViewById(R.id.editMensaje);
		btnEnviar = (Button) view.findViewById(R.id.btnEnviar);

		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
		editAsunto.setTypeface(fontRegular);
		editMensaje.setTypeface(fontRegular);
		btnEnviar.setTypeface(fontRegular);

		btnEnviar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		return view;
	}
}
