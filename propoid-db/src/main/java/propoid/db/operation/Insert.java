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

import android.database.sqlite.SQLiteStatement;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.aspect.Row;
import propoid.db.mapping.Mapper;

/**
 * Insert a single {@link Propoid}.
 */
public class Insert extends Operation {

	public Insert(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid) {
		if (Row.getID(propoid) != Row.TRANSIENT) {
			throw new RepositoryException("cannot insert non transient propoid");
		}

		SQL sql = new SQL();
		sql.raw("INSERT INTO ");
		sql.escaped(repository.naming.table(repository, propoid.getClass()));
		sql.raw(" (_type");

		int questionMarks = 0;
		for (Property<?> property : propoid.properties()) {
			sql.raw(", ");
			sql.escaped(property.meta().name);

			questionMarks++;
		}

		sql.separate(null);
		sql.raw(") VALUES (?");

		for (int q = 0; q < questionMarks; q++) {
			sql.raw(", ");
			sql.raw("?");
		}

		sql.raw(")");

		SQLiteStatement statement = repository.getDatabase().compileStatement(
				sql.toString());
		try {
			String type = repository.naming.encodeType(repository,
					propoid.getClass());
			if (type != null) {
				statement.bindString(1, type);
			}

			bind(statement, propoid, 2);

			Row.setID(propoid, statement.executeInsert());
		} finally {
			statement.close();
		}
	}

	@Override
	protected void beforeBind(Property<?> property, Mapper<?> mapper) {
		repository.cascading.onInsert(repository, property, mapper);
	}
}