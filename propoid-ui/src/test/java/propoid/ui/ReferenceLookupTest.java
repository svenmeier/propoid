package propoid.ui;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.Reference;
import propoid.db.References;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.aspect.Row;
import propoid.db.operation.Query;
import propoid.ui.list.MatchAdapter;
import propoid.ui.list.ReferenceLookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ReferenceLookupTest {

	private ActivityController<Activity> controller;

	@Mock
	Repository repository;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		controller = Robolectric.buildActivity(Activity.class).create();

		Activity activity = controller.get();
		activity.setContentView(new TextView(activity));
	}

	@Test
	public void test() throws Exception {

		final AtomicInteger lookupInvoked = new AtomicInteger();
		final AtomicInteger onLookupInvoked = new AtomicInteger();

		final Foo foo = new Foo();
		Row.setID(foo, 1);
		Reference<Foo> reference = new Reference<>(foo);

		when(repository.lookup(reference)).thenAnswer(new Answer<Foo>() {
			@Override
			public Foo answer(InvocationOnMock invocation) throws Throwable {
				lookupInvoked.set(lookupInvoked.get() + 1);

				return foo;
			}
		});

		controller.start();

		ReferenceLookup<Foo> lookup = new ReferenceLookup<Foo>(repository, reference) {
			@Override
			protected void onLookup(Foo propoid) {
				onLookupInvoked.set(onLookupInvoked.get() + 1);
			}
		};

		assertEquals(0, lookupInvoked.get());
		assertEquals(0, onLookupInvoked.get());

		lookup.initLoader(0, controller.get());

		assertEquals(1, lookupInvoked.get());
		assertEquals(2, onLookupInvoked.get()); // bug in loadermanager: onLoadFinished is called twice :(

		lookup.initLoader(0, controller.get());

		assertEquals(1, lookupInvoked.get());
		assertEquals(3, onLookupInvoked.get());

		lookup.restartLoader(0, controller.get());

		assertEquals(2, lookupInvoked.get());
		assertEquals(4, onLookupInvoked.get());

		controller.stop().destroy();
	}
}
