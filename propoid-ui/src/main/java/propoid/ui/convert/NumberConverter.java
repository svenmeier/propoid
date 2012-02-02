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
import java.text.ParseException;

import propoid.core.Property;

/**
 * Converter for {@link Number}s.
 */
public class NumberConverter implements Converter<Number> {

	private int resId;

	private NumberFormat format = NumberFormat.getInstance();

	public NumberConverter(int resId) {
		this.resId = resId;
	}

	@Override
	public String fromProperty(Property<Number> property, Number value) {
		if (value == null) {
			return "";
		}

		return format.format(value);
	}

	@Override
	public Number toProperty(Property<Number> property, String string) {
		if (string.length() == 0) {
			return null;
		}

		try {
			Number parsed = format.parse(string);

			Property<?> temp = property;
			if (temp.type() == Byte.class) {
				return parsed.byteValue();
			} else if (temp.type() == Short.class) {
				return parsed.shortValue();
			} else if (temp.type() == Integer.class) {
				return parsed.intValue();
			} else if (temp.type() == Long.class) {
				return parsed.longValue();
			} else if (temp.type() == Float.class) {
				return parsed.floatValue();
			} else if (temp.type() == Double.class) {
				return parsed.doubleValue();
			}
		} catch (ParseException failed) {
		}

		throw new ConverterException(resId);
	}
}