package propoid.test.ui;

import propoid.test.Foo;
import propoid.ui.bind.TextBinding;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

/**
 * Test for {@link TextBinding}.
 */
public class TextBindingTest extends AbstractBindingTest {

	private EditText editText;

	@Override
	protected View createContentView(Context context) {
		editText = new EditText(context);
		return editText;
	}

	public void test() throws Throwable {
		final Foo foo = new Foo();

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextBinding.string(foo.stringP, editText);

				assertEquals("", editText.getText().toString());
			}
		});

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				foo.stringP.set("fromProperty");

				assertEquals("fromProperty", editText.getText().toString());
			}
		});

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				editText.setText("fromView");

				assertEquals("fromView", foo.stringP.get());
			}
		});
	}
}