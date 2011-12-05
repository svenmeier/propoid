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
package propoid.db.naming;

import java.util.HashMap;
import java.util.Map;

import propoid.core.Propoid;
import propoid.db.Naming;
import propoid.db.Repository;

/**
 * Caching naming.
 */
public class CachingNaming implements Naming {

	private Map<Class<? extends Propoid>, String> toTable = new HashMap<Class<? extends Propoid>, String>();

	private Map<Class<? extends Propoid>, String> toType = new HashMap<Class<? extends Propoid>, String>();

	private Map<String, Class<? extends Propoid>> fromType = new HashMap<String, Class<? extends Propoid>>();

	private Naming naming;

	public CachingNaming(Naming naming) {
		this.naming = naming;
	}

	@Override
	public String toTable(Repository repository, Class<? extends Propoid> clazz) {
		String cached = toTable.get(clazz);
		if (cached == null) {
			cached = naming.toTable(repository, clazz);
			toTable.put(clazz, cached);
		}
		return cached;
	}

	@Override
	public String toType(Repository repository, Class<? extends Propoid> clazz) {
		String cached = toType.get(clazz);
		if (cached == null) {
			cached = naming.toType(repository, clazz);
			toType.put(clazz, cached);
		}
		return cached;
	}

	@Override
	public Class<? extends Propoid> fromType(Repository repository,
			Class<? extends Propoid> clazz, String type) {
		String key = clazz.getName() + ":" + type;

		Class<? extends Propoid> cached = fromType.get(key);
		if (cached == null) {
			cached = naming.fromType(repository, clazz, type);
			fromType.put(key, cached);
		}
		return cached;
	}
}
