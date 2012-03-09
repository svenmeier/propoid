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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An owner of properties.
 */
public abstract class Propoid implements Aspect {

	private static Map<Class<? extends Propoid>, Meta> metas = new HashMap<Class<? extends Propoid>, Meta>();

	/**
	 * The root aspect.
	 */
	Aspect aspect = this;

	/**
	 * The root property.
	 */
	Property<?> property;

	/**
	 * Add a new property.
	 */
	protected <A> Property<A> property() {
		Property<A> property = new Property<A>(this, this.property);

		this.property = property;

		return property;
	}

	public <T> T onGet(Property<T> property, T value) {
		return value;
	}

	public <T> T onSet(Property<T> property, T value) {
		return value;
	}

	public Meta meta() {
		return getMeta(this);
	}

	/**
	 * All {@link Aspect}s of this propoid.
	 */
	public Iterable<Aspect> aspects() {
		return new Iterable<Aspect>() {
			@Override
			public Iterator<Aspect> iterator() {
				return new AspectIterator();
			}
		};
	}

	/**
	 * All {@link Property}s of this propoid.
	 */
	public Iterable<Property<?>> properties() {
		return new Iterable<Property<?>>() {
			@Override
			public Iterator<Property<?>> iterator() {
				return new PropertyIterator();
			}
		};
	}

	private class AspectIterator implements Iterator<Aspect> {

		private Aspect previous;

		private Aspect current;

		private Aspect next = Propoid.this.aspect;

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Aspect next() {
			previous = current;

			current = next;

			if (current instanceof AbstractAspect) {
				next = ((AbstractAspect) current).next;
			} else {
				next = null;
			}

			return current;
		}

		@Override
		public void remove() {
			if (current == null || !(current instanceof AbstractAspect)) {
				throw new IllegalStateException();
			}

			if (previous == null) {
				Propoid.this.aspect = this.next;
			} else {
				((AbstractAspect) previous).next = this.next;
			}
		}
	}

	private class PropertyIterator implements Iterator<Property<?>> {

		private Property<?> current;

		private Property<?> next = property;

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Property<?> next() {
			current = next;

			next = current.next;

			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	static Meta getMeta(Propoid propoid) {
		Meta meta = metas.get(propoid.getClass());
		if (meta == null) {
			meta = new Meta(propoid);

			metas.put(propoid.getClass(), meta);
		}
		return meta;
	}

	public static final class Meta {

		private Property.Meta[] metas;

		Meta(Propoid propoid) {
			int count = 0;
			if (propoid.property != null) {
				count = propoid.property.index + 1;
			}

			metas = new Property.Meta[count];
			for (Property<?> property : propoid.properties()) {
				Field field = getField(propoid, property);

				count--;
				metas[count] = Property.getMeta(field);
			}
		}

		private Field getField(Propoid propoid, Property<?> property) {
			try {
				for (Field field : propoid.getClass().getFields()) {
					if (Property.class.isAssignableFrom(field.getType())) {
						Property<?> value = (Property<?>) field.get(propoid);
						if (value == property) {
							return field;
						}
					}
				}
			} catch (Exception ex) {
				throw new IllegalStateException("property not accessible", ex);
			}

			throw new IllegalStateException("property not known");
		}

		public Property.Meta get(Property<?> property) {
			return metas[property.index];
		}

		public Property.Meta get(String name) {
			for (Property.Meta meta : metas) {
				if (meta.name.equals(name)) {
					return meta;
				}
			}

			return null;
		}
	}
}