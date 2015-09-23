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

import java.io.Closeable;
import java.util.List;

import propoid.core.Property;
import propoid.core.Propoid;
import android.database.Cursor;

/**
 * A match of a query.
 * 
 * @param <P>
 *            the type of matched propoids
 * 
 * @see Repository#query(Propoid, Where)
 */
public interface Match<P extends Propoid> {

	/**
	 * List all matched {@link Propoid}s.
	 * <p>
	 * See {@link #list(Range, Order...)} for further details regarding the list
	 * implementation.
	 * 
	 * @param ordering
	 *            order of listing
	 * @return all matched propoids
	 */
	public List<P> list(Order... ordering);

	/**
	 * List all matched {@link Propoid}s in the given {@link Range}.
	 * <p>
	 * The returned list is backed by a {@link Cursor} and must be closed. This
	 * can be done either by calling {@link Closeable#close()} on this match or
	 * the returned list. Additionally the returned list will be closed by:
	 * <ul>
	 * <li>getting all elements with {@link List#toArray()}</li>
	 * <li>getting all elements with {@link List#toArray(Object[])}</li>
	 * <li>iterating over all elements from its {@link List#iterator()}
	 * (e.g. when using the Java 5 foreach-loop)</li>
	 * </ul>
	 * Note that one of the above is usually involved if you add all elements to
	 * another collection, e.g. {@code new ArrayList(match.list())}.
	 * <p>
	 * {@link List#isEmpty()}, {@link List#size()} and {@link List#get(int)}
	 * will leave the {@link Cursor} open, all other methods of the returned
	 * list will throw {@link UnsupportedOperationException}.
	 * 
	 * @param range
	 *            of propoids to list
	 * @param ordering
	 *            order of listing
	 * @return all matched propoids
	 */
	public List<P> list(Range range, Order... ordering);

	/**
	 * References to all matched propoids.
	 *
	 * @return references
	 */
	public References<P> references();

	/**
	 * The first {@link Propoid} matched.
	 * 
	 * @return propoid or {@code null} if none matched.
	 */
	public P first(Order... ordering);

	/**
	 * The single {@link Propoid} matched.
	 * 
	 * @return propoid or {@code null} if none matched.
	 * @throws RepositoryException
	 *             if multiple propoids are matched
	 */
	public P single() throws RepositoryException;

	/**
	 * The count of {@link Propoid}s matched.
	 * 
	 * @return count
	 */
	public long count();

	/**
	 * Max property value.
	 * 
	 * @param property
	 *            property to get max for
	 * @return max value
	 */
	public <T> T max(Property<T> property);

	/**
	 * Min property value.
	 * 
	 * @param property
	 *            property to get min for
	 * @return min value
	 */
	public <T> T min(Property<T> property);

	/**
	 * Sum of property values.
	 * 
	 * @param property
	 *            property to get sum for
	 * @return sum of values
	 */
	public <T> T sum(Property<T> property);

	/**
	 * Average of property values.
	 * 
	 * @param property
	 *            property to get average for
	 * @return avarage of values
	 */
	public <T> T avg(Property<T> property);

	/**
	 * Delete all matched {@link Propoid}s.
	 */
	public void delete();

	/**
	 * Set a property on all matched propoids.
	 * 
	 * @param property
	 *            property to set
	 * @param value
	 *            value
	 */
	public <T> void set(Property<T> property, T value);
}
