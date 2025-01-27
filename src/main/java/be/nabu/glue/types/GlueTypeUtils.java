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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.core.impl.DefaultOptionalTypeProvider;
import be.nabu.glue.utils.ScriptUtils;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.ModifiableComplexType;
import be.nabu.libs.types.api.ModifiableComplexTypeGenerator;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.base.ComplexElementImpl;
import be.nabu.libs.types.base.SimpleElementImpl;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.properties.MaxOccursProperty;
import be.nabu.libs.types.properties.MinOccursProperty;
import be.nabu.libs.types.properties.NillableProperty;
import be.nabu.libs.types.simple.Bytes;

public class GlueTypeUtils {
	
	public static ComplexType toType(List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator, ScriptRepository repository) {
		return toType(null, parameters, generator, repository, DefinedTypeResolverFactory.getInstance().getResolver());
	}
	
	public static ComplexType toType(String name, List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator, ScriptRepository repository) {
		return toType(name, parameters, generator, repository, new HashMap<String, Type>(), DefinedTypeResolverFactory.getInstance().getResolver());
	}
	
	public static ComplexType toType(List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator, ScriptRepository repository, DefinedTypeResolver typeResolver) {
		return toType(null, parameters, generator, repository, typeResolver);
	}
	
	public static ComplexType toType(String name, List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator, ScriptRepository repository, DefinedTypeResolver typeResolver) {
		return toType(name, parameters, generator, repository, new HashMap<String, Type>(), typeResolver);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ComplexType toType(String name, List<ParameterDescription> parameters, ModifiableComplexTypeGenerator generator, ScriptRepository repository, Map<String, Type> resolved, DefinedTypeResolver typeResolver) {
		ModifiableComplexType structure = generator.newComplexType();
		resolved.put(name, structure);
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
			Type type = resolved.get(typeString);
			if (type == null) {
				if (typeString.equals("[B")) {
					type = new Bytes();
				}
				else {
					type = typeResolver.resolve(typeString);
				}
				if (type == null && repository != null) {
					try {
						Script script = repository.getScript(typeString);
						if (script != null) {
							type = toType(ScriptUtils.getFullName(script), ScriptUtils.getInputs(script), generator, repository, resolved, typeResolver);
						}
					}
					catch (Exception e) {
						throw new RuntimeException("Could not parse script: " + typeString);
					}
				}
			}
			if (type == null) {
				throw new IllegalArgumentException("Can not resolve type: " + typeString);
			}
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
