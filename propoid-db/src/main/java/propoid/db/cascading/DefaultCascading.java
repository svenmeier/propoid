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
package propoid.db.cascading;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import propoid.core.Property;
import propoid.core.PropertyAspect;
import propoid.core.Propoid;
import propoid.db.Cascading;
import propoid.db.LookupException;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.aspect.LazyLoad;
import propoid.db.aspect.Row;
import propoid.db.aspect.ToManyRelation;
import propoid.db.aspect.ToOneRelation;
import propoid.db.mapping.Mapper;
import propoid.db.mapping.PropoidsMapper;

/**
 * Default {@link Cascading} of properties.
 * <p>
 * Each {@link Property} to be cascaded has to be registered with
 * {@link #setCascaded(Property)}.
 *
 * @see Cascader
 */
public class DefaultCascading implements Cascading {

	private Set<Property.Meta> cascaded = new HashSet<Property.Meta>();

	/**
	 * Set a {@link Property} to be cascaded.
	 */
	public void setCascaded(Property<?> property) {
		cascaded.add(property.meta());
	}

	/**
	 * Is the property cascaded.
	 * 
	 * @param property
	 *            property
	 * @return {@code true} if cascaded
	 */
	public boolean isCascaded(Property<?> property) {
		return cascaded.contains(property.meta());
	}

	/**
	 * Merges the relation's {@link Propoid} if the property is registered for
	 * cascading.
	 * 
	 * @see #setCascaded(Property)
	 */
	@Override
	public void onInsert(Repository repository, Property<?> property, Mapper<?> mapper) {
		if (isCascaded(property)) {
			if (mapper instanceof Cascader) {
				((Cascader) mapper).cascadeInsert(repository, property);
			} else {
				throw new RepositoryException("cannot beforeBind property");
			}
		}
	}

	/**
	 * Merges the relation's {@link Propoid} if the property is registered for
	 * cascading.
	 * 
	 * @see #setCascaded(Property)
	 */
	@Override
	public void onUpdate(Repository repository, Property<?> property, Mapper<?> mapper) {
		if (isCascaded(property)) {
			if (mapper instanceof Cascader) {
				((Cascader) mapper).cascadeUpdate(repository, property);
			} else {
				throw new RepositoryException("cannot beforeBind property");
			}
		}
	}

	/**
	 * Deletes the relation's {@link Propoid} if the property is registered for
	 * cascading.
	 * 
	 * @see #setCascaded(Property)
	 */
	@Override
	public void onDelete(Repository repository, Property<?> property, Mapper<?> mapper) {
		if (isCascaded(property)) {
			if (mapper instanceof Cascader) {
				((Cascader) mapper).cascadeDelete(repository, property);
			} else {
				throw new RepositoryException("cannot beforeBind property");
			}
		}
	}
}