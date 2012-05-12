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

import java.util.List;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.mapping.Mapper;
import propoid.db.schema.Column;
import android.database.sqlite.SQLiteStatement;

/**
 * Prepare schema for a {@link Propoid}.
 */
public class Schema extends Operation {

	public Schema(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid) {
		List<Column> columns = Column.get(
				repository.naming.table(repository, propoid.getClass()),
				repository.getDatabase());

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
		sql.escaped(repository.naming.table(repository, propoid.getClass()));
		sql.raw(" (_id INTEGER PRIMARY KEY, _type TEXT");

		for (Property<?> property : propoid.properties()) {
			Mapper<Object> mapper = (Mapper<Object>) repository.mapping
					.getMapper(repository, property);

			sql.raw(", ");
			sql.escaped(property.meta().name);
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
			if (column.name.equals(property.meta().name)) {
				if (type.equals(column.type)) {
					return;
				} else {
					throw new RepositoryException(
							String.format(
									"column type %s does not match %s for property %s#%s",
									column.type, type, property.propoid
											.getClass().getSimpleName(),
									property.meta().name));
				}
			}
		}

		SQL alter = new SQL();
		alter.raw("ALTER TABLE ");
		alter.escaped(repository.naming.table(repository,
				property.propoid.getClass()));
		alter.raw(" ADD COLUMN ");
		alter.escaped(property.meta().name);
		alter.raw(" ");
		alter.raw(type);

		repository.getDatabase().execSQL(alter.toString());

		SQL update = new SQL();
		update.raw("UPDATE ");
		update.escaped(repository.naming.table(repository,
				property.propoid.getClass()));
		update.raw(" SET  ");
		update.escaped(property.meta().name);
		update.raw(" = ?");

		SQLiteStatement statement = repository.getDatabase().compileStatement(
				update.toString());
		try {
			mapper.bind((Property<Object>) property, repository, statement, 1);

			statement.execute();
		} finally {
			statement.close();
		}
	}
}