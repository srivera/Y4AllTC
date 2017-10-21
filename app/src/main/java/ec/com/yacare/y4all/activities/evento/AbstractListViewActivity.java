package ec.com.yacare.y4all.activities.evento;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ec.com.yacare.y4all.adapter.EventoArrayAdapter;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.SectionItem;

public abstract class AbstractListViewActivity extends ListActivity
{

	protected EventoDataSource datasource;

	protected static final int PAGESIZE = 20;

	protected TextView textViewDisplaying;

	protected View footerView;

	protected boolean loading = false;

	protected class LoadNextPage extends AsyncTask<String, Void, String>
	{
		private ArrayList<Evento> newData = null;

		@Override
		protected String doInBackground(String... arg0)
		{
			// para que de tiempo a ver el footer ;)
			try
			{
				Thread.sleep(1500);
			}
			catch (InterruptedException e)
			{
				Log.e("AbstractListActivity", e.getMessage());
			}
			newData = datasource.getPaginaEventos(getListAdapter().getCount(), PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			EventoArrayAdapter customArrayAdapter = ((EventoArrayAdapter) getListAdapter());
			String fecha = null;
//			ArrayList<Item> eventos = new ArrayList<Item>();
			
			for(Evento evento : newData){
				if (!isTheSame(fecha, evento.getFecha().substring(0, 10))){
					fecha = evento.getFecha().substring(0, 10);	
					customArrayAdapter.add(new SectionItem(evento.getFecha().substring(0, 10)));
				}
				customArrayAdapter.add(evento);
			}
			
			
			customArrayAdapter.notifyDataSetChanged();

			getListView().removeFooterView(footerView);
//			updateDisplayingTextView();
			loading = false;
		}

	}

//	protected void updateDisplayingTextView()
//	{
//		textViewDisplaying = (TextView) findViewById(R.id.displaying);
//		String text = getString(R.string.display);
//		text = String.format(text, getListAdapter().getCount(), datasource.getSize());
//		textViewDisplaying.setText(text);
//	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Toast.makeText(this, "pos" + getListAdapter().getItem(position), Toast.LENGTH_SHORT).show();
	}
	
	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		boolean lastItem = firstVisibleItem + visibleItemCount == totalItemCount && getListView().getChildAt(visibleItemCount -1) != null && getListView().getChildAt(visibleItemCount-1).getBottom() <= getListView().getHeight();
		boolean moreRows = getListAdapter().getCount() < datasource.getTotal();
		return moreRows && lastItem && !loading;
		
	}
	
	private boolean isTheSame(final String previousSection, final String newSection) {
		if (previousSection == null) {
			return newSection == null;
		} else {
			return previousSection.equals(newSection);
		}
	}
}