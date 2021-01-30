/*
 * 0.0.4    30Jan21     ameded "list all" and "list next"
 * 
 * 
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class birthday {

    static String birthdayXML;
    static Document doc = null;
    static Integer list = 20;

    public static void openXML() {

        File xmlFile = new File(birthdayXML);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NodeList getBirthday(String date) {

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            XPathExpression expr = xpath.compile("/birthdays/birthday[date='" + date + "']");
            return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void listNext()   {

        SimpleDateFormat formatter = new SimpleDateFormat("d.M");
        Calendar calendar = Calendar.getInstance();

        do  {
            String date = formatter.format(calendar.getTime());
            NodeList nl = getBirthday(date);
            if (nl != null && nl.getLength() > 0) {
                for (int j = 0; j < nl.getLength(); j++) {
                    Element birthday = (Element) nl.item(j).getChildNodes();
                    NodeList birthdate = birthday.getElementsByTagName("date");
                    NodeList name = birthday.getElementsByTagName("name");
                    NodeList year = birthday.getElementsByTagName("year");
                    if (year.getLength() > 0) {
						System.out.printf("%5s.%s\t%-20s%n", birthdate.item(0).getTextContent(), year.item(0).getTextContent(), name.item(0).getTextContent());
                    } else {
						System.out.printf("%5s\t%-20s%n", birthdate.item(0).getTextContent(), name.item(0).getTextContent());
                    }
                }
                break;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }   while(true);
    }

    public static void listAll()    {

        SimpleDateFormat formatter = new SimpleDateFormat("d.M");
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        calendar.set(thisYear, 0, 1, 0, 0);
        Calendar finish = (Calendar)calendar.clone();
        finish.set(thisYear, 11, 31, 0, 0);

        do  {
            String date = formatter.format(calendar.getTime());
            NodeList nl = getBirthday(date);
            if (nl != null && nl.getLength() > 0) {
                for (int j = 0; j < nl.getLength(); j++) {
                    Element birthday = (Element) nl.item(j).getChildNodes();
                    NodeList birthdate = birthday.getElementsByTagName("date");
                    NodeList name = birthday.getElementsByTagName("name");
                    NodeList year = birthday.getElementsByTagName("year");
                    if (year.getLength() > 0) {
						System.out.printf("%5s.%s\t%-20s%n", birthdate.item(0).getTextContent(), year.item(0).getTextContent(), name.item(0).getTextContent());
                    } else {
						System.out.printf("%5s\t%-20s%n", birthdate.item(0).getTextContent(), name.item(0).getTextContent());
                    }
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }   while(calendar.compareTo(finish) <= 0);
    }

    public static void list() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("d.M");

        for (int i = 0; i < list; i++) {
            String date = formatter.format(calendar.getTime());
            NodeList nl = getBirthday(date);
            if (nl != null && nl.getLength() > 0) {
                for (int j = 0; j < nl.getLength(); j++) {
                    Element birthday = (Element) nl.item(j).getChildNodes();
                    NodeList birthdate = birthday.getElementsByTagName("date");
                    NodeList name = birthday.getElementsByTagName("name");
                    NodeList year = birthday.getElementsByTagName("year");
                    if (year.getLength() > 0) {
                        int age = calendar.get(Calendar.YEAR) - Integer.parseInt(year.item(0).getTextContent());
                        System.out.printf("%5s\t%-20s (%d)%n", birthdate.item(0).getTextContent(), name.item(0).getTextContent(), age);
                    } else {
                        System.out.printf("%5s\t%-20s%n", birthdate.item(0).getTextContent(), name.item(0).getTextContent());
                    }
                }
            }
            else    {
                System.out.printf("%5s%n", date);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    private static void saveXML() {

        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        }
        StreamResult result = new StreamResult(birthdayXML);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    public static void add(String strName, String strDate, String strYear)   {

        Element birthday = doc.createElement("birthday");

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(strName));
        birthday.appendChild(name);

        Element date = doc.createElement("date");
        date.appendChild(doc.createTextNode(strDate));
        birthday.appendChild(date);

        if (!strYear.isEmpty())   {
            Element year = doc.createElement("year");
            year.appendChild(doc.createTextNode(strYear));
            birthday.appendChild(year);
        }

        Element root = doc.getDocumentElement();
        root.appendChild(birthday);

        saveXML();

        System.out.println("Birthday added:");
        System.out.println("\tname=" + strName + "\tbirth date=" + strDate + "." + strYear);
    }

    public static void remove(String strName, String strDate)   {

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            XPathExpression expr = xpath.compile("/birthdays/birthday[name='" + strName + "' and date='" + strDate + "']");
            expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++)    {
                    Node birthday = nl.item(i);
                    birthday.getParentNode().removeChild(birthday);
                    System.out.println(strName + ", born " + strDate + " : removed.");
                }
                saveXML();
            }
            else    {
                System.out.println(strName + " " + strDate + " NOT found!");
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

    }

    public static void help()   {
        System.out.println("Usage:");
        System.out.println("birthday <birthday-file.xml> [ action ]");
        System.out.println("action:");
        System.out.println("\tlist [ number-of-days | all | next ]\tlist 20 is default action");
        System.out.println("\tadd name date [ year ]\tDate format is d.m");
        System.out.println("\tdel name date  \t\tdelete, remove are synonyms, Date format is d.m");
        System.out.println("\thelp\t\t\tthis help");
    }


    public static void main(String[] args)  {

        String action = "list";

        System.out.println("birtday version 0.0.4\t(c) 2021 OM");

        if (args.length > 0)    {
            birthdayXML = args[0];
        }
        if (args.length > 1)    {
            action = args[1];
        }

        openXML();
        if (doc == null)    {
            return;
        }

        switch (action.toLowerCase())   {
            case "list":
                if (args.length > 2)    {
                    try {
                        list = Integer.parseInt(args[2]);
                        list();
                    }   catch (NumberFormatException ex)    {
                        switch (args[2].toLowerCase())  {
                            case "next":
                                listNext();
                                break;
                            case "all":
                                listAll();
                                break;
                            default:
                                help();
                        }
                    }
                }
                else {
                    list();
                }
                break;
            case "add":
                if (args.length > 4)    {
                    add(args[2], args[3], args[4]);
                }
                else if (args.length > 3)   {
                    add(args[2], args[3], "");
                }
                else    {
                    System.out.println("Invalid number of parameters!");
                }
                break;
            case "remove":
            case "delete":
            case "del":
                if (args.length > 3)    {
                    remove(args[2], args[3]);
                }
                else    {
                    System.out.println("Invalid number of parameters!");
                }
                break;
            case "help":
            case "-h":
            case "/h":
            case "/?":
            default:
                help();
                break;
        }
    }
}
