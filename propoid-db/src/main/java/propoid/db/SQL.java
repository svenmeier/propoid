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

/**
 * A builder of SQL statements.
 */
public class SQL {

	private StringBuilder builder;

	private String separator = null;

	public SQL() {
		builder = new StringBuilder();
	}

	public SQL(String string) {
		builder = new StringBuilder(string);
	}

	/**
	 * Append a raw string.
	 */
	public SQL raw(String string) {
		builder.append(string);

		return this;
	}

	/**
	 * Append a string to be escaped.
	 */
	public SQL escaped(String string) {
		builder.append("[");
		builder.append(string);
		builder.append("]");

		return this;
	}

	/**
	 * Add a separator. The separator is appended only after this method is
	 * called the second time with the same parameter.
	 */
	public void separate(String separator) {
		if (separator != null && separator.equals(this.separator)) {
			builder.append(separator);
		}
		this.separator = separator;
	}

	/**
	 * Get the SQL.
	 */
	public String toString() {
		return builder.toString();
	}
}