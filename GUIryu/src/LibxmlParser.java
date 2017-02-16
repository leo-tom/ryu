import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LibxmlParser {
	private Document document;
	private FilterInfo[] filterinfos;
	public LibxmlParser() {
		// TODO Auto-generated constructor stub
	}
	public void ParseThis(File f) throws ParserConfigurationException,IOException, SAXException{
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fac.newDocumentBuilder();
		document = builder.parse(new InputSource(new FileReader(f)));
		_parse();
	}
	public void ParseThis(Document doc){
		if(doc==null)return;
		document = doc;
		_parse();
	}
	public FilterInfo[] GetFilterInfo(){
		return filterinfos;
	}
	private void _parse(){
		try{
			Element documentEle = document.getDocumentElement();
			documentEle.normalize();
			NodeList filters = documentEle.getElementsByTagName(xmlRule.LibFilterInfo);
			filterinfos = new FilterInfo[filters.getLength()];
			for(int i=0;i<filters.getLength();i++){
				Element ele = (Element)filters.item(i);
				
				NodeList list = ele.getElementsByTagName(xmlRule.LibName);
				Element nameE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibNameInC);
				Element nameInCE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibVersion);
				Element versionE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibAcceptAbleIn);
				Element acceptInE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibAcceptAbleOut);
				Element acceptOutE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibCode);
				Element codeE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibDescription);
				Element descriptionE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibUrl);
				Element UrlE = list.getLength()>0?(Element)list.item(0):null;
				list = ele.getElementsByTagName(xmlRule.LibIncludes);
				String[] includes = new String[list.getLength()];
				for(int index = 0;index<list.getLength();index++){
					includes[index] = list.item(index).getTextContent();
				}
				list = ele.getElementsByTagName(xmlRule.LibFamilly);
				Element FamillyE = list.getLength()>0?((Element)list.item(0)):null;
				String name = nameE==null?null:nameE.getTextContent();
				filterinfos[i] = new FilterInfo.Builder(name==null?null:nameE.getTextContent())
						.setNameInC(nameInCE==null?null:nameInCE.getTextContent())
						.setVersion(versionE==null?null:versionE.getTextContent())
						.setCode(codeE==null?null:codeE.getTextContent())
						.setAcceptableInputStream(Integer.parseInt(acceptInE==null?null:acceptInE.getTextContent()))
						.setAcceptableOutputStream(Integer.parseInt(acceptOutE==null?null:acceptOutE.getTextContent()))
						.setDescription(descriptionE==null?null:descriptionE.getTextContent())
						.setUrl(UrlE==null?null:UrlE.getTextContent())
						.addIncludes(includes)
						.setFamilly(FamillyE==null?"else":FamillyE.getTextContent())
						.build()
						;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
