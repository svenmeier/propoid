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

import propoid.core.AbstractAspect;
import propoid.core.Aspect;
import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.operation.Lookup;

/**
 * Aspect representing the relation to another {@link Propoid}.
 */
public class Relation extends AbstractAspect {

	/**
	 * The row id of a void relation.
	 */
	public static final long VOID = -2;

	public final Property<Propoid> property;

	public transient Repository repository;

	public long id;

	public Relation(Property<Propoid> property, Repository repository, long id) {
		super(property.propoid);

		this.property = property;
		this.repository = repository;

		this.id = id;
	}

	public Relation(Property<Propoid> property, Repository repository) {
		super(property.propoid);

		this.property = property;
		this.repository = repository;

		Propoid propoid = property.getInternal();
		if (propoid == null) {
			this.id = VOID;
		} else {
			this.id = Row.getID(propoid);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T onGet(Property<T> property, T value) {
		if (property == this.property && value == null && id != VOID) {
			if (repository == null) {
				throw new RepositoryException("cannot get detached relation");
			}

			Reference reference = new Reference((Class) this.property.type(),
					id);

			value = (T) new Lookup(repository).now(reference);
		}

		return super.onGet(property, value);
	}

	@Override
	public <T> T onSet(Property<T> property, T value) {
		value = super.onSet(property, value);

		if (property == this.property) {
			if (value == null) {
				id = VOID;
			} else {
				id = Row.getID((Propoid) value);
			}
		}

		return value;
	}

	public static Relation get(final Property<? extends Propoid> property) {
		for (Aspect aspect : property.propoid.aspects()) {
			if (aspect instanceof Relation
					&& ((Relation) aspect).property == property) {
				return (Relation) aspect;
			}
		}
		return null;
	}
}