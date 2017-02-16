import java.awt.Component;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class xmlBuilder extends Thread{
	private JPanel panel;
	private String xml;
	private boolean IsDone;
	private ArrayList<Stream_ryu> streams;
	Document document;
	public xmlBuilder(JPanel panel){
		this.panel = panel;
		document = null;
		IsDone = false;
	}
	public boolean hasFinished(){return IsDone;}
	public void setPanel(JPanel val){this.panel=val;}
	public String getXml(){
		return xml;
	}
	public Document getDocument(){
		return document;
	}
	public void build() throws Exception{
		this.run();
	}
	public void buildinBack(){
		this.start();
	}
	@Override public void run(){
		try{this.Build();}
		catch(Exception er)
		{
			er.printStackTrace();
			IsDone = true;
			xml = null;
		}
		
	}
	private void Build() throws Exception{
		if(this.panel==null) throw new NullPointerException();
		IsDone = false;
		try{
			streams = new ArrayList<Stream_ryu>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation domimp = builder.getDOMImplementation();
			document = domimp.createDocument("", xmlRule.xmlProject, null);
			Element documentelement = document.getDocumentElement();
			documentelement.setAttribute(xmlRule.xmlProjectName, panel.getToolTipText());
			for(Component com : panel.getComponents()){
				if(com instanceof StreamInitializer){
					documentelement.appendChild(toNode((StreamInitializer)com,document));
				}else if(com instanceof Filter){
					documentelement.appendChild(toNode((Filter)com,document));
				}
			}
			TransformerFactory transfactory = TransformerFactory.newInstance();
			transfactory.setAttribute("indent-number", new Integer(2));
			Transformer former = transfactory.newTransformer();
			former.setOutputProperty(OutputKeys.METHOD, "xml");
			former.setOutputProperty(OutputKeys.INDENT,"yes" );
			StringWriter stwriter = new StringWriter();
			former.transform(new DOMSource(document), new StreamResult(stwriter));
			xml = stwriter.toString();
			IsDone = true;
			streams = null;//tell gc that im not using this anymore
		}catch(Exception er){
			throw er;
		}
	}
	private org.w3c.dom.Element toNode(Filter filter, Document doc) {
		Element element = doc.createElement(xmlRule.xmlFilter);
		
		//input stream
		int i = 0;
		Element ele = doc.createElement(xmlRule.xmlInSt);
		for(;i<8;i++){
			Element input = doc.createElement(xmlRule.xmlIn);
			input.setAttribute(xmlRule.xmlIndex, Integer.toString(i));
			if(filter.getInStreamAt(i)!=null){
				input.appendChild(doc.createTextNode(Integer.toString(filter.getInStreamAt(i).hashCode())));
				if(!streams.contains(filter.getInStreamAt(i)))
					streams.add(filter.getInStreamAt(i));
			}else{
				input.appendChild(doc.createTextNode(xmlRule.xmlNull));
			}
			ele.appendChild(input);
		}
		element.appendChild(ele);
		//output stream
		ele = doc.createElement(xmlRule.xmlOutSt);
		for(i=0;i<8;i++){
			Element out = doc.createElement(xmlRule.xmlOut);
			out.setAttribute(xmlRule.xmlIndex, Integer.toString(i));
			if(filter.getOutStreamAt(i)!=null){
				out.appendChild(doc.createTextNode(Integer.toString(filter.getOutStreamAt(i).hashCode())));
				if(!streams.contains(filter.getOutStreamAt(i)))
					streams.add(filter.getOutStreamAt(i));
			}else{
				out.appendChild(doc.createTextNode(xmlRule.xmlNull));
			}
			ele.appendChild(out);
		}
		element.appendChild(ele);
		//set filter
		ele = doc.createElement(xmlRule.xmlFilterInfo);
		FilterInfo info = filter.getFilterInfo();
		if(info!=null){
			Element name_ele = doc.createElement(xmlRule.xmlFilterInfo_Name);
			name_ele.appendChild(doc.createTextNode(info.getName()));
			Element ver_ele = doc.createElement(xmlRule.xmlFilterInfo_Version);
			if(info.getVersion()!=null)
				ver_ele.appendChild(doc.createTextNode(info.getVersion()));
			else
				ver_ele.appendChild(doc.createTextNode(""));
			Element url_ele = doc.createElement(xmlRule.xmlFilterInfo_Url);
			if(info.getUrl()!=null)
				url_ele.appendChild(doc.createTextNode(info.getUrl().toString()));
			else
				url_ele.appendChild(doc.createTextNode(""));
			Element hash_ele = doc.createElement(xmlRule.xmlFilterInfo_Hash);
			hash_ele.appendChild(doc.createTextNode(Integer.toString(info.hashCode())));
			ele.appendChild(name_ele);
			ele.appendChild(ver_ele);
			ele.appendChild(hash_ele);
			ele.appendChild(url_ele);
		}
		element.appendChild(ele);
		//set bounds
		ele = doc.createElement(xmlRule.xmlX);
		ele.appendChild(doc.createTextNode(Integer.toString(filter.getX())));
		element.appendChild(ele);
		ele = doc.createElement(xmlRule.xmlY);
		ele.appendChild(doc.createTextNode(Integer.toString(filter.getY())));
		element.appendChild(ele);
		ele = doc.createElement(xmlRule.xmlWidth);
		ele.appendChild(doc.createTextNode(Integer.toString(filter.getWidth())));
		element.appendChild(ele);
		ele = doc.createElement(xmlRule.xmlHeight);
		ele.appendChild(doc.createTextNode(Integer.toString(filter.getHeight())));
		element.appendChild(ele);
		return element;
	}
	private org.w3c.dom.Element toNode(StreamInitializer initializer,Document doc){
		org.w3c.dom.Element element = doc.createElement(xmlRule.xmlStreamInitializer);
		//stream
		Stream_ryu st = initializer.getStream();
		org.w3c.dom.Element ele = doc.createElement(xmlRule.xmlStream);
		int hash=0;
		if(st!=null){
			if(!streams.contains(st)){
				streams.add(st);
			}
			hash = st.hashCode();
			ele.appendChild(doc.createTextNode(Integer.toString(hash)));
		}
		element.appendChild(ele);
		//x
		ele = doc.createElement(xmlRule.xmlX);
		ele.appendChild(doc.createTextNode(Integer.toString(initializer.getX())));
		element.appendChild(ele);
		//y
		ele = doc.createElement(xmlRule.xmlY);
		ele.appendChild(doc.createTextNode(Integer.toString(initializer.getY())));
		element.appendChild(ele);
		//width
		ele = doc.createElement(xmlRule.xmlWidth);
		ele.appendChild(doc.createTextNode(Integer.toString(initializer.getWidth())));
		element.appendChild(ele);
		//height
		ele = doc.createElement(xmlRule.xmlHeight);
		ele.appendChild(doc.createTextNode(Integer.toString(initializer.getHeight())));
		element.appendChild(ele);
		//text
		org.w3c.dom.Element node = doc.createElement(xmlRule.xmlStreamInitializer_text);
		node.appendChild(doc.createTextNode(initializer.getText()));
		element.appendChild(node);
		
		return element;
	}
		
}
