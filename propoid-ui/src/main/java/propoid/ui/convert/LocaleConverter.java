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

import java.util.Locale;

import propoid.core.Property;

public class LocaleConverter implements Converter<Locale> {

	@Override
	public String fromProperty(Property<Locale> property, Locale value) {
		if (value == null) {
			return "";
		}

		return asString(value);
	}

	@Override
	public Locale toProperty(Property<Locale> property, String string) {
		if (string.length() == 0) {
			return null;
		}

		return new Locale(string);
	}

	public static String asString(Locale locale) {
		if (locale.getLanguage().length() == 2) {
			return locale.getDisplayLanguage();
		} else {
			return locale.getLanguage();
		}
	}
}
