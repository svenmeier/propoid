package propoid.db.operation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import propoid.db.BuildConfig;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.aspect.Row;
import propoid.db.locator.InMemoryLocator;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link Lookup}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class LookupTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {

		repository = new Repository(RuntimeEnvironment.application, new InMemoryLocator());

		repository.insert(new Foo());
		repository.insert(new FooEx());
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void testFoo() {

		Foo foo = repository.query(new Foo()).list().get(0);

		Reference<Foo> reference = new Reference<Foo>(foo);

		assertEquals(Row.getID(foo), Row.getID(repository.lookup(reference)));
	}

	@Test
	public void testFooEx() {

		FooEx fooEx = repository.query(new FooEx()).list().get(0);

		Reference<Foo> reference = new Reference<Foo>(fooEx);

		assertEquals(Row.getID(fooEx), Row.getID(repository.lookup(reference)));
	}
}