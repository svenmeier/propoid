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
import android.net.Uri;

/**
 * A reference to a {@link Propoid}.
 * 
 * @see Repository#lookup(Reference)
 */
public class Reference<P extends Propoid> {

	public final Class<P> type;
	public final long id;

	@SuppressWarnings("unchecked")
	public Reference(P propoid) {
		this((Class<P>) propoid.getClass(), Row.getID(propoid));
	}

	public Reference(Class<P> type, long id) {
		this.type = type;
		this.id = id;
	}

	public Uri toUri() {
		return Uri.parse(toString());
	}

	public String toString() {
		return String.format("propoid://%s/%s", type.getName(), id);
	}

	@SuppressWarnings("unchecked")
	public static <P extends Propoid> Reference<P> fromString(String string) {
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

	public static <P extends Propoid> Reference<P> fromUri(Uri data) {
		return fromString(data.toString());
	}
}