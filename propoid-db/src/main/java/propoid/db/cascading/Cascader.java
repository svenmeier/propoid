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

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.aspect.ToOneRelation;

/**
 */
public interface Cascader<T> {

	/**
	 * Cascade the given {@link Property} on insertion of the owning
	 * {@link Propoid}.
	 *
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	void cascadeInsert(Repository repository, Property<T> property);

	/**
	 * Cascade the given {@link Property} on updating of the owning
	 * {@link Propoid}.
	 *
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	void cascadeUpdate(Repository repository, Property<T> property);

	/**
	 * Cascade the given {@link Property} on deletion of the owning
	 * {@link Propoid}.
	 *
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	void cascadeDelete(Repository repository, Property<T> property);
}