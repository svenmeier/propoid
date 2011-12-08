package propoid.validation;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;

public class ValidatorTest extends TestCase {

	public void test() throws Exception {
		Foo foo = new Foo();

		foo.bar.set("123");

		try {
			foo.bar.set("1");
			fail();
		} catch (ValidatorException expected) {
		}

		try {
			foo.bar.set("12345");
			fail();
		} catch (ValidatorException expected) {
		}
	}

	public static class Foo extends Propoid {

		public final Property<String> bar = property();

		public Foo() {
			new MaxLengthValidator(bar, 0, 4);
			new MinLengthValidator(bar, 0, 2);
		}
	}
}
