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

/**
 * Order of an index or query.
 * 
 * @see Repository#index(propoid.core.Propoid, boolean, Order...)
 * @see Match#first(Order...)
 * @see Match#list(Range, Order...)
 */
public class Order {

	private final Property.Meta property;
	private final boolean ascending;

	Order(Property.Meta property, boolean ascending) {
		this.property = property;
		this.ascending = ascending;
	}

	public String toString() {
		return property.name;
	}

	public String toString(Repository repository) {
		if (property == null) {
			return "random()";
		}

		SQL sql = new SQL();
		sql.escaped(property.name);

		if (property.type == String.class) {
			sql.raw(" COLLATE NOCASE");
		}

		if (ascending) {
			sql.raw(" asc");
		} else {
			sql.raw(" desc");
		}
		return sql.toString();
	}

	@Override
	public int hashCode() {
		if (property == null) {
			return 42;
		} else {
			return property.hashCode();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Order)) {
			return false;
		}

		Order other = (Order) o;

		return other.property == this.property
				&& other.ascending == this.ascending;
	}

	/**
	 * Random order.
	 */
	public static Order random() {
		return new Order(null, true);
	}

	/**
	 * Ascending by property.
	 */
	public static Order ascending(Property<?> property) {
		return new Order(property.meta(), true);
	}

	/**
	 * Descending by property.
	 */
	public static Order descending(Property<?> property) {
		return new Order(property.meta(), false);
	}
}