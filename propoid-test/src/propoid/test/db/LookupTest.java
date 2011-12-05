package propoid.test.db;

import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.aspect.Row;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Lookup;
import propoid.test.Foo;
import propoid.test.FooEx;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Test for {@link Lookup}.
 */
public class LookupTest extends ApplicationTestCase<Application> {

	private Repository repository;

	public LookupTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new Repository(new InMemoryLocator());

		repository.insert(new Foo());
		repository.insert(new FooEx());
	}

	@Override
	protected void tearDown() throws Exception {
		repository.close();

		super.tearDown();
	}

	public void testFoo() {

		Foo foo = repository.query(new Foo()).list().get(0);

		Reference<Foo> reference = new Reference<Foo>(foo);

		assertEquals(Row.getID(foo), Row.getID(repository.lookup(reference)));
	}

	public void testFooEx() {

		FooEx fooEx = repository.query(new FooEx()).list().get(0);

		Reference<Foo> reference = new Reference<Foo>(fooEx);

		assertEquals(Row.getID(fooEx), Row.getID(repository.lookup(reference)));
	}
}