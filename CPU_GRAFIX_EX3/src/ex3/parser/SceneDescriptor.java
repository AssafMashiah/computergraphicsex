package ex3.parser;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains a scene description. Ensures syntactic correctness but not semantic.
 * 
 */
public class SceneDescriptor {

	protected Map<String, String> sceneAttributes;
	protected Map<String, String> cameraAttributes;
	protected List<Element> objects;

	/**
	 * Constructs scene description from given xml formatted text. Verifies
	 * syntactic requirements that at least one scene element and one camera
	 * element should exist.
	 * 
	 * @param text
	 *            XML string
	 * @throws ParseException
	 */
	public void fromXML(String text) throws ParseException {

		objects = new LinkedList<Element>();

		SceneXMLParser parser = new SceneXMLParser();
		parser.parse(text, this);

		// Verify that scene structure is syntactically correct
		if (sceneAttributes == null)
			throw new ParseException("No scene element found!", 0);
		if (cameraAttributes == null)
			throw new ParseException("No camera element found!", 0);
	}

	public Map<String, String> getSceneAttributes() {
		return sceneAttributes;
	}

	public void setSceneAttributes(Map<String, String> sceneAttributes) {
		this.sceneAttributes = sceneAttributes;
	}

	public Map<String, String> getCameraAttributes() {
		return cameraAttributes;
	}

	public void setCameraAttributes(Map<String, String> cameraAttributes) {
		this.cameraAttributes = cameraAttributes;
	}

	public List<Element> getObjects() {
		return objects;
	}

	public void setObjects(List<Element> objects) {
		this.objects = objects;
	}

}
