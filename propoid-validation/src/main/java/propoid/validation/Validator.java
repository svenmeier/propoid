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

import propoid.core.Property;
import propoid.core.PropertyAspect;
import propoid.core.Propoid;

/**
 * A validator of a {@link Propoid}.
 */
public abstract class Validator<T> extends PropertyAspect<T> {

	private int resId;

	protected Validator(Property<T> property, int resId) {
		super(property);

		this.resId = resId;
	}

	protected T onSet(T value) {
		validate(value);

		return super.onSet(value);
	}

	protected abstract void validate(T value);

	/**
	 * Report a violation with a {@link ValidatorException}.
	 * 
	 * @param args
	 *            message arguments
	 * @throws ValidatorException
	 *             always
	 */
	protected void violated(Object... args) throws ValidatorException {
		throw new ValidatorException(resId, args);
	}
}