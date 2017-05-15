package propoid.ui;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import propoid.ui.list.GenericListAdapter;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GenericListAdapterTest {

	private ListView listView;
	private ActivityController<Activity> controller;

	@Before
	public void setUp() {
		controller = Robolectric.buildActivity(Activity.class).create();

		Activity activity = controller.get();

		listView = new ListView(activity);
		activity.setContentView(listView);
	}

	@Test
	public void test() throws Exception {
		List list = new ArrayList();

		final AtomicBoolean registered = new AtomicBoolean();
		final AtomicBoolean unregistered = new AtomicBoolean();

		GenericListAdapter<Foo> adapter = new GenericListAdapter<Foo>(list) {
			@Override
			protected void bind(int position, View view, Foo item) {

			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				super.registerDataSetObserver(observer);

				registered.set(true);
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				super.unregisterDataSetObserver(observer);

				unregistered.set(true);
			}
		};

		listView.setAdapter(adapter);

		assertTrue(registered.get());

		listView.setAdapter(null);

		assertTrue(unregistered.get());
	}
}
