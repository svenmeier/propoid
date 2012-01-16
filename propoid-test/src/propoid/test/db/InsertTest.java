package propoid.test.db;

import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Insert;
import propoid.test.Bar;
import propoid.test.Foo;
import propoid.test.FooEx;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Test for {@link Insert}.
 */
public class InsertTest extends ApplicationTestCase<Application> {

	private Repository repository;

	public InsertTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);
	}

	@Override
	protected void tearDown() throws Exception {
		repository.close();

		super.tearDown();
	}

	public void testFoo() throws Exception {

		Foo foo = new Foo();
		foo.barP.set(null);

		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}

	public void testFooWithBar() throws Exception {

		Foo foo = new Foo();
		foo.barP.set(new Bar());

		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(1, repository.query(new Bar()).count());
	}

	public void testFooEx() throws Exception {
		repository.insert(new Foo());
		repository.insert(new FooEx());

		assertEquals(2, repository.query(new Foo()).count());
	}

	public void testFooWithExistingBar() throws Exception {
		repository.insert(new Bar());

		Foo foo = new Foo();
		foo.barP.set(repository.query(new Bar()).single());
		repository.insert(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(1, repository.query(new Bar()).count());
	}
}