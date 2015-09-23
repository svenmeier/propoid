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
package propoid.db.operation;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.Reference;
import propoid.db.References;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.Where;
import propoid.db.aspect.Row;
import android.database.Cursor;

/**
 * Query {@link Propoid}s.
 */
public class Query extends Operation {

	public Query(Repository repository) {
		super(repository);
	}

	public Match<Propoid> now(Propoid propoid, Where where) {
		return new MatchImpl(propoid, where);
	}

	class MatchImpl implements Match<Propoid> {

		private Propoid propoid;

		private Where where;

		public MatchImpl(Propoid propoid, Where where) {
			this.propoid = propoid;
			this.where = where;
		}

		private SQL from(Aliaser aliaser, Arguments arguments,
				Order... ordering) {
			SQL sql = new SQL();

			sql.raw(" FROM ");
			sql.escaped(repository.naming.table(repository, propoid.getClass()));
			sql.raw(" ");
			sql.raw(aliaser.alias(propoid));

			Set<Propoid> joined = new HashSet<Propoid>();
			for (Order order : ordering) {
				sql.append(order.toJoin(repository, aliaser, joined));
			}

			return sql;
		}

		private SQL where(Aliaser aliaser, Arguments arguments) {
			SQL sql = new SQL();

			sql.raw(" WHERE ");

			String type = repository.naming.encodeType(repository,
					propoid.getClass());
			if (type != null) {
				sql.raw(" _type = ? AND ");
				arguments.add(type);
			}

			sql.append(where.toWhere(repository, arguments, aliaser));

			return sql;
		}

		private SQL orderBy(Aliaser aliaser, Order... ordering) {
			SQL sql = new SQL();
			if (ordering.length > 0) {
				sql.raw(" ORDER BY ");
				for (Order order : ordering) {
					sql.separate(", ");
					sql.append(order.toOrderBy(aliaser));
				}
			}
			return sql;
		}

		@Override
		public Propoid single() {

			PropoidList list = list(Range.offsetLimit(0, 2));
			try {
				if (list.size() == 0) {
					return null;
				}

				Propoid first = list.get(0);

				if (list.size() > 1) {
					throw new RepositoryException(String.format(
							"multiple propoids matched, first id: %s",
							Row.getID(first)));
				}

				return first;
			} finally {
				list.close();
			}
		}

		@Override
		public Propoid first(Order... ordering) {

			PropoidList list = list(Range.offsetLimit(0, 1), ordering);
			try {
				if (list.size() == 0) {
					return null;
				} else {
					return list.get(0);
				}
			} finally {
				list.close();
			}
		}

		@Override
		public PropoidList list(Order... ordering) {
			return list(Range.all(), ordering);
		}

		@Override
		public PropoidList list(Range range, Order... ordering) {
			final SQL sql = new SQL();
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			sql.raw("SELECT ");
			sql.raw(aliaser.alias(propoid));
			sql.raw(".*");
			sql.append(from(aliaser, arguments, ordering));
			sql.append(where(aliaser, arguments));
			sql.append(orderBy(aliaser, ordering));
			sql.append(range.toLimit(repository));

			return new PropoidList(propoid.getClass(), repository.getDatabase()
					.rawQuery(sql.toString(), arguments.get()));
		}

		@Override
		public References<Propoid> references() {
			final SQL sql = new SQL();
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			sql.raw("SELECT ");
			sql.raw(aliaser.alias(propoid));
			sql.raw("._id, ");
			sql.raw(aliaser.alias(propoid));
			sql.raw("._type");
			sql.append(from(aliaser, arguments));
			sql.append(where(aliaser, arguments));

			Cursor cursor = repository.getDatabase().rawQuery(sql.toString(), arguments.get());

			Class<? extends Propoid> type = null;
			long[] ids = new long[cursor.getCount()];
			try {
				while (cursor.moveToNext()) {
					long id = cursor.getLong(cursor.getColumnIndex("_id"));

					if (type == null) {
						String _type = cursor.getString(cursor.getColumnIndex("_type"));

						 type = repository.naming.decodeType(repository, propoid.getClass(), _type);
					}
				}
			} finally {
				cursor.close();
			}

			if (ids.length == 0) {
				return new References<>();
			} else {
				return new References<Propoid>(type, ids);
			}
		}

		@Override
		public long count() {
			final SQL sql = new SQL();
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			sql.raw("SELECT COUNT(*)");
			sql.append(from(aliaser, arguments));
			sql.append(where(aliaser, arguments));

			Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
					arguments.get());
			try {
				cursor.moveToFirst();

				return cursor.getLong(0);
			} finally {
				cursor.close();
			}
		}

		@Override
		public <T> T avg(Property<T> property) {
			return aggregate("AVG", property);
		}

		@Override
		public <T> T max(Property<T> property) {
			return aggregate("MAX", property);
		}

		@Override
		public <T> T min(Property<T> property) {
			return aggregate("MIN", property);
		}

		@Override
		public <T> T sum(Property<T> property) {
			return aggregate("TOTAL", property);
		}

		private <T> T aggregate(String function, Property<T> property) {
			final SQL sql = new SQL();
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			sql.raw("SELECT ");
			sql.raw(function);
			sql.raw("(");
			sql.escaped(property.meta().name);
			sql.raw(")");
			sql.append(from(aliaser, arguments));
			sql.append(where(aliaser, arguments));

			Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
					arguments.get());
			try {
				cursor.moveToFirst();

				repository.mapping.getMapper(repository, property).retrieve(
						property, repository, cursor, 0);

				return property.get();
			} finally {
				cursor.close();
			}
		}

		public <T> void set(Property<T> property, T value) {
			final SQL sql = new SQL();
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			sql.raw("UPDATE ");
			sql.escaped(repository.naming.table(repository, propoid.getClass()));
			sql.raw(" SET ");
			sql.escaped(property.meta().name);
			sql.raw(" = ? ");

			arguments
					.add(value == null ? null : repository.mapping.getMapper(
							repository, property).argument(property,
							repository, value));

			sql.raw(" WHERE _id IN (");

			sql.raw("SELECT _id");
			sql.append(from(aliaser, arguments));
			sql.append(where(aliaser, arguments));
			sql.raw(")");

			repository.getDatabase().execSQL(sql.toString(), arguments.get());
		}

		@Override
		public void delete() {
			final Arguments arguments = new Arguments();
			final Aliaser aliaser = new Aliaser();

			SQL sql = new SQL();
			sql.raw("DELETE FROM ");
			sql.escaped(repository.naming.table(repository, propoid.getClass()));
			sql.raw(" WHERE _id IN (");

			sql.raw("SELECT _id");
			sql.append(from(aliaser, arguments));
			sql.append(where(aliaser, arguments));
			sql.raw(")");

			repository.getDatabase().execSQL(sql.toString(), arguments.get());
		}
	}

	class PropoidList implements List<Propoid>, Closeable {

		private Cursor cursor;

		private Class<? extends Propoid> clazz;

		public PropoidList(Class<? extends Propoid> clazz, Cursor cursor) {
			this.clazz = clazz;
			this.cursor = cursor;
		}

		public void close() {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		public Cursor cursor() {
			if (cursor == null) {
				throw new IllegalStateException("already closed");
			}
			return cursor;
		}

		@Override
		public Object[] toArray() {
			return toArray(new Object[cursor().getCount()]);
		}

		@SuppressWarnings("unchecked")
		public <T extends Object> T[] toArray(T[] array) {
			Cursor cursor = cursor();

			try {
				if (array.length < cursor.getCount()) {
					array = (T[]) new Object[cursor.getCount()];
				}

				for (int p = 0; p < cursor.getCount(); p++) {
					cursor.moveToPosition(p);
					array[p] = (T) instantiate(clazz, cursor);
				}
			} finally {
				close();
			}

			return array;
		}

		@Override
		public Iterator<Propoid> iterator() {
			return new PropoidIterator();
		}

		@Override
		public boolean isEmpty() {
			return cursor().getCount() == 0;
		}

		@Override
		public int size() {
			return cursor().getCount();
		}

		@Override
		public Propoid get(int location) {
			Cursor cursor = cursor();

			cursor.moveToPosition(location);

			return instantiate(clazz, cursor);
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int location, Propoid object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Propoid object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int location,
				Collection<? extends Propoid> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Propoid> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int lastIndexOf(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<Propoid> listIterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<Propoid> listIterator(int location) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Propoid remove(int location) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Propoid set(int location, Propoid object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Propoid> subList(int start, int end) {
			throw new UnsupportedOperationException();
		}

		class PropoidIterator implements Iterator<Propoid>, Closeable {

			private Boolean next = null;

			@Override
			public boolean hasNext() {
				if (next == null) {
					next = cursor().moveToNext();

					if (!next) {
						close();
					}
				}
				return next;
			}

			@Override
			public Propoid next() {
				hasNext();

				if (next == false) {
					throw new RepositoryException("no next");
				}

				try {
					Propoid propoid = instantiate(clazz, cursor());

					next = null;

					return propoid;
				} catch (RepositoryException ex) {
					close();

					throw ex;
				} catch (Exception ex) {
					close();

					throw new RepositoryException(ex);
				}
			}

			@Override
			public void remove() {
				close();

				throw new RepositoryException("remove not supported");
			}

			@Override
			public void close() {
				PropoidList.this.close();
			}
		}
	}
}