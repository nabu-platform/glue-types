/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.glue.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import be.nabu.glue.core.api.CollectionIterable;
import be.nabu.glue.core.impl.methods.ScriptMethods;
import be.nabu.glue.core.impl.methods.v2.SeriesMethods;
import be.nabu.libs.types.IntegerCollectionProviderBase;

@SuppressWarnings("rawtypes")
public class IterableCollectionHandler extends IntegerCollectionProviderBase<CollectionIterable> {

	public IterableCollectionHandler() {
		super(CollectionIterable.class);
	}

	@Override
	public CollectionIterable create(Class<? extends CollectionIterable> definitionClass, int size) {
		return new CollectionArrayList<Object>(size);
	}

	@Override
	public Class<?> getComponentType(Type type) {
		if (!(type instanceof ParameterizedType))
			throw new IllegalArgumentException("Raw lists are not supported, you need to add generics (" + type.getClass() + ")");
		else {
			Type result = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (result instanceof ParameterizedType) {
				return (Class<?>) ((ParameterizedType) result).getRawType();
			}
			// using a generic parameter
			else if (result instanceof TypeVariable || result instanceof WildcardType) {
				return Object.class;
			}
			else if (!Class.class.equals(result.getClass())) {
				throw new IllegalArgumentException("The parameter " + result + " (of type " + result.getClass() + ") is not a class");
			}
			return (Class<?>) result;
		}
	}

	@Override
	public CollectionIterable set(CollectionIterable collection, Integer index, Object value) {
		if (collection == null) {
			collection = new CollectionArrayList<Object>();
		}
		int size = ScriptMethods.size(collection);
		if (index >= size) {
			List padding = Arrays.asList(new Object[index - size]);
			return (CollectionIterable) SeriesMethods.merge(collection, padding, value);
		}
		else {
			Iterable<?> start = SeriesMethods.limit(index, collection);
			Iterable<?> end = SeriesMethods.offset(index, collection);
			return (CollectionIterable) SeriesMethods.merge(start, value, end);
		}
	}

	@Override
	public Object get(CollectionIterable collection, Integer index) {
		if (collection == null || index >= ScriptMethods.size(collection)) {
			return null;
		}
		Iterable<?> start = SeriesMethods.offset(index, collection);
		return SeriesMethods.first(start);
	}

	@Override
	public CollectionIterable delete(CollectionIterable collection, Integer index) {
		if (collection == null || index >= ScriptMethods.size(collection)) {
			return collection;
		}
		Iterable<?> start = SeriesMethods.limit(index, collection);
		Iterable<?> end = SeriesMethods.offset(index + 1, collection);
		return (CollectionIterable) SeriesMethods.merge(start, end);
	}

	@Override
	public Iterable<?> getAsIterable(final CollectionIterable collection) {
		return new Iterable() {
			public Iterator iterator() {
				return new Iterator() {
					private Iterator original = collection.iterator();
					@Override
					public boolean hasNext() {
						return original.hasNext();
					}
					@Override
					public Object next() {
						Object next = original.next();
						if (next instanceof Callable) {
							try {
								next = ((Callable) next).call();
							}
							catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
						return next;
					}
				};
			}
		};
	}
	
	@Override
	public Collection<?> getAsCollection(CollectionIterable collection) {
		return collection == null ? null : SeriesMethods.resolve(collection);
	}

	@Override
	public Collection<Integer> getIndexes(CollectionIterable collection) {
		return generateIndexes(collection == null ? 0 : ScriptMethods.size(collection));
	}

	public static class CollectionArrayList<T> extends ArrayList<T> implements CollectionIterable<T> {

		private static final long serialVersionUID = 1L;

		public CollectionArrayList() {
			// empty
		}
		public CollectionArrayList(Collection<? extends T> c) {
			super(c);
		}
		public CollectionArrayList(int initialCapacity) {
			super(initialCapacity);
		}
	}
}
