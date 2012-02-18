package propoid.ui.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.Repository;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PropoidSelection<T extends Propoid> implements
		OnCheckedChangeListener {

	private Set<Reference<T>> references = new HashSet<Reference<T>>();

	/**
	 * Bind the given checkbox to a propoid.
	 * 
	 * @param checkBox
	 *            checkbox to bind
	 * @param propoid
	 *            propoid to bind to
	 */
	public void bind(CheckBox checkBox, T propoid) {
		checkBox.setOnCheckedChangeListener(null);

		Reference<T> reference = new Reference<T>(propoid);
		checkBox.setTag(reference);

		checkBox.setChecked(references.contains(reference));

		checkBox.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		@SuppressWarnings("unchecked")
		Reference<T> reference = (Reference<T>) buttonView.getTag();

		if (isChecked) {
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