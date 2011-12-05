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
import java.util.List;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.mapping.Mapper;
import android.database.Cursor;

/**
 * Prepare schema for a {@link Propoid}.
 */
public class Schema extends Operation {

	public Schema(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid) {

		List<Column> columns = existingColumns(propoid);
		if (columns.isEmpty()) {
			create(propoid);
		} else {
			for (Property<?> property : propoid.properties()) {
				alter(property, columns);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void create(Propoid propoid) {
		SQL sql = new SQL();
		sql.raw("CREATE TABLE ");
		sql.escaped(repository.naming.toTable(repository, propoid.getClass()));
		sql.raw(" (_id INTEGER PRIMARY KEY, _type TEXT");

		for (Property<?> property : propoid.properties()) {
			Mapper<Object> mapper = (Mapper<Object>) repository.mapping
					.getMapper(repository, property);

			sql.raw(", ");
			sql.escaped(property.name());
			sql.raw(" ");
			sql.raw(mapper.type((Property<Object>) property, repository));
		}
		sql.raw(")");

		repository.getDatabase().execSQL(sql.toString());
	}

	@SuppressWarnings("unchecked")
	private void alter(Property<?> property, List<Column> columns) {
		Mapper<Object> mapper = (Mapper<Object>) repository.mapping.getMapper(
				repository, property);

		String type = mapper.type((Property<Object>) property, repository);

		int space = type.indexOf(' ');
		if (space != -1) {
			// TEXT COLLATE ...
			type = type.substring(0, space);
		}

		for (Column column : columns) {
			if (column.name.equals(property.name())) {
				if (type.equals(column.type)) {
					return;
				} else {
					throw new RepositoryException(
							String.format(
									"column type %s does not match %s for property %s#%s",
									column.type, type, property.propoid
											.getClass().getSimpleName(),
									property.name()));
				}
			}
		}

		SQL sql = new SQL();
		sql.raw("ALTER TABLE ");
		sql.escaped(repository.naming.toTable(repository,
				property.propoid.getClass()));
		sql.raw(" ADD COLUMN ");
		sql.escaped(property.name());
		sql.raw(" ");
		sql.raw(type);

		repository.getDatabase().execSQL(sql.toString());
	}

	private List<Column> existingColumns(Propoid propoid) {
		SQL sql = new SQL();
		sql.raw("PRAGMA table_info(");
		sql.escaped(repository.naming.toTable(repository, propoid.getClass()));
		sql.raw(")");

		Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
				new String[0]);
		try {
			List<Column> columns = new ArrayList<Column>();

			while (cursor.moveToNext()) {
				columns.add(new Column(cursor.getString(1), cursor.getString(2)));
			}

			return columns;
		} finally {
			cursor.close();
		}
	}

	private class Column {
		public final String name;

		public final String type;

		public Column(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}
}