package propoid.db.operation;

import propoid.db.BuildConfig;
import propoid.db.Repository;
import propoid.db.cascading.DefaultCascading;
import propoid.db.locator.InMemoryLocator;
import propoid.db.operation.Delete;
import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class DeleteTest {

	private Repository repository;

	@Before
	public void setUp() throws Exception {

		repository = new Repository(RuntimeEnvironment.application, new InMemoryLocator());

		((DefaultCascading) repository.cascading).setCascaded(new Foo().barP);
		((DefaultCascading) repository.cascading).setCascaded(new Foo().barsP);

		Foo foo = new Foo();
		foo.barP.set(new Bar());
		foo.barsP.set(Arrays.asList(new Bar(), new Bar()));
		repository.insert(foo);
	}

	@After
	public void tearDown() throws Exception {
		repository.close();
	}

	@Test
	public void test() throws Exception {

		Foo foo = repository.query(new Foo()).single();
		repository.delete(foo);

		assertEquals(0, repository.query(new Foo()).count());
		assertEquals(0, repository.query(new Bar()).count());
	}
}