import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

public class LibxmlWriter extends Thread{
	private FilterInfo[] filterinfos;
	private org.w3c.dom.Document document;
	private String xml;
	private File writingTo;

	public LibxmlWriter(FilterInfo[] infos) {
		// TODO Auto-generated constructor stub
		filterinfos = infos;
	}
	public String getXml(){
		if(xml==null){
			StringWriter resultGot = GetResult();
			if(resultGot==null)return null;
			xml = resultGot.toString();
		}
		return xml;
	}
	public String Build(){
		run();
		return getXml();
	}
	public void BuildinBack(File f){
		start();
		writingTo = f;
	}
	public StringWriter GetResult(){
		StringWriter result = null;
		try{
			TransformerFactory transfactory = TransformerFactory.newInstance();
			transfactory.setAttribute("indent-number", new Integer(2));
			Transformer former = transfactory.newTransformer();
			former.setOutputProperty(OutputKeys.METHOD, "xml");
			former.setOutputProperty(OutputKeys.INDENT,"yes" );
			result = new StringWriter();
			former.transform(new DOMSource(document.getDocumentElement()), new StreamResult(result));
		}catch(Exception er){
			er.printStackTrace();
			result = null;
		}
		return result;
	}
	@Override public void run(){
		try {
			if(document==null){
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				DOMImplementation domimp = builder.getDOMImplementation();
				document = domimp.createDocument("", xmlRule.Lib, null);
			}
			_build();
			if(writingTo!=null){
				String result = getXml();
				try{
					FileWriter writer = new FileWriter(writingTo);
					writer.write(result);
					writer.close();
				}catch(Exception er){
					er.printStackTrace();
				}
				writingTo = null;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void _build(){
		try{
			Element elementRoot = document.getDocumentElement();
			for(FilterInfo info : filterinfos){
				elementRoot.appendChild(_makeElement(info));
			}
		}catch(Exception e){
			e.printStackTrace();
			document = null;
		}
	}
	private Element _makeElement(FilterInfo info)throws Exception{
		if(info==null||document==null)return null;
		Element filterinfoElement = document.createElement(xmlRule.LibFilterInfo);
		Element nameE = document.createElement(xmlRule.LibName);
		Element nameInCE = document.createElement(xmlRule.LibNameInC);
		Element versionE = document.createElement(xmlRule.LibVersion);
		Element acceptInE = document.createElement(xmlRule.LibAcceptAbleIn);
		Element acceptOutE = document.createElement(xmlRule.LibAcceptAbleOut);
		Element codeE = document.createElement(xmlRule.LibCode);
		Element descriptionE = document.createElement(xmlRule.LibDescription);
		Element UrlE = document.createElement(xmlRule.LibUrl);
		Element FamillyE = document.createElement(xmlRule.LibFamilly);
		filterinfoElement.appendChild(nameE);
		filterinfoElement.appendChild(nameInCE);
		filterinfoElement.appendChild(versionE);
		filterinfoElement.appendChild(acceptInE);
		filterinfoElement.appendChild(acceptOutE);
		filterinfoElement.appendChild(codeE);
		filterinfoElement.appendChild(descriptionE);
		filterinfoElement.appendChild(UrlE);
		filterinfoElement.appendChild(FamillyE);
		nameE.setTextContent(info.getName());
		nameInCE.setTextContent(info.getNameInC());
		versionE.setTextContent(info.getVersion());
		acceptInE.setTextContent(""+info.getAcceptableInputStream());
		acceptOutE.setTextContent(""+info.getAcceptableOutputStream());
		codeE.setTextContent(info.getCode());
		descriptionE.setTextContent(info.getDescription());
		UrlE.setTextContent(info.getUrl()==null?null:info.getUrl().toString());
		FamillyE.setTextContent(info.getFamilly());
		int n = 0;
		for(String str : info.getIncludes()){
			Element includesE = document.createElement(xmlRule.LibIncludes);
			includesE.setAttribute(xmlRule.LibIncludesNumber,""+n );
			includesE.setTextContent(str);
			filterinfoElement.appendChild(includesE);
			n++;
		}
		return filterinfoElement;
	}

}
