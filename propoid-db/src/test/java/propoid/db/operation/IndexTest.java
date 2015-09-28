package propoid.db.operation;

import propoid.db.BuildConfig;
import propoid.db.Order;
import propoid.db.Repository;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Index;
import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Test for {@link Index}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class IndexTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {
		repository = new Repository(RuntimeEnvironment.application, new InMemoryLocator());
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void test() throws Exception {

		Foo foo = new Foo();
		repository.index(foo, true, Order.ascending(foo.intP),
				Order.ascending(foo.longP));

		repository.index(foo, false, Order.descending(foo.intP),
				Order.descending(foo.longP));
	}
}