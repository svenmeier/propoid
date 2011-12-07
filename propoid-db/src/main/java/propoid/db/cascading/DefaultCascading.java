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

import java.util.HashSet;
import java.util.Set;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Cascading;
import propoid.db.Repository;
import propoid.db.aspect.Relation;
import propoid.db.aspect.Row;

/**
 * Default implementation of {@link Cascading}.
 */
public class DefaultCascading implements Cascading {

	private Set<Property.Meta> cascaded = new HashSet<Property.Meta>();

	/**
	 * Set a {@link Property} to be cascaded.
	 */
	public void setCascaded(Property<? extends Propoid> property) {
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

	@Override
	public void onInsert(Repository repository, Relation relation) {
		merge(repository, relation);
	}

	@Override
	public void onUpdate(Repository repository, Relation relation) {
		merge(repository, relation);
	}

	private void merge(Repository repository, Relation relation) {
		if (isCascaded(relation.property)) {
			if (relation.id != Relation.VOID) {
				Propoid propoid = relation.property.meta().getInternal(
						relation.property);

				if (propoid != null) {
					repository.merge(relation.property.get());

					relation.id = Row.getID(propoid);
				}
			}
		}
	}

	@Override
	public void onDelete(Repository repository, Relation relation) {
		if (isCascaded(relation.property)) {
			if (relation.id != Relation.VOID && relation.id != Row.TRANSIENT) {
				repository.delete(relation.property.get());
			}
		}
	}
}