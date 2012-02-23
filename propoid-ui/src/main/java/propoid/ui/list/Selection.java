package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RadioButton;

/**
 * A selection of items in a {@link ListView}:
 * 
 * <pre>
 * {@code
 * public final View getView(int position, View convertView, ViewGroup parent) {
 *     ...
 *     selection.bind(checkBoxOrRadioButton, item);
 *     ...
 * }
 * </pre>
 * 
 * @param <T>
 *            item type
 */
public class Selection<T> implements OnClickListener {

	private List<T> items = new ArrayList<T>();

	/**
	 * Bind the given checkable to an item.
	 * 
	 * @param checkable
	 *            checkable view to bind to
	 * @param item
	 *            item to bind
	 */
	public void bind(Checkable checkable, T item) {
		View view = (View) checkable;

		view.setOnClickListener(null);

		view.setTag(item);

		checkable.setChecked(items.contains(item));

		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		@SuppressWarnings("unchecked")
		T item = (T) view.getTag();

		if (((Checkable) view).isChecked()) {
			if (view instanceof RadioButton) {
				items.clear();
			}

			if (!items.contains(item)) {
				items.add(item);

			}
		} else {
			items.remove(item);
		}
	}

	/**
	 * Clear selection.
	 */
	public void clear() {
		items.clear();
	}

	/**
	 * Get selection size.
	 * 
	 * @return size
	 */
	public int size() {
		return items.size();
	}

	/**
	 * List all selected.
	 * 
	 * @param repository
	 * @return all selected
	 */
	public List<T> items() {
		return items;
	}

	/**
	 * Get the first selected.
	 * 
	 * @return first selected or {@code null}
	 */
	public T first() {
		if (items.isEmpty()) {
			return null;
		} else {
			return items.get(0);
		}
	}
}