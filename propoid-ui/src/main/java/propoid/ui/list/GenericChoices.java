package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.ListView;

/**
 */
public class GenericChoices<T> implements OnClickListener {

	private ListView listView;

	public void bind(Checkable checkable, ListView listView, int position) {
		if (this.listView == null) {
			this.listView = listView;

			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}

		View view = (View) checkable;

		view.setOnClickListener(null);

		view.setTag(position);

		checkable.setChecked(listView.isItemChecked(position));

		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		int position = (Integer) view.getTag();

		listView.setItemChecked(position, ((Checkable) view).isChecked());
	}

	public int size() {
		if (listView == null) {
			return 0;
		}

		return listView.getCheckItemIds().length;
	}

	@SuppressWarnings("unchecked")
	public List<T> getChoices() {
		List<T> choices = new ArrayList<T>();

		if (listView == null) {
			return choices;
		}

		SparseBooleanArray positions = listView.getCheckedItemPositions();
		for (int position = 0; position < listView.getCount(); position++) {
			if (positions.get(position)) {
				choices.add((T) listView.getAdapter().getItem(position));
			}
		}

		return choices;
	}
}