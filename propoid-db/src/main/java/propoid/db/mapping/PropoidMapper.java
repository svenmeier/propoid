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
package propoid.db.mapping;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.aspect.Relation;
import propoid.db.aspect.Row;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link Propoid} properties.
 */
public class PropoidMapper implements Mapper<Propoid> {

	@Override
	public boolean maps(Property<?> property) {
		return property.meta().type instanceof Class
				&& Propoid.class.isAssignableFrom((Class<?>) property.meta().type);
	}

	public String type(Property<Propoid> property, Repository repository) {
		return "INTEGER";
	}

	@Override
	public void bind(Property<Propoid> property, Repository repository,
			SQLiteStatement statement, int index) {

		long id;

		Relation relation = Relation.get(property);
		if (relation == null) {
			Propoid propoid = property.getInternal();
			if (propoid == null) {
				id = Relation.VOID;
			} else {
				id = Row.getID(propoid);
			}
		} else {
			id = relation.id;
		}

		if (relation.id == Row.TRANSIENT) {
			throw new RepositoryException("cannot bind transient "
					+ relation.id);
		} else if (relation.id == Relation.VOID) {
			statement.bindNull(index);
		} else {
			statement.bindLong(index, id);
		}
	}

	@Override
	public void retrieve(Property<Propoid> property, Repository repository,
			Cursor cursor, int index) {

		property.setInternal(null);

		long id;
		if (cursor.isNull(index)) {
			id = Relation.VOID;
		} else {
			id = cursor.getLong(index);
		}

		Relation relation = Relation.get(property);
		if (relation == null) {
			new Relation(property, repository, id);
		} else {
			relation.repository = repository;
			relation.id = id;
		}
	}

	@Override
	public String argument(Property<Propoid> newParam, Repository repository,
			Propoid value) {
		long id = Row.getID(value);
		if (id == Row.TRANSIENT) {
			throw new RepositoryException(
					"transient propoid cannot be used as argument");
		}
		return Long.toString(id);
	}
}