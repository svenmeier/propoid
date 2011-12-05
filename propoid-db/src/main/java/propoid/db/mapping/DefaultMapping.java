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
package propoid.db.mapping;

import java.util.ArrayList;
import java.util.List;

import propoid.core.Property;
import propoid.db.Mapping;
import propoid.db.Repository;
import propoid.db.RepositoryException;

/**
 * Default mapping implementation supporting basic types.
 */
public class DefaultMapping implements Mapping {

	private List<Mapper<?>> mappers = new ArrayList<Mapper<?>>();

	public DefaultMapping() {
		registerDefaults();
	}

	protected void registerDefaults() {
		register(new ClassMapper());
		register(new StringMapper());
		register(new CharacterMapper());
		register(new BooleanMapper());
		register(new BytesMapper());
		register(new ByteMapper());
		register(new ShortMapper());
		register(new IntegerMapper());
		register(new LongMapper());
		register(new FloatMapper());
		register(new DoubleMapper());
		register(new LocaleMapper());
		register(new DateMapper());
		register(new LocationMapper());
		register(new EnumMapper());
		register(new PropoidMapper());
	}

	public void register(Mapper<?> mapper) {
		mappers.add(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Mapper<T> getMapper(Repository repository,
			Property<? extends T> property) {
		for (Mapper<?> mapper : mappers) {
			if (mapper.maps(property)) {
				return (Mapper<T>) mapper;
			}
		}

		throw new RepositoryException("no mapper for " + property.type());
	}
}