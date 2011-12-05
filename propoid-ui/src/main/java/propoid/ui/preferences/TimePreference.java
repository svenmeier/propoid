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
package propoid.ui.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {

	private static final int HOUR = 60 * 60 * 1000;

	private static final int MINUTE = 60 * 1000;

	private TimePicker picker;

	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setPersistent(true);
	}

	@Override
	protected View onCreateDialogView() {
		picker = new TimePicker(getContext());
		picker.setIs24HourView(true);

		int value;
		try {
			value = getPersistedInt(0);
		} catch (Exception ex) {
			value = 0;
		}
		picker.setCurrentHour(value / HOUR);
		picker.setCurrentMinute((value % HOUR) / MINUTE);

		return picker;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		persistInt(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
	}

	@Override
	public void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			persistInt(picker.getCurrentHour() * HOUR
					+ picker.getCurrentMinute() * 60 * 1000);
		}
	}
}