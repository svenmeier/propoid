package propoid.db.operation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Iterator;

import propoid.db.BuildConfig;
import propoid.db.Order;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.Where;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test for {@link Query}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
public class QueryTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {
		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);

		Foo foo = new Foo();
		foo.barP.set(new Bar());
		foo.barsP.set(Arrays.asList(new Bar(), new Bar()));
		repository.insert(foo);

		repository.insert(new FooEx());
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void testFoo() {

		Iterator<Foo> foos = repository.query(new Foo()).list().iterator();
		assertTrue(foos.hasNext());
		assertTrue(foos.next().getClass() == Foo.class);
		assertTrue(foos.hasNext());
		assertTrue(foos.next().getClass() == FooEx.class);
		assertFalse(foos.hasNext());
	}

	@Test
	public void testFooEx() {

		Iterator<FooEx> foos = repository.query(new FooEx()).list().iterator();
		assertTrue(foos.hasNext());
		assertTrue(foos.next().getClass() == FooEx.class);
		assertFalse(foos.hasNext());
	}

	@Test
	public void testFooOrdered() {
		Foo foo = new Foo();
		Bar bar = new Bar();

		Iterator<Foo> descending = repository.query(foo)
				.list(Order.descending(foo.barP, bar.intP), //
						Order.descending(foo.barP, bar.stringP)) //
				.iterator();
		assertTrue(descending.hasNext());
		assertTrue(descending.next().getClass() == Foo.class);
		assertTrue(descending.hasNext());
		assertTrue(descending.next().getClass() == FooEx.class);
		assertFalse(descending.hasNext());

		Iterator<Foo> ascending = repository.query(foo)
				.list(Order.ascending(foo.barP, bar.intP), //
						Order.ascending(foo.barP, bar.stringP)) //
				.iterator();
		assertTrue(ascending.hasNext());
		assertTrue(ascending.next().getClass() == FooEx.class);
		assertTrue(ascending.hasNext());
		assertTrue(ascending.next().getClass() == Foo.class);
		assertFalse(ascending.hasNext());
	}

	@Test
	public void testFooHasBarOrdered() {
		Foo foo = new Foo();
		Bar bar = new Bar();

		repository.query(foo, Where.has(foo.barP, bar, Where.any())).first(
				Order.ascending(foo.barP, bar.stringP));
	}

	@Test
	public void testFooHasBar() {
		Foo foo = new Foo();
		Bar bar = new Bar();

		assertEquals(1,
				repository.query(foo, Where.has(foo.barP, bar, Where.any()))
						.count());
	}

	@Test
	public void testFooHasNotBar() {
		Foo foo = new Foo();
		Bar bar = new Bar();

		assertEquals(
				1,
				repository.query(foo,
						Where.not(Where.has(foo.barP, bar, Where.any())))
						.count());
	}

	@Test
	public void testFooSetBar() {
		Foo foo = new Foo();

		repository.query(foo).set(foo.barP, null);

		assertEquals(2, repository.query(foo, Where.equal(foo.barP, null))
				.count());
	}

	@Test
	public void testFooExDelete() {
		FooEx fooEx = new FooEx();

		repository.query(fooEx).delete();

		assertEquals(1, repository.query(new Foo()).count());
	}

	@Test
	public void testFooSingle() {
		try {
			repository.query(new Foo()).single();
		} catch (RepositoryException expected) {
		}

		repository.query(new FooEx()).single();
	}
}