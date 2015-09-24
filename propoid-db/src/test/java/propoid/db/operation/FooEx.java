package propoid.db.operation;

import propoid.core.Property;

public class FooEx extends Foo {

	public final Property<String> exP = property();

	public FooEx() {
		exP.set("ex");
	}
}