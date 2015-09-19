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

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.aspect.Row;
import propoid.db.mapping.Mapper;

import android.database.sqlite.SQLiteStatement;

/**
 * Update a single {@link Propoid}.
 */
public class Update extends Operation {

	public Update(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid) {
		long id = Row.getID(propoid);
		if (id == Row.TRANSIENT) {
			throw new RepositoryException("cannot update transient propoid");
		}

		SQL sql = new SQL();

		sql.raw("update ");
		sql.escaped(repository.naming.table(repository, propoid.getClass()));
		sql.raw(" set ");

		boolean hasProperty = false;
		for (Property<?> property : propoid.properties()) {
			sql.separate(", ");
			sql.escaped(property.meta().name);
			sql.raw(" = ?");

			hasProperty = true;
		}

		if (!hasProperty) {
			// nothing to update
			return;
		}

		sql.raw("where _id = ?");

		SQLiteStatement statement = repository.getDatabase().compileStatement(
				sql.toString());
		try {
			int index = bind(statement, propoid, 1);

			statement.bindLong(index, id);

			statement.execute();
		} finally {
			statement.close();
		}
	}

	@Override
	protected void beforeBind(Property<?> property, Mapper<?> mapper) {
		repository.cascading.onUpdate(repository, property, mapper);
	}
}