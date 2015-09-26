package propoid.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import propoid.core.Property;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.References;
import propoid.db.RepositoryException;
import propoid.ui.list.MatchAdapter;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class MatchAdapterTest {

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
	public void testDestroyActivity() throws Exception {

		controller.start();

		final AtomicBoolean cleared = new AtomicBoolean();

		final List list = new ArrayList() {
			@Override
			public void clear() {
				super.clear();

				cleared.set(true);
			}
		};

		Match match = new Match<Foo>() {
			@Override
			public List<Foo> list(Order... ordering) {
				return list;
			}

			@Override
			public List<Foo> list(Range range, Order... ordering) {
				return list;
			}

			@Override
			public References<Foo> references() {
				return null;
			}

			@Override
			public Foo first(Order... ordering) {
				return null;
			}

			@Override
			public Foo single() throws RepositoryException {
				return null;
			}

			@Override
			public long count() {
				return 0;
			}

			@Override
			public <T> T max(Property<T> property) {
				return null;
			}

			@Override
			public <T> T min(Property<T> property) {
				return null;
			}

			@Override
			public <T> T sum(Property<T> property) {
				return null;
			}

			@Override
			public <T> T avg(Property<T> property) {
				return null;
			}

			@Override
			public void delete() {
			}

			@Override
			public <T> void set(Property<T> property, T value) {
			}
		};

		MatchAdapter<Foo> adapter = new MatchAdapter<Foo>(match) {
			@Override
			protected void bind(int position, View view, Foo item) {

			}
		};

		listView.setAdapter(adapter);

		adapter.restart(0, controller.get());

		controller.stop().destroy();

		assertTrue(cleared.get());
	}
}
