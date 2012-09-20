package propoid.test.util;

import java.util.Arrays;
import java.util.Collections;

import propoid.test.R;
import propoid.util.content.Preference;
import propoid.util.content.Preference.OnChangeListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;

/**
 * Test for {@link Preference}.
 */
public class PreferenceTest extends InstrumentationTestCase {

	private SharedPreferences preferences;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getInstrumentation().getContext());

	}

	@Override
	protected void tearDown() throws Exception {
		preferences.edit().clear().commit();
	}

	public void testBoolean() {
		Preference<Boolean> preference = Preference.getBoolean(
				getInstrumentation().getContext(), R.string.preference_test);

		assertFalse(preference.get());

		preference.set(true);

		assertTrue(preference.get());
	}

	public void testInteger() {
		Preference<Integer> preference = Preference.getInt(getInstrumentation()
				.getContext(), R.string.preference_test);

		assertEquals(Integer.valueOf(0), preference.get());

		preference.set(1);

		assertEquals(Integer.valueOf(1), preference.get());

		preference.range(2, 2);

		assertEquals(Integer.valueOf(2), preference.get());
	}

	public void testLong() {
		Preference<Long> preference = Preference.getLong(getInstrumentation()
				.getContext(), R.string.preference_test);

		assertEquals(Long.valueOf(0), preference.get());

		preference.set(1l);

		assertEquals(Long.valueOf(1), preference.get());

		preference.range(2l, 2l);

		assertEquals(Long.valueOf(2), preference.get());
	}

	public void testFloat() {
		Preference<Float> preference = Preference.getFloat(getInstrumentation()
				.getContext(), R.string.preference_test);

		assertEquals(Float.valueOf(0), preference.get());

		preference.set(1f);

		assertEquals(Float.valueOf(1), preference.get());

		preference.range(2f, 2f);

		assertEquals(Float.valueOf(2), preference.get());
	}

	public void testString() {
		Preference<String> preference = Preference.getString(
				getInstrumentation().getContext(), R.string.preference_test);

		assertEquals(null, preference.get());

		preference.set("test");

		assertEquals("test", preference.get());
	}

	public void testStringList() {
		Preference<String> preference = Preference.getString(
				getInstrumentation().getContext(), R.string.preference_test);

		assertEquals(0, preference.getList().size());

		preference.setList(Arrays.asList("test1", "test2"));

		assertEquals(Arrays.asList("test1", "test2"), preference.getList());

		preference.setList(Arrays.asList("test3"));

		assertEquals(Arrays.asList("test3"), preference.getList());

		preference.setList(Collections.<String> emptyList());

		assertTrue(preference.getList().isEmpty());
	}

	public void testListen() throws Throwable {
		Preference<String> preference = Preference.getString(
				getInstrumentation().getContext(), R.string.preference_test);

		final boolean[] changed = { false };

		try {
			preference.listen(new OnChangeListener() {
				@Override
				public void onChanged() {
					changed[0] = true;
				}
			});

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					preferences
							.edit()
							.putString(
									getInstrumentation()
											.getContext()
											.getString(R.string.preference_test),
									"foo").commit();
				}
			});

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					assertTrue(changed[0]);
				}
			});
		} finally {
			preference.listen(null);
		}
	}
}