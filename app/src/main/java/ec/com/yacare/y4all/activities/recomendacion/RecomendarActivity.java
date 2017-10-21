package ec.com.yacare.y4all.activities.recomendacion;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Locale;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Contacto;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecomendarActivity extends AppCompatActivity {

	private ListView mListView;
	private ProgressDialog pDialog;
	private Handler updateBarHandler;
	private ArrayList<Contacto> contactList;
	private ArrayList<Contacto> contactoCompleto;
	Cursor cursor;
	int counter;
	EditText editsearch;
	private RecomendarActivity.ContactoAdapter mAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_recomendar);

		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Cargando contactos...");
		pDialog.setCancelable(false);
		pDialog.show();
		mListView = (ListView) findViewById(R.id.list);
		updateBarHandler =new Handler();
		// Since reading contacts takes more time, let's run it on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				getContacts();
			}
		}).start();
		// Set onclicklistener to the list item.
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				//TODO Do whatever you want with the list data
				Toast.makeText(getApplicationContext(), "item clicked : \n"+contactList.get(position), Toast.LENGTH_SHORT).show();
			}
		});

		// Locate the EditText in listview_main.xml
		editsearch = (EditText) findViewById(R.id.search);

		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
				mAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub
			}
		});
	}


	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	public void getContacts() {
		contactList = new ArrayList<Contacto>();
		contactoCompleto = new ArrayList<Contacto>();
		String phoneNumber = null;
		String email = null;
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		StringBuffer output;
		ContentResolver contentResolver = getContentResolver();
		cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
		// Iterate every contact in the phone
		if (cursor.getCount() > 0) {
			counter = 0;
			while (cursor.moveToNext()) {
				output = new StringBuffer();
				// Update the progress message

				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
				if (hasPhoneNumber > 0) {

					//This is to read multiple phone numbers associated with the same contact
//					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
//					while (phoneCursor.moveToNext()) {
//						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//						output.append("\n Phone number:" + phoneNumber);
//					}
//					phoneCursor.close();
					// Read every email id associated with the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					if(emailCursor.getCount() > 0) {
						Contacto contacto = new Contacto();
						updateBarHandler.post(new Runnable() {
							public void run() {
								pDialog.setMessage("Leyendo contacto: "+ counter++);
							}
						});
						//output.append("\n First Name:" + name);
						contacto.setNombre(name);
						Bitmap foto = openPhoto(Long.valueOf(contact_id));
						contacto.setFoto(foto);
//						if(foto != null){
//							output.append("\n Tiene foto:");
//						}else{
//							output.append("\n No Tiene foto:");
//						}
						while (emailCursor.moveToNext()) {
							email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
//							output.append("\n Email:" + email);
							contacto.setCorreo(email);
						}
						// Add the contact to the ArrayList
						contactoCompleto.add(contacto);
					}
					emailCursor.close();
				}

			}
			contactList.addAll(contactoCompleto);
			// ListView has to be updated using a ui thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter = new RecomendarActivity.ContactoAdapter();
					mListView.setAdapter(mAdapter);

//					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.li_contacto, R.id.text1, contactList);
//					mListView.setAdapter(adapter);
				}
			});
			// Dismiss the progressbar after 500 millisecondds
			updateBarHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					pDialog.cancel();
				}
			}, 500);
		}
	}

	public Bitmap openPhoto(long contactId) {
		Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = getContentResolver().query(photoUri,
				new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
				}
			}
		} finally {
			cursor.close();
		}
		return null;

	}

	class ContactoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public Contacto getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.li_contacto, null);
				new RecomendarActivity.ContactoAdapter.ViewHolder(convertView);
			}
			RecomendarActivity.ContactoAdapter.ViewHolder holder = (RecomendarActivity.ContactoAdapter.ViewHolder) convertView.getTag();
			Contacto contacto = getItem(position);


			if(contacto.getFoto() != null){
				mostrarImagen(holder.iv_icon, contacto.getFoto());
			}else {
				Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.usuarioperfil)).getBitmap();
				mostrarImagen(holder.iv_icon, bitmap);
			}
			Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
			holder.txtNombreContacto.setText(contacto.getNombre());
			holder.txtNombreContacto.setTypeface(fontRegular);
			holder.txtCorreoContacto.setText(contacto.getCorreo());
			holder.txtCorreoContacto.setTypeface(fontRegular);
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView txtNombreContacto;
			TextView txtCorreoContacto;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.ivFoto);
				txtNombreContacto = (TextView) view.findViewById(R.id.txtNombreContacto);
				txtCorreoContacto = (TextView) view.findViewById(R.id.txtCorreoContacto);
				view.setTag(this);

			}
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			contactList.clear();
			if (charText.length() == 0) {
				contactList.addAll(contactoCompleto);
			}
			else
			{
				for (Contacto c : contactoCompleto)
				{
					if (c.getNombre().toLowerCase(Locale.getDefault()).contains(charText))
					{
						contactList.add(c);
					}
				}
			}
			notifyDataSetChanged();
		}
	}


	private void mostrarImagen(ImageView mimageView, Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 2000;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		mimageView.setImageBitmap(output);
	}
}
