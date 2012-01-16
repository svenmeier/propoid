package propoid.test.ui;

import propoid.test.Foo;
import propoid.ui.bind.CheckBinding;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

/**
 * Test for {@link CheckBinding}.
 */
public class CheckBindingTest extends AbstractBindingTest {

	private CheckBox checkBox;

	@Override
	protected View createContentView(Context context) {
		checkBox = new CheckBox(context);
		return checkBox;
	}

	public void test() throws Throwable {
		final Foo foo = new Foo();

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				new CheckBinding(foo.booleanP, checkBox);
			}
		});

		assertEquals(true, checkBox.isChecked());

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				foo.booleanP.set(false);
			}
		});

		assertEquals(false, checkBox.isChecked());

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				checkBox.setChecked(true);
			}
		});

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertEquals(Boolean.TRUE, foo.booleanP.get());
			}
		});
	}
}