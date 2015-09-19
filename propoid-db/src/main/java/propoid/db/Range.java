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

import propoid.core.Propoid;

/**
 * Range of a query match.
 * 
 * @see Match#list(Range, Order...)
 */
public class Range {

	/**
	 * The offset of this range.
	 */
	public final int offset;

	/**
	 * The limit of this range.
	 */
	public final int limit;

	Range(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	/**
	 * Get SQL representation of this range.
	 */
	public SQL toLimit(Repository repository) {
		if (offset == 0 && limit == Integer.MAX_VALUE) {
			return new SQL("");
		}

		SQL sql = new SQL();

		sql.raw(" limit ");
		sql.raw(Integer.toString(offset));
		sql.raw(",");
		sql.raw(Integer.toString(limit));

		return sql;
	}

	/**
	 * All {@link Propoid}s.
	 */
	public static Range all() {
		return new Range(0, Integer.MAX_VALUE);
	}

	public static Range offset(int offset) {
		return new Range(offset, Integer.MAX_VALUE);
	}

	public static Range offsetLimit(int offset, int limit) {
		return new Range(offset, limit);
	}

	public static Range limit(int limit) {
		return new Range(0, limit);
	}
}