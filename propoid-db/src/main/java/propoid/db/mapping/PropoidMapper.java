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
import propoid.core.PropertyAspect;
import propoid.core.Propoid;
import propoid.db.LookupException;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.aspect.LazyLoad;
import propoid.db.aspect.ToOneRelation;
import propoid.db.aspect.Row;
import propoid.db.cascading.Cascader;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link Propoid} properties.
 */
public class PropoidMapper implements Mapper<Propoid>, Cascader<Propoid> {

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

		ToOneRelation relation = PropertyAspect.find(property, ToOneRelation.class);
		if (relation == null || relation.loaded) {
			Propoid propoid = property.getInternal();

			if (propoid == null) {
				id = Row.TRANSIENT;
			} else {
				id = Row.getID(propoid);
				if (id == Row.TRANSIENT) {
					throw new RepositoryException("cannot bind transient propoid");
				}
			}
		} else {
			id = relation.id;
		}

		if (id == Row.TRANSIENT) {
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
			id = Row.TRANSIENT;
		} else {
			id = cursor.getLong(index);
		}

		ToOneRelation relation = PropertyAspect.find(property, ToOneRelation.class);
		if (relation == null) {
			new ToOneRelation(property, repository, id);
		} else {
			relation.loaded = false;
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

	@Override
	public void cascadeDelete(Repository repository, Property<Propoid> property) {
		Propoid propoid = property.get();
		if (propoid != null) {
			repository.delete(propoid);
		}
	}

	@Override
	public void cascadeInsert(Repository repository, Property<Propoid> property) {
		merge(repository, property);
	}

	@Override
	public void cascadeUpdate(Repository repository, Property<Propoid> property) {
		merge(repository, property);
	}

	private void merge(Repository repository, Property<Propoid> property) {
		ToOneRelation relation = PropertyAspect.find(property, ToOneRelation.class);

		if (relation == null || relation.loaded) {
			Propoid propoid = property.get();
			if (propoid != null) {
				repository.merge(propoid);
			}
		}

		if (relation != null && relation.loaded) {
			Propoid propoid = property.get();

			long newId = (propoid == null) ? Row.TRANSIENT : Row.getID(propoid);
			long oldId = ((ToOneRelation) relation).id;
			if (oldId != Row.TRANSIENT && oldId != newId) {
				Class<? extends Propoid> itemType = (Class<? extends Propoid>) property.meta().type;

				try {
					Propoid oldPropoid = repository.lookup(new Reference<Propoid>(itemType, oldId));

					repository.delete(oldPropoid);
				} catch (LookupException alreadyDeleted) {
				}
			}


			((ToOneRelation) relation).id = newId;
		}
	}
}