# Glue Types Support

This library adds two things:

- **Optional Typing**: it allows types exposed via the types-api to be used in the optional type system of glue and supports conversion where applicable
- **Complex Content**: it allows complex content to be accessed by the evaluator-api. It provides access for three different types of complex content:
	- **ComplexContent**: an actual instance of ComplexContent (e.g. a structure instance, webmethods idata, XMLContent,...)
	- **org.w3c.dom.Node**: Allows access to standard DOM XML using the XMLContent in the background. Note that in this case it **does not need** the definition. It just uses the DOM directly.
	- **Beans**: allows access to standard beans using the types-beans implementation (which has support for a lot of standards like JAXB). It is broader than that default java access provided by the evaluator package itself.
	
The support for dom node opens up a few interesting avenues:

- In a case for a customer they were returning domain objects over a webservice but we did not want to add all domain objects to the glue distribution. We simply let glue access the XML directly without knowledge of the actual structure of the domain object.
- The XML support allows "transparent" access to XML nodes gotten from the glue-xml plugin, for example:

```python
# A call over a webservice returning a list of domain objects as XML
content = getDossiers()
for (dossier : nodes(content, "//dossier"))
	myId1 = xpath(dossier, "@myId")
	myId2 = dossier/@myId
	validateEquals("The ids should be the same", myId1, myId2)
```