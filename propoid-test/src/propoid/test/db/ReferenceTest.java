package propoid.test.db;

import junit.framework.TestCase;
import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.aspect.Row;

public class ReferenceTest extends TestCase {

	public void testEquals() {
		assertTrue(new Reference<Foo>(Foo.class, 1).equals(new Reference<Foo>(
				Foo.class, 1)));
		assertFalse(new Reference<Foo>(Foo.class, 1).equals(new Reference<Foo>(
				Foo.class, 2)));
	}

	public void testHashCode() {
		assertTrue(new Reference<Foo>(Foo.class, 1).hashCode() == new Reference<Foo>(
				Foo.class, 1).hashCode());
		assertFalse(new Reference<Foo>(Foo.class, 1).hashCode() == new Reference<Foo>(
				Foo.class, 2).hashCode());
	}

	public void testToString() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		assertEquals("propoid://propoid.test.db.ReferenceTest$Foo/1",
				new Reference<Foo>(foo).toString());
	}

	public void testFromString() throws Exception {
		Reference.from("propoid://propoid.test.db.ReferenceTest$Foo/1");
	}

	public void testFromStringNull() throws Exception {
		assertEquals(null, Reference.from((String) null));
	}

	public void testFromInvalid() throws Exception {
		assertNull(Reference.from("propoid.test.db.Foo/1"));
	}

	public void testTransient() {
		try {
			new Reference<Foo>(Foo.class, Row.TRANSIENT);
		} catch (IllegalArgumentException expected) {
		}
	}

	public static class Foo extends Propoid {

	}
}