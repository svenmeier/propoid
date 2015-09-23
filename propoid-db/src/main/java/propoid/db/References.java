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
package propoid.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.aspect.Row;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/**
 * A parcel of {@link Reference}s.
 */
public class References<T extends Propoid> implements Parcelable, Iterable<Reference<T>> {

	private Class<? extends T> type;

	private long[] ids;

	public References() {
		type = null;
		ids = new long[0];
	}

	public References(Class<? extends T> type, long[] ids) {
		this.type = type;
		this.ids = ids;
	}

	public int size() {
		return ids.length;
	}

	@Override
	public Iterator<Reference<T>> iterator() {
		return new Iterator<Reference<T>>() {
			private int index = -1;

			@Override
			public boolean hasNext() {
				return index + 1 < ids.length;
			}

			@Override
			public Reference<T> next() {
				index++;

				return new Reference<T>(type, ids[index]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(ids.length);

		if (ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				parcel.writeLong(ids[i]);
			}

			parcel.writeString(type.getName());
		}
	}

	private static References<Propoid> read(Parcel parcel) {
		long[] ids = new long[parcel.readInt()];

		if (ids.length > 0) {
			Class<Propoid> type;

			try {
				type = (Class<Propoid>) Class.forName(parcel.readString());
			} catch (ClassNotFoundException ex) {
				throw new ParcelFormatException(ex.getMessage());
			}

			for (int i = 0; i < ids.length; i++) {
				ids[i] = parcel.readLong();
			}
			return new References<>(type, ids);
		} else {
			return new References<>();
		}
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<References> CREATOR = new Parcelable.Creator<References>() {
		public References createFromParcel(Parcel in) {
			return read(in);
		}

		public References[] newArray(int size) {
			return new References[size];
		}
	};

	/**
	 * Create a parcel of references.
	 * 
	 * @param propoids
	 *            propoids
	 */
	public static <S extends Propoid> References<S> from(List<S> propoids) {

		Class<? extends Propoid> type = null;

		long[] ids = new long[propoids.size()];
		for (int i = 0; i < ids.length; i++) {
			S propoid = propoids.get(i);

			ids[i] = Row.getID(propoid);
			type = propoid.getClass();
		}

		if (type == null) {
			return new References();
		} else {
			return new References<S>((Class<? extends S>) type, ids);
		}
	}
}