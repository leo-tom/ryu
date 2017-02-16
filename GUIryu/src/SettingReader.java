import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class SettingReader {
	public static final String settingFilePath = RYU.SETTINGFILE;
	private File settingfile;
	private HashMap<String,String> map;
	public SettingReader() {
		// TODO Auto-generated constructor stub
	}
	public void ReadSetting() throws IOException{
		if(settingfile==null) settingfile = new File(settingFilePath);
		if(!settingfile.exists()){
			File myf = new File(RYU.MYFOULDER);
			if(!(myf.exists())){
				myf.mkdir();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingfile));
			BufferedReader reader = new BufferedReader(new FileReader(new File(RYU.SETTINGDEFAULTFILE)));
			String str;
			while((str=reader.readLine())!=null){
				writer.write(str);
				writer.newLine();
			}
			reader.close();
			writer.close();
		}
		BufferedReader reader = new BufferedReader(new FileReader(settingfile));
		String str;
		if(map==null)
			map = new HashMap<String,String>();
		else
			map.clear();
		while((str = reader.readLine())!=null){
			if(str.indexOf('#')!=0&&!(str.indexOf('=')<0)&&str!=null){
				map.put(str.substring(0, str.indexOf("=")).trim().toUpperCase(),str.substring(str.indexOf("=")+1).trim());
			}
		}
		reader.close();
	}
	public void WriteMySelf() throws IOException{
		if(settingfile==null) settingfile = new File(settingFilePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(settingfile));
		Set<Entry<String, String>> set = map.entrySet();
	    Iterator<Entry<String, String>> i = set.iterator();
	    ArrayList<String> written = new ArrayList<String>();
	    while(i.hasNext()) {
	    	Entry<String, String> ent = (Entry<String, String>)i.next();
	    	String writing = ent.getKey().toUpperCase();
	    	if(written.indexOf(writing)<0){
	    		written.add(writing);
		        writer.append(writing);
		        writer.append('=');
		        writer.append(map.get(ent.getKey()));
		        writer.newLine();
	    	}
	    }
		writer.close();
	}
	public void SetValue(String _key,String value){
		if(_key==null||value==null)return;
		String key = _key.toUpperCase();
		if(map.containsKey(key))
			map.replace(key, value);
		else
			map.put(key.toUpperCase(), value);
	}
	public void SetBooleanValue(String _key,boolean value){
		if(_key==null)return;
		String key = _key.toUpperCase();
		if(map.containsKey(key)){
			map.replace(key, String.valueOf(value));
		}else{
			if(value)
				map.put(key, "true");
			else
				map.put(key, "false");
		}
	}
	public String getValueOf(String val){
		return map.get(val.toUpperCase());
	}
	public boolean getBoolValueOf(String val){
		String str = map.get(val.toUpperCase());
		return str==null ? false : str.toUpperCase().equals("TRUE");
	}
	
}
