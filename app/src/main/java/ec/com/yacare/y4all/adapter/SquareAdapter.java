package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.ImageItem;

public class SquareAdapter extends ArrayAdapter
{
	private ArrayList<ImageItem> items = new ArrayList<ImageItem>();
	private LayoutInflater inflater;

	public SquareAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> items) {
		super(context, layoutResourceId);
		this.items = items;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		return items.size();
	}



	@Override
	public View getView(int i, View view, ViewGroup viewGroup)
	{
		View v = view;
		ImageView picture;
		TextView name;

		if(v == null)
		{
			v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
			v.setTag(R.id.picture, v.findViewById(R.id.picture));
			v.setTag(R.id.text, v.findViewById(R.id.text));
		}

		picture = (ImageView)v.getTag(R.id.picture);
		name = (TextView)v.getTag(R.id.text);

		ImageItem item = (ImageItem) items.get(i);


		picture.setImageBitmap(item.getImage());
		name.setText(item.getTitle());

		return v;
	}


}
