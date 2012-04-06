package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseBooleanArray;
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

	private SparseBooleanArray choices;

	public GenericChoices(ListView listView, int choiceMode) {
		if (listView == null) {
			throw new IllegalArgumentException();
		}

		this.listView = listView;

		this.listView.setChoiceMode(choiceMode);
	}

	public GenericChoices(ListView listView) {
		this(listView, ListView.CHOICE_MODE_NONE);

		choices = new SparseBooleanArray();
	}

	public void bind(Checkable checkable, int position) {
		if (checkable == null) {
			throw new IllegalArgumentException();
		}

		View view = (View) checkable;

		view.setOnClickListener(null);

		view.setTag(position);

		checkable.setChecked(isChosen(listView, position));

		view.setOnClickListener(this);
	}

	private boolean isChosen(ListView listView, int position) {
		if (choices == null) {
			return listView.isItemChecked(position);
		} else {
			return choices.get(position);
		}
	}

	private void setChosen(ListView listView, int position, boolean chosen) {
		if (choices == null) {
			listView.setItemChecked(position, chosen);
		} else {
			if (chosen) {
				choices.put(position, true);
			} else {
				choices.delete(position);
			}
		}

		changed();
	}

	protected void changed() {
	}

	@Override
	public void onClick(View view) {

		int position = (Integer) view.getTag();

		setChosen(listView, position, ((Checkable) view).isChecked());
	}

	public void clearChoices() {
		if (choices == null) {
			listView.clearChoices();
		} else {
			choices.clear();
		}

		listView.invalidateViews();
	}

	public int size() {
		if (choices == null) {
			return listView.getCheckItemIds().length;
		} else {
			return choices.size();
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getChoices() {
		List<T> choices = new ArrayList<T>();

		for (int position = 0; position < listView.getCount(); position++) {
			if (isChosen(listView, position)) {
				choices.add((T) listView.getAdapter().getItem(position));
			}
		}

		return choices;
	}
}