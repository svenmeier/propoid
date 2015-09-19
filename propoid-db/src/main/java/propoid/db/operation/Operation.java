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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.aspect.Row;
import propoid.db.mapping.Mapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * An operation on a {@link Repository}.
 */
public abstract class Operation {

	public final Repository repository;

	protected Operation(Repository repository) {
		this.repository = repository;
	}

	protected Propoid instantiate(Class<? extends Propoid> clazz, Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex("_id"));

		String type = cursor.getString(cursor.getColumnIndex("_type"));
		clazz = repository.naming.decodeType(repository, clazz, type);

		Propoid propoid = repository.factory.create(repository, clazz, id);
		Row.setID(propoid, id);

		retrieve(cursor, propoid);

		return propoid;
	}

	/**
	 * Retrieve a {@link Propoid} from a cursor.
	 */
	@SuppressWarnings("unchecked")
	protected Propoid retrieve(Cursor cursor, Propoid propoid) {
		for (Property<?> property : propoid.properties()) {
			Mapper<Object> mapper = (Mapper<Object>) repository.mapping
					.getMapper(repository, property);

			int index = cursor.getColumnIndex(property.meta().name);
			if (index != -1) {
				mapper.retrieve((Property<Object>) property, repository,
						cursor, index);
			}
		}

		return propoid;
	}

	/**
	 * Bind a propoid to a statement.
	 */
	@SuppressWarnings("unchecked")
	protected int bind(SQLiteStatement statement, Propoid propoid, int index) {

		for (Property<?> property : propoid.properties()) {
			Mapper<Object> mapper = (Mapper<Object>) repository.mapping.getMapper(repository, property);

			beforeBind((Property<Propoid>) property, mapper);

			mapper.bind((Property<Object>) property, repository, statement,
					index);

			index++;
		}

		return index;
	}

	/**
	 * A property will be bound.
	 *
	 * @param property
	 * @param mapper
	 */
	protected void beforeBind(Property<?> property, Mapper<?> mapper) {
	}

	/**
	 * Aliaser for {@link Propoid}s.
	 */
	public class Aliaser {

		private char current = 'a';

		private Map<Propoid, String> map = new HashMap<Propoid, String>();

		public String alias(Propoid propoid) {
			String alias = map.get(propoid);
			if (alias == null) {
				alias = "" + this.current;

				map.put(propoid, alias);

				this.current++;
			}

			return alias;
		}

		@Override
		public String toString() {
			return map.toString();
		}
	}

	/**
	 * Collector of arguments.
	 */
	public class Arguments {

		private List<String> list = new ArrayList<String>();

		public void add(String argument) {
			list.add(argument);
		}

		public String[] get() {
			return list.toArray(new String[list.size()]);
		}

		@Override
		public String toString() {
			return list.toString();
		}
	}
}