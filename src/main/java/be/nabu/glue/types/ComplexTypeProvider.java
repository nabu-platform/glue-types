package be.nabu.glue.types;

import java.util.Collection;
import java.util.Map;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.core.api.OptionalTypeConverter;
import be.nabu.glue.core.api.OptionalTypeProvider;
import be.nabu.libs.types.BaseTypeInstance;
import be.nabu.libs.types.CollectionHandlerFactory;
import be.nabu.libs.types.ComplexContentWrapperFactory;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.SimpleTypeWrapperFactory;
import be.nabu.libs.types.TypeConverterFactory;
import be.nabu.libs.types.api.CollectionHandlerProvider;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedSimpleType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.map.MapContent;
import be.nabu.libs.types.mask.MaskedContent;

public class ComplexTypeProvider implements OptionalTypeProvider {

	@Override
	public OptionalTypeConverter getConverter(String type) {
		DefinedType resolved = DefinedTypeResolverFactory.getInstance().getResolver().resolve(type);
		return resolved == null ? null : new ComplexTypeConverter(resolved); 
	}
	
	public static class ComplexTypeConverter implements OptionalTypeConverter {

		private DefinedType type;

		public ComplexTypeConverter(DefinedType type) {
			this.type = type;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object convert(Object object) {
			if (object == null) {
				return null;
			}
			// if it is a dynamic type, just mask it
			else if (object instanceof ExecutionContext) {
				return new MaskedContent(new MapContent((ComplexType) type, ((ExecutionContext) object).getPipeline()), (ComplexType) type);
			}
			else if (object instanceof Map) {
				return new MaskedContent(new MapContent((ComplexType) type, (Map) object), (ComplexType) type);
			}
			else if (object instanceof MapContent) {
				return new MaskedContent((MapContent) object, (ComplexType) type);
			}
			
			CollectionHandlerProvider handler = CollectionHandlerFactory.getInstance().getHandler().getHandler(object.getClass());
			// we have a collection on our hands
			if (handler != null) {
				Collection indexes = handler.getIndexes(object);
				Object newCollection = handler.create(object.getClass(), indexes.size());
				for (Object index : indexes) {
					handler.set(newCollection, index, convert(handler.get(object, index)));
				}
				return newCollection;
			}
			
			Type sourceType = null;
			if (object instanceof ComplexContent) {
				sourceType = ((ComplexContent) object).getType();
			}
			else {
				DefinedSimpleType<? extends Object> wrap = SimpleTypeWrapperFactory.getInstance().getWrapper().wrap(object.getClass());
				if (wrap != null) {
					sourceType = wrap;
				}
				else {
					object = ComplexContentWrapperFactory.getInstance().getWrapper().wrap(object);
					if (object == null) {
						throw new ClassCastException("Can not find type for object");
					}
					sourceType = ((ComplexContent) object).getType();
				}
			}
			if (sourceType == null) {
				throw new ClassCastException("Can not resolve the class " + type.getClass() + " to a type");
			}
			// check if converted is null? if it is null but source is not, we failed conversion?
			Object convert = TypeConverterFactory.getInstance().getConverter().convert(object, new BaseTypeInstance(sourceType), new BaseTypeInstance(type));
			if (convert == null && object != null) {
				throw new ClassCastException("Can not cast " + object + " to type: " + type);
			}
			return convert;
		}

		@Override
		public Class<?> getComponentType() {
			return Object.class;
		}
		
	}
}
