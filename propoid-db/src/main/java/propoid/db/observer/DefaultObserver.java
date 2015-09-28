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

	private void notify(Propoid propoid) {
		contentResolver.notifyChange(new Reference<Propoid>(propoid).toUri(), null);
	}
}