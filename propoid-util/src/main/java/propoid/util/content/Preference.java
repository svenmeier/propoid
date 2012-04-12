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
import android.preference.PreferenceManager;

/**
 * Simple preference wrapper offering typed access via string id.
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
 *   new Preferences(context, R.string.my_preference).getBoolean();
 * }
 * </pre>
 * 
 * Note that default value is automagically converted to the requested type. Use
 * {@link PreferenceManager#setDefaultValues(Context, int, boolean)} to
 * initialize defaults.
 */
public class Preference {

	private String key;

	private SharedPreferences preferences;

	/**
	 * Get access to the preference with the given string id in the given
	 * context.
	 * 
	 * @param context
	 * @param id
	 */
	public Preference(Context context, int id) {
		this.key = context.getString(id);

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean getBoolean() {
		try {
			return preferences.getBoolean(key, false);
		} catch (ClassCastException ex) {
			return Boolean.valueOf(preferences.getString(key, "false"));
		}
	}

	public int getInteger() {
		try {
			String foo = key;
			return preferences.getInt(foo, 0);
		} catch (ClassCastException ex) {
			return Integer.valueOf(preferences.getString(key, "0"));
		}
	}

	public long getLong() {
		try {
			return preferences.getLong(key, 0);
		} catch (ClassCastException ex) {
			return Long.valueOf(preferences.getString(key, "0"));
		}
	}

	public float getFloat() {
		try {
			return preferences.getFloat(key, 0f);
		} catch (ClassCastException ex) {
			return Float.valueOf(preferences.getString(key, "0"));
		}
	}

	public String getString() {
		return preferences.getString(key, null);
	}

	public void setBoolean(boolean value) {
		preferences.edit().putBoolean(key, value).commit();
	}

	public void setInteger(int value) {
		preferences.edit().putInt(key, value).commit();
	}

	public void setLong(long value) {
		preferences.edit().putLong(key, value).commit();
	}

	public void setFloat(float value) {
		preferences.edit().putFloat(key, value).commit();
	}
}