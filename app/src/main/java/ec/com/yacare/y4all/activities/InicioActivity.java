package ec.com.yacare.y4all.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ec.com.yacare.y4all.activities.instalacion.InstalarEquipoActivity;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class InicioActivity extends FragmentActivity {

	static final int CERRAR_PANTALLA = 10;
	static final int CERRAR_PANTALLA_LOGIN = 10;

	private Button primerDispositivo;
	private Button tengoCuenta;


//	/**
//	 * The number of pages (wizard steps) to show in this demo.
//	 */
//	private static final int NUM_PAGES = 5;
//
//	/**
//	 * The pager widget, which handles animation and allows swiping horizontally to access previous
//	 * and next wizard steps.
//	 */
//	private ViewPager mPager;
//
//	/**
//	 * The pager adapter, which provides the pages to the view pager widget.
//	 */
//	private CustomPagerAdapter mPagerAdapter;
//
//
//	private ImageView[] dots;
//	LinearLayout linearLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setContentView(R.layout.activity_inicio);

		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");

		TextView leyenda1 = (TextView) findViewById(R.id.leyenda1);
		TextView leyenda2 = (TextView) findViewById(R.id.leyenda2);
		leyenda1.setTypeface(fontRegular);
		leyenda2.setTypeface(fontRegular);

		LinearLayout piePantalla = (LinearLayout) findViewById(R.id.piePantalla);
		Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_down);
		piePantalla.startAnimation(animation);

		leyenda1.bringToFront();
		leyenda2.bringToFront();

		primerDispositivo = (Button) findViewById(R.id.btnMiPrimer);
		primerDispositivo.setTypeface(fontRegular);
		primerDispositivo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(InicioActivity.this, InstalarEquipoActivity.class);
				i.putExtra("primerEquipo", true);
				startActivity(i);
				overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
			}
		});

		tengoCuenta = (Button) findViewById(R.id.btnTengoCuenta);
		tengoCuenta.setTypeface(fontRegular);
		tengoCuenta.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(InicioActivity.this, LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
			}
		});


//		// Instantiate a ViewPager and a PagerAdapter.
//		mPager = (ViewPager) findViewById(R.id.pager);
//
//		mPagerAdapter = new CustomPagerAdapter(this);
//
//
//		mPager.setAdapter(mPagerAdapter);
//		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//			@Override
//			public void onPageSelected(int position) {
//				// When changing pages, reset the action bar actions since they are dependent
//				// on which page is currently active. An alternative approach is to have each
//				// fragment expose actions itself (rather than the activity exposing actions),
//				// but for simplicity, the activity provides the actions in this sample.
//				invalidateOptionsMenu();
//			}
//		});;
//
//		drawPageSelectionIndicators(0);
//		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//			@Override
//			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//			}
//
//			@Override
//			public void onPageSelected(int position) {
//				drawPageSelectionIndicators(position);
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int state) {
//			}
//		});
//
//		mPager.setPageTransformer(false, new ViewPager.PageTransformer() {
//
//			private float MIN_SCALE = 0.75f;
//			@Override
//			public void transformPage(View view, float position) {
//				int pageWidth = view.getWidth();
//
//				if (position < -1) { // [-Infinity,-1)
//					// This page is way off-screen to the left.
//					view.setAlpha(0);
//
//				} else if (position <= 0) { // [-1,0]
//					// Use the default slide transition when moving to the left page
//					view.setAlpha(1);
//					view.setTranslationX(0);
//					view.setScaleX(1);
//					view.setScaleY(1);
//
//				} else if (position <= 1) { // (0,1]
//					// Fade the page out.
//					view.setAlpha(1 - position);
//
//					// Counteract the default slide transition
//					view.setTranslationX(pageWidth * -position);
//
//					// Scale the page down (between MIN_SCALE and 1)
//					float scaleFactor = MIN_SCALE
//							+ (1 - MIN_SCALE) * (1 - Math.abs(position));
//					view.setScaleX(scaleFactor);
//					view.setScaleY(scaleFactor);
//
//				} else { // (1,+Infinity]
//					// This page is way off-screen to the right.
//					view.setAlpha(0);
//				}
//			}
//		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == CERRAR_PANTALLA_LOGIN){
				Intent i = new Intent(InicioActivity.this, SplashActivity.class);
				startActivity(i);
				finish();
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
	}

//	/**
//	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
//	 * sequence.
//	 */
//	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//		public ScreenSlidePagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			return ScreenSlidePageFragment.create(position);
//		}
//
//		@Override
//		public int getCount() {
//			return NUM_PAGES;
//		}
//	}
//
//	private void drawPageSelectionIndicators(int mPosition){
//		if(linearLayout!=null) {
//			linearLayout.removeAllViews();
//		}
//		linearLayout=(LinearLayout)findViewById(R.id.viewPagerCountDots);
//		dots = new ImageView[NUM_PAGES];
//		for (int i = 0; i < NUM_PAGES; i++) {
//			dots[i] = new ImageView(getApplicationContext());
//			if(i==mPosition)
//				dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));
//			else
//				dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));
//
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.WRAP_CONTENT,
//					LinearLayout.LayoutParams.WRAP_CONTENT
//			);
//
//			params.setMargins(4, 0, 4, 0);
//			linearLayout.addView(dots[i], params);
//		}
//	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

}


