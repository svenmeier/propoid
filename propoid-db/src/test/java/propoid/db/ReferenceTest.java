package propoid.db;

import junit.framework.TestCase;
import propoid.core.Propoid;
import propoid.db.aspect.Row;

public class ReferenceTest extends TestCase {

	public void testToString() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		assertEquals("propoid://propoid.db.Foo/1",
				new Reference<Foo>(foo).toString());
	}

	public void testFromString() throws Exception {
		Reference.from("propoid://propoid.db.Foo/1");
	}

	public void testFromStringNull() throws Exception {
		assertEquals(null, Reference.from((String) null));
	}

	public void testFromInvalid() throws Exception {
		assertNull(Reference.from("propoid.db.Foo/1"));
	}
}

class Foo extends Propoid {

}
