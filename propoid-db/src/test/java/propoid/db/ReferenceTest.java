package propoid.db;

import junit.framework.TestCase;
import propoid.core.Propoid;
import propoid.db.aspect.Row;

public class ReferenceTest extends TestCase {

	public void testToString() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		assertEquals("propoid.db.Foo/1", new Reference<Foo>(foo).toString());
	}

	public void testFromStringNull() throws Exception {
		assertEquals(null, Reference.fromString(null));
	}

	public void testFromString() throws Exception {
		Reference.fromString("propoid.db.Foo/1");
	}
}

class Foo extends Propoid {

}
