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

import propoid.core.Property;

/**
 * A converter for {@link Enum}s.
 */
public class EnumConverter<E extends Enum<E>> implements Converter<E> {

	private int resId;

	public EnumConverter() {
		this(-1);
	}

	public EnumConverter(int resId) {
		this.resId = resId;
	}

	public String fromProperty(Property<E> property, E value) {
		return value.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E toProperty(Property<E> property, String string)
			throws ConverterException {

		Class<E> type = (Class<E>) property.meta().type;

		try {
			return Enum.valueOf(type, string);
		} catch (IllegalArgumentException ex) {
			if (resId == -1) {
				throw ex;
			} else {
				throw new ConverterException(resId);
			}
		}
	}
}