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

import propoid.core.Propoid;
import propoid.db.Naming;
import propoid.db.Repository;
import propoid.db.RepositoryException;

/**
 * Default naming.
 */
public class DefaultNaming implements Naming {

	/**
	 * Uses the simple name of the propoid's first ancestor extending
	 * {@link Propoid} as table name.
	 * 
	 * @see Class#getSimpleName()
	 */
	@Override
	public String toTable(Repository repository, Class<? extends Propoid> clazz) {
		Class<? extends Propoid> ancestor = ancestor(clazz);

		return ancestor.getSimpleName();
	}

	@Override
	public String toType(Repository repository, Class<? extends Propoid> clazz) {
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

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Propoid> fromType(Repository repository,
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

	@SuppressWarnings("unchecked")
	private Class<? extends Propoid> ancestor(Class<? extends Propoid> clazz) {
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
