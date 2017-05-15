package propoid.ui;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
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
import java.util.concurrent.atomic.AtomicInteger;

import propoid.core.Property;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.References;
import propoid.db.RepositoryException;
import propoid.db.operation.Query;
import propoid.ui.list.MatchListAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MatchAdapterTest {

	private ListView listView;
	private ActivityController<FragmentActivity> controller;

	@Before
	public void setUp() {
		controller = Robolectric.buildActivity(FragmentActivity.class).create();

		Activity activity = controller.get();

		listView = new ListView(activity);
		activity.setContentView(listView);
	}

	@Test
	public void test() throws Exception {

		controller.start();

		final AtomicBoolean clearInvoked = new AtomicBoolean();
		final AtomicInteger listedInvoked = new AtomicInteger();
		final AtomicInteger setItemsInvoked = new AtomicInteger();

		final List list = new ArrayList() {
			@Override
			public void clear() {
				super.clear();

				clearInvoked.set(true);
			}
		};

		Match match = new Match<Foo>() {

			@Override
			public Foo getPrototype() {
				return new Foo();
			}

			@Override
			public Uri getUri() {
				return Query.getUri(Foo.class);
			}

			@Override
			public List<Foo> list(Order... ordering) {
				fail();

				return null;
			}

			@Override
			public List<Foo> list(Range range, Order... ordering) {
				listedInvoked.set(listedInvoked.get() + 1);

				return list;
			}

			@Override
			public References<Foo> references() {
				fail();

				return null;
			}

			@Override
			public Foo first(Order... ordering) {
				fail();

				return null;
			}

			@Override
			public Foo single() throws RepositoryException {
				fail();

				return null;
			}

			@Override
			public long count() {
				return 0;
			}

			@Override
			public <T> T max(Property<T> property) {
				fail();

				return null;
			}

			@Override
			public <T> T min(Property<T> property) {
				fail();

				return null;
			}

			@Override
			public <T> T sum(Property<T> property) {
				fail();

				return null;
			}

			@Override
			public <T> T avg(Property<T> property) {
				fail();

				return null;
			}

			@Override
			public void delete() {
				fail();
			}

			@Override
			public <T> void set(Property<T> property, T value) {
				fail();
			}
		};

		MatchListAdapter<Foo> adapter = new MatchListAdapter<Foo>(match) {
			@Override
			protected void bind(int position, View view, Foo item) {
			}

			@Override
			public void setItems(List<Foo> items) {
				super.setItems(items);

				setItemsInvoked.set(setItemsInvoked.get() + 1);
			}
		};

		listView.setAdapter(adapter);

		assertEquals(0, listedInvoked.get());
		assertEquals(0, setItemsInvoked.get());

		adapter.initLoader(0, controller.get());

		assertEquals(1, listedInvoked.get());
		assertEquals(2, setItemsInvoked.get()); // bug in loadermanager: onLoadFinished is called twice :(

		adapter.initLoader(0, controller.get());

		assertEquals(1, listedInvoked.get());
		assertEquals(3, setItemsInvoked.get());

		adapter.restartLoader(0, controller.get());

		assertEquals(2, listedInvoked.get());
		assertEquals(4, setItemsInvoked.get());

		controller.stop().destroy();

		assertTrue(clearInvoked.get());
	}
}
