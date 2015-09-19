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
package propoid.core;

import java.util.Collection;

/**
 * An aspect related to a {@link Property}.
 */
public abstract class PropertyAspect<T> extends AbstractAspect {

	public final Property<T> property;

	protected PropertyAspect(Property<T> property) {
		super(property.propoid);

		this.property = property;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <U> U onSet(Property<U> property, U value) {
		if (property == this.property) {
			return (U) onSet((T) value);
		}

		return super.onSet(property, value);
	}

	@SuppressWarnings("unchecked")
	public final <U> U onGet(Property<U> property, U value) {
		if (property == this.property) {
			return (U) onGet((T) value);
		}

		return super.onGet(property, value);
	}

	/**
	 * Hook method on set of the property.
	 */
	protected T onSet(T value) {
		return super.onSet(this.property, value);
	}

	/**
	 * Hook method on get of the property.
	 */
	protected T onGet(T value) {
		return super.onGet(this.property, value);
	}

	public static <T> T find(final Property<?> property, Class<T> clazz) {
		for (Aspect aspect : property.propoid.aspects()) {
			if (clazz.isInstance(aspect)
					&& aspect instanceof PropertyAspect
					&& ((PropertyAspect)aspect).property == property) {
				return (T) aspect;
			}
		}
		return null;
	}
}