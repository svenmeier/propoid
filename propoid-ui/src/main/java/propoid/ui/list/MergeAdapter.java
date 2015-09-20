package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A merging list adapter.
 */
public class MergeAdapter implements ListAdapter, OnItemClickListener {

	private List<ListAdapter> adapters = new ArrayList<ListAdapter>();

	public MergeAdapter(ListAdapter... adapters) {
		add(adapters);
	}

	public void add(ListAdapter... adapters) {
		for (ListAdapter adapter : adapters) {
			this.adapters.add(adapter);
		}
	}

	public void add(ListAdapter adapter) {
		this.adapters.add(adapter);
	}

	public void add(View view) {
		this.adapters.add(new ViewsAdapter(view));
	}

	/**
	 * Convenience method to a single preference-like category.
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

	/**
	 * Convenience method to merge a single preference-like category.
	 * 
	 * @param activity
	 *            owning activity
	 * @param text
	 *            category text
	 */
	public void addCategory(Activity activity, String text) {
		View view = activity.getLayoutInflater().inflate(
				R.layout.preference_category, null);
		((TextView) view).setText(text);
		add(view);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		int offset = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				return adapter.isEnabled(position - offset);
			}

			offset += adapter.getCount();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public boolean isEmpty() {
		return adapters.isEmpty();
	}

	@Override
	public int getCount() {
		int count = 0;

		for (ListAdapter adapter : adapters) {
			count += adapter.getCount();
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		int offset = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				return adapter.getItem(position - offset);
			}

			offset += adapter.getCount();
		}
		
		throw new IllegalArgumentException();
	}

	@Override
	public int getViewTypeCount() {
		int count = 0;

		for (ListAdapter adapter : adapters) {
			count += adapter.getViewTypeCount();
		}

		return count;
	}

	@Override
	public int getItemViewType(int position) {
		int offset = 0;

		int typeCount = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				int type = adapter.getItemViewType(position - offset);
				if (type == ListAdapter.IGNORE_ITEM_VIEW_TYPE) {
					return type;
				}
				return typeCount + type;
			}

			typeCount += adapter.getViewTypeCount();
			offset += adapter.getCount();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int offset = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				return adapter.getView(position - offset, convertView, parent);
			}

			offset += adapter.getCount();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public boolean hasStableIds() {
		for (ListAdapter adapter : adapters) {
			if (!adapter.hasStableIds()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public long getItemId(int position) {
		int offset = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				return adapter.getItemId(position - offset);
			}

			offset += adapter.getCount();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		for (ListAdapter adapter : adapters) {
			adapter.registerDataSetObserver(observer);
		}
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		for (ListAdapter adapter : adapters) {
			adapter.unregisterDataSetObserver(observer);
		}
	}

	/**
	 * Helper method to install to the given {@link ListView}, i.e. set as
	 * adapter, register as {@link OnItemClickListener} and keep the previous
	 * scroll position.
	 */
	public void install(ListView view) {
		int position = view.getFirstVisiblePosition();
		View child = view.getChildAt(0);
		int top = (child == null) ? 0 : child.getTop();

		view.setAdapter(this);
		view.setOnItemClickListener(this);

		view.setSelectionFromTop(position, top);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		int offset = 0;

		for (ListAdapter adapter : adapters) {
			if (position - offset < adapter.getCount()) {
				if (adapter instanceof OnItemClickListener) {
					((OnItemClickListener) adapter).onItemClick(parent, view,
							position - offset, id);
				}
				return;
			}

			offset += adapter.getCount();
		}
		throw new IllegalArgumentException();
	}
}