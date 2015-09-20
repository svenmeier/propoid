package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * A list adapter for a fixed list of {@link View}s.
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

	/**
	 * Convenience method to add a single preference-like category.
	 * 
	 * @param activity
	 *            owning activity
	 * @param textId
	 *            resource id for the category's text
	 */
	public void addCategory(Activity activity, int textId) {
		View view = activity.getLayoutInflater().inflate(
				R.layout.preference_category, null);
		((TextView) view).setText(textId);
		add(view);
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