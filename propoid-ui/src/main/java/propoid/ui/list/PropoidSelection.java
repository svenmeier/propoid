package propoid.ui.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private Set<Reference<T>> references = new HashSet<Reference<T>>();

	/**
	 * Bind the given checkable to a propoid.
	 * 
	 * @param checkable
	 *            checkable to bind
	 * @param propoid
	 *            propoid to bind to
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
			references.add(reference);
		} else {
			references.remove(reference);
		}
	}

	public void clear() {
		references.clear();
	}

	public int size() {
		return references.size();
	}

	public List<T> list(Repository repository) {
		List<T> selected = new ArrayList<T>();

		for (Reference<T> reference : references) {
			selected.add(repository.lookup(reference));
		}

		return selected;
	}

	public T first(Repository repository) {
		return repository.lookup(references.iterator().next());
	}
}