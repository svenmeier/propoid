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

import propoid.core.Propoid;
import propoid.db.aspect.Relation;
import propoid.db.operation.Operation;

/**
 * Cascading of {@link Operation}s on {@link Propoid}s.
 */
public interface Cascading extends Setting {

	/**
	 * Cascade the given {@link Relation} on insert of the owning
	 * {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param relation
	 *            relation to cascade
	 */
	public void onInsert(Repository repository, Relation relation);

	/**
	 * Cascade the given {@link Relation} on update of the owning
	 * {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param relation
	 *            relation to cascade
	 */
	public void onUpdate(Repository repository, Relation relation);

	/**
	 * Cascade the given {@link Relation} on delete of the owning
	 * {@link Propoid}.
	 * 
	 * @param repository
	 *            repository
	 * @param relation
	 *            relation to cascade
	 */
	public void onDelete(Repository repository, Relation relation);
}