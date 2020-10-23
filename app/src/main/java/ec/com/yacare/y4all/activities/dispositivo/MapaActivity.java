package ec.com.yacare.y4all.activities.dispositivo;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

/*import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;*/

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class MapaActivity {
	/*	extends Activity {

	private GoogleMap googleMap;

	private MapView mMapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(AudioQueu.getRequestedOrientation());

		setContentView(R.layout.activity_mapa);


		mMapView = (MapView) findViewById(R.id.mapview);

		if (mMapView != null) {
			mMapView.onCreate(savedInstanceState);

			mMapView.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap map) {
					googleMap = map;
					map.setMyLocationEnabled(true);
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					mMapView.onResume();
					UiSettings mapSettings;
					mapSettings = googleMap.getUiSettings();
					mapSettings.setZoomControlsEnabled(true);
					mapSettings.setMyLocationButtonEnabled(false);
					mapSettings.setMapToolbarEnabled(false);
					mapSettings.setCompassEnabled(true);
					googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

				}
			});
		}
	}

	GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
		@Override
		public void onMyLocationChange(Location location) {
			if (googleMap != null) {
						CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
						googleMap.moveCamera(center);
						CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
						googleMap.animateCamera(zoom);
			}
		}
	};*/
}
