package file;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.CustomException;

public class FileElement {

	private final Element	element;
	private final Document	document;


	public FileElement(Element _element, Document _document) {
		element = _element;
		document = _document;
	}


	public void add(BooleanAttr _attr, boolean _b) throws CustomException {
		element.setAttribute(_attr.name, Boolean.toString(_b));
	}

	public void add(FloatAttr _attr, float _f) throws CustomException {
		element.setAttribute(_attr.name, Float.toString(_f));
	}

	public void add(IntAttr _attr, int _i) throws CustomException {
		element.setAttribute(_attr.name, Integer.toString(_i));
	}

	public void add(StringAttr _attr, String _s) throws CustomException {
		element.setAttribute(_attr.name, _s);
	}

	public FileElement createChild(Tag _tag) {
		Element childE = document.createElement(_tag.string);
		element.appendChild(childE);
		return new FileElement(childE, document);
	}

	public boolean get(BooleanAttr _attr) throws CustomException {
		return Boolean.parseBoolean(element.getAttribute(_attr.value));
	}

	public float get(FloatAttr _attr) throws CustomException {
		return Float.parseFloat(element.getAttribute(_attr.value));
	}

	public int get(IntAttr _attr) throws CustomException {
		return Integer.parseInt(element.getAttribute(_attr.value));
	}

	public String get(StringAttr _attr) throws CustomException {
		return element.getAttribute(_attr.value);
	}

	public FileElement getChild(Tag _tag) throws CustomException {
		NodeList nodes = element.getElementsByTagName(_tag.string);
		if (nodes.getLength() == 1) {
			Node node = nodes.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				return new FileElement(element, document);
			} else {
				throw new CustomException("node.getNodeType() != Node.ELEMENT_NODE");
			}
		} else {
			throw new CustomException("list.getLength != 1");
		}
	}

	public ArrayList<FileElement> getChildren() throws CustomException {
		ArrayList<FileElement> eList = new ArrayList<FileElement>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				eList.add(new FileElement(element, document));
			} else {
				throw new CustomException("node.getNodeType() != Node.ELEMENT_NODE");
			}
		}
		return eList;
	}

	public ArrayList<FileElement> getChildren(Tag _tag) throws CustomException {
		ArrayList<FileElement> eList = new ArrayList<FileElement>();
		NodeList nodes = element.getElementsByTagName(_tag.string);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				eList.add(new FileElement(element, document));
			} else {
				throw new CustomException("node.getNodeType() != Node.ELEMENT_NODE");
			}
		}
		return eList;
	}

}
