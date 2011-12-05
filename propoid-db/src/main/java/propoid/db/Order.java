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

	private final Property<?> property;
	private final boolean ascending;

	Order(Property<?> property, boolean ascending) {
		this.property = property;
		this.ascending = ascending;
	}

	public Property<?> getProperty() {
		if (property == null) {
			throw new IllegalStateException("random does not have a property");
		}
		return property;
	}

	public String toString(Repository repository) {
		if (property == null) {
			return "random()";
		}

		SQL sql = new SQL();
		sql.escaped(property.name());
		if (ascending) {
			sql.raw(" asc");
		} else {
			sql.raw(" desc");
		}
		return sql.toString();
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
		return new Order(property, true);
	}

	/**
	 * Descending by property.
	 */
	public static Order descending(Property<?> property) {
		return new Order(property, false);
	}
}