Propoid
=======

Propoid is a small library to support properties in Java:

- small footprint
- very simple to use: distributed as jars, no code generation, no effect on tool chain, no setup required
- negligible runtime overhead through reflection on application startup (i.e. when classes are loaded)
- very small memory overhead for each propoid
- see propoid-core for caveats

propoid-db
----------

Simple but powerful solution to persist your propoids in Sqlite.

No creation of table schema needed, just insert your objects

    Foo foo = new Foo();
    repository.insert(foo);

Typed queries

    Foo foo = new Foo();
    repository.query(foo, Where.equal(foo.bar, "my bar")).first();

Efficiently iterate over matched propoids (backed by a cursor)

    Foo foo = new Foo();
    for (Foo match : repository.query(foo, Where.equal(foo.bar, "my bar")).list(Order.ascending(foo.baz)) {
        match.baz();
    };

Perform mass updates

    Foo foo = new Foo();
    repository.query(foo).set(foo.bar, "my bar");

- Use inheritance (single-table mapping)
- Relations are lazily loaded (many-to-one)
- Table schema is altered for new properties automagically.

propoid-validation
------------------

Add validation to your objects:

    Foo foo = new Foo();
    new MinLengthValidator(foo.bar, R.string.bar_min_length, 4);

propoid-ui
----------

Bind ListView to matched propoids (backed by a cursor):

    listView.setAdapater(new GenericAdapter(repository.query(Foo.class)));

Bind properties to views:

    Foo foo = new Foo();
    new TextBinding(foo.bar, editText);

- all changes are automatically synced between property and a view, bidirectional!
- use one of default converters or add your own
- handles conversion and validation errors automatically

propoid-core
------------

To benefit from these features your objects have to adhere to the following restrictions:

    public class Foo {
      public final Property<String> bar = property();

      public Foo() {
      }
    }

- extend propoid.core.Propoid
- add a default constructor
- use propoid.core.Property for all properties (no casting needing due to generics though)

Tags
====

android, java, properties, persistence, db, database, sqlite, sql, orm, or-mapper, hibernate, active-record, binding

