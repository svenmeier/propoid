package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.Repository;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RadioButton;

/**
 * A selection of {@link Propoid}s. Can be used in {@link ListView}s to keep the
 * currently selected items:
 * 
 * <pre>
 * {@code
 * public final View getView(int position, View convertView, ViewGroup parent) {
 *     ...
 *     selection.bind(checkBoxOrRadioButton, propoid);
 *     ...
 * }
 * </pre>
 * 
 * @param <T>
 *            propoid type
 */
public class PropoidSelection<T extends Propoid> implements OnClickListener {

	private List<Reference<T>> references = new ArrayList<Reference<T>>();

	/**
	 * Bind the given checkable to a propoid.
	 * 
	 * @param checkable
	 *            checkable view to bind to
	 * @param propoid
	 *            propoid to bind
	 */
	public void bind(Checkable checkable, T propoid) {
		View view = (View) checkable;

		view.setOnClickListener(null);

		Reference<T> reference = new Reference<T>(propoid);
		view.setTag(reference);

		checkable.setChecked(references.contains(reference));

		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		@SuppressWarnings("unchecked")
		Reference<T> reference = (Reference<T>) view.getTag();

		if (((Checkable) view).isChecked()) {
			if (view instanceof RadioButton) {
				references.clear();
			}

			if (!references.contains(reference)) {
				references.add(reference);

			}
		} else {
			references.remove(reference);
		}
	}

	/**
	 * Clear selection.
	 */
	public void clear() {
		references.clear();
	}

	/**
	 * Get selection size.
	 * 
	 * @return size
	 */
	public int size() {
		return references.size();
	}

	/**
	 * List all selected.
	 * 
	 * @param repository
	 * @return all selected
	 */
	public List<T> list(Repository repository) {
		List<T> selected = new ArrayList<T>();

		for (Reference<T> reference : references) {
			selected.add(repository.lookup(reference));
		}

		return selected;
	}

	/**
	 * Get the first selected.
	 * 
	 * @param repository
	 * @return first selected or {@code null}
	 */
	public T first(Repository repository) {
		if (references.isEmpty()) {
			return null;
		} else {
			return repository.lookup(references.iterator().next());
		}
	}
}