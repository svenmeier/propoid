package propoid.test.db;

import propoid.db.Order;
import propoid.db.Repository;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Index;
import propoid.test.Foo;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Test for {@link Index}.
 */
public class IndexTest extends ApplicationTestCase<Application> {

	private Repository repository;

	public IndexTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new Repository(new InMemoryLocator());
	}

	@Override
	protected void tearDown() throws Exception {
		repository.close();

		super.tearDown();
	}

	public void test() throws Exception {

		Foo foo = new Foo();
		repository.index(foo, true, Order.ascending(foo.intP),
				Order.ascending(foo.longP));

		repository.index(foo, false, Order.descending(foo.intP),
				Order.descending(foo.longP));
	}
}