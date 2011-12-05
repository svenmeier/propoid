package propoid.test.ui;

import propoid.test.Foo;
import propoid.ui.bind.SpinnerBinding;
import android.content.Context;
import android.util.Xml.Encoding;
import android.view.View;
import android.widget.Spinner;

/**
 * Test for {@link SpinnerBinding}.
 */
public class SpinnerBindingTest extends AbstractBindingTest {

	private Spinner spinner;

	@Override
	protected View createContentView(Context context) {
		spinner = new Spinner(context);
		return spinner;
	}

	public void test() throws Throwable {
		final Foo foo = new Foo();

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				SpinnerBinding.enumeration(foo.enumP, spinner);
			}
		});

		assertEquals(null, spinner.getSelectedItem());

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				foo.enumP.set(Encoding.UTF_8);
			}
		});

		assertEquals(Encoding.UTF_8, spinner.getSelectedItem());

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				spinner.setSelection(0);
			}
		});

		// item selection is delivered to listener only after the next UI event
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertEquals(Encoding.US_ASCII, foo.enumP.get());
			}
		});
	}
}