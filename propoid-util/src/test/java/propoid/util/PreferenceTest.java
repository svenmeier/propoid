package propoid.util;

import java.util.Arrays;
import java.util.Collections;

import propoid.util.content.Preference;
import propoid.util.content.Preference.OnChangeListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link Preference}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
public class PreferenceTest {

	private SharedPreferences preferences;

	@Before
	public void setUp() throws Exception {
		preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

	}

	@After
	public void tearDown() throws Exception {
		preferences.edit().clear().commit();
	}

	@Test
	public void testBoolean() {
		Preference<Boolean> preference = Preference.getBoolean(
				RuntimeEnvironment.application, R.string.preference_test);

		assertFalse(preference.get());

		preference.set(true);

		assertTrue(preference.get());
	}

	@Test
	public void testInteger() {
		Preference<Integer> preference = Preference.getInt(RuntimeEnvironment.application, R.string.preference_test);

		assertEquals(Integer.valueOf(0), preference.get());

		preference.set(1);

		assertEquals(Integer.valueOf(1), preference.get());

		preference.range(2, 2);

		assertEquals(Integer.valueOf(2), preference.get());
	}

	@Test
	public void testLong() {
		Preference<Long> preference = Preference.getLong(RuntimeEnvironment.application, R.string.preference_test);

		assertEquals(Long.valueOf(0), preference.get());

		preference.set(1l);

		assertEquals(Long.valueOf(1), preference.get());

		preference.range(2l, 2l);

		assertEquals(Long.valueOf(2), preference.get());
	}

	@Test
	public void testFloat() {
		Preference<Float> preference = Preference.getFloat(RuntimeEnvironment.application, R.string.preference_test);

		assertEquals(Float.valueOf(0), preference.get());

		preference.set(1f);

		assertEquals(Float.valueOf(1), preference.get());

		preference.range(2f, 2f);

		assertEquals(Float.valueOf(2), preference.get());
	}

	@Test
	public void testString() {
		Preference<String> preference = Preference.getString(
				RuntimeEnvironment.application, R.string.preference_test);

		assertEquals(null, preference.get());

		preference.set("test");

		assertEquals("test", preference.get());
	}

	@Test
	public void testStringList() {
		Preference<String> preference = Preference.getString(RuntimeEnvironment.application, R.string.preference_test);

		assertEquals(0, preference.getList().size());

		preference.setList(Arrays.asList("test1", "test2"));

		assertEquals(Arrays.asList("test1", "test2"), preference.getList());

		preference.setList(Arrays.asList("test3"));

		assertEquals(Arrays.asList("test3"), preference.getList());

		preference.setList(Collections.<String> emptyList());

		assertTrue(preference.getList().isEmpty());
	}

	@Test
	public void testListen() throws Throwable {
		Preference<String> preference = Preference.getString(
				RuntimeEnvironment.application, R.string.preference_test);

		final boolean[] changed = { false };

		try {
			preference.listen(new OnChangeListener() {
				@Override
				public void onChanged() {
					changed[0] = true;
				}
			});

			preferences
					.edit()
					.putString(
							RuntimeEnvironment.application
									.getString(R.string.preference_test),
							"foo").commit();

			assertTrue(changed[0]);
		} finally {
			preference.listen(null);
		}
	}
}