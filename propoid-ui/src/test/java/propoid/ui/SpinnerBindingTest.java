package propoid.ui;

import android.app.Activity;
import android.util.Xml.Encoding;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import propoid.ui.bind.SpinnerBinding;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link SpinnerBinding}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = propoid.ui.BuildConfig.class)
public class SpinnerBindingTest {

	private Spinner spinner;

	@Before
	public void setUp() {
		Activity activity = Robolectric.buildActivity(Activity.class).create().get();;

		spinner = new Spinner(activity);
		activity.setContentView(spinner);
	}

	@Test
	public void test() throws Throwable {
		final Foo foo = new Foo();

		SpinnerBinding.enumeration(foo.enumP, spinner);

		assertEquals(Encoding.UTF_8, spinner.getSelectedItem());

		foo.enumP.set(Encoding.UTF_16);

		assertEquals(Encoding.UTF_16, spinner.getSelectedItem());

		spinner.setSelection(0);

		assertEquals(Encoding.US_ASCII, foo.enumP.get());
	}
}