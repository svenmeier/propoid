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
import android.util.Log;

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
public abstract class Preference<T extends Comparable<T>> {

	protected String key;

	protected SharedPreferences preferences;

	protected T fallback;

	protected T min;

	protected T max;

	protected Preference(Context context, int id, T fallback) {
		this.key = context.getString(id);

		this.preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		this.fallback = fallback;
	}

	public Preference<T> fallback(T fallback) {
		this.fallback = fallback;

		return this;
	}

	public Preference<T> range(T min, T max) {
		this.min = min;
		this.max = max;

		return this;
	}

	public Preference<T> minimum(T min) {
		this.min = min;

		return this;
	}

	public Preference<T> maximum(T max) {
		this.max = max;

		return this;
	}

	/**
	 * Get the value.
	 */
	public T get() {
		T value;
		try {
			value = getImpl();
		} catch (ClassCastException valueFromPreferenceIsString) {
			String fromPreferences = preferences.getString(key, null);

			value = parse(fromPreferences);
		}

		if (min != null && min.compareTo(value) == 1) {
			Log.w("propoid-util", key + " < " + min);
			value = min;
		}

		if (max != null && max.compareTo(value) == -1) {
			Log.w("propoid-util", key + " > " + max);
			value = max;
		}

		return value;
	}

	/**
	 * Parse a value if the preference was initialized with a string from
	 * <code>preferences.xml</code>
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
		return new Preference<String>(context, id, null) {
			@Override
			protected String getImpl() {
				return preferences.getString(key, fallback);
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
	public static Preference<Integer> getInt(Context context, int id) {
		return new Preference<Integer>(context, id, 0) {
			@Override
			protected Integer getImpl() {
				return preferences.getInt(key, fallback);
			}

			@Override
			protected Integer parse(String fromPreferences) {
				try {
					return Integer.valueOf(fromPreferences);
				} catch (NumberFormatException ex) {
					return fallback;
				}
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
		return new Preference<Long>(context, id, 0l) {
			@Override
			protected Long getImpl() {
				return preferences.getLong(key, fallback);
			}

			@Override
			protected Long parse(String fromPreferences) {
				try {
					return Long.valueOf(fromPreferences);
				} catch (NumberFormatException ex) {
					return fallback;
				}
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
		return new Preference<Float>(context, id, 0f) {
			@Override
			protected Float getImpl() {
				return preferences.getFloat(key, fallback);
			}

			@Override
			protected Float parse(String fromPreferences) {
				try {
					return Float.valueOf(fromPreferences);
				} catch (NumberFormatException ex) {
					return fallback;
				}
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
		return new Preference<Boolean>(context, id, false) {
			@Override
			protected Boolean getImpl() {
				return preferences.getBoolean(key, fallback);
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