package propoid.db.operation;

import propoid.db.BuildConfig;
import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Insert;
import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link Insert}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
public class InsertTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {
		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void testFoo() throws Exception {

		Foo foo = new Foo();
		foo.barP.set(null);

		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}

	@Test
	public void testFooWithBar() throws Exception {

		Foo foo = new Foo();
		foo.barP.set(new Bar());

		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(1, repository.query(new Bar()).count());
	}

	@Test
	public void testFooEx() throws Exception {
		repository.insert(new Foo());
		repository.insert(new FooEx());

		assertEquals(2, repository.query(new Foo()).count());
	}

	@Test
	public void testFooWithExistingBar() throws Exception {
		repository.insert(new Bar());

		Foo foo = new Foo();
		foo.barP.set(repository.query(new Bar()).single());
		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(1, repository.query(new Bar()).count());
	}
}