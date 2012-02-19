package propoid.db;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;

public class WhereTest extends TestCase {

	public void testWhere() throws Exception {
		Foo foo = new Foo();

		Where.greaterEqual(foo.bar, "A");
	}

	private class Foo extends Propoid {
		public final Property<String> bar = property();
	}
}
