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
package propoid.db.observer;

import android.content.ContentResolver;
import android.content.Context;

import propoid.core.Propoid;
import propoid.db.Observer;
import propoid.db.Reference;
import propoid.db.Setting;
import propoid.db.aspect.Row;

/**
 * Observer of changes on {@link Propoid}s, that notifies {@link android.database.ContentObserver}s.
 */
public class DefaultObserver implements Observer {

	private final ContentResolver contentResolver;

	public DefaultObserver(Context context) {
		this.contentResolver = context.getContentResolver();
	}

	public void onInsert(Propoid propoid) {
		notify(propoid);
	}

	public void onDelete(Propoid propoid) {
		notify(propoid);
	}

	public void onUpdate(Propoid propoid) {
		notify(propoid);
	}

	/**
	 * Notify a change for the propoid's class and <em>all</em> its superclasses. This way observers
	 * of a superclass will be notified too, although {@code Uri}s do not actually support
	 * polymorphism.
	 *
	 * @param propoid
	 */
	private void notify(Propoid propoid) {
		Class<? extends Propoid> clazz = propoid.getClass();
		long id = Row.getID(propoid);

		while (clazz != Propoid.class) {
			contentResolver.notifyChange(new Reference<Propoid>(clazz, id).toUri(), null);

			clazz = (Class<? extends Propoid>) clazz.getSuperclass();
		}
	}
}