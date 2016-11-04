package be.nabu.glue.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		Iterable<?> start = SeriesMethods.limit(index, collection);
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