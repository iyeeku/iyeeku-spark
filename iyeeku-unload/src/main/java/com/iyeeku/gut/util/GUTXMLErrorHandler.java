package com.iyeeku.gut.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @ClassName GUTXMLErrorHandler
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 21:18
 * @Version 1.0
 **/
public class GUTXMLErrorHandler implements ErrorHandler {

    private String getSaxParserString(SAXParseException paramSAXParseException){
        return "NO." + new Integer(paramSAXParseException.getLineNumber()).toString() + " line of xml file is unavailabled!" + paramSAXParseException.getMessage();
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        throw new SAXException(getSaxParserString(exception));
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw new SAXException(getSaxParserString(exception));
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw new SAXException(getSaxParserString(exception));
    }
}
