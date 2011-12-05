package propoid.test.ui;

import android.content.Context;
import android.view.View;

/**
 */
public abstract class AbstractBindingTest extends
		android.test.ActivityInstrumentationTestCase2<TestActivity> {

	private TestActivity activity;

	public AbstractBindingTest() {
		super("propoid.test", TestActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		activity = getActivity();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				activity.setContentView(createContentView(activity));
			}
		});
	}

	protected abstract View createContentView(Context context);
}