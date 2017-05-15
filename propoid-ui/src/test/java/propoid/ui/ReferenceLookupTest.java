package propoid.ui;

import android.app.Activity;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.concurrent.atomic.AtomicInteger;

import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.aspect.Row;
import propoid.ui.list.ReferenceLookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
