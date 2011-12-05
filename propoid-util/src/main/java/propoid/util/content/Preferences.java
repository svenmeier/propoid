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
package propoid.util.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

/**
 * Simple preferences wrapper offering typed access to preferences via string
 * ids.
 * <p>
 * Declare preference key in strings.xml with:
 * 
 * <pre>
 * {@code
 *   <string name="my_preferences">my_preference</string>
 * }
 * </pre>
 * 
 * ... and the actual preference in preferences.xml:
 * 
 * <pre>
 * {@code
 *          <CheckBoxPreference
 *                 android:key="@string/my_preference"
 *                 android:title="@string/my_preference_title"
 *                 android:summary="@string/my_preference_summary"
 *                 android:defaultValue="true"
 * />
 * }
 * </pre>
 * 
 * Now you can get access to the actual preference with:
 * 
 * <pre>
 * {@code
 *   new Preferences(context).getBoolean(R.string.my_preference);
 * }
 * </pre>
 * 
 * Note that default values are automagically converted to the right type. Use
 * {@link PreferenceManager#setDefaultValues(Context, int, boolean)} to
 * initialize defaults.
 */
public class Preferences {

	private SharedPreferences preferences;
	private Context context;

	public Preferences(Context context) {
		this.context = context;

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		preferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public boolean getBoolean(int id) {
		try {
			return preferences.getBoolean(context.getString(id), false);
		} catch (ClassCastException ex) {
			return Boolean.valueOf(preferences.getString(context.getString(id),
					"false"));
		}
	}

	public int getInteger(int id) {
		try {
			String foo = context.getString(id);
			return preferences.getInt(foo, 0);
		} catch (ClassCastException ex) {
			return Integer.valueOf(preferences.getString(context.getString(id),
					"0"));
		}
	}

	public long getLong(int id) {
		try {
			return preferences.getLong(context.getString(id), 0);
		} catch (ClassCastException ex) {
			return Long.valueOf(preferences.getString(context.getString(id),
					"0"));
		}
	}

	public String getString(int id) {
		return preferences.getString(context.getString(id), null);
	}
}