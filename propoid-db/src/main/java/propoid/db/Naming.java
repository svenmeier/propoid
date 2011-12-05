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

/**
 * Naming of {@link Propoid}s.
 */
public interface Naming extends Setting {

	/**
	 * Get the table name for the given {@link Propoid} class.
	 */
	public String toTable(Repository repository, Class<? extends Propoid> clazz);

	/**
	 * Get the type name for the given {@link Propoid} class.
	 */
	public String toType(Repository repository, Class<? extends Propoid> clazz);

	/**
	 * Get the {@link Propoid} class from the given type name.
	 */
	public Class<? extends Propoid> fromType(Repository repository,
			Class<? extends Propoid> clazz, String type);
}