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
import propoid.db.RepositoryException;

/**
 * Default naming.
 */
public class DefaultNaming implements Naming {

	private Map<Class<? extends Propoid>, String> toTable = new HashMap<Class<? extends Propoid>, String>();

	private Map<Class<? extends Propoid>, String> toType = new HashMap<Class<? extends Propoid>, String>();

	private Map<String, Class<? extends Propoid>> fromType = new HashMap<String, Class<? extends Propoid>>();

	@Override
	public String table(Repository repository, Class<? extends Propoid> clazz) {
		String cached = toTable.get(clazz);
		if (cached == null) {
			cached = tableImpl(repository, clazz);
			toTable.put(clazz, cached);
		}
		return cached;
	}

	@Override
	public String encodeType(Repository repository, Class<? extends Propoid> clazz) {
		String cached = toType.get(clazz);
		if (cached == null) {
			cached = encodeTypeImpl(repository, clazz);
			toType.put(clazz, cached);
		}
		return cached;
	}

	@Override
	public Class<? extends Propoid> decodeType(Repository repository,
			Class<? extends Propoid> clazz, String type) {
		String key = clazz.getName() + ":" + type;

		Class<? extends Propoid> cached = fromType.get(key);
		if (cached == null) {
			cached = decodeTypeImpl(repository, clazz, type);
			fromType.put(key, cached);
		}
		return cached;
	}

	/**
	 * Uses the simple name of the propoid's first ancestor extending
	 * {@link Propoid} as table name.
	 * 
	 * @see Class#getSimpleName()
	 */
	protected String tableImpl(Repository repository,
			Class<? extends Propoid> clazz) {
		Class<? extends Propoid> ancestor = ancestor(clazz);

		return ancestor.getSimpleName();
	}

	/**
	 * Encode the given class in a database representation:
	 * <ul>
	 * <li>{@code null} if it hasn't an ancestor</li>
	 * <li>
	 * {@link Class#getSimpleName()} if it is in the same package as its
	 * ancestor</li>
	 * <li>{@link Class#getName()} otherwise</li>
	 * </ul>
	 * 
	 * @see #ancestor(Class)
	 */
	protected String encodeTypeImpl(Repository repository,
			Class<? extends Propoid> clazz) {
		Class<? extends Propoid> ancestor = ancestor(clazz);

		if (clazz == ancestor) {
			return null;
		}

		if (clazz.getPackage() == ancestor.getPackage()) {
			return clazz.getSimpleName();
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Decode the class from the given database representation:
	 * <ul>
	 * <li>if {@code null} assuming to be the given class itself</li>
	 * <li>
	 * if not containing a {@code .} (dot) assuming a class located in the same
	 * package as the one from the given class</li>
	 * <li>assuming a full class name otherwise</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Propoid> decodeTypeImpl(Repository repository,
			Class<? extends Propoid> clazz, String type) {
		if (type != null) {
			if (type.indexOf('.') == -1) {
				type = clazz.getPackage().getName() + "." + type;
			}
			try {
				clazz = (Class<? extends Propoid>) Class.forName(type);
			} catch (Exception ex) {
				throw new RepositoryException(ex);
			}
		}
		return clazz;
	}

	/**
	 * Returns the ancestor of the given class, i.e. the superclass extending
	 * {@link Propoid} or the class itself if it is extending {@link Propoid}
	 * directly.
	 * 
	 * @see Class#getSuperclass()
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Propoid> ancestor(Class<? extends Propoid> clazz) {
		while (true) {
			Class<? extends Propoid> superclass = (Class<? extends Propoid>) clazz
					.getSuperclass();
			if (superclass == Propoid.class) {
				return clazz;
			}
			clazz = superclass;
		}
	}
}
