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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import be.nabu.libs.evaluator.EvaluationException;
import be.nabu.libs.evaluator.api.ContextAccessor;
import be.nabu.libs.types.xml.XMLContent;

public class XMLContextAccessor implements ContextAccessor<Node> {

	@Override
	public Class<Node> getContextType() {
		return Node.class;
	}

	@Override
	public boolean has(Node context, String name) throws EvaluationException {
		// we don't have a definition so can't check
		return get(context, name) != null;
	}

	@Override
	public Object get(Node context, String name) throws EvaluationException {
		return new XMLContent(context instanceof Document ? ((Document) context).getDocumentElement() : (Element) context)
			.get(name);
	}

}
