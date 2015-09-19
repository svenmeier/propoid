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
package propoid.db;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.aspect.ToOneRelation;
import propoid.db.mapping.Mapper;
import propoid.db.operation.Operation;

/**
 * Cascading of {@link Operation}s on {@link Propoid}s.
 */
public interface Cascading extends Setting {

	/**
	 * A propoperty will be bound for insertion of a {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	public void onInsert(Repository repository, Property<?> property, Mapper<?> mapper);

	/**
	 * A propoperty will be bound for updating of a {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	public void onUpdate(Repository repository, Property<?> property, Mapper<?> mapper);

	/**
	 * A propoperty will be bound for deletion of a {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param property
	 *            property to beforeBind
	 */
	public void onDelete(Repository repository, Property<?> property, Mapper<?> mapper);
}