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
