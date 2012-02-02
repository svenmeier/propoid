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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import propoid.core.Property;
import android.content.Context;

public class DateConverter implements Converter<Date> {

	private DateFormat format;
	private int resId;

	public DateConverter(Context context, int resId) {
		this(android.text.format.DateFormat.getDateFormat(context), resId);
	}

	public DateConverter(DateFormat format, int resId) {
		this.format = format;
		this.resId = resId;
	}

	@Override
	public String fromProperty(Property<Date> property, Date value) {
		if (value == null) {
			return "";
		}

		return format.format(value);
	}

	@Override
	public Date toProperty(Property<Date> property, String string) {
		if (string.length() == 0) {
			return null;
		}

		try {
			return format.parse(string);
		} catch (ParseException e) {
			throw new ConverterException(resId);
		}
	}

	public static DateConverter getTimeConverter(Context context, int resId) {
		return new DateConverter(
				android.text.format.DateFormat.getTimeFormat(context), resId);
	}

	public static DateConverter getTimestampConverter(int resId) {
		return new DateConverter(DateFormat.getDateTimeInstance(), resId);
	}

	public static DateConverter getDateConverter(Context context, int resId) {
		return new DateConverter(
				android.text.format.DateFormat.getDateFormat(context), resId);
	}

	public static DateConverter getMediumDateConverter(Context context,
			int resId) {
		return new DateConverter(
				android.text.format.DateFormat.getMediumDateFormat(context),
				resId);
	}

	public static DateConverter getLongDateConverter(Context context, int resId) {
		return new DateConverter(
				android.text.format.DateFormat.getLongDateFormat(context),
				resId);
	}
}
