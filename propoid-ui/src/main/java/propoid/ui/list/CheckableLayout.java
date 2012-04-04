package propoid.ui.list;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * A layout which can be used for the chosen items of a {@link ListView}
 * 
 * @see ListView#setChoiceMode(int)
 * @see ListView#setItemChecked(int, boolean)
 */
public class CheckableLayout extends FrameLayout implements Checkable {

	private static final int[] STATES_CHECKED = { R.attr.state_checked };

	private boolean checked = false;

	public CheckableLayout(Context context) {
		super(context, null);
	}

	public CheckableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setChecked(boolean b) {
		checked = b;

		refreshDrawableState();
	}

	public boolean isChecked() {
		return checked;
	}

	public void toggle() {
		setChecked(!checked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, STATES_CHECKED);
		}
		return drawableState;
	}
}