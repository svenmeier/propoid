package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.ListView;

/**
 * A generic listener to checked items in a {@link ListView}.
 * 
 * @see ListView#setItemChecked(int, boolean)
 * @see ListView#isItemChecked(int)
 */
public class GenericChoices<T> implements OnClickListener {

	public static int CHOICE_MODE_SINGLE = ListView.CHOICE_MODE_SINGLE;

	public static int CHOICE_MODE_MULTIPLE = ListView.CHOICE_MODE_MULTIPLE;

	private ListView listView;

	public GenericChoices(ListView listView, int choiceMode) {
		if (listView == null) {
			throw new IllegalArgumentException();
		}

		this.listView = listView;

		this.listView.setChoiceMode(choiceMode);
	}

	public void bind(Checkable checkable, int position) {
		if (checkable == null) {
			throw new IllegalArgumentException();
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

	public void clearChoices() {
		listView.clearChoices();

		listView.invalidateViews();
	}

	public int size() {
		return listView.getCheckItemIds().length;
	}

	@SuppressWarnings("unchecked")
	public List<T> getChoices() {
		List<T> choices = new ArrayList<T>();

		for (int position = 0; position < listView.getCount(); position++) {
			if (listView.isItemChecked(position)) {
				choices.add((T) listView.getAdapter().getItem(position));
			}
		}

		return choices;
	}
}