package propoid.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * A {@link Checkable} layout.
 */
public class CheckableLayout extends FrameLayout implements Checkable {

	private static final int[] CHECKED_STATE = { android.R.attr.state_checked };

	private boolean checked;

	public CheckableLayout(Context context) {
		super(context);
	}

	public CheckableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;

		refreshDrawableState();

		invalidate();
	}

	public void toggle() {
		setChecked(!checked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace
				+ CHECKED_STATE.length);
		if (checked) {
			mergeDrawableStates(drawableState, CHECKED_STATE);
		}
		return drawableState;
	}
}