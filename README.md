Propoid
=======

Propoid is a small library to support properties in Java:

- small footprint
- very simple to use: distributed as aar, no code generation, no effect on tool chain, no setup required
- reflection runtime overhead only during application startup (i.e. when classes are loaded)
- very small memory overhead for each propoid
- see propoid-core for caveats

propoid-db
----------

Simple but powerful solution to persist your propoids in Sqlite:

    Repository repository = new Repository(context, "foos");

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
- Relations are lazily loaded (many-to-one and one-to-many)
- Table schema is altered for new properties automagically.

Pass propoids between activities and fragments via references:

    Reference<Foo> in = new Reference<>(foo);

    Intent intent = new Intent(context, FooActivity.class);
    intent.setData(in.toUri());

    ...

    Reference<Foo> out = Reference.from(intent);
    Foo foo = repository.get(out);

propoid-validation
------------------

Add validation to your objects:

    Foo foo = new Foo();
    new MinLengthValidator(foo.bar, R.string.bar_min_length, 4);

propoid-ui
----------

Bind properties to views:

    Foo foo = new Foo();
    new TextBinding(foo.bar, editText);

- all changes are automatically synced between property and a view, bidirectional!
- use one of default converters or add your own
- handles conversion and validation errors automatically
 
Bind ListView to matched Propoids (backed by a cursor):

    listView.setAdapter(new GenericAdapter<Foo>(repository.query(new Foo()).list()) {
        protected void bind(int position, View view, Foo foo) {
            Index index = Index.get(view);
            
            TextBinding.string(foo.bar,	index.<TextView>get(R.id.foo_bar));
        }
    });

- Propoids are instantiated for visible views only (backed by cursor)
- fast and convenient access to views via propoid.ui.Index

propoid-core
------------

To benefit from these features your objects have to adhere to the following restrictions:

    public class Foo extends Propoid {
      public final Property<String> bar = property();

      public Foo() {
      }
    }

- extend propoid.core.Propoid
- add a default constructor
- declare all fields as `public final propoid.core.Property`

Tags
====

android, java, properties, persistence, db, database, sqlite, sql, orm, or-mapper, hibernate, active-record, binding

