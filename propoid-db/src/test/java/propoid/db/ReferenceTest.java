package propoid.db;

import android.content.Intent;
import android.os.Bundle;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.aspect.Row;
import propoid.db.operation.Foo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ReferenceTest {

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

		assertEquals("propoid://propoid.db.operation.Foo/1",
				new Reference<Foo>(foo).toString());
	}

	@Test
	public void testFromString() throws Exception {
		Reference<Propoid> reference = Reference.from("propoid://propoid.db.operation.Foo/1");

		assertEquals(Foo.class, reference.type);
		assertEquals(1l, reference.id);
	}

	@Test
	public void testToIntent() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		Intent intent = new Intent();

		Reference<Foo> reference = new Reference<>(foo);
		reference.to(intent);

		assertEquals(reference, Reference.from(intent));
	}

	@Test
	public void testToArguments() {
		Foo foo = new Foo();

		Row.setID(foo, 1l);

		Bundle arguments = new Bundle();

		Reference<Foo> reference = new Reference<>(foo);
		reference.to(arguments);

		assertEquals(reference, Reference.from(arguments));
	}

	@Test
	public void testFromStringNull() throws Exception {
		assertEquals(null, Reference.from((String) null));
	}

	@Test
	public void testFromInvalid() throws Exception {
		assertNull(Reference.from("propoid.db.operation.Quux/1"));
	}

	@Test
	public void testTransient() {
		try {
			new Reference<Foo>(Foo.class, Row.TRANSIENT);

			fail();
		} catch (IllegalArgumentException expected) {
		}
	}
}