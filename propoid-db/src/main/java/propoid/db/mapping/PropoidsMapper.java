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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import propoid.core.Property;
import propoid.core.PropertyAspect;
import propoid.core.Propoid;
import propoid.db.LookupException;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.aspect.ToManyRelation;
import propoid.db.aspect.Row;
import propoid.db.cascading.Cascader;

/**
 * A mapper for a collection of {@link Propoid}s.
 */
public class PropoidsMapper implements Mapper<Collection<Propoid>>, Cascader<Collection<Propoid>> {

	public static final char ID_PREFIX = '{';
	public static final char ID_SUFFIX = '}';

	@Override
	public boolean maps(Property<?> property) {
		return itemType(property) != null;

	}

	public String type(Property<Collection<Propoid>> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<Collection<Propoid>> property, Repository repository,
			SQLiteStatement statement, int index) {

		long[] ids;

		ToManyRelation relation = PropertyAspect.find(property, ToManyRelation.class);
		if (relation == null || relation.loaded) {
			ids = toIds(property.getInternal());
		} else {
			ids = relation.ids;
		}

		if (ids == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, join(ids));
		}
	}

	@Override
	public void retrieve(Property<Collection<Propoid>> property, Repository repository,
						 Cursor cursor, int index) {

		property.setInternal(null);

		long[] ids;
		if (cursor.isNull(index)) {
			ids = null;
		} else {
			ids = split(cursor.getString(index));
		}

		ToManyRelation relation = PropertyAspect.find(property, ToManyRelation.class);
		if (relation == null) {
			new ToManyRelation(property, repository, ids);
		} else {
			relation.loaded = false;
			relation.repository = repository;
			relation.ids = ids;
		}
	}

	@Override
	public String argument(Property<Collection<Propoid>> newParam, Repository repository,
						   Collection<Propoid> value) {

		long[] ids = toIds(value);
		return join(ids);
	}

	@Override
	public void cascadeDelete(Repository repository, Property<Collection<Propoid>> property) {
		Collection<Propoid> propoids = property.get();
		if (propoids != null) {
			for (Propoid propoid : propoids) {
				repository.delete(propoid);
			}
		}
	}

	@Override
	public void cascadeInsert(Repository repository, Property<Collection<Propoid>> property) {
		merge(repository, property);
	}

	@Override
	public void cascadeUpdate(Repository repository, Property<Collection<Propoid>> property) {
		merge(repository, property);
	}

	private void merge(Repository repository, Property<Collection<Propoid>> property) {
		ToManyRelation relation = PropertyAspect.find(property, ToManyRelation.class);

		if (relation == null || relation.loaded) {
			Collection<Propoid> propoids = property.get();
			if (propoids != null) {
				for (Propoid propoid : propoids) {
					repository.merge(propoid);
				}
			}
		}

		if (relation != null && relation.loaded) {
			long[] newIds = toIds(property.get());

			long[] oldIds = ((ToManyRelation) relation).ids;
			if (oldIds != null) {
				Class<? extends Propoid> itemType = PropoidsMapper.itemType(property);

				for (long oldId : oldIds) {
					if (oldId != Row.TRANSIENT && (newIds == null || contained(oldId, newIds) == false)) {
						try {
							Propoid oldPropoid = repository.lookup(new Reference<Propoid>(itemType, oldId));

							repository.delete(oldPropoid);
						} catch (LookupException alreadyDeleted) {
						}
					}
				}
			}
			((ToManyRelation) relation).ids = newIds;
		}
	}

	private String join(long[] longs) {
		StringBuilder string = new StringBuilder();

		for (int l = 0; l < longs.length; l++) {
			string.append(ID_PREFIX);
			string.append(longs[l]);
			string.append(ID_SUFFIX);
		}

		return string.toString();
	}

	private long[] split(String string) {
		int count = 0;
		for (int c = 0; c < string.length(); c++) {
			if (string.charAt(c) == ID_PREFIX) {
				count++;
			}
		}

		long[] ids = new long[count];
		int from = 0;
		for (int i = 0; i < ids.length; i++) {
			if (string.charAt(from) != ID_PREFIX) {
				throw new RepositoryException("invalid ids " + string);
			}
			from++;

			int to = string.indexOf(ID_SUFFIX, from);
			try {
				ids[i] = Long.parseLong(string.substring(from, to).trim());
			} catch (NumberFormatException ex) {
				throw new RepositoryException("invalid ids " + string);
			}

			from = to + 1;
		}

		return ids;
	}

	private boolean contained(long id, long[] ids) {
		for (int l = 0; l < ids.length; l++) {
			if (ids[l] == id) {
				return true;
			}
		}
		return false;
	}

	private long[] toIds(Collection<Propoid> propoids) {
		if (propoids == null) {
			return null;
		}

		List<Propoid> list;
		if (propoids instanceof List) {
			list = (List<Propoid>) propoids;
		} else {
			list = new ArrayList<>(propoids);
		}

		long[] ids = new long[propoids.size()];
		for (int i = 0; i < ids.length; i++) {
			Propoid propoid = list.get(i);

			if (propoid == null) {
				ids[i] = Row.TRANSIENT;
			} else {
				ids[i] = Row.getID(propoid);
				if (ids[i] == Row.TRANSIENT) {
					throw new RepositoryException("cannot bind transient");
				}
			}
		}

		return ids;
	}

	public static Class<? extends Propoid> itemType(Property<?> property) {
		Type type = property.meta().type;

		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;

			if (Collection.class.isAssignableFrom((Class)parameterizedType.getRawType())) {
				Type argumentType = parameterizedType.getActualTypeArguments()[0];
				if (argumentType instanceof Class && Propoid.class.isAssignableFrom((Class<?>) argumentType)) {
					return (Class<? extends Propoid>) argumentType;
				}
			}
		}

		return null;
	}
}