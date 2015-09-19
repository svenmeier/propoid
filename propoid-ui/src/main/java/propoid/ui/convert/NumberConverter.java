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
package propoid.ui.convert;

import java.text.NumberFormat;
import java.text.ParsePosition;

import propoid.core.Property;

/**
 * Converter for {@link Number}s.
 */
public class NumberConverter<N extends Number> implements Converter<N> {

	private Property<N> property;

	private int resId;

	private NumberFormat format = NumberFormat.getInstance();

	public NumberConverter(Property<N> property, int resId) {
		this.property = property;
		this.resId = resId;

		if (property.meta().type == Integer.class
				|| property.meta().type == Long.class) {
			format.setParseIntegerOnly(true);
		}
	}

	@Override
	public String toString(N value) {
		if (value == null) {
			return "";
		}

		return format.format(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public N fromString(String string) {
		if (string.length() == 0) {
			return null;
		}
		string = string.trim();

		ParsePosition position = new ParsePosition(0);

		Number parsed = (Number) format.parse(string, position);

		if (position.getIndex() != string.length()) {
			throw new ConverterException(resId);
		}

		Object number;
		Property<?> temp = property;
		if (temp.meta().type == Byte.class) {
			number = parsed.byteValue();
		} else if (temp.meta().type == Short.class) {
			number = parsed.shortValue();
		} else if (temp.meta().type == Integer.class) {
			number = parsed.intValue();
		} else if (temp.meta().type == Long.class) {
			number = parsed.longValue();
		} else if (temp.meta().type == Float.class) {
			number = parsed.floatValue();
		} else if (temp.meta().type == Double.class) {
			number = parsed.doubleValue();
		} else {
			throw new IllegalArgumentException("unexpected number subclass '"
					+ temp.meta().type + "'");
		}

		return (N) number;
	}
}