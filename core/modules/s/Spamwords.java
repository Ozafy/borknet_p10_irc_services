import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.util.*;
import borknet_services.core.*;
public class Spamwords
{
 private HashMap<String,Integer> spamwords = new HashMap<String,Integer>();
 private Core C;
 
 public Spamwords(Core C)
 {
  this.C = C;
  File xmlDocument=new File(System.getProperty("user.dir")+ File.separator + "core" + File.separator + "modules" + File.separator + "s" + File.separator + "spamwords.xml");
  loadSpamwords(xmlDocument);
 }
 
 private void loadSpamwords(File xmlDocument)
 {
  try
  {
   DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
   DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
   Document doc = docBuilder.parse(xmlDocument);
   doc.getDocumentElement().normalize ();
   NodeList spam = doc.getElementsByTagName("spam");
   for(int s=0; s<spam.getLength() ; s++)
   {
    Node spamNode = spam.item(s);
    if(spamNode.getNodeType() == Node.ELEMENT_NODE)
    {
     Element spamElement = (Element)spamNode;
     NodeList wordNode = spamElement.getElementsByTagName("word");
     Element wordElement = (Element)wordNode.item(0);
     NodeList wordL = wordElement.getChildNodes();
     String word = (((Node)wordL.item(0)).getNodeValue().trim());
     NodeList scoreNode = spamElement.getElementsByTagName("score");
     Element scoreElement = (Element)scoreNode.item(0);
     NodeList scoreL = scoreElement.getChildNodes();
     String score = (((Node)scoreL.item(0)).getNodeValue().trim());
     spamwords.put(word,Integer.parseInt(score));
    }
   }
  }
  catch (Exception e)
  {
   C.report("Failed loading spamwords. " + e.toString());
  }
 }

 public Integer getPoints(String msg)
 {
  if(spamwords.containsKey(msg))
  {
   return spamwords.get(msg);
  }
  else
  {
   return 0;
  }
 }
}