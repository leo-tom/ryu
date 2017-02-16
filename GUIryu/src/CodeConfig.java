import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CodeConfig extends JFrame{
	private SettingReader reader;
	private CodeConfig me = this;
	private File Writeto;
	private JCheckBox chckbxWriteMain;
	private JLabel lblWriteTo;
	private JButton WriteTo;
	private JButton btnWrite;
	private JButton btnCancel;
	private JLabel lblCompiler;
	private JTextField CompilerT;
	private JPanel panel;
	private JPanel panel_1;
	
	private JLabel []lbls;
	private JTextField[] texts;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JLabel lblWriteRyuh;
	private JCheckBox chckbxWriteRyuh;
	private JLabel lblWriteBasicryuc;
	private JCheckBox chckbxWriteBasicryuc;
	private JLabel lblWriteRyumallocc;
	private JCheckBox chckbxWriteRyumallocc;
	private JLabel lblWriteStaticfuncsc;
	private JCheckBox chckbxWriteStaticfuncsc;
	private JLabel lblWriteStarterc;
	private JCheckBox chckbxWriteStarter;
	private JLabel lblWriteFilters;
	private JCheckBox chckbxWriteFilters;
	private JLabel lblIncludeRyuh;
	private JCheckBox chckbxIncludeRyuh;

	public CodeConfig(SettingReader reader) {
		setTitle("Config");
		this.setSize(new Dimension(300, 400));
		this.reader = reader;
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		lblWriteRyuh = new JLabel("Write ryu.h");
		getContentPane().add(lblWriteRyuh);
		
		chckbxWriteRyuh = new JCheckBox("Write ryu.h");
		getContentPane().add(chckbxWriteRyuh);
		
		lblWriteRyumallocc = new JLabel("Write ryumalloc.c");
		getContentPane().add(lblWriteRyumallocc);
		
		chckbxWriteRyumallocc = new JCheckBox("Write ryumalloc.c");
		getContentPane().add(chckbxWriteRyumallocc);
		
		lblWriteBasicryuc = new JLabel("Write basic_ryu.c");
		getContentPane().add(lblWriteBasicryuc);
		
		chckbxWriteBasicryuc = new JCheckBox("Write basic_ryu.c");
		getContentPane().add(chckbxWriteBasicryuc);
		
		lblWriteFilters = new JLabel("Write filters");
		getContentPane().add(lblWriteFilters);
		
		chckbxWriteFilters = new JCheckBox("Write filters");
		getContentPane().add(chckbxWriteFilters);
		
		lblWriteStaticfuncsc = new JLabel("Write staticfuncs.c");
		getContentPane().add(lblWriteStaticfuncsc);
		
		chckbxWriteStaticfuncsc = new JCheckBox("Write staticfuncs.c");
		getContentPane().add(chckbxWriteStaticfuncsc);
		
		lblWriteStarterc = new JLabel("Write starter");
		getContentPane().add(lblWriteStarterc);
		
		chckbxWriteStarter = new JCheckBox("Write starter");
		getContentPane().add(chckbxWriteStarter);
		
		JLabel lblWriteMain = new JLabel("Write main");
		getContentPane().add(lblWriteMain);
		
		chckbxWriteMain = new JCheckBox("Write main");
		getContentPane().add(chckbxWriteMain);
		
		lblIncludeRyuh = new JLabel("Include ryu.h");
		getContentPane().add(lblIncludeRyuh);
		
		chckbxIncludeRyuh = new JCheckBox("Include ryu.h");
		getContentPane().add(chckbxIncludeRyuh);
		
		lblWriteTo = new JLabel("Write to");
		getContentPane().add(lblWriteTo);
		
		WriteTo = new JButton("");
		WriteTo.addActionListener(new ActionListener() {
			JFileChooser cho;
			public void actionPerformed(ActionEvent arg0) {
				if(cho==null){
					cho = new JFileChooser();
				}
				if(cho.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
					Writeto = cho.getSelectedFile();
				}
			}
		});
		getContentPane().add(WriteTo);
		
		btnWrite = new JButton("OK");
		btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Okay();
				me.dispose();
			}
		});
		
		lblCompiler = new JLabel("Compiler");
		getContentPane().add(lblCompiler);
		
		CompilerT = new JTextField();
		getContentPane().add(CompilerT);
		CompilerT.setColumns(10);
		
		scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1);
		
		panel = new JPanel();
		scrollPane_1.setViewportView(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		lbls = new JLabel[10];
		texts = new JTextField[10];
		for(int i=1;i<=10;i++){
			lbls[i-1] = new JLabel(SettingRyu.COMPILEROPTION+""+i);
			panel.add(lbls[i-1]);
		}
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane);
		
		panel_1 = new JPanel();
		scrollPane.setViewportView(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		for(int i=1;i<=10;i++){
			String str = reader.getValueOf(SettingRyu.COMPILEROPTION+i);
			texts[i-1] = new JTextField(str);
			panel_1.add(texts[i-1]);
		}
		getContentPane().add(btnWrite);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				me.dispose();
			}
		});
		getContentPane().add(btnCancel);
	}
	
	public void Init(String projectN){
		if(reader==null)return;
		try {
			reader.ReadSetting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String val =reader.getValueOf(SettingRyu.WriteMain);
		chckbxWriteMain.setSelected((val==null)?true:val.toLowerCase().equals("true"));
		chckbxWriteRyuh.setSelected(reader.getBoolValueOf(SettingRyu.WriteRyuH));
		chckbxWriteRyumallocc.setSelected(reader.getBoolValueOf(SettingRyu.WriteRyuMalloc));
		chckbxWriteBasicryuc.setSelected(reader.getBoolValueOf(SettingRyu.WriteBasicRyu));
		chckbxWriteStaticfuncsc.setSelected(reader.getBoolValueOf(SettingRyu.WriteStaticFuncs));
		chckbxWriteStarter.setSelected(reader.getBoolValueOf(SettingRyu.WriteStarter));
		chckbxWriteFilters.setSelected(reader.getBoolValueOf(SettingRyu.WriteFilters));
		chckbxIncludeRyuh.setSelected(reader.getBoolValueOf(SettingRyu.IncludeRyuH));
		val = reader.getValueOf(SettingRyu.Compiler);
		CompilerT.setText(val);
		WriteTo.setText(Writeto==null?"":Writeto.getAbsolutePath());
	}
	private void Okay(){
		reader.SetBooleanValue(SettingRyu.WriteMain,chckbxWriteMain.isSelected());
		reader.SetValue(SettingRyu.defaultCsourceCodeFile, Writeto==null?null:Writeto.getAbsolutePath());
		reader.SetValue(SettingRyu.Compiler, CompilerT.getText());
		reader.SetBooleanValue(SettingRyu.WriteRyuH,chckbxWriteRyuh.isSelected());
		reader.SetBooleanValue(SettingRyu.WriteRyuMalloc, chckbxWriteRyumallocc.isSelected());
		reader.SetBooleanValue(SettingRyu.WriteBasicRyu, chckbxWriteBasicryuc.isSelected());
		reader.SetBooleanValue(SettingRyu.WriteStaticFuncs, chckbxWriteStaticfuncsc.isSelected());
		reader.SetBooleanValue(SettingRyu.WriteStarter, chckbxWriteStarter.isSelected());
		reader.SetBooleanValue(SettingRyu.WriteFilters, chckbxWriteFilters.isSelected());
		reader.SetBooleanValue(SettingRyu.IncludeRyuH, chckbxIncludeRyuh.isSelected());
		for(int i=0;i<10;i++){
			reader.SetValue(SettingRyu.COMPILEROPTION+(i+1), texts[i].getText());
		}
		try {
			reader.WriteMySelf();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
