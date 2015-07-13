package be.nabu.glue.types;

import be.nabu.glue.api.OptionalTypeConverter;
import be.nabu.glue.api.OptionalTypeProvider;
import be.nabu.libs.types.BaseTypeInstance;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.SimpleTypeWrapperFactory;
import be.nabu.libs.types.TypeConverterFactory;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.java.BeanResolver;

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
		
		@Override
		public Object convert(Object object) {
			if (object == null) {
				return null;
			}
			Type sourceType = null;
			if (object instanceof ComplexContent) {
				sourceType = ((ComplexContent) object).getType();
			}
			else if (type instanceof SimpleType) {
				sourceType = SimpleTypeWrapperFactory.getInstance().getWrapper().wrap(type.getClass());
			}
			else {
				sourceType = BeanResolver.getInstance().resolve(object.getClass());
			}
			if (sourceType == null) {
				throw new ClassCastException("Can not resolve the class " + type.getClass() + " to a type");
			}
			return TypeConverterFactory.getInstance().getConverter().convert(object, new BaseTypeInstance(sourceType), new BaseTypeInstance(type));
		}
		
	}
}
