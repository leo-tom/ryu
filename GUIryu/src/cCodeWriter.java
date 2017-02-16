import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class cCodeWriter extends Thread{
	protected Document doc;
	protected FilterInfoManager filterManager;
	protected StringBuilder writer;
	protected String result;
	protected ArrayList<String> streamList;
	protected ArrayList<FilterInformation> filterInfomationList;
	protected ArrayList<StreamInitInfo> streamInitInfoList;
	protected SettingReader settingReader;
	protected String projectName;
	
	protected FinishedListener listener;
	
	protected final String newLine = System.lineSeparator();
	protected final String First_comment = 
			  "/*This is auto generated code by ryu"+newLine
			+ "*To see detail of ryu visit http://leotom.890m.com/"+newLine
			+ "*/";
	public void setFinishedListener(FinishedListener listener){
		this.listener = listener;
	}
	public cCodeWriter(Document doc,FilterInfoManager val){
		this.doc =doc;
		this.filterManager = val;
	}
	public String Compile(){
		run();
		return result;
	}
	public void CompileBack(){
		start();
	}
	public boolean HasDone(){return !(result==null);}
	public String getString() {
		return result;
	}
	public void run(){
		writer = new StringBuilder();
		result = null;
		settingReader = new SettingReader();
		try {
			settingReader.ReadSetting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		_Compile();
		result = writer.toString();
		if(listener!=null)
			listener.ActionFinished(result);
	}
	protected void _Compile() {
		try{
			streamList = new ArrayList<String>();
			filterInfomationList = new ArrayList<FilterInformation>();
			streamInitInfoList = new ArrayList<StreamInitInfo>();
			NodeList rootList = doc.getElementsByTagName(xmlRule.xmlProject);
			Element root = (Element) rootList.item(0);
			if(root!=null){
				projectName = root.getAttribute(xmlRule.xmlProjectName);
				projectName = 
						projectName.trim();
			}
			if(projectName==null){
				projectName = "";
			}
			AddAllInfoToList();
			writer.append(First_comment);
			writer.append(newLine);
			String beginstr = settingReader.getValueOf("WriteBeggingOfCSource");
			if(beginstr!=null){
				writer.append(beginstr);
			}
			writer.append(newLine);
			String codeFor = settingReader.getValueOf("codeForWindows");
			if(RYU.IsWindows||(codeFor!=null&&codeFor.toLowerCase().equals("true"))){
				writer.append("#define __RYU_WIN__"+newLine);
			}
			if(settingReader.getBoolValueOf(SettingRyu.IncludeRyuH)){
				writer.append("#include \"ryu.h\""+newLine);
			}
			//writer.append("#include \"pthread.h\""+newLine);
			WriteAllIncludes();
			if(settingReader.getBoolValueOf(SettingRyu.WriteRyuH))
				Writeryu_H();
			writer.append(newLine);
			if(settingReader.getBoolValueOf(SettingRyu.WriteRyuMalloc)){
				writer.append("/*ryu malloc*/"+newLine);
				Write_ryumalloc();
				writer.append("/*End of ryu malloc*/"+newLine);
			}
			if(settingReader.getBoolValueOf(SettingRyu.WriteBasicRyu)){
				writer.append("/*Basics*/"+newLine);
				WriteBasic();
				writer.append("/*End of basics*/"+newLine);
			}
			if(settingReader.getBoolValueOf(SettingRyu.WriteFilters)){
				writer.append("/*All of Filters*/"+newLine);
				WriteAllFilters();
				writer.append("/*End of Filters*/"+newLine);
			}
			if(settingReader.getBoolValueOf(SettingRyu.WriteStaticFuncs))
				Write_StaticFuncs();
			if(settingReader.getBoolValueOf(SettingRyu.WriteStarter))
				Write_starter();
			if(settingReader.getBoolValueOf(SettingRyu.WriteMain)){
				writeMain();
			}
		}catch(Exception er){
			er.printStackTrace();
		}
		
	}
	protected void writeMain() {
		writer.append(newLine
				+ "int main(int argc,char *argv[]){"+newLine
				+ "  ryu_info *info = ryu_info_init(NULL);"+newLine
				+ "  set_argc(info,argc);"+newLine
				+ "  set_argv(info,argv);"+newLine
				+ "  don_back(info);"+newLine
				+ "  return start_ryu_"+projectName+"(info);"+newLine
				+ "}"+newLine
				);
	}
	protected void Write_starter() {
		writer.append("int start_ryu_"+projectName+"(ryu_info *info){"+newLine);
		writer.append(
				     "  if(info!=NULL&&info->BackGround){"+newLine
				   + "    info->BackGround = 0;"+newLine
				   + "    return pthread_create(&(info->thread),NULL,(void *)start_ryu_"+projectName+",info);"+newLine
				   + "  }"
				   + "  struct begin_data_ryu data;"+newLine
		);
		writer.append("  pse_stream *streams["+streamList.size()+"];"+newLine);
		writer.append("  struct order *orders["+filterInfomationList.size()+"];"+newLine);
		
		writer.append("  void *funcs[] = {"+newLine);
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInfo info = filterInfomationList.get(i).filterinfo;
			writer.append("  (void *)"+info.getNameInC()+"_starter,"+newLine);
		}
		//last ',' is unnecessary
		int index_del = writer.lastIndexOf(",");
		writer.deleteCharAt(index_del);
		writer.append("  };"+newLine);
		
		writer.append(
				  "  data.orders = orders;"+newLine
				+ "  data.streams = streams;"+newLine
		);
		writer.append("  data.orderCou ="+filterInfomationList.size()+";"+newLine);
		writer.append("  data.streamCou ="+streamList.size()+";"+newLine);
		writer.append("  data.funcs = funcs;"+newLine);
		writer.append(
				  "  _ryu_malloc_initialize();"+newLine
				+ "  initAll_ryu(&data);"+newLine
		);
		
		//start setting streams
		for(int i=0;i<streamInitInfoList.size();i++){
			StreamInitInfo stInit = streamInitInfoList.get(i);
			int index = streamList.indexOf(stInit.streamName);
			String writetxt = stInit.text.replace("\"","\\\"")
					.replaceAll("\'", "\\\'");
			writer.append("  write_str_to_stream(streams["+index+"],\""+writetxt+"\");");
			writer.append("  pse_stream_end_write(streams["+index+"]);"+newLine);
		}
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInformation info = filterInfomationList.get(i);
			writer.append("/*"+info.filterinfo.getNameInC()+"*/"+newLine);
			writer.append("  orders["+i+"]->info = info;"+newLine);
			int index = 0;
			for(String str : info.in){
				if(str!=null){
					int indexOfStream = streamList.indexOf(str);
					writer.append("  orders["+i+"]->st_read["+index+"] = streams["+indexOfStream+"];"+newLine);
				}
				index++;
			}
			index = 0;
			for(String str : info.out){
				if(str!=null){
					int indexOfStream = streamList.indexOf(str);
					writer.append("  orders["+i+"]->st_write["+index+"] = streams["+indexOfStream+"];"+newLine);
				}
				index++;
			}
		}		
		
		writer.append(
				  "  startAll(&data);"+newLine
				+ "  startGC(&data);"+newLine
				+ "  return 0;"+newLine
				+ "}"
		);
	}
	protected void Write_StaticFuncs() throws Exception{
		BufferedReader r = new BufferedReader(new FileReader(RYU.RYUSOURCEC_STATICFUNCS));
		String str;
		while((str=r.readLine())!=null){
			writer.append(str);
			writer.append(newLine);
		}
		r.close();
	}
	protected void ConstructDefault() throws Exception{
		try{
			if(settingReader==null)settingReader = new SettingReader();
			settingReader.ReadSetting();
			writer.append(First_comment);
			writer.append(newLine);
			writer.append(settingReader.getValueOf("WriteBeggingOfCSource"));
			writer.append(newLine);
			String codeFor = settingReader.getValueOf("codeForWindows");
			if(RYU.IsWindows||(codeFor!=null&&codeFor.toLowerCase().equals("true"))){
				writer.append("#define __RYU_WIN__"+newLine);
			}
			writer.append("#include \"pthread.h\""+newLine);
			WriteAllIncludes();
			Writeryu_H();
			writer.append(newLine);
			writer.append("/*ryu malloc*/"+newLine);
			Write_ryumalloc();
			writer.append("/*End of ryu malloc*/"+newLine);
			writer.append("/*Basics*/"+newLine);
			WriteBasic();
			writer.append("/*End of basics*/"+newLine);
			writer.append("/*All of Filters*/"+newLine);
			WriteAllFilters();
			writer.append("/*End of Filters*/"+newLine);
		}catch(Exception er){
			throw er;
		}
		
	}
	protected void Write_ryumalloc()throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(new File(RYU.RYUSOURCEC_RYUMALLOC)));
		String str;
		while((str=reader.readLine())!=null){
			writer.append(str);
			writer.append(newLine);
		}
		reader.close();
	}
	protected void Write_setStreams_ryu()throws Exception{
		writer.append("void setStreams_ryu(struct begin_data_ryu *data){"+newLine);
		for(int i=0;i<streamInitInfoList.size();i++){
			StreamInitInfo stInit = streamInitInfoList.get(i);
			int index = streamList.indexOf(stInit.streamName);
			writer.append("  write_str_to_stream(streams_ryu["+index+"],\""+stInit.text+"\");");
			writer.append("pse_stream_end_write(streams_ryu["+index+"]);"+newLine);
		}
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInformation info = filterInfomationList.get(i);
			writer.append("/*"+info.filterinfo.getNameInC()+"*/"+newLine);
			int index = 0;
			for(String str : info.in){
				if(str!=null){
					int indexOfStream = streamList.indexOf(str);
					writer.append("  orders_ryu["+i+"]->st_read["+index+"] = streams_ryu["+indexOfStream+"];"+newLine);
				}
				index++;
			}
			index = 0;
			for(String str : info.out){
				if(str!=null){
					int indexOfStream = streamList.indexOf(str);
					writer.append("  orders_ryu["+i+"]->st_write["+index+"] = streams_ryu["+indexOfStream+"];"+newLine);
				}
				index++;
			}
		}
		writer.append("}"+newLine);
	}
	protected void WriteAllFilters(){
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInfo info = filterInfomationList.get(i).filterinfo;
			if(!list.contains(info.getNameInC())){
				list.add(info.getNameInC());
				writer.append(newLine);
				String defining = "__"+info.getNameInC().toUpperCase().replaceAll(" ", "")+"_"+String.valueOf(info.hashCode()<0?info.hashCode()*(-1):info.hashCode())+"__";
				writer.append("#ifndef "+defining+newLine);
				writer.append("#define "+defining+newLine);
				writer.append(info.getCode());
				writer.append(newLine);
				writer.append("void "+info.getNameInC()+"_starter(void *order_void)"+newLine);
				writer.append(
						"{"+newLine
						+ "if(order_void==NULL) {fprintf(stderr,\"NULL pointer was Given[I am "+info.getNameInC()+"_starter]\");exit(1);}"+newLine
						+ "  struct order *order_desu = (struct order *)order_void;"+newLine
						+ "  "+info.getNameInC()+"(order_desu);"+newLine
						+ "  order_done(order_desu);"+newLine
						+ "  ryufree_unreachable(order_desu);"+newLine
						+ "}"+newLine
						);
				writer.append(newLine);
				writer.append("#endif"+newLine);
			}
			
		}
	}
	protected void WriteBasic() throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(RYU.RYUSOURCEC_BASIC));
		String str;
		while((str=reader.readLine())!=null){
			writer.append(str);
			writer.append(newLine);
		}
		reader.close();
	}
	protected void GlbalVariables() throws Exception{
		String appending = "const int stream_cou = " + streamList.size() + ";";
		writer.append(appending);
		writer.append(newLine);
		appending = "const int orders_cou = "+filterInfomationList.size()+";";
		writer.append(appending);
		writer.append(newLine);
		writer.append("const int wait_time_GC = 2;"+newLine);
		writer.append("const char CALLGCNOW = 0;"+newLine);		
		writer.append("void *filters_ptr[] = {"+newLine);
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInfo info = filterInfomationList.get(i).filterinfo;
			writer.append("  (void *)"+info.getNameInC()+"_starter,"+newLine);
		}
		//last ',' is unnecessary
		int i = writer.lastIndexOf(",");
		writer.deleteCharAt(i);
		writer.append("};"+newLine);
		writer.append("pse_stream *streams_ryu["+streamList.size()+"];"+newLine);
		writer.append("struct order *orders_ryu["+filterInfomationList.size()+"];"+newLine);
	}
	protected void Writeryu_H() throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(RYU.RYUSOURCEC_HEDDER));
		String str = null;
		while((str=reader.readLine())!=null){
			writer.append(str);
			writer.append(newLine);
		}
		reader.close();
	}
	protected void WriteAllIncludes()throws Exception{
		ArrayList<String> Includes = new ArrayList<String>();
		for(int i=0;i<filterInfomationList.size();i++){
			FilterInfo filin = filterInfomationList.get(i).filterinfo;
			for(String str : filin.getIncludes()){
				str=str.trim();
				if(str.endsWith(".h"))
					str = str.substring(0,str.length()-2);
				if(!Includes.contains(str)&&!str.equals("ryu")&&str.length()>0&&!str.equals("pthread")){
					Includes.add(str);
					writer.append("#include "+"\"");
					writer.append(str);
					writer.append(".h"+"\""+newLine);
				}
			}
		}
	}
	protected void AddAllInfoToList(){
		try
		{
			NodeList rootList = doc.getElementsByTagName(xmlRule.xmlProject);
			Element root = (Element) rootList.item(0);
			//adding all filter's info
			NodeList filterNodeList = root.getElementsByTagName(xmlRule.xmlFilter);
			for(int n=0;n<filterNodeList.getLength();n++){
				Element ele = (Element) filterNodeList.item(n);
				//filterinfo
				FilterInfo filterinfo = _FindFilterInfo((Element) ele.getElementsByTagName(xmlRule.xmlFilterInfo).item(0));
				FilterInformation info = null;
				if(filterinfo!=null){
					info = FilterInformation.Build(filterinfo);
					filterInfomationList.add(info);
				}
				
				//input stream
				NodeList nlist = ele.getElementsByTagName(xmlRule.xmlInSt);
				Element stInEle = (Element) nlist.item(0); 
				nlist = stInEle.getElementsByTagName(xmlRule.xmlIn);
				for(int i=0;i<nlist.getLength();i++){
					Element streamEle = (Element) nlist.item(i);
					if(!(streamEle.getTextContent().equals(xmlRule.xmlNull))&&!(streamEle.getTextContent().trim().isEmpty())){
						int index = Integer.parseInt(streamEle.getAttribute(xmlRule.xmlIndex));
						String streamname = streamEle.getTextContent();
						if(streamList.contains(streamname)){
							info.setInstAt(streamname, index);
						}else{
							streamList.add(streamname);
							info.setInstAt(streamname, index);
						}
						
					}
				}
				//output stream
				nlist = ele.getElementsByTagName(xmlRule.xmlOutSt);
				Element stOutEle = (Element) nlist.item(0); 
				nlist = stOutEle.getElementsByTagName(xmlRule.xmlOut);
				for(int i=0;i<nlist.getLength();i++){
					Element streamEle = (Element) nlist.item(i);
					if(!(streamEle.getTextContent().equals("null"))&&!(streamEle.getTextContent().trim().isEmpty())){
						int index = Integer.parseInt(streamEle.getAttribute(xmlRule.xmlIndex));
						String streamname = streamEle.getTextContent();
						if(streamList.contains(streamname)){
							info.setOutstAt(streamname, index);
						}else{
							streamList.add(streamname);
							info.setOutstAt(streamname, index);
						}
					}
				}
			}
			//adding all stream initializer's info
			NodeList stInitNList = root.getElementsByTagName(xmlRule.xmlStreamInitializer);
			for(int i = 0;i<stInitNList.getLength();i++){
				Element stInitEle = (Element) stInitNList.item(i);
				streamInitInfoList.add(mkStreamInitInfo(stInitEle));
			}
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	protected StreamInitInfo mkStreamInitInfo(Element StreamInitializerElement){
		NodeList nList = StreamInitializerElement.getElementsByTagName(xmlRule.xmlStreamInitializer_text);
		String txt = ((Element)nList.item(0)).getTextContent();
		nList = StreamInitializerElement.getElementsByTagName(xmlRule.xmlStream);
		String streamName = ((Element)nList.item(0)).getTextContent();
		if(!streamList.contains(streamName)){
			streamList.add(streamName);
		}
		return new StreamInitInfo(txt, streamName);
	}
	protected FilterInfo _FindFilterInfo(Element element){
		NodeList nlist = element.getElementsByTagName(xmlRule.xmlFilterInfo_Name);
		Element ele = (Element) nlist.item(0);
		if(ele==null){
			return null;
		}
		String name = ele.getTextContent();
		FilterInfo[] info = filterManager.getFilterbyName(name);
		return info.length>0?info[0]:null;
	}
	
	protected static class StreamInitInfo{
		protected String text;
		protected String streamName;
		public StreamInitInfo(String text, String streamName) {
			text = text.replace("\n","\\n");
			this.text = text;
			this.streamName = streamName;
		}
	}
	protected static class FilterInformation{
		protected String[] in;
		protected String[] out;
		protected FilterInfo filterinfo;
		protected FilterInformation(){}
		public static FilterInformation Build(FilterInfo filterinfo){
			FilterInformation val = new FilterInformation();
			val.filterinfo = filterinfo;
			val.in = new String[8];
			val.out = new String[8];
			return val;
		}
		public void setInstAt(String st,int i){
			if(i>=0&&i<8)
				in[i]=st;
		}
		public void setOutstAt(String st,int i){
			if(i>=0&&i<8)
				out[i]=st;
		}
		public String getInAt(int i) {
			if(i>=0&&i<8)
				return in[i];
			return null;
		}
		public String getOutAt(int i) {
			if(i>=0&&i<8)
				return out[i];
			return null;
		}
		
	}
}
