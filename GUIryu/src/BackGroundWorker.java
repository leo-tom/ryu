import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JPanel;

import org.w3c.dom.Document;

public class BackGroundWorker extends Thread{
	private MainWindow_ryu mainWin;
	private JPanel panel;
	private FilterInfoManager filinfomaneger;
	private Document[] currentDoc;
	private int currentIndex;
	private boolean ReadNow;
	private File ReadNowFile;
	private File BackUpFoulder;
	private File HomeDir;
	private boolean backUpNow = false;
	private boolean AmIBusy = true;
	
	public boolean isBusy() {
		return AmIBusy;
	}
	private static int stackSize = 32;
	private static int MaxBackUpFiles = 400;
	
	public BackGroundWorker(MainWindow_ryu mainWin) {
		this.mainWin = mainWin;
		this.filinfomaneger = mainWin.getFilterManager();
		currentDoc = new Document[stackSize];
		ReadNow = false;
		currentIndex = 0;
		ReadNowFile = new File(RYU.READNOWFILE+GetProcessId());
		panel = mainWin.getPanel();
		HomeDir = new File(RYU.HOMEDIR);
		if(HomeDir==null){
			System.out.println("Cant get Home dir");
		}
		BackUpFoulder = new File(RYU.BACKUOFILEFOULDER);
		if(!BackUpFoulder.exists()||!BackUpFoulder.isDirectory()){
			BackUpFoulder.mkdir();
		}
	}
	public boolean canGetBack(){
		return currentIndex>0;
	}
	public boolean canGetForward(){
		if(currentDoc.length<currentIndex+1){
			return false;
		}
		return currentDoc[currentIndex+1]!=null;
	}
	public void InitializeRedoer(){
		currentDoc = new Document[stackSize];
		currentIndex = 0;
	}
	public void startBackGroundWorker(){
		if(mainWin!=null){
			start();
		}
	}
	public void BacuUpNow(){
		backUpNow = true;
	}
	public Document popForward(){
		if(canGetForward()){
			Document doc = currentDoc[++currentIndex];
			return doc;
		}
		return null;
	}
	public Document popBack(){
		if(currentIndex>0){
			if(currentDoc[currentIndex]==null){
				currentDoc[currentIndex] = mkDocument();
			}
			Document doc = currentDoc[--currentIndex];
			return doc;
		}
		return null;
	}
	public void pushBack(Document val){
		if(currentIndex<currentDoc.length){
			currentDoc[currentIndex++] = val;
			int i = currentIndex;
			for(;i<currentDoc.length;i++){
				currentDoc[i] = null;
			}
		}else{
			int n = currentDoc.length/2;
			int i=0;
			for(;n<currentDoc.length;i++){
				currentDoc[i] = currentDoc[n++];
			}
			currentIndex = i;
			for(;i<currentDoc.length;i++){
				currentDoc[i] = null;
			}
		}
	}
	public static int GetProcessId(){
		String pidstr = ManagementFactory.getRuntimeMXBean().getName();
		pidstr = pidstr.substring(0, pidstr.indexOf('@'));
		return Integer.parseInt(pidstr);
	}
	public static void readNow(){
		int pid = GetProcessId();
		File f = new File(RYU.READNOWFILE+pid);
		if(!f.exists()){
			try{
				f.createNewFile();
				File donef = new File(f.getAbsolutePath()+"done");
				while(!donef.exists()){
					Thread.sleep(10);
				}
				donef.delete();
			}catch(Exception er){
				er.printStackTrace();
			}
		}
	}
	public void Read(){
		ReadNow = true;
	}
	public void DontRead(){
		ReadNow = false;
	}
	@Override public void run(){
		_BackGroundWorker();
	}
	private Document mkDocument(){
		Document doc = null;
		panel = mainWin.getPanel();
		try {
			xmlBuilder builder = new xmlBuilder(panel);
			builder.build();
			doc = builder.getDocument();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
	public void ForceToRead(){
		pushBack(mkDocument());
	}
	private void DelOldBack(){
		try{
			File[] files = BackUpFoulder.listFiles();
			if(files.length<MaxBackUpFiles)
				return;
			for(int i=0;i<files.length;i++){
				int n=i;
				long LastStr = files[n].lastModified();
				int best = n;
				for(;n<files.length;n++){
					if(files[n].lastModified()<LastStr){
						best = n;
						LastStr = files[best].lastModified();
					}
				}
				File tmp = files[best];
				files[best] = files[i];
				files[i] = tmp;
			}
			for(int i=0;i<MaxBackUpFiles/2&&i<files.length;i++){
				files[i].delete();
			}
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private void BackUp(){
		panel = mainWin.getPanel();
		if(panel.getComponentCount()<=2){
			//If panel has less than 2 objects do not make backup file
			return;
		}
		try{
			xmlBuilder builder = new xmlBuilder(panel);
			builder.build();
			DelOldBack();
			Calendar c = Calendar.getInstance();
			String fName = 
					"BAK_"
					+c.get(Calendar.YEAR)
					+"_"
					+(1+c.get(Calendar.MONTH))
					+"_"
					+c.get(Calendar.DATE)
					+"_"
					+c.get(Calendar.HOUR)+"_"
					+c.get(Calendar.MINUTE)+"_"
					+c.get(Calendar.SECOND)+"_"
					;
			File OutPut = new File(
					BackUpFoulder.getAbsolutePath()
					+File.separator
					+fName
					+".zip"
					);
			if(OutPut.exists())
				System.err.println(OutPut.getPath()+"Already exist");
			else{
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(OutPut));
				ZipEntry entry = new ZipEntry(fName);
				out.putNextEntry(entry);
				out.write(builder.getXml().getBytes());
				out.closeEntry();
				out.close();
			}
		}catch(Exception er){
			er.printStackTrace();
		}
		
	}
	private void _BackGroundWorker(){
		try{
			int counter = 0;
			filinfomaneger.ReadLibs();
			while(true){
				if(ReadNowFile.exists()){
					ReadNowFile.delete();
					ReadNow = true;
				}
				if(ReadNow){
					pushBack(mkDocument());
					ReadNow = false;
					String tellDone = ReadNowFile.getAbsolutePath();
					File done = new File(tellDone+"done");
					done.createNewFile();
				}
				if(backUpNow){
					backUpNow = false;
					BackUp();
				}
				if(counter>500){
					BackUp();
					counter = 0;
				}
				counter++;
				AmIBusy = false;
				Thread.sleep(300);
				AmIBusy = true;
			}
		}catch(Exception er){
			er.printStackTrace();
			_BackGroundWorker();
		}
	}
}
