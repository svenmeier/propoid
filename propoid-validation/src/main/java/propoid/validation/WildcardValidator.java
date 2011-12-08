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
package propoid.validation;

import java.util.regex.Pattern;

import propoid.core.Property;

/**
 */
public class WildcardValidator extends Validator<String> {

	private final String wildcards;

	private Pattern p;

	public WildcardValidator(Property<String> property, int resId,
			String wildcards) {
		super(property, resId);

		this.wildcards = wildcards;
	}

	@Override
	protected void validate(String value) {
		if (p == null) {
			p = Pattern.compile(new Transformer().transform(wildcards));
		}

		if (!p.matcher(value).matches()) {
			violated(wildcards);
		}
	}

	public static class Transformer {

		private StringBuilder s;

		private boolean wild = false;

		public String transform(String wildcards) {
			s = new StringBuilder(wildcards.length());

			for (int i = 0; i < wildcards.length(); i++) {
				if (wildcards.charAt(i) == '*') {
					wild(false);

					s.append(".*");
				} else if (wildcards.charAt(i) == '?') {
					wild(false);

					s.append(".");
				} else {
					wild(true);

					s.append(wildcards.charAt(i));
				}
			}

			wild(false);

			return s.toString();
		}

		private void wild(boolean wild) {
			if (this.wild != wild) {
				if (wild) {
					s.append("\\Q");
				} else {
					s.append("\\E");
				}

				this.wild = wild;
			}
		}
	}
}