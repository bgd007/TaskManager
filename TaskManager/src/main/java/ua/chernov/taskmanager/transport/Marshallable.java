package ua.chernov.taskmanager.transport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Marshallable {
	
	/**
//	 * convert object to xml node
//	 * 
//	 * @param doc
//	 *            DOM document for result node
//	 * @return DOM element
//	 */
	public Element toXML(Document doc);
}
