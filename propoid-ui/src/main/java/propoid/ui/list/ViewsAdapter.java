package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * A list adapter for {@link View}s.
 */
public class ViewsAdapter implements ListAdapter {

	private final ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();

	private List<View> views = new ArrayList<View>();

	public ViewsAdapter(View... views) {
		add(views);
	}

	public void add(View... views) {
		for (View view : views) {
			this.views.add(view);
		}
	}

	public void add(View view) {
		this.views.add(view);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return views.isEmpty();
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return views.size();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return views.get(position);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		observers.remove(observer);
	}
}