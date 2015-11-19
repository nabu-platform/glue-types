package be.nabu.glue.types;

import java.util.ArrayList;
import java.util.List;

import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.impl.DefaultOptionalTypeProvider;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.ModifiableComplexType;
import be.nabu.libs.types.api.ModifiableComplexTypeGenerator;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.base.ComplexElementImpl;
import be.nabu.libs.types.base.SimpleElementImpl;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.properties.MaxOccursProperty;
import be.nabu.libs.types.properties.MinOccursProperty;
import be.nabu.libs.types.properties.NillableProperty;

public class GlueTypeUtils {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ComplexType toType(List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator) {
		ModifiableComplexType structure = generator.newComplexType();
		for (ParameterDescription description : parameters) {
			String typeString = description.getType();
			// any object
			if (typeString == null) {
				typeString = Object.class.getName();
			}
			else {
				Class<?> wrapDefault = DefaultOptionalTypeProvider.wrapDefault(typeString);
				if (wrapDefault != null) {
					typeString = wrapDefault.getName();
				}
			}
			DefinedType type = DefinedTypeResolverFactory.getInstance().getResolver().resolve(typeString);
			List<Value<?>> values = new ArrayList<Value<?>>();
			values.add(new ValueImpl<Boolean>(NillableProperty.getInstance(), true));
			if (description.isList()) {
				values.add(new ValueImpl<Integer>(MaxOccursProperty.getInstance(), 0));
			}
			// if there is a default value, set it to optional
			if (description.getDefaultValue() != null) {
				values.add(new ValueImpl<Integer>(MinOccursProperty.getInstance(), 0));
			}
			if (type instanceof ComplexType) {
				structure.add(new ComplexElementImpl(description.getName(), (ComplexType) type, structure, values.toArray(new Value[values.size()])));
			}
			else {
				structure.add(new SimpleElementImpl(description.getName(), (SimpleType<?>) type, structure, values.toArray(new Value[values.size()])));
			}
		}
		return structure;
	}
}
