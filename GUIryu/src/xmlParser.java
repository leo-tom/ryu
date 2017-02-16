import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class xmlParser extends Thread{
	private class streamInfo{
		String name;
		Stream_ryu stream;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((stream == null) ? 0 : stream.hashCode());
			return result;
		}
		public streamInfo(String name, Stream_ryu stream) {
			this.name = name;
			this.stream = stream;
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof streamInfo){
				streamInfo stinfo = (streamInfo)obj;
				return stinfo.name.equals(this.name);
			}
			return name.equals(obj);
		}
		/**
		 * @return the name
		 */
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the stream
		 */
		public Stream_ryu getStream() {
			return stream;
		}
		/**
		 * @param stream the stream to set
		 */
		@SuppressWarnings("unused")
		public void setStream(Stream_ryu stream) {
			this.stream = stream;
		}
		private xmlParser getOuterType() {
			return xmlParser.this;
		}
		
	}
	private File file;
	private JPanel panel;
	private FilterConnector filterconnector;
	private ArrayList<streamInfo> streams;
	Document document;
	/**
	 * @param filterconnector the filterconnector to set
	 */
	public void setFilterconnector(FilterConnector filterconnector) {
		this.filterconnector = filterconnector;
	}
	/**
	 * @param filterinfomanager the filterinfomanager to set
	 */
	public void setFilterinfomanager(FilterInfoManager filterinfomanager) {
		this.filterinfomanager = filterinfomanager;
	}
	private FilterInfoManager filterinfomanager;
	public xmlParser(){
		
	}
	public xmlParser(JPanel panel,File file,FilterInfoManager filinfo,FilterConnector filcone){
		if(panel==null||filcone==null||filinfo==null)
			throw new NullPointerException();
		this.file = file;
		this.panel = panel;
		this.filterconnector = filcone;
		this.filterinfomanager = filinfo;
	}
	public void setPanel(JPanel pane){panel=pane;}
	public void setFile(File f) {file = f;}
	public void parse(){
		run();
	}
	public void parse(Document doc){
		document = doc;
		run();
	}
	public void parseBack() {
		start();
	}
	public void parseBack(Document doc) {
		document = doc;
		start();
	}
	@Override public void run(){
		try{
			parser();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private void parser(){
		try{
			if(document==null){
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = fac.newDocumentBuilder();
				document = builder.parse(new InputSource(new FileReader(file)));
			}
			document.getDocumentElement().normalize();
			NodeList roots = document.getElementsByTagName(xmlRule.xmlProject);
			if(roots.getLength()==0||roots.getLength()>1)System.err.println("Not supported");
			Element root = (Element)roots.item(0);
			String projectName = root.getAttribute(xmlRule.xmlProjectName);
			if(projectName!=null){
				panel.setToolTipText(projectName);
			}
			NodeList nodes = root.getElementsByTagName(xmlRule.xmlFilter);
			streams = new ArrayList<streamInfo>();
			for(int i =0 ;i<nodes.getLength();i++){
				Node node = nodes.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE){
					if(node.getNodeName().equals(xmlRule.xmlFilter)){
						panel.add(parsefilter((Element)node));
					}
				}
			}
			panel.updateUI();
			nodes = root.getElementsByTagName(xmlRule.xmlStreamInitializer);
			for(int i =0 ;i<nodes.getLength();i++){
				Node node = nodes.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE){
					if(node.getNodeName().equals(xmlRule.xmlStreamInitializer)){
						panel.add(parseInitializer((Element)node));
					}
				}
			}
			panel.updateUI();
			for(int i=0;i<streams.size();i++){
				Stream_ryu st = streams.get(i).getStream();
				st.UpdateLine();
				st.addon(panel);
			}
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private boolean DoesStreamListContain(String streamhash){
		for(int i =0;i<streams.size();i++){
			if(streams.get(i).name.equals(streamhash))
				return true;
		}
		return false;
	}
	private streamInfo getFromStreamList(String val){
		for(int i = 0;i<streams.size();i++){
			if(streams.get(i).equals(val))
				return streams.get(i);
		}
		return null;
	}
	private StreamInitializer parseInitializer(Element node){
		StreamInitializer stInit =null;
		try{
			stInit = new StreamInitializer(filterconnector, filterinfomanager);
			//stream
			NodeList nodes = node.getElementsByTagName(xmlRule.xmlStream);
			if(nodes.getLength()>0){
				String streamname = ((Element)nodes.item(0)).getTextContent();
				if(DoesStreamListContain(streamname)){
					Stream_ryu st = getFromStreamList(streamname).getStream();
					int index = 0;
					stInit.setOutStreamAt(st,index);
					st.setUp(stInit);
					st.setIndexUp(index);
				}else{
					streamInfo stinfo = new streamInfo(streamname,new Stream_ryu());
					streams.add(stinfo);
					Stream_ryu st = stinfo.getStream();
					int index = 0;
					stInit.setInStreamAt(st,index);
					st.setUp(stInit);
					st.setIndexUp(index);
				}
			}
			//text
			nodes = node.getElementsByTagName(xmlRule.xmlStreamInitializer_text);
			if(nodes.getLength()>0){
				stInit.setText(((Element)nodes.item(0)).getTextContent());
			}
			//x y width height
			int x,y,width,height;
			x=y=width=height=50;
			nodes = node.getElementsByTagName(xmlRule.xmlX);
			if(nodes.getLength()>0)
				x = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlY);
			if(nodes.getLength()>0)
				y = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlWidth);
			if(nodes.getLength()>0)
				width = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlHeight);
			if(nodes.getLength()>0)
				height = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			stInit.setBounds(x, y, width, height);
		}catch(Exception er){
			er.printStackTrace();
		}
		return stInit;
	}
	private Filter parsefilter(Element node){
		Filter filter =null;
		try{
			filter = new Filter(filterconnector, filterinfomanager);
			int x,y,width,height;
			x=y=width=height=50;
			//filter
			NodeList nodes = node.getElementsByTagName(xmlRule.xmlFilterInfo);
			if(nodes.getLength()!=0){
				String name = null;
				String url = null;
				String version = null;
				try{
					name = ((Element)nodes.item(0)).getElementsByTagName(xmlRule.xmlFilterInfo_Name).item(0).getTextContent();
					url = ((Element)nodes.item(0)).getElementsByTagName(xmlRule.xmlFilterInfo_Url).item(0).getTextContent();
					version = ((Element)nodes.item(0)).getElementsByTagName(xmlRule.xmlFilterInfo_Version).item(0).getTextContent();
				}catch(Exception er){;}
				if(name==null) /*System.err.println("Could not find information of filter")*/;
				else{
					FilterInfo[] info = filterinfomanager.getFilterbyName(name);
					if(info.length==0){
						System.err.println("Filter can not be found.."+System.lineSeparator());
						System.err.println("Name:"+name==null?"null":name);
						System.err.println("version:"+version==null?"null":version);
						System.err.println("Url:"+url==null?"null":url);
					}else if(info.length>1){
						for(FilterInfo aInfo : info){
							if(aInfo.getVersion().equals(version)||aInfo.getUrl().toString().equals(url)){
								filter.setFilterInfo(aInfo);
								break;
							}
						}
					}else{
						filter.setFilterInfo(info[0]);
					}
				}
			}
			//input stream
			nodes = node.getElementsByTagName(xmlRule.xmlInSt);
			if(nodes.getLength()!=0){
				NodeList stIn = ((Element)nodes.item(0)).getElementsByTagName(xmlRule.xmlIn);
				for(int i=0;i<stIn.getLength();i++){
					Element ele = (Element) stIn.item(i);
					if(!ele.getTextContent().equals(xmlRule.xmlNull)){
						if(DoesStreamListContain(ele.getTextContent())){
							Stream_ryu st = getFromStreamList(ele.getTextContent()).getStream();
							int index = Integer.parseInt(ele.getAttribute(xmlRule.xmlIndex));
							filter.setInStreamAt(st,index);
							st.setDown(filter);
							st.setIndexDown(index);
						}else{
							streamInfo stinfo = new streamInfo(ele.getTextContent(),new Stream_ryu());
							streams.add(stinfo);
							Stream_ryu st = stinfo.getStream();
							int index = Integer.parseInt(ele.getAttribute(xmlRule.xmlIndex));
							filter.setInStreamAt(st,index);
							st.setDown(filter);
							st.setIndexDown(index);
						}
					}
				}
			}
			//output stream
			nodes = node.getElementsByTagName(xmlRule.xmlOutSt);
			if(nodes.getLength()!=0){
				NodeList stIn = ((Element)nodes.item(0)).getElementsByTagName(xmlRule.xmlOut);
				for(int i=0;i<stIn.getLength();i++){
					Element ele = (Element) stIn.item(i);
					if(!ele.getTextContent().equals(xmlRule.xmlNull)){
						if(DoesStreamListContain(ele.getTextContent())){
							Stream_ryu addingst = getFromStreamList(ele.getTextContent()).getStream();
							int index = Integer.parseInt(ele.getAttribute(xmlRule.xmlIndex));
							filter.setOutStreamAt(addingst,index);
							addingst.setIndexUp(index);
							addingst.setUp(filter);
						}else{
							streamInfo stinfo = new streamInfo(ele.getTextContent(),new Stream_ryu());
							streams.add(stinfo);
							Stream_ryu addingst = stinfo.getStream();
							int index = Integer.parseInt(ele.getAttribute(xmlRule.xmlIndex));
							filter.setOutStreamAt(addingst, index);
							addingst.setUp(filter);
							addingst.setIndexUp(index);
						}
					}
				}
			}
			//x y width height
			nodes = node.getElementsByTagName(xmlRule.xmlX);
			x = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlY);
			y = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlWidth);
			width = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			nodes = node.getElementsByTagName(xmlRule.xmlHeight);
			height = Integer.parseInt(((Element)nodes.item(0)).getTextContent());
			filter.setBounds(x, y, width, height);
		}catch(Exception er){
			er.printStackTrace();
		}
		return filter;
	}
}
