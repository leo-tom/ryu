import java.io.File;


public class RYU {

	public static boolean IsWindows = System.getProperty("os.name").toLowerCase().contains("windows");
	public static String HOMEDIR = IsWindows?"":System.getProperty("user.home");
	public static String SEPARATOR = File.separator;
	public static String MYFOULDER = HOMEDIR + (IsWindows?"":SEPARATOR+".ryu");
	public static String MYLIB = MYFOULDER + SEPARATOR + "lib";
	public static String RYUFOULDER = IsWindows ? "" : "/etc/ryu";
	public static String RESOURCEFOULDER = IsWindows ? RYUFOULDER+"resources" : "/etc/ryu/resources";
	public static String SETTINGDEFAULTFILE = RESOURCEFOULDER + SEPARATOR + "setting";
	public static String SHAREDLIBFOULDER =IsWindows?"lib":RYUFOULDER+"/lib";
	public static String ICONS = RESOURCEFOULDER+SEPARATOR+"iconS.png";
	public static String CSOURCES = RESOURCEFOULDER+SEPARATOR+"csources";
	public static String RYUSOURCEC_HEDDER = CSOURCES+SEPARATOR +"ryu.h";
	public static String RYUSOURCEC_STRINGPLUS = CSOURCES+SEPARATOR+"string+.c";
	public static String RYUSOURCEC_BASIC = CSOURCES + SEPARATOR +"basic_ryu.c";
	public static String RYUSOURCEC_RYUMALLOC =CSOURCES + SEPARATOR +"ryumalloc.c";
	public static String RYUSOURCEC_STATICFUNCS = CSOURCES + SEPARATOR +"staticFuncs.c";
	public static String SIZECHANGEICON = RESOURCEFOULDER+SEPARATOR+"sizechange.png";
	public static String CLOSEICON = RESOURCEFOULDER +SEPARATOR+"close.png";
	public static String BACKUOFILEFOULDER = MYFOULDER + "/BAK";
	public static String SETTINGFILE = MYFOULDER+(IsWindows?"":SEPARATOR)+"setting";
	public static String READNOWFILE = IsWindows?"ReadNow":"/tmp/ReadNow";
	public static String RYUSIMBOLS = RYUFOULDER + SEPARATOR + "simbols";
	public static String tmpDir = IsWindows?"C:\\temp":"/tmp";
}
