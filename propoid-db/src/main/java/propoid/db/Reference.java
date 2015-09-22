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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import propoid.core.Propoid;
import propoid.db.aspect.Row;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * A reference to a {@link Propoid}.
 * 
 * @see Repository#lookup(Reference)
 */
public class Reference<P extends Propoid> {

	private static final String ARGUMENT_KEY = "propoid:reference";

	public final Class<? extends P> type;
	public final long id;

	@SuppressWarnings("unchecked")
	public Reference(P propoid) {
		this((Class<P>) propoid.getClass(), Row.getID(propoid));
	}

	public Reference(Class<? extends P> type, long id) {
		if (id == Row.TRANSIENT) {
			throw new IllegalArgumentException(
					"cannot reference transient propoid");
		}
		this.type = type;
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Reference<?>) {
			Reference<?> other = (Reference<?>) o;

			return this.type == other.type && this.id == other.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	public Uri toUri() {
		return Uri.parse(toString());
	}

	public String toString() {
		return String.format("propoid://%s/%s", type.getName(), id);
	}

	/**
	 * Create a reference from its string representation.
	 * 
	 * @param string
	 *            string representation of a reference
	 * @return reference or {@code null}
	 * @see #toString()
	 */
	@SuppressWarnings("unchecked")
	public static <P extends Propoid> Reference<P> from(String string) {
		try {
			Matcher matcher = Pattern.compile("propoid://(.*)/(.*)").matcher(
					string);
			if (matcher.matches()) {
				Class<P> type = (Class<P>) Class.forName(matcher.group(1));
				long id = Long.parseLong(matcher.group(2));

				return new Reference<P>(type, id);
			}
		} catch (Exception noReference) {
		}
		return null;
	}

	/**
	 * Create a reference from its {@link Uri}.
	 * 
	 * @param uri
	 *            uri
	 * @return reference or {@code null}
	 * @see #toUri()
	 */
	public static <P extends Propoid> Reference<P> from(Uri uri) {
		return from(uri.toString());
	}

	/**
	 * Create a reference from an {@link Intent}s data.
	 * 
	 * @param intent
	 *            intent with references as data
	 * @return reference or {@code null}
	 * @see Intent#getData()
	 */
	public static <P extends Propoid> Reference<P> from(Intent intent) {
		Uri data = intent.getData();
		if (data == null) {
			return null;
		}
		return from(data.toString());
	}

	/**
	 * Store this reference as {@link Intent}s data.
	 *
	 * @param intent
	 *            intent
	 * @see Intent#setData(Uri)
	 */
	public void to(Intent intent) {
		intent.setData(toUri());
	}

	/**
	 * Create a reference from fragment arguments.
	 *
	 * @param arguments
	 *            fragment arguments containing the reference
	 * @return reference or {@code null}
	 */
	public static <P extends Propoid> Reference<P> from(Bundle arguments) {
		String string = arguments.getString(ARGUMENT_KEY);
		return from(string);
	}

	/**
	 * Set a reference to fragment arguments.
	 *
	 * @param arguments
	 *            fragment arguments
	 * @return the bundle
	 */
	public Bundle set(Bundle arguments) {
		arguments.putString(ARGUMENT_KEY, toString());

		return arguments;
	}
}