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
package propoid.ui.bind;

import java.util.Iterator;

import propoid.core.AbstractAspect;
import propoid.core.Aspect;
import propoid.core.Property;
import android.view.View;

/**
 * A binding from a {@link Property} to a {@link View}.
 * 
 * @param <T>
 *            property type
 */
public abstract class Binding<T> extends AbstractAspect {

	private final View view;

	/**
	 * The bound property.
	 */
	public final Property<T> property;

	/**
	 * Is the property being set currently.
	 */
	private boolean setting;

	protected Binding(Property<T> property, View view) {
		super(property.propoid);

		this.view = view;

		this.property = property;

		Object tag = view.getTag();
		if (tag != null) {
			if (tag instanceof Binding<?>) {
				((Binding<?>) tag).unbind();
			} else {
				throw new IllegalStateException("view tag is already set");
			}
		}

		view.setTag(this);
	}

	/**
	 * Intercepts a set on the bound property to update the view.
	 */
	@SuppressWarnings("unchecked")
	public <S> S onSet(Property<S> property, S value) {
		value = super.onSet(property, value);

		if (!setting && property == this.property) {
			onChange((T) value);
		}

		return value;
	}

	/**
	 * Change the value on the bound property, must be called by subclasses to
	 * set the property.
	 * 
	 * @param value
	 *            value to set
	 */
	protected void change(T value) {
		setting = true;

		try {
			property.set(value);
		} finally {
			setting = false;
		}
	}

	/**
	 * Notification that the property has changed.
	 * 
	 * @param value
	 *            set value
	 */
	protected abstract void onChange(T value);

	/**
	 * Get the view bound to.
	 * 
	 * @return view the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * Unbind the property, overriden implementations must invoke
	 * {@code super.unbind()}.
	 */
	protected void unbind() {
		Iterator<Aspect> aspects = property.propoid.aspects().iterator();
		while (aspects.hasNext()) {
			Aspect aspect = aspects.next();
			if (aspect == this) {
				aspects.remove();
				break;
			}
		}
	}
}