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
import android.content.SharedPreferences.Editor;
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
 * If you specify a default value, it will automagically be converted to the
 * requested type. Use
 * {@link PreferenceManager#setDefaultValues(Context, int, boolean)} to
 * initialize the defaults.
 * <p>
 * Now you can get access to the actual preference with:
 * 
 * <pre>
 * @code{
 * 	Preference&lt;Boolean&gt; myPreference = Preference.getBoolean(context,
 * 			R.string.my_preference);
 * }
 * </pre>
 * 
 * @see #get()
 * @see #set(Object)
 */
public abstract class Preference<T> {

	protected String key;

	protected SharedPreferences preferences;

	protected Preference(Context context, int id) {
		this.key = context.getString(id);

		this.preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	/**
	 * Get the value.
	 */
	public T get() {
		try {
			return getImpl();
		} catch (ClassCastException ex) {
			String fromPreferences = preferences.getString(key, null);

			return parse(fromPreferences);
		}
	}

	/**
	 * Parse a default value if the preference was initialized with a string
	 * from <code>preferences.xml</code>
	 * 
	 * @see PreferenceManager#setDefaultValues(Context, int, boolean)
	 */
	protected abstract T parse(String fromPreferences);

	protected abstract T getImpl();

	/**
	 * Set the value.
	 */
	public void set(T t) {
		Editor edit = preferences.edit();
		setImpl(edit, t);
		edit.commit();
	}

	protected abstract void setImpl(Editor edit, T t);

	/**
	 * Get a {@link String} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @return preference
	 */
	public static Preference<String> getString(Context context, int id) {
		return getString(context, id, null);
	}

	/**
	 * Get a {@link String} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @param defaultValue
	 *            default value
	 * @return preference
	 */
	public static Preference<String> getString(Context context, int id,
			final String defaultValue) {
		return new Preference<String>(context, id) {
			@Override
			protected String getImpl() {
				return preferences.getString(key, defaultValue);
			}

			@Override
			protected String parse(String fromPreferences) {
				return fromPreferences;
			}

			@Override
			protected void setImpl(Editor edit, String value) {
				edit.putString(key, value);
			}
		};
	}

	/**
	 * Get an {@link Integer} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @return preference
	 */
	public static Preference<Integer> getInteger(Context context, int id) {
		return getInteger(context, id, 0);
	}

	/**
	 * Get an {@link Integer} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @param defaultValue
	 *            default value
	 * @return preference
	 */
	public static Preference<Integer> getInteger(Context context, int id,
			final int defaultValue) {
		return new Preference<Integer>(context, id) {
			@Override
			protected Integer getImpl() {
				return preferences.getInt(key, defaultValue);
			}

			@Override
			protected Integer parse(String fromPreferences) {
				return Integer.valueOf(fromPreferences);
			}

			@Override
			protected void setImpl(Editor edit, Integer value) {
				edit.putInt(key, value);
			}
		};
	}

	/**
	 * Get a {@link Long} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @return preference
	 */
	public static Preference<Long> getLong(Context context, int id) {
		return getLong(context, id, 0);
	}

	/**
	 * Get an {@link Integer} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @param defaultValue
	 *            default value
	 * @return preference
	 */
	public static Preference<Long> getLong(Context context, int id,
			final long defaultValue) {
		return new Preference<Long>(context, id) {
			@Override
			protected Long getImpl() {
				return preferences.getLong(key, defaultValue);
			}

			@Override
			protected Long parse(String fromPreferences) {
				return Long.valueOf(fromPreferences);
			}

			@Override
			protected void setImpl(Editor edit, Long value) {
				edit.putLong(key, value);
			}
		};
	}

	/**
	 * Get a {@link Float} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @return preference
	 */
	public static Preference<Float> getFloat(Context context, int id) {
		return getFloat(context, id, 0f);
	}

	/**
	 * Get a {@link Float} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @param defaultValue
	 *            default value
	 * @return preference
	 */
	public static Preference<Float> getFloat(Context context, int id,
			final float defaultValue) {
		return new Preference<Float>(context, id) {
			@Override
			protected Float getImpl() {
				return preferences.getFloat(key, defaultValue);
			}

			@Override
			protected Float parse(String fromPreferences) {
				return Float.valueOf(fromPreferences);
			}

			@Override
			protected void setImpl(Editor edit, Float value) {
				edit.putFloat(key, value);
			}
		};
	}

	/**
	 * Get a {@link Boolean} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @return preference
	 */
	public static Preference<Boolean> getBoolean(Context context, int id) {
		return getBoolean(context, id, false);
	}

	/**
	 * Get a {@link Boolean} preference.
	 * 
	 * @param context
	 *            context
	 * @param id
	 *            string id of preference
	 * @param defaultValue
	 *            default value
	 * @return preference
	 */
	public static Preference<Boolean> getBoolean(Context context, int id,
			final boolean defaultValue) {
		return new Preference<Boolean>(context, id) {
			@Override
			protected Boolean getImpl() {
				return preferences.getBoolean(key, defaultValue);
			}

			@Override
			protected Boolean parse(String fromPreferences) {
				return Boolean.valueOf(fromPreferences);
			}

			@Override
			protected void setImpl(Editor edit, Boolean value) {
				edit.putBoolean(key, value);
			}
		};
	}
}