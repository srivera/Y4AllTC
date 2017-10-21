package ec.com.yacare.y4all.activities.cuenta;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ec.com.yacare.y4all.activities.R;


public class CuentaActivity  extends AppCompatActivity {

	private AppBarLayout appBar;
	private TabLayout pestanas;
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_cuenta_paginada);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(appBar != null) {
			appBar.removeView(pestanas);
		}
	}

	/**
	 * Un {@link FragmentStatePagerAdapter} que gestiona las secciones, fragmentos y
	 * títulos de las pestañas
	 */
	public class AdaptadorSecciones extends FragmentStatePagerAdapter {
		private final List<Fragment> fragmentos = new ArrayList<>();
		private final List<String> titulosFragmentos = new ArrayList<>();

		public AdaptadorSecciones(FragmentManager fm) {
			super(fm);
		}

		private Fragment mCurrentFragment;

		public Fragment getCurrentFragment() {
			return mCurrentFragment;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			if (getCurrentFragment() != object) {
				mCurrentFragment = ((Fragment) object);
			}
			super.setPrimaryItem(container, position, object);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return fragmentos.get(position);
		}

		@Override
		public int getCount() {
			return fragmentos.size();
		}

		public void addFragment(android.support.v4.app.Fragment fragment, String title) {
			fragmentos.add(fragment);
			titulosFragmentos.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titulosFragmentos.get(position);
		}
	}


	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new PerfilActivity(), "Cuenta");
		adapter.addFragment(new ListaEquiposActivity(), "Equipos");
		adapter.addFragment(new ListaDispositivosActivity(), "Celulares");
		viewPager.setAdapter(adapter);
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

}