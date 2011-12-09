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

import java.util.HashMap;
import java.util.Map;

import propoid.core.Propoid;
import propoid.db.Factory;
import propoid.db.Repository;

/**
 * A factory caching {@link Propoid}s by type and id.
 * 
 * @see #create(Repository, Class, long)
 */
public class IdentityFactory implements Factory {

	private Map<String, Propoid> propoids = new HashMap<String, Propoid>();

	private Factory factory;

	/**
	 * Cache {@link Propoid}s instantiated by the given factory.
	 * 
	 * @param factory
	 *            factory to delegate to
	 */
	public IdentityFactory(Factory factory) {
		this.factory = factory;
	}

	/**
	 * Returns a cached propoid if already present for type and id.
	 * 
	 * @param repository
	 *            repository to get propoid for
	 * @param clazz
	 *            class of propoid
	 * @param id
	 *            id of propoid
	 */
	@Override
	public Propoid create(Repository repository,
			Class<? extends Propoid> clazz, long id) {
		String key = key(clazz, id);

		Propoid propoid = propoids.get(key);
		if (propoid == null) {
			propoid = factory.create(repository, clazz, id);

			propoids.put(key, propoid);
		}

		return propoid;
	}

	private String key(Class<? extends Propoid> clazz, long id) {
		return clazz.getName() + ":" + id;
	}

	/**
	 * Clear cache.
	 */
	public void clear() {
		propoids.clear();
	}
}