package propoid.core;

/**
 * Created by sven on 09.09.15.
 */
public class PropoidTest {

    public void test() {
        Foo foo = new Foo();

        foo.bar.set("bar");
    }
}

class Foo extends Propoid {

    public Property<String> bar = property();
}