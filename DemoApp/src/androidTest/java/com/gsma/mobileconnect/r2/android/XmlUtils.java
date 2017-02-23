package com.gsma.mobileconnect.r2.android;


import android.content.Context;

import com.gsma.mobileconnect.r2.android.demo.R;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class XmlUtils {

	private final Context context;

	public XmlUtils(Context context){
		this.context = context;
	}
	/** provide node text by xpath expression
	 * @param xpathExpression like //serviceUri
	 * @return text from the node
	 */
    public String getNodeText(String xpathExpression) throws FileNotFoundException {
		InputStream inputStream = context.getResources().openRawResource(R.raw.testconfiguration);
		InputSource inputSource = new InputSource(inputStream);
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return node.getTextContent();
	}
}