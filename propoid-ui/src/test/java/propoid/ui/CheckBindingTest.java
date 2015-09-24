package propoid.ui;

import propoid.ui.bind.CheckBinding;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link CheckBinding}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = propoid.db.BuildConfig.class, emulateSdk = 18)
public class CheckBindingTest {

	private CheckBox checkBox;

	@Before
	public void setUp() {
		Activity context = Robolectric.buildActivity(Activity.class).create().get();;

		checkBox = new CheckBox(context);
	}

	@Test
	public void test() throws Throwable {
		final Foo foo = new Foo();

		new CheckBinding(foo.booleanP, checkBox);

		assertEquals(true, checkBox.isChecked());

		foo.booleanP.set(false);

		assertEquals(false, checkBox.isChecked());

		checkBox.setChecked(true);

		assertEquals(Boolean.TRUE, foo.booleanP.get());
	}
}