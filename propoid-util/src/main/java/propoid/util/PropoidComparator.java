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
package propoid.util;

import java.util.Comparator;

import propoid.core.Property;
import propoid.core.Property.Meta;
import propoid.core.Propoid;

/**
 * A {@link Comparator} for {@link Propoid}s based on a {@link Property}.
 */
public class PropoidComparator<T extends Propoid> implements Comparator<T> {

	private Meta property;

	/**
	 * Compare by the given property.
	 * 
	 * @param property
	 *            property's meta
	 */
	public PropoidComparator(Meta property) {
		this.property = property;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(T propoid0, T propoid1) {
		Object value0 = property.get(propoid0).get();
		Object value1 = property.get(propoid1).get();

		if (value0 == null && value1 == null) {
			return 0;
		} else if (value0 == null) {
			return -1;
		} else if (value1 == null) {
			return 1;
		} else if (value0 instanceof Comparable && value1 instanceof Comparable) {
			return ((Comparable) value0).compareTo(value1);
		}

		return 0;
	}
}