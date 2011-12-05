package propoid.test.db;

import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Update;
import propoid.test.Bar;
import propoid.test.Foo;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Test for {@link Update}.
 */
public class UpdateTest extends ApplicationTestCase<Application> {

	private Repository repository;

	public UpdateTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);

		Foo foo = Foo.sample();
		repository.insert(foo);
	}

	@Override
	protected void tearDown() throws Exception {
		repository.close();

		super.tearDown();
	}

	public void testFoo() throws Exception {

		Foo foo = repository.query(new Foo()).single();
		foo.barP.set(null);

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}

	public void testFooWithBar() throws Exception {

		Foo foo = repository.query(new Foo()).single();
		foo.barP.set(new Bar());

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(1, repository.query(new Bar()).count());

		foo.barP.set(new Bar());

		repository.update(foo);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(2, repository.query(new Bar()).count());

		foo.barP.set(null);

		assertEquals(1, repository.query(new Foo()).count());
		assertEquals(2, repository.query(new Bar()).count());
	}
}