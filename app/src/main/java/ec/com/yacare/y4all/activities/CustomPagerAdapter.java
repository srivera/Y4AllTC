package ec.com.yacare.y4all.activities;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

class CustomPagerAdapter extends PagerAdapter {

	Context mContext;
	LayoutInflater mLayoutInflater;

	int[] mResources = {
			R.drawable.t2,
			R.drawable.t1,
			R.drawable.t3,
			R.drawable.t4,
			R.drawable.t2
	};

	public CustomPagerAdapter(Context context) {
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mResources.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = mLayoutInflater.inflate(R.layout.fragment_screen_slide_page, container, false);

		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView1);
		imageView.setImageResource(mResources[position]);

		container.addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}
}