package propoid.db;

import junit.framework.TestCase;

import org.junit.Test;

import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.aspect.Row;

public class ReferenceTest extends TestCase {

	@Test
	public void testEquals() {
		assertTrue(new Reference<Foo>(Foo.class, 1).equals(new Reference<Foo>(
				Foo.class, 1)));
		assertFalse(new Reference<Foo>(Foo.class, 1).equals(new Reference<Foo>(
				Foo.class, 2)));
	}

	@Test
	public void testHashCode() {
		assertTrue(new Reference<Foo>(Foo.class, 1).hashCode() == new Reference<Foo>(
				Foo.class, 1).hashCode());
		assertFalse(new Reference<Foo>(Foo.class, 1).hashCode() == new Reference<Foo>(
				Foo.class, 2).hashCode());
	}

	@Test
	public void testToString() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		assertEquals("propoid://propoid.db.ReferenceTest$Foo/1",
				new Reference<Foo>(foo).toString());
	}

	@Test
	public void testFromString() throws Exception {
		Reference<Propoid> reference = Reference.from("propoid://propoid.db.ReferenceTest$Foo/1");

		assertEquals(Foo.class, reference.type);
		assertEquals(1l, reference.id);
	}

	@Test
	public void testFromStringNull() throws Exception {
		assertEquals(null, Reference.from((String) null));
	}

	@Test
	public void testFromInvalid() throws Exception {
		assertNull(Reference.from("propoid.db.Foo/1"));
	}

	@Test
	public void testTransient() {
		try {
			new Reference<Foo>(Foo.class, Row.TRANSIENT);

			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	public static class Foo extends Propoid {

	}
}