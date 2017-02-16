import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//this class is completely constant after it is constructed
public class FilterInfo{
	//values
	private String name;
	private String nameInC;
	private String code;
	private String version;
	private int acceptIn;
	private int acceptOut;
	private String description;
	private String[] Includes;
	private String Familly;
	private URL url;
	//end values
	private volatile int lazyHash;
	public String getName() {return this.name;}
	public String getNameInC() {return this.nameInC;}
	public String getCode(){return this.code;}
	public String getVersion(){return this.version;}
	public int getAcceptableInputStream(){return this.acceptIn;}
	public int getAcceptableOutputStream(){return this.acceptOut;}
	public String getDescription() {return this.description;}
	public URL getUrl(){return this.url;}
	public String[] getIncludes(){return Includes.clone();}
	public String getFamilly() {return Familly;}
	
	
	@Override public String toString(){
		return name;
	}
	@Override public int hashCode(){
		int hash = lazyHash;
		if(hash==0){
			hash = 17;
			if(name!=null)
				hash = 31 * hash + name.hashCode();
			if(nameInC!=null)
				hash = 31 * hash + nameInC.hashCode();
			if(code!=null)
				hash = 31 * hash + code.hashCode();
			if(version!=null)
				hash = 31 * hash + version.hashCode();
			if(acceptIn>0)
				hash = 31 * hash + acceptIn;
			if(acceptOut>0)
				hash = 31 * hash + acceptOut;
			if(description!=null)
				hash = 31 * hash + description.hashCode();
			if(url!=null)
				hash = 31 * hash + url.hashCode();
				hash = 31 * hash + Familly.hashCode();
			if(Includes!=null)
				for(String str :Includes)
					hash = 31 * hash + str.hashCode();
			if(hash<0)
				hash*=-1;
			lazyHash = hash;
		}
		return lazyHash;
	}
	@Override public boolean equals(Object o){
		//this function return true if object that is given is logically equal
		if(o==null||(!(o instanceof FilterInfo)))
			return false;
		FilterInfo got = (FilterInfo)o;
		if(got == this)
			return true;
		if(this.Includes.length!=got.Includes.length)
			return false;
		for(String str : Includes){
			boolean found = false;
			for(String stringo : got.Includes){
				if(stringo.equals(str)){
					found = true;break;
				}
			}
			if(!found)
				return false;
		}
		return got.name.equals(this.name)
				&&got.code.equals(this.code)
				&&got.nameInC.equals(this.nameInC)
				&&got.version.equals(this.version)
				&&got.Familly.equals(this.Familly);
	}
	//Filter info builder
	private FilterInfo(Builder builder)throws NullPointerException{
		if(builder==null) throw new NullPointerException();
		this.name = builder._name;
		this.nameInC = builder._nameInC;
		this.code = builder._code;
		this.version = builder._version;
		this.acceptIn = builder._acceptIn;
		this.acceptOut = builder._acceptOut;
		this.description = builder._description;
		this.url = builder._url;
		this.Includes = (String[])builder._Includes.toArray(new String[builder._Includes.size()]);
		this.Familly = builder._Familly;
		lazyHash = 0;
	}
	public static class Builder{
		private String _name;
		private String _nameInC;
		private String _code;
		private String _version;
		private int _acceptIn;
		private int _acceptOut;
		private String _description;
		private ArrayList<String> _Includes;
		private String _Familly = "else";
		private URL _url;
		public Builder(String name){
			_name = name;
			_acceptIn = -1;
			_acceptOut = -1;
			_Includes = new ArrayList<String>();
		}
		public Builder setNameInC(String val){_nameInC = val;return this;}
		public Builder setCode(String val) {_code = val;return this;}
		public Builder setCode(File f){
			if(f==null||!f.exists()||!f.canRead()||!f.getName().endsWith(".c"))
				return this;
			try{
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String str;
				while((str=reader.readLine())!=null){
					builder.append(str);
					builder.append(System.lineSeparator());
				}
				reader.close();
				if(builder.length()>5)
					_code = builder.toString();
				else
					System.out.println("shit");
			}catch(Exception er){
				er.printStackTrace();
			}
			return this;
		}
		public Builder setVersion(String val) {this._version = val;return this;}
		public Builder setAcceptableInputStream(int val){_acceptIn = val;return this;}
		public Builder setAcceptableOutputStream(int val){_acceptOut = val;return this;}
		public Builder setDescription(String val){_description = val;return this;}
		public Builder setDescription(File f){
			if(f==null||!f.exists()||!f.canRead())
				return this;
			try{
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String str;
				while((str=reader.readLine())!=null){
					builder.append(str);
					builder.append(System.lineSeparator());
				}
				reader.close();
				if(builder.length()>5)
					_code = builder.toString();
			}catch(Exception er){
				er.printStackTrace();
			}
			return this;
		}
		public Builder setUrl(String val) throws MalformedURLException{
			if(val==null||val.isEmpty()){
				_url = null;
				return this;
			}
			_url = new URL(val);
			return this;
		}
		public Builder setUrl(URL val){_url = val;return this;}
		public Builder addIncludes(String val){	_Includes.add(val);	return this;}
		public Builder addIncludes(String[] val){
			if(val==null)
				return this;
			for(String str : val)
				_Includes.add(str);
			return this;
		}
		public Builder setFamilly(String val){_Familly = val==null?"else":val;return this;}
		public FilterInfo build()throws NullPointerException{
			if(this._nameInC==null||_code==null)
				throw new NullPointerException();
			if(this._name==null)
				this._name = this._nameInC;
			return new FilterInfo(this);
		}
	}
}
