package ex3.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses the scene xml description and initializes the scene description object
 * 
 * 
 */
public class SceneXMLParser extends DefaultHandler {

	SceneDescriptor sceneDesc;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Map<String, String> myAttributes = new HashMap<String, String>();

		for (int i = 0; i < attributes.getLength(); ++i) {
			String k = attributes.getQName(i);
			String v = attributes.getValue(i).trim();

			myAttributes.put(k, v);
		}

		if (qName == "scene")
			sceneDesc.sceneAttributes = myAttributes;
		else if (qName == "camera")
			sceneDesc.cameraAttributes = myAttributes;
		else {
			Element e = new Element(qName);
			e.attributes = myAttributes;
			sceneDesc.objects.add(e);
		}

		super.startElement(uri, localName, qName, attributes);
	}

	/**
	 * Start parsing scene description XML
	 * 
	 * @param text
	 *            XML Scene description
	 * @param sceneDesc
	 *            Scene Description object to contain scene description
	 */
	public void parse(String text, SceneDescriptor sceneDesc) {

		this.sceneDesc = sceneDesc;

		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			xr.parse(new InputSource(new StringReader(text)));

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
