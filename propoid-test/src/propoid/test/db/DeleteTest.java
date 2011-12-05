package propoid.test.db;

import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Delete;
import propoid.test.Bar;
import propoid.test.Foo;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Test for {@link Delete}.
 */
public class DeleteTest extends ApplicationTestCase<Application> {

	private Repository repository;

	public DeleteTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new Repository(new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);

		Foo foo = Foo.sample();
		foo.barP.set(new Bar());
		repository.insert(foo);
	}

	@Override
	protected void tearDown() throws Exception {
		repository.close();

		super.tearDown();
	}

	public void test() throws Exception {

		Foo foo = repository.query(new Foo()).single();
		repository.delete(foo);

		assertEquals(0, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}
}