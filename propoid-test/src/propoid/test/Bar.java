package propoid.test;

import propoid.core.Property;
import propoid.core.Propoid;

public class Bar extends Propoid {

	public final Property<String> stringP = property();

	public Bar() {
	}
}