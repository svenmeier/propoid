/*
 * Copyright 2011 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package propoid.db;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

import propoid.core.Propoid;
import propoid.db.aspect.Row;
import propoid.db.cascading.DefaultCascading;
import propoid.db.factory.DefaultFactory;
import propoid.db.locator.ContextBasedLocator;
import propoid.db.mapping.DefaultMapping;
import propoid.db.naming.DefaultNaming;
import propoid.db.operation.Delete;
import propoid.db.operation.Index;
import propoid.db.operation.Insert;
import propoid.db.operation.Lookup;
import propoid.db.operation.Query;
import propoid.db.operation.Refresh;
import propoid.db.operation.Schema;
import propoid.db.operation.Update;
import propoid.db.version.DefaultVersioning;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * A repository of {@link Propoid}s.
 * <p>
 * If not specified otherwise uses the following {@link Setting}s:
 * <ul>
 * <li>{@link DefaultVersioning}</li>
 * <li>{@link DefaultCascading}</li>
 * <li>{@link DefaultFactory}</li>
 * <li>{@link DefaultNaming}</li>
 * <li>{@link DefaultMapping}</li>
 * </ul>
 */
public class Repository {

	private Locator locator;

	private SQLiteDatabase database;

	public final Versioning versioning;

	public final Cascading cascading;

	public final Factory factory;

	public final Naming naming;

	public final Mapping mapping;

	private Set<Class<? extends Propoid>> schemas = new HashSet<Class<? extends Propoid>>();

	/**
	 * Create a repository.
	 * 
	 * @param context
	 *            context
	 * @param name
	 *            file name
	 */
	public Repository(Context context, String name, Setting... settings) {
		this(new ContextBasedLocator(context, name));
	}

	/**
	 * Create a repository.
	 * 
	 * @param locator
	 *            locator
	 */
	public Repository(Locator locator, Setting... settings) {

		this.locator = locator;

		this.versioning = lookup(settings, Versioning.class,
				new DefaultVersioning());
		this.cascading = lookup(settings, Cascading.class,
				new DefaultCascading());
		this.factory = lookup(settings, Factory.class, new DefaultFactory());
		this.naming = lookup(settings, Naming.class, new DefaultNaming());
		this.mapping = lookup(settings, Mapping.class, new DefaultMapping());

		open();
	}

	@SuppressWarnings("unchecked")
	private <T> T lookup(Setting[] settings, Class<T> clazz, T defaultSetting) {
		for (Setting setting : settings) {
			if (clazz.isInstance(setting)) {
				return (T) setting;
			}
		}
		return defaultSetting;
	}

	/**
	 * Derive a repository.
	 * 
	 * @param settings
	 *            settings to replace
	 * @return repository
	 */
	public Repository derive(Setting... settings) {
		Locator locator = new Locator() {
			@Override
			public SQLiteDatabase open() {
				return database;
			}

			@Override
			public void close() {
				throw new IllegalStateException(
						"derived repository can not be closed");
			}
		};

		Versioning versioning = lookup(settings, Versioning.class,
				this.versioning);
		Cascading cascading = lookup(settings, Cascading.class, this.cascading);
		Factory factory = lookup(settings, Factory.class, this.factory);
		Naming naming = lookup(settings, Naming.class, this.naming);
		Mapping mapping = lookup(settings, Mapping.class, this.mapping);

		return new Repository(locator, versioning, cascading, factory, naming,
				mapping);
	}

	private void open() {
		database = locator.open();

		versioning.upgrade(this);
	}

	public void close() {
		if (database != null) {
			locator.close();
			database = null;
		}
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	/**
	 * Index {@link Propoid}s.
	 */
	public void index(Propoid propoid, boolean unique, Order... order) {
		schema(propoid);

		new Index(this).now(propoid, unique, order);
	}

	/**
	 * Delete the given {@link Propoid}.
	 */
	public void delete(Propoid propoid) {
		schema(propoid);

		new Delete(this).now(propoid);
	}

	/**
	 * Merge the given {@link Propoid}, i.e. insert or update depending whether
	 * the propoid is transient.
	 * 
	 * @param propoid
	 */
	public void merge(Propoid propoid) {
		long id = Row.getID(propoid);

		if (id == Row.TRANSIENT) {
			insert(propoid);
		} else {
			update(propoid);
		}
	}

	/**
	 * Insert the given {@link Propoid}.
	 * 
	 * @param propoid
	 */
	public void insert(Propoid propoid) {
		schema(propoid);

		database.beginTransaction();
		try {
			new Insert(this).now(propoid);

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * Update the given {@link Propoid}.
	 * 
	 * @param propoid
	 */
	public void update(Propoid propoid) {
		schema(propoid);

		database.beginTransaction();
		try {
			new Update(this).now(propoid);

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * Refresh the given {@link Propoid}.
	 * 
	 * @param propoid
	 *            propoid to refresh
	 */
	public void refresh(Propoid propoid) {
		new Refresh(this).now(propoid);
	}

	/**
	 * Lookup a {@link Propoid} by reference.
	 * 
	 * @param reference
	 *            reference of propoid
	 */
	@SuppressWarnings("unchecked")
	public <P extends Propoid> P lookup(Reference<P> reference) {
		return (P) new Lookup(this).now(reference);
	}

	/**
	 * Query for {@link Propoid}s.
	 * 
	 * @param propoid
	 *            propoid describing the query
	 * @return matching propoids
	 */
	public <P extends Propoid> Match<P> query(P propoid) {
		return query(propoid, Where.all());
	}

	/**
	 * Query for {@link Propoid}s.
	 * 
	 * @param propoid
	 *            propoid describing the query
	 * @param where
	 *            where condition
	 * @return matching propoids
	 */
	@SuppressWarnings("unchecked")
	public <P extends Propoid> Match<P> query(P propoid, Where where) {
		schema(propoid);

		return (Match<P>) new Query(this).now(propoid, where);
	}

	private void schema(Propoid propoid) {
		if (!schemas.contains(propoid.getClass())) {
			new Schema(this).now(propoid);

			schemas.add(propoid.getClass());
		}
	}

	/**
	 * Perform transactional operations.
	 * 
	 * @param transactional
	 */
	public void transactional(Transaction transactional) {
		database.beginTransaction();

		try {
			transactional.doTransactional();

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * Backup this repository to the given destination.
	 */
	public void backup(File destination) throws IOException {
		File source = new File(database.getPath());

		FileChannel src = null;
		FileChannel dst = null;
		try {
			src = new FileInputStream(source).getChannel();
			dst = new FileOutputStream(destination).getChannel();
			dst.transferFrom(src, 0, src.size());
		} finally {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
		}
	}

	/**
	 * Restore this database from the given file.
	 * 
	 * @param source
	 *            source file
	 * @throws IOException
	 */
	public void restore(File source) throws IOException {
		restore(new FileInputStream(source));
	}

	/**
	 * Restore this database from the given file descriptor.
	 * 
	 * @param source
	 *            source file
	 * @throws IOException
	 */
	public void restore(FileDescriptor source) throws IOException {
		restore(new FileInputStream(source));
	}

	private void restore(FileInputStream source) throws IOException {
		File destination = new File(database.getPath());

		close();

		FileChannel src = null;
		FileChannel dst = null;
		try {
			src = source.getChannel();
			dst = new FileOutputStream(destination).getChannel();
			dst.transferFrom(src, 0, src.size());
		} finally {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
		}

		open();
	}

	/**
	 * Vacuum the database.
	 */
	public void vacuum() {
		database.execSQL("VACUUM");
	}
}