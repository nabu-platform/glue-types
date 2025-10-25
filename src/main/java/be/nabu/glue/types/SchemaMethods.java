/*
* Copyright (C) 2025 Alexander Verbruggen
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import be.nabu.libs.evaluator.annotations.MethodProviderClass;
import be.nabu.libs.types.ComplexContentWrapperFactory;
import be.nabu.libs.types.DefinedTypeResolverFactory;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.definition.xml.XMLDefinitionMarshaller;
import be.nabu.libs.types.mask.MaskedContent;

@MethodProviderClass(namespace = "schema")
public class SchemaMethods {
	
	@SuppressWarnings("unchecked")
	public static String describe(Object object) throws IOException {
		ComplexContent content = object instanceof ComplexContent ? (ComplexContent) object : ComplexContentWrapperFactory.getInstance().getWrapper().wrap(object);
		if (content == null) {
			throw new IllegalArgumentException("Not a valid complex content: " + object);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new XMLDefinitionMarshaller().marshal(output, content.getType());
		return new String(output.toByteArray(), Charset.defaultCharset());
	}
	
}
