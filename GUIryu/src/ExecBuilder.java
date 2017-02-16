import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class ExecBuilder implements ActionListener{

	MainWindow_ryu mainW;
	private ArrayList<String> cmdMaker = new ArrayList<String>();
	private SettingReader settingReader;
	
	public ExecBuilder(MainWindow_ryu mainWindow,SettingReader val) {
		this.mainW = mainWindow;
		settingReader = val;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			File writingC = mainW.getCsource();
			if(writingC==null){
				System.err.println("Could not get c source file");
			}
			xmlBuilder xmlB = new xmlBuilder(mainW.getPanel());
			xmlB.buildinBack();
			String compiler = settingReader.getValueOf("compiler");
			if(compiler==null)compiler = SettingDefault.DEFAILTCOMPILER;
			cmdMaker.add(compiler);
			for(int n=0;n<11;n++){
				String option = settingReader.getValueOf("compilerOPTION"+n);
				if(option!=null&&!option.isEmpty())
					cmdMaker.add(option);
			}
			cmdMaker.add(writingC.getAbsolutePath());
			String[] cmd = cmdMaker.toArray(new String[0]);
			cmdMaker.clear();
			FileWriter Fwriter = new FileWriter(writingC);
			StringBuilder builder = new StringBuilder();
			while(!xmlB.hasFinished()){
				Thread.sleep(100);
			}
			cCodeWriter writer = new cCodeWriter(xmlB.getDocument(),mainW.getFilterManager());
			Fwriter.write(writer.Compile());
			Fwriter.close();
			Process process = Runtime.getRuntime().exec(cmd, null, writingC.getParentFile());
			process.waitFor();
			InputStream stream = process.getErrorStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String str = null;
			builder.ensureCapacity(200);
			String newLine = System.lineSeparator();
			int cou = 0;
			int exit = process.exitValue();
			while((str=reader.readLine())!=null){
				builder.append(str);
				builder.append(newLine);
				cou++;
			}
			if(exit!=0){
				StringBuilder executed = new StringBuilder();
				int n = 0;
				for(;n<cmd.length;n++){
					executed.append(cmd[n]);
				}
				JOptionPane.showMessageDialog(null, "ERROR : exit value is"+exit+newLine
						+"Executed : "+executed.toString()+newLine
						+builder.toString());
			}else if(cou>0){
				JOptionPane.showMessageDialog(null, builder.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
