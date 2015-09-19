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
package propoid.db.factory;

import java.lang.reflect.Constructor;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

import propoid.core.Propoid;
import propoid.db.Factory;
import propoid.db.Repository;
import propoid.db.RepositoryException;

/**
 * Default factory for {@link Propoid}s.
 */
public class DefaultFactory implements Factory {

	private Map<Class, Constructor<? extends Propoid>> constructors = new HashMap<>();

	/**
	 * Create with {@link Class#newInstance()}.
	 */
	@Override
	public Propoid create(Repository repository,
			Class<? extends Propoid> clazz, long id) {

		try {
			Constructor<? extends Propoid> constructor = constructors.get(clazz);
			if (constructor == null) {
				constructor = clazz.getDeclaredConstructor();
				constructor.setAccessible(true);

				constructors.put(clazz, constructor);
			}

			return constructor.newInstance();
		} catch (Exception ex) {
			throw new RepositoryException("default constructor needed for "
					+ clazz.getName(), ex);
		}
	}
}