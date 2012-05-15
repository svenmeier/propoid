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

import java.util.Set;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.operation.Operation.Aliaser;

/**
 * Order of an index or query.
 * 
 * @see Repository#index(propoid.core.Propoid, boolean, Order...)
 * @see Match#first(Order...)
 * @see Match#list(Range, Order...)
 */
public class Order {

	public final Property<?>[] property;
	public final boolean ascending;

	Order(boolean ascending, Property<?>... property) {
		this.ascending = ascending;
		this.property = property;
	}

	/**
	 * Get DDL representation of this order to be used to create indices.
	 * 
	 * @see Repository#index(propoid.core.Propoid, boolean, Order...)
	 */
	public SQL toIndex(Repository repository) {
		if (property.length != 1) {
			throw new IllegalStateException();
		}

		Property<?> last = property[property.length - 1];

		SQL sql = new SQL();
		sql.escaped(last.meta().name);

		if (last.meta().type == String.class) {
			sql.raw(" COLLATE NOCASE");
		}

		if (ascending) {
			sql.raw(" asc");
		} else {
			sql.raw(" desc");
		}
		return sql;
	}

	/**
	 * Get SQL representation of this range.
	 */
	public SQL toJoin(Repository repository, Aliaser aliaser,
			Set<Propoid> joined) {
		SQL sql = new SQL();

		for (int p = 0; p < property.length - 1; p++) {
			if (joined.contains(property[p + 1].propoid)) {
				continue;
			}

			joined.add(property[p + 1].propoid);

			sql.raw(" LEFT JOIN ");
			sql.escaped(repository.naming.table(repository,
					property[p + 1].propoid.getClass()));
			sql.raw(" ");
			sql.raw(aliaser.alias(property[p + 1].propoid));
			sql.raw(" ON ");
			sql.raw(aliaser.alias(property[p + 1].propoid));
			sql.raw("._id");
			sql.raw(" = ");
			sql.raw(aliaser.alias(property[p].propoid));
			sql.raw(".");
			sql.escaped(property[p].meta().name);
		}

		return sql;
	}

	public SQL toOrderBy(Aliaser aliaser) {
		if (property.length == 0) {
			return new SQL("random()");
		}

		Property<?> last = property[property.length - 1];

		SQL sql = new SQL();
		sql.raw(aliaser.alias(last.propoid));
		sql.raw(".");
		sql.escaped(last.meta().name);

		if (last.meta().type == String.class) {
			sql.raw(" COLLATE NOCASE");
		}

		if (ascending) {
			sql.raw(" asc");
		} else {
			sql.raw(" desc");
		}
		return sql;
	}

	@Override
	public int hashCode() {
		int code = 13;

		for (Property<?> property : this.property) {
			code = code * 13 + property.meta().hashCode();
		}

		return code;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Order)) {
			return false;
		}

		Order other = (Order) o;

		if (other.ascending != this.ascending) {
			return false;
		}

		if (other.property.length != this.property.length) {
			return false;
		}

		for (int p = 0; p < this.property.length; p++) {
			if (this.property[p].meta() != other.property[p].meta()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Random order.
	 */
	public static Order random() {
		return new Order(true);
	}

	/**
	 * Ascending by property.
	 */
	public static Order ascending(Property<?>... property) {
		return new Order(true, property);
	}

	/**
	 * Descending by property.
	 */
	public static Order descending(Property<?>... property) {
		return new Order(false, property);
	}
}