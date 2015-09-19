/*
 * Copyright 2011 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package propoid.ui;

import android.app.Activity;
import android.app.Dialog;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

public class Index {

	private View parent;

	private SparseArray<View> views = new SparseArray<View>();

	private Index(View parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public <V extends View> V get(int id) {
		View view = views.get(id);

		if (view == null) {
			view = parent.findViewById(id);
			if (view == null) {
				throw new IllegalArgumentException("unknown id " + id);
			}
			views.put(id, view);
		}

		return (V) view;
	}

	/**
	 * Does any indexed {@link TextView} has an error.
	 * 
	 * @return {@code true} if error exists
	 * 
	 * @see TextView#getError()
	 */
	public boolean hasError() {
		for (int v = 0; v < views.size(); v++) {
			View view = views.valueAt(v);

			if (view instanceof TextView) {
				if (((TextView) view).getError() != null) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get the index for the given view.
	 * 
	 * @param view
	 *            view to get index for
	 * @return index
	 */
	public static Index get(View view) {
		Index index;

		Object tag = view.getTag();
		if (tag == null) {
			index = new Index(view);

			view.setTag(index);
		} else {
			if (tag instanceof Index) {
				index = (Index) tag;
			} else {
				throw new IllegalStateException("view tag is already set");
			}
		}

		return index;
	}

	/**
	 * Get the index for the given dialog.
	 * 
	 * @param dialog
	 *            dialog to get index for
	 * @return index
	 */
	public static Index get(Dialog dialog) {
		return get(dialog.getWindow().getDecorView());
	}

	/**
	 * Get the index for the given activity.
	 * 
	 * @param activity
	 *            activity to get index for
	 * @return index
	 */
	public static Index get(Activity activity) {
		return get(activity.getWindow().getDecorView());
	}
}