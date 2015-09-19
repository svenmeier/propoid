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
package propoid.db.aspect;

import propoid.core.Property;
import propoid.core.PropertyAspect;

/**
 */
public abstract class LazyLoad<T> extends PropertyAspect<T> {

	public boolean loaded = false;

	protected LazyLoad(Property<T> property) {
		super(property);
	}

	@Override
	protected T onGet(T value) {
		if (loaded == false) {
			value = load();

			loaded = true;
		}

		return super.onGet(value);
	}

	protected abstract T load();

	@Override
	public T onSet(T value) {
		loaded = true;

		return value;
	}
}