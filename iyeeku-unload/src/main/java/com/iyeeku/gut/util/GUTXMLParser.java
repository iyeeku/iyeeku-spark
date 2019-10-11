package com.iyeeku.gut.util;


import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.InitContextException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * @ClassName GUTXMLParser
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/10 22:10
 * @Version 1.0
 **/
public class GUTXMLParser {

    public static void checkXMLFormat(String paramString1, String paramString2)
            throws SAXException, IOException, ParserConfigurationException{
        DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        localDocumentBuilderFactory.setNamespaceAware(true);
        localDocumentBuilderFactory.setValidating(true);
        String str = paramString2;
        localDocumentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/","");
        localDocumentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/","");
        GUTXMLErrorHandler localGUTXMLErrorHandler = new GUTXMLErrorHandler();
        try {
            DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
            localDocumentBuilder.setErrorHandler(localGUTXMLErrorHandler);
            localDocumentBuilder.parse(paramString1);
        }catch (SAXException localSAXException){
            throw localSAXException;
        }catch (ParserConfigurationException localParserConfigurationException){
            throw localParserConfigurationException;
        }catch (IOException localIOException){
            throw localIOException;
        }
    }

    public static Document getDocumentByPath(String paramString)
        throws FileNotFoundException, DocumentException
    {
        try {
            FileInputStream localFileInputStream = new FileInputStream(paramString);
            return getDocumentByInputStream(localFileInputStream);
        }catch (FileNotFoundException localFileNotFoundException){
            throw localFileNotFoundException;
        }catch (DocumentException localDocumentException){
            throw localDocumentException;
        }
    }

    public static Document getDocumentByInputStream(InputStream paramInputStream)
            throws FileNotFoundException, DocumentException
    {
        try {
            SAXReader localSAXReader = new SAXReader();
            Document localDocument = localSAXReader.read(paramInputStream);
            return localDocument;
        }catch (DocumentException localDocumentException){
            throw localDocumentException;
        }
    }

    public static boolean FileisExist(String paramString) throws InitContextException{
        File localFile = null;
        boolean bool = false;
        if ((paramString != null) && (paramString.trim() != "")){
            localFile = new File(paramString);
            bool = localFile.exists();
        }
        if (!bool){
            throw new InitContextException(GUTExceptionConstants.GUT100200, paramString + " Directory not found");
        }
        return bool;
    }

}
