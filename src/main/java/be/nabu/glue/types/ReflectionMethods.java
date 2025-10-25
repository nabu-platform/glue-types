package be.nabu.glue.types;

import be.nabu.libs.evaluator.annotations.MethodProviderClass;
import be.nabu.libs.types.ComplexContentWrapperFactory;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.mask.MaskedContent;

@MethodProviderClass(namespace = "reflection")
public class ReflectionMethods {
	@SuppressWarnings("unchecked")
	public static Object mask(Object instance, Object type) {
		if (instance == null) {
			return null;
		}
		if (type == null) {
			return null;
		}
		if (!(instance instanceof ComplexContent)) {
			instance = ComplexContentWrapperFactory.getInstance().getWrapper().wrap(instance);
		}
		if (!(type instanceof ComplexType)) {
			type = DefinedTypeResolverFactory.getInstance().getResolver().resolve(type.toString());
		}
		return new MaskedContent((ComplexContent) instance, (ComplexType) type);
	}
}
