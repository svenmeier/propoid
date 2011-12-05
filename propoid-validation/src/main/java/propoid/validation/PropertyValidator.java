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

/**
 * A validator of a single {@link Property}.
 */
public abstract class PropertyValidator<T> extends Validator {

	public final Property<T> property;

	/**
	 * Validate the given property.
	 * 
	 * @param property
	 *            property to validate
	 * @param resId
	 *            resource id for violations
	 */
	protected PropertyValidator(Property<T> property, int resId) {
		super(property.propoid, resId);

		this.property = property;
	}

	/**
	 * Intercept set on the property to validate new values.
	 * 
	 * @see #validate(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <U> U onSet(Property<U> property, U value) {
		if (property == this.property) {
			validate((T) value);
		}

		return super.onSet(property, value);
	}

	/**
	 * Validate the property's value.
	 * 
	 * @param value
	 *            value to validate
	 * 
	 * @throws ValidatorException
	 *             in case of a violation
	 * @see #violated(Object...)
	 */
	protected abstract void validate(T value) throws ValidatorException;
}