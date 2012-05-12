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
import propoid.core.Propoid;
import propoid.db.operation.Operation.Aliaser;
import propoid.db.operation.Operation.Arguments;

/**
 * Where condition of a query.
 * 
 * @see Repository#query(Propoid, Where)
 */
public class Where {
	Where() {
	}

	/**
	 * Prepare schema for this where condition.
	 */
	public void schema(Repository repository) {
	}

	/**
	 * Get SQL representation of this where condition.
	 */
	public String sql(Repository repository, Arguments arguments,
			Aliaser aliases) {
		return "1 = 1";
	}

	/**
	 * ... property is equal to a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where equal(Property<T> property, final T value) {
		return new Comparison(property, value == null ? "is null" : "= ?",
				value);
	}

	/**
	 * ... property is not equal to a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where unequal(Property<T> property, final T value) {
		return new Comparison(property, value == null ? "is not null" : "<> ?",
				value);
	}

	/**
	 * ... property is less than a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where lessThan(Property<T> property, final T value) {
		return new Comparison(property, "< ?", value);
	}

	/**
	 * ... property is less equal a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where lessEqual(Property<T> property, final T value) {
		return new Comparison(property, "<= ?", value);
	}

	/**
	 * ... property is greater than a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where greaterThan(Property<T> property, final T value) {
		return new Comparison(property, "> ?", value);
	}

	/**
	 * ... property is greater equal than a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static <T> Where greaterEqual(Property<T> property, final T value) {
		return new Comparison(property, ">= ?", value);
	}

	/**
	 * ... property is like a value.
	 * 
	 * @param property
	 *            property
	 * @param value
	 *            value
	 * @return where expression
	 */
	public static Where like(Property<String> property, final String value) {
		return new Comparison(property, value == null ? "is null" : "like ?",
				value);
	}

	/**
	 * ... condition is not met.
	 * 
	 * @param where
	 *            where condition
	 * @return where expression
	 */
	public static Where not(Where where) {
		return new Not(where);
	}

	/**
	 * ... all conditions are met.
	 * 
	 * @param where
	 *            where conditions
	 * @return where expression
	 */
	public static Where all(Where... where) {
		return new Operation(" AND ", where);
	}

	/**
	 * ... any condition is met.
	 * 
	 * @param where
	 *            where conditions
	 * @return where expression
	 */
	public static Where any(Where... where) {
		return new Operation(" OR ", where);
	}

	/**
	 * ... property has a {@link Propoid} where a condition is met.
	 * 
	 * @param property
	 * @param value
	 * @param where
	 * @return this
	 */
	public static <P extends Propoid> Where has(Property<P> property, P value,
			Where where) {
		return new Has<P>(property, value, where);
	}

	private static class Not extends Where {

		private final Where where;

		public Not(Where where) {
			this.where = where;
		}

		@Override
		public void schema(Repository repository) {
			where.schema(repository);
		}

		@Override
		public String sql(Repository repository, Arguments arguments,
				Aliaser aliaser) {
			SQL sql = new SQL();

			sql.raw("(not ");
			sql.raw(where.sql(repository, arguments, aliaser));
			sql.raw(")");

			return sql.toString();
		}
	}

	private static class Operation extends Where {

		private final Where[] operands;

		private String op;

		Operation(String op, Where... operands) {
			this.op = op;
			this.operands = operands;
		}

		@Override
		public void schema(Repository repository) {
			for (Where where : operands) {
				where.schema(repository);
			}
			super.schema(repository);
		}

		@Override
		public String sql(Repository repository, Arguments arguments,
				Aliaser aliaser) {
			SQL sql = new SQL();

			if (operands.length == 0) {
				sql.raw("1 = 1");
			} else {
				sql.raw("(");
				for (Where where : this.operands) {
					sql.separate(op);
					sql.raw(where.sql(repository, arguments, aliaser));
				}
				sql.raw(")");
			}

			return sql.toString();
		}
	}

	private static class Has<P extends Propoid> extends Where {

		private Property<P> property;
		private P value;
		private Where where;

		public Has(Property<P> property, P value, Where where) {
			if (value == null) {
				throw new IllegalArgumentException("value must no be null");
			}
			this.property = property;
			this.value = value;
			this.where = where;
		}

		@Override
		public void schema(Repository repository) {
			repository.schema(value);
		}

		@Override
		public String sql(Repository repository, Arguments arguments,
				Aliaser aliaser) {
			SQL sql = new SQL();

			sql.raw("exists (select null from ");
			sql.escaped(repository.naming.table(repository, value.getClass()));
			sql.raw(" ");
			sql.raw(aliaser.alias(value));
			sql.raw(" where ");
			sql.raw(aliaser.alias(value));
			sql.raw("._id = ");
			sql.raw(aliaser.alias(property.propoid));
			sql.raw(".");
			sql.escaped(property.meta().name);
			sql.raw(" and ");
			sql.raw(where.sql(repository, arguments, aliaser));
			sql.raw(")");

			return sql.toString();
		}
	}

	private static class Comparison extends Where {

		private Property<Object> property;

		private final String comparator;

		private final Object comparand;

		@SuppressWarnings("unchecked")
		Comparison(Property<?> property, String comparator, Object comparand) {
			this.property = (Property<Object>) property;
			this.comparator = comparator;
			this.comparand = comparand;
		}

		@Override
		public String sql(Repository repository, Arguments arguments,
				Aliaser aliaser) {
			SQL sql = new SQL();

			sql.raw(aliaser.alias(property.propoid));
			sql.raw(".");
			sql.escaped(property.meta().name);
			sql.raw(" ");
			sql.raw(comparator);

			if (comparand != null) {
				String constrain = repository.mapping.getMapper(repository,
						property).argument(property, repository, comparand);
				arguments.add(constrain);
			}

			return sql.toString();
		}
	}
}