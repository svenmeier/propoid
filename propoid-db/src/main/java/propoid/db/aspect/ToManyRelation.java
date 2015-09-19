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
package propoid.db.aspect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.mapping.PropoidsMapper;
import propoid.db.operation.Lookup;

/**
 * Aspect representing the relation to another {@link Propoid}.
 */
public class ToManyRelation extends LazyLoad<Collection<Propoid>> {

	public transient Repository repository;

	public long[] ids;

	public ToManyRelation(Property<Collection<Propoid>> property, Repository repository, long[] ids) {
		super(property);

		this.repository = repository;
		this.ids = ids;
	}

	@Override
	protected Collection<Propoid> load() {
		if (repository == null) {
			throw new RepositoryException("cannot get detached relation");
		}

		if (ids == null) {
			return null;
		} else {
			Collection<Propoid> collection;
			if (Set.class.isAssignableFrom(collectionType(property))) {
				collection = new HashSet<>();
			} else{
				collection = new ArrayList<>();
			}

			Class itemType = PropoidsMapper.itemType(property);
			for (int i = 0; i < ids.length; i++) {
				long id = ids[i];

				Propoid propoid;
				if (id == Row.TRANSIENT) {
					propoid = null;
				} else {
					Reference reference = new Reference(itemType, id);

					propoid = new Lookup(repository).now(reference);
				}

				collection.add(propoid);
			}

			return collection;
		}
	}

	private Class<? extends Collection> collectionType(Property<?> property) {
		Type type = property.meta().type;

		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;

			if (Collection.class.isAssignableFrom((Class)parameterizedType.getRawType())) {
				return (Class)parameterizedType.getRawType();
			}
		}

		return null;
	}
}