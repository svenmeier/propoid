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

import propoid.core.Propoid;
import propoid.db.aspect.Row;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/**
 * A reference to a {@link Propoid}.
 */
public class Reference<P extends Propoid> implements Parcelable {

	public final Class<P> type;
	public final long id;

	@SuppressWarnings("unchecked")
	public Reference(P propoid) {
		this((Class<P>) propoid.getClass(), Row.getID(propoid));
	}

	public Reference(Class<P> type, long id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type.getName());
		dest.writeLong(id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final Creator<Reference> CREATOR = new Creator<Reference>() {

		@Override
		public Reference createFromParcel(Parcel source) {
			Class<? extends Propoid> type;
			try {
				type = (Class<? extends Propoid>) Class.forName(source
						.readString());
			} catch (ClassNotFoundException ex) {
				throw new ParcelFormatException();
			}

			long id = source.readLong();

			return new Reference(type, id);
		}

		@Override
		public Reference[] newArray(int size) {
			return new Reference[size];
		}
	};
}