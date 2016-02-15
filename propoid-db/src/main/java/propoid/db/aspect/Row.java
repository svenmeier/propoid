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

import java.util.Iterator;

import propoid.core.AbstractAspect;
import propoid.core.Aspect;
import propoid.core.Propoid;
import propoid.db.RepositoryException;

/**
 * Aspect representing the database row of a {@link Propoid}.
 */
public class Row extends AbstractAspect {

	/**
	 * The row id for transient {@link Propoid}s.
	 */
	public static final long TRANSIENT = -1;

	public final long id;

	public Row(Propoid propoid, long id) {
		super(propoid);

		this.id = id;
	}

	public static long getID(Propoid propoid) {
		Row row = get(propoid);

		if (row == null) {
			return TRANSIENT;
		} else {
			return row.id;
		}
	}

	public static void setID(Propoid propoid, long id) {
		Row row = get(propoid);

		if (row == null) {
			new Row(propoid, id);
		} else {
			if (row.id != id) {
				throw new RepositoryException("id already set");
			}
		}
	}

	public static void reset(Propoid propoid) {
		Iterator<Aspect> aspects = propoid.aspects().iterator();
		while (aspects.hasNext()) {
			Aspect aspect = aspects.next();
			if (aspect instanceof Row) {
				aspects.remove();
				break;
			}
		}
	}

	private static Row get(Propoid propoid) {
		for (Aspect aspect : propoid.aspects()) {
			if (aspect instanceof Row) {
				return (Row) aspect;
			}
		}
		return null;
	}
}