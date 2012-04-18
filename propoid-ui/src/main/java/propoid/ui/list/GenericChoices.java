package propoid.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ListView;

/**
 * A generic listener to checked items in a {@link ListView}.
 * <p>
 * Note that the {@link ListView} is put into the requested choice mode only
 * after at least one bound {@link Checkable} was checked. The current
 * {@link OnItemClickListener} of the {@link ListView} is then suspended until
 * the choices are cleared againg.
 * 
 * @see #bind(Checkable, int)
 * @see #clearChoices();
 */
public class GenericChoices<T> implements OnClickListener {

	public static int CHOICE_MODE_SINGLE = ListView.CHOICE_MODE_SINGLE;

	public static int CHOICE_MODE_MULTIPLE = ListView.CHOICE_MODE_MULTIPLE;

	private ListView listView;

	private int choiceMode;

	private OnItemClickListener listener;

	public GenericChoices(ListView listView, int choiceMode) {
		if (listView == null) {
			throw new IllegalArgumentException();
		}

		this.listView = listView;
		this.listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

		this.choiceMode = choiceMode;
	}

	public GenericChoices(ListView listView) {
		this(listView, ListView.CHOICE_MODE_MULTIPLE);
	}

	/**
	 * Bind the given {@link Checkable} in a {@link ListView} item to the given
	 * position.
	 * 
	 * @param checkable
	 *            checkable representing an item
	 * @param position
	 *            position of item
	 */
	public void bind(Checkable checkable, int position) {
		if (checkable == null) {
			throw new IllegalArgumentException();
		}

		View view = (View) checkable;

		view.setOnClickListener(null);

		checkable.setChecked(isChosen(listView, position));

		view.setOnClickListener(this);
	}

	private boolean isChosen(ListView listView, int position) {
		return listView.isItemChecked(position);
	}

	private void setChosen(ListView listView, int position, boolean chosen) {
		if (chosen && listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
			listener = listView.getOnItemClickListener();

			listView.setOnItemClickListener(null);

			listView.setChoiceMode(choiceMode);
		}

		listView.setItemChecked(position, chosen);

		changed(listView, position);
	}

	/**
	 * Notification of a change at the given position.
	 */
	protected void changed(ListView listView, int position) {
	}

	@Override
	public void onClick(View view) {

		int position = listView.getPositionForView(view);
		if (position != ListView.INVALID_POSITION) {
			setChosen(listView, position, ((Checkable) view).isChecked());
		}
	}

	public void clearChoices() {
		listView.clearChoices();

		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setOnItemClickListener(listener);
		listener = null;

		listView.invalidateViews();
	}

	public int size() {
		return listView.getCheckItemIds().length;
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