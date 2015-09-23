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
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A parcel of {@link Reference}s.
 */
public class References<T extends Propoid> implements Parcelable, Iterable<Reference<T>> {

	private List<Reference<T>> references;

	public References(List<Reference<T>> references) {
		this.references = references;
	}

	public List<Reference<T>> getReferences() {
		return references;
	}

	public boolean isEmpty() {
		return references.isEmpty();
	}

	public int size() {
		return references.size();
	}

	@Override
	public Iterator<Reference<T>> iterator() {
		return references.iterator();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Object temp = references;
		write(dest, (List<Reference<Propoid>>) temp);
	}

	private static void write(Parcel parcel, List<Reference<Propoid>> references) {
		parcel.writeInt(references.size());

		for (Reference<Propoid> reference : references) {
			parcel.writeString(reference.toString());
		}
	}

	private static List<Reference<Propoid>> read(Parcel parcel) {
		int size = parcel.readInt();

		List<Reference<Propoid>> references = new ArrayList<Reference<Propoid>>(
				size);

		for (int r = 0; r < size; r++) {
			references.add(Reference.from(parcel.readString()));
		}

		return references;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<References> CREATOR = new Parcelable.Creator<References>() {
		public References createFromParcel(Parcel in) {
			return new References<Propoid>(read(in));
		}

		public References[] newArray(int size) {
			return new References[size];
		}
	};

	/**
	 * Create a parcel of references.
	 * 
	 * @param propoids
	 *            propoids to put into parcel
	 */
	public static <S extends Propoid> References<S> from(List<S> propoids) {
		List<Reference<S>> references = new ArrayList<Reference<S>>();

		for (S propoid : propoids) {
			references.add(new Reference<S>(propoid));
		}

		return new References<S>(references);
	}
}