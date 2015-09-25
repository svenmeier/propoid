package propoid.db;

import junit.framework.TestCase;

import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.naming.DefaultNaming;
import propoid.db.operation.Foo;
import propoid.db.operation.FooEx;

public class DefaultNamingTest extends TestCase {

	public void test() throws Exception {
		Repository repository = null;

		DefaultNaming naming = new DefaultNaming();

		assertEquals("Foo", naming.table(repository, Foo.class));
		assertEquals("Foo", naming.table(repository, FooEx.class));

		assertEquals(null, naming.encodeType(repository, Foo.class));
		assertEquals("FooEx",
				naming.encodeType(repository, FooEx.class));

		assertEquals(Foo.class, naming.decodeType(repository, Foo.class, null));
		assertEquals(FooEx.class,
				naming.decodeType(repository, Foo.class, "FooEx"));
		assertEquals(FooEx.class,
				naming.decodeType(repository, FooEx.class, "FooEx"));
		assertEquals(FooEx.class, naming.decodeType(repository,
				Foo.class, "propoid.db.operation.FooEx"));
		assertEquals(FooEx.class, naming.decodeType(repository,
				FooEx.class, "propoid.db.operation.FooEx"));
	}
}