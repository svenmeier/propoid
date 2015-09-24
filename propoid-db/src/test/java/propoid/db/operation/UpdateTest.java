package propoid.db.operation;

import propoid.db.BuildConfig;
import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Update;
import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link Update}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
public class UpdateTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {
		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);
		((DefaultCascading) repository.cascading).setCascaded(new Foo().barsP);

		Foo foo = new Foo();
		repository.insert(foo);
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void testFoo() throws Exception {

		Foo foo = repository.query(new Foo()).single();

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}

	@Test
	public void testFooWithBar() throws Exception {

		Foo foo = repository.query(new Foo()).single();
		foo.barP.set(new Bar());
		foo.barsP.set(Arrays.asList(new Bar(), new Bar()));

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(3, repository.query(new Bar()).count());

		foo.barP.set(new Bar());
		foo.barsP.set(Arrays.asList(new Bar(), new Bar()));

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(3, repository.query(new Bar()).count());

		foo.barP.set(null);
		foo.barsP.set(Collections.<Bar>emptyList());

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}
}