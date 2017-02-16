import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;

/*
 * 
Copyright (c) 2016, Leo Tomura
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
* Neither the name of the Leo Tomura nor the names of its contributors
  may be used to endorse or promote products derived from this software
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class MainWindow_ryu {
	private JFrame frame;
	private JPanel panel  = new JPanel();
	private JPopupMenu popMenu;
	private int addAtX;
	private int addAtY;
	private JScrollPane ScrollPane;
	private JScrollBar barver,barhori;
	private FilterConnector filcone;
	private MainWindow_ryu me = this;
	private File saveto;
	private JFileChooser filechooser;
	private FilterInfoManager filterManager;
	private BackGroundWorker backgroundworker;
	private JTree tree;

	private File Csource;
	private JMenu mnCompile;
	private JMenuItem mntmMakeCSource;
	private SettingReader settingReader;
	public SettingReader getSettingReader() {
		return settingReader;
	}
	private String xml;

	public static String getHomeDir(){
		return System.getProperty("user.home");
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow_ryu window = new MainWindow_ryu();
					window.frame.setVisible(true);
					if(args.length>0){
						File f = new File(args[0]);
						if(f.exists())
							window.OpenProjectFile(f);
						else
							System.err.println("This file does not exist");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow_ryu() throws Exception{
		try{
			File myDir = new File(RYU.HOMEDIR);
			if(!myDir.exists()){
				File f = new File(RYU.MYLIB);
				myDir.mkdir();
				f.mkdir();

			}
		}catch(Exception er){
			er.printStackTrace();
		}
		settingReader = new SettingReader();
		settingReader.ReadSetting();
		try{
			String val = settingReader.getValueOf("defaultlookandfeel");
			UIManager.setLookAndFeel(val==null?SettingDefault.DEFAULTLOOKANDFEEL:val);
		}catch(Exception er){
			er.printStackTrace();
		}
		filterManager = new FilterInfoManager(new DefaultListModel<FilterInfo>());
		backgroundworker = new BackGroundWorker(this);
		backgroundworker.startBackGroundWorker();
		initialize();
		while(backgroundworker.isBusy()){
			Thread.sleep(100);
		}
		tree.setModel(filterManager.getTreemodel());
		filcone = new FilterConnector();
		{
			popMenu = new JPopupMenu();
			JMenuItem itemAdd = new JMenuItem("add");
			itemAdd.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addFilter(addAtX, addAtY);
				}
			});
			popMenu.add(itemAdd);
			itemAdd = new JMenuItem("add Stream Initializer");
			itemAdd.addActionListener(new ActionListener() {

				@Override public void actionPerformed(ActionEvent e) {
					backgroundworker.ForceToRead();
					StreamInitializer stIni = new StreamInitializer(filcone, filterManager);
					stIni.setBounds(addAtX, addAtY, stIni.getPreferredSize().width,stIni.getPreferredSize().height);
					panel.add(stIni);
					panel.updateUI();
				}
			});
			popMenu.add(itemAdd);
			itemAdd = new JMenuItem("Undo");
			itemAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					Back();
				}
			});
			popMenu.add(itemAdd);
			itemAdd = new JMenuItem("Redo");
			itemAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					ReBack();
				}
			});
			popMenu.add(itemAdd);
		}		
		String val = settingReader.getValueOf("defaultCsourceCodeFile");
		setFileForCSource( (val==null)? null : new File(val) );
	}

	/**
	 * @return the filterManager
	 */
	public FilterInfoManager getFilterManager() {
		return filterManager;
	}

	/**
	 * @return the filcone
	 */
	public FilterConnector getFilcone() {
		return filcone;
	}

	/**
	 * @return the filechooser
	 */
	public JFileChooser getFilechooser() {
		return filechooser;
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		frame = new JFrame();
		frame.setBounds(50, 50, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Ryu");
		frame.setIconImage(ImageIO.read(new File(RYU.ICONS)));


		panel.addContainerListener(new ContainerAdapter() {
			@Override public void componentAdded(ContainerEvent arg0) {
				UpdatePanelSize();
			}
			@Override public void componentRemoved(ContainerEvent arg0) {
				UpdatePanelSize();
			}
		});
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			int lastx,lasty;
			long when;
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if(when+30<arg0.getWhen()){
					lastx = arg0.getXOnScreen();
					lasty = arg0.getYOnScreen();
					when = arg0.getWhen();
					return;
				}
				try{
					if(lastx!=arg0.getXOnScreen())
						barhori.setValue((barhori.getValue()+(lastx-arg0.getXOnScreen())));
					if(lasty!=arg0.getYOnScreen())
						barver.setValue((barver.getValue()+(lasty-arg0.getYOnScreen())));
					lastx = arg0.getXOnScreen();
					lasty = arg0.getYOnScreen();
					when = arg0.getWhen();
				}catch(Exception er){
					er.printStackTrace();
				}
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg) {
				if(arg.isPopupTrigger()){
					ShowPopUp(arg);
				}
			}
			public void mouseReleased(MouseEvent arg){
				if(arg.isPopupTrigger()){
					ShowPopUp(arg);
				}
			}
		});
		ScrollPane = new JScrollPane(panel);
		ScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		ScrollPane.addMouseListener(filcone);
		barver = ScrollPane.getVerticalScrollBar();
		barhori = ScrollPane.getHorizontalScrollBar();
		panel.setLayout(null);

		JSeparator separator = new JSeparator();
		separator.setBounds(76, 98, 133, -4);
		panel.add(separator);
		frame.getContentPane().add(ScrollPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(100, 10));
		JScrollPane scpane = new JScrollPane(panel_1);
		frame.getContentPane().add(scpane, BorderLayout.EAST);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		tree = new JTree();
		tree.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.add(tree, BorderLayout.CENTER);
		filterManager.setAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeModel model =  filterManager.getTreemodel();
				tree.setModel(model);
			}
		});
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Exit();
			}
		});

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveXml();
			}
		});

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OpenProjectFile();
			}
		});

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(RYU.IsWindows){
						Runtime.getRuntime().exec(new String[]{"java","-jar","ryu.jar"});
					}else{
						ProcessBuilder proBuilder = new ProcessBuilder("ryu");
						proBuilder.start();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);
		
		JMenuItem mntmOpenBackups = new JMenuItem("Open back ups");
		mntmOpenBackups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OpenBackUp();
			}
		});
		mnFile.add(mntmOpenBackups);
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save as");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveto = null;
				saveXml();
			}
		});
		mnFile.add(mntmSaveAs);
		mnFile.add(mntmExit);

		JMenu mnEditer = new JMenu("Editer");
		JMenuItem mntmReBack = new JMenuItem("Redo");
		JMenuItem mntmBack = new JMenuItem("Undo");
		mnEditer.addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				// TODO Auto-generated method stub
				if(backgroundworker.canGetBack())
					mntmBack.setEnabled(true);
				else
					mntmBack.setEnabled(false);
				if(backgroundworker.canGetForward())
					mntmReBack.setEnabled(true);
				else
					mntmReBack.setEnabled(false);
			}

			@Override
			public void menuDeselected(MenuEvent e) {}

			@Override
			public void menuCanceled(MenuEvent e) {	}
		});
		menuBar.add(mnEditer);

		mntmBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Back();
			}
		});
		
		JMenuItem mntmClear = new JMenuItem("Clear");
		mntmClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				backgroundworker.BacuUpNow();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DeleteInfos();
			}
		});
		mnEditer.add(mntmClear);
		mnEditer.add(mntmBack);

		mntmReBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ReBack();
			}
		});
		mnEditer.add(mntmReBack);

		mnCompile = new JMenu("Compile");
		menuBar.add(mnCompile);

		mntmMakeCSource = new JMenuItem("Make C source to..");
		mntmMakeCSource.addActionListener(new ActionListener() {
			JFileChooser chooserForC = null;
			public void actionPerformed(ActionEvent arg0) {
				if(chooserForC==null){
					chooserForC = new JFileChooser();
					chooserForC.setFileFilter(new FileNameExtensionFilter("C source file", "c"));
					String str = settingReader.getValueOf(SettingRyu.WorkingDir);
					if(str!=null){
						chooserForC.setCurrentDirectory(new File(str));
					}
				}
				if(chooserForC.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
					setFileForCSource(chooserForC.getSelectedFile());
				}
				Compile();
			}
		});
		mnCompile.add(mntmMakeCSource);

		JMenuItem mntmMakeCSource_1 = new JMenuItem("Make C source code");
		mntmMakeCSource_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Compile();
			}
		});
		mntmMakeCSource_1.setEnabled(false);
		mnCompile.add(mntmMakeCSource_1);

		JMenuItem mntmMakeExec = new JMenuItem("Make Exec");
		mntmMakeExec.addActionListener(new ExecBuilder(this,settingReader));
		mntmMakeExec.setEnabled(false);
		mnCompile.add(mntmMakeExec);
		
		mntmCodeGenerationSetting = new JMenuItem("Code generation setting");
		mnCompile.add(mntmCodeGenerationSetting);
		mntmCodeGenerationSetting.addActionListener(new ActionListener() {
			CodeConfig config = null;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(config==null)
					config = new CodeConfig(settingReader);
				config.setVisible(true);
				config.Init(getProjectname());
			}
		});
		JMenu mnSystem = new JMenu("Look&Feel");
		for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
			JMenuItem item = new JMenuItem(info.getName());
			item.addActionListener(new LookAndFeelListener(info));
			mnSystem.add(item);
		}

		JMenu mnLib = new JMenu("Lib");
		menuBar.add(mnLib);

		JMenuItem mntmReReadLib = new JMenuItem("Re read lib");
		mntmReReadLib.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filterManager.ReadLibs();
			}
		});
		mnLib.add(mntmReReadLib);

		JMenuItem mntmMakeFilter = new JMenuItem("Make Filter");
		mntmMakeFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FilterMaker maker = new FilterMaker(filterManager);
				maker.setVisible(true);
			}
		});
		mnLib.add(mntmMakeFilter);

		JMenuItem mntmMakeLibrary = new JMenuItem("Make Library");
		mntmMakeLibrary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LibMaker libmaker = new LibMaker(filterManager);
				libmaker.setVisible(true);
			}
		});
		mnLib.add(mntmMakeLibrary);
		menuBar.add(mnSystem);
	}
	public void DeleteInfos(){
		panel.removeAll();
		panel.setToolTipText("");
		panel.updateUI();
		setFileForCSource(null);
	}
	protected void OpenBackUp() {
		if(filechooser==null){
			filechooser = new JFileChooser();
		}
		filechooser.setFileFilter(new FileNameExtensionFilter("Zip", "zip"));
		filechooser.setCurrentDirectory(new File(RYU.BACKUOFILEFOULDER));
		if(filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try{
				File f = filechooser.getSelectedFile();
				ZipInputStream inst = new ZipInputStream(new FileInputStream(f));
				ZipEntry entry = null;
				while((entry=inst.getNextEntry())!=null){
					if(!entry.isDirectory()){
						String name = entry.getName();
						File tmp = new File(RYU.tmpDir+"/"+BackGroundWorker.GetProcessId()+"tmp"+name);
						FileOutputStream out = new FileOutputStream(tmp);
						int c;
						while((c=inst.read())>=0){
							out.write(c);
						}
						xmlParser parser = new xmlParser(panel, tmp, filterManager, filcone);
						DeleteInfos();
						parser.parseBack();
						out.close();
						break;
					}
				}
				inst.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * @return the csource
	 */
	public File getCsource() {
		return Csource;
	}

	private void Exit() {
		System.exit(0);
	}
	private JMenuItem back;
	private JMenuItem reback;
	private JMenuItem mntmCodeGenerationSetting;
	private void ShowPopUp(MouseEvent arg){
		popMenu.show(arg.getComponent(), arg.getX(), arg.getY());
		addAtX = arg.getX();
		addAtY = arg.getY();
		if(back==null||reback==null){
			for(Component c : popMenu.getComponents()){
				if(c instanceof JMenuItem){
					JMenuItem item = (JMenuItem)c;
					if(item.getText().equals("Undo")){
						back = item;
					}else if(item.getText().equals("Redo")){
						reback = item;
					}
				}
			}
		}
		if(backgroundworker.canGetBack())
			back.setEnabled(true);
		else
			back.setEnabled(false);
		if(backgroundworker.canGetForward())
			reback.setEnabled(true);
		else
			reback.setEnabled(false);
	}
	private void setFileForCSource(File f){
		Csource = f;
		if(f!=null){
			for(int i=0;i<mnCompile.getItemCount();i++){
				JMenuItem item = mnCompile.getItem(i);
				item.setEnabled(true);
				item.updateUI();
			}
		}else{
			for(int i=0;i<mnCompile.getItemCount();i++){
				JMenuItem item = mnCompile.getItem(i);
				item.setEnabled(false);
			}
			mntmMakeCSource.setEnabled(true);
			mntmCodeGenerationSetting.setEnabled(true);
		}
	}
	private void addFilter(int x,int y){
		try{
			backgroundworker.ForceToRead();
			Filter newfilter = new Filter(filcone,filterManager);
			newfilter.setBounds(x, y,(int)newfilter.getPreferredSize().getWidth(),(int)newfilter.getPreferredSize().getHeight());
			TreePath path = tree.getSelectionPath();
			FilterInfo info=null;
			if(path!=null){
				DefaultMutableTreeNode obj = (DefaultMutableTreeNode) path.getLastPathComponent();
				if(obj.getUserObject() instanceof FilterInfo)
					info = (FilterInfo) obj.getUserObject();
			}
			newfilter.setFilterInfo(info);
			panel.add(newfilter);
			UpdatePanelSize();
			panel.updateUI();
		}catch(Exception er){
			er.printStackTrace();
		}

	}
	private void UpdatePanelSize(){
		try{
			int width = 0;
			int height = 0;
			for(Component com : panel.getComponents()){
				Filter filter;
				int x,y;
				if(!(com instanceof Filter))
					continue;
				filter = (Filter)com;
				if((x=filter.getX()+filter.getWidth()+50)>width)
					width = x;
				if((y=filter.getY()+filter.getHeight()+50)>height)
					height = y;
			}
			panel.setPreferredSize(new Dimension(width + 300, height + 300));
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private void saveXml(){
		String tmp =me.frame.getTitle();;
		String projectName = panel.getToolTipText();
		if((projectName==null)?false:projectName.isEmpty()) projectName = null;
		try{
			if(projectName==null){
				projectName = JOptionPane.showInputDialog("Type project name");
			}
			if(projectName!=null)
				panel.setToolTipText(projectName);
			//using panel's tool tip text to save project's name
			
			me.frame.setTitle(tmp+"-building xml");
			if(saveto==null){
				if(filechooser==null){
					filechooser = new JFileChooser();
					filechooser.setFileFilter(new FileNameExtensionFilter("Ryu project file", "ryu"));
					String str = settingReader.getValueOf(SettingRyu.WorkingDir);
					if(str!=null){
						filechooser.setCurrentDirectory(new File(str));
					}
				}
				if(filechooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
					saveto = filechooser.getSelectedFile();
					if(projectName==null){
						projectName = saveto.getName();
						panel.setToolTipText(projectName);
					}
					xmlBuilder xmlbuilder = new xmlBuilder(me.panel);
					xmlbuilder.build();
					xml = xmlbuilder.getXml();
					FileWriter writer = new FileWriter(saveto);
					writer.write(xml);
					writer.close();
				}
			}else{
				if(projectName==null){
					projectName = saveto.getName();
					panel.setToolTipText(projectName);
				}
				xmlBuilder xmlbuilder = new xmlBuilder(me.panel);
				xmlbuilder.build();
				xml = xmlbuilder.getXml();
				FileWriter writer = new FileWriter(saveto);
				writer.write(xml);
				writer.close();
			}
			me.frame.setTitle(tmp);
		}catch(Exception er){
			me.frame.setTitle(tmp);
			er.printStackTrace();
		}
	}
	public void Back(){
		Document doc = null;
		if((doc=backgroundworker.popBack())!=null){
			xmlParser parser = new xmlParser();
			parser.setFilterconnector(filcone);
			parser.setFilterinfomanager(filterManager);
			parser.setPanel(panel);
			panel.removeAll();
			parser.parseBack(doc);
		}
	}
	public void ReBack(){
		Document doc = null;
		if((doc=backgroundworker.popForward())!=null){
			xmlParser parser = new xmlParser();
			parser.setFilterconnector(filcone);
			parser.setFilterinfomanager(filterManager);
			parser.setPanel(panel);
			panel.removeAll();
			parser.parseBack(doc);
		}
	}
	public void OpenProjectFile(){
		try{
			if(filechooser==null){
				filechooser = new JFileChooser();
				filechooser.setFileFilter(new FileNameExtensionFilter("Ryu project file", "ryu"));
				String str = settingReader.getValueOf(SettingRyu.WorkingDir);
				if(str!=null){
					filechooser.setCurrentDirectory(new File(settingReader.getValueOf(SettingRyu.WorkingDir)));
				}
			}
			if(filechooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				saveto = filechooser.getSelectedFile();
				backgroundworker.InitializeRedoer();
				xmlParser xmlparser = new xmlParser(panel, saveto, filterManager, filcone);
				panel.removeAll();
				xmlparser.parseBack();
				setFileForCSource(null);
			}
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void OpenProjectFile(File file){
		saveto = file;
		xmlParser xmlparser = new xmlParser(panel, saveto, filterManager, filcone);
		xmlparser.parseBack();
	}
	public boolean AskProjectName(){
		String projectName = null;
		projectName = JOptionPane.showInputDialog("Type project name");
		if(projectName!=null){
			panel.setToolTipText(projectName);
			return false;
		}
		return true;
	}
	public String getProjectname(){
		return panel.getToolTipText();
	}
	public void Compile(){
		try{
			if(Csource==null){
				setFileForCSource(null);
				return;
			}
			if(getProjectname()==null&&AskProjectName()){
				JOptionPane.showMessageDialog(null, "You need to input project name.");
				return;
			}
			xmlBuilder builder = new xmlBuilder(panel);
			builder.build();
			cCodeWriter writer = new cCodeWriter(builder.getDocument(),filterManager);
			writer.CompileBack();
			writer.setFinishedListener(new FinishedListener() {
				FileWriter writeTo = new FileWriter(Csource);
				@Override
				public void ActionFinished(String Ccode) {
					// TODO Auto-generated method stub
					try {
						writeTo.write(Ccode);
						writeTo.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
		}catch(Exception er){
			er.printStackTrace();
		}
	}

	/**
	 * @return the panel
	 */
	public JPanel getPanel() {
		return panel;
	}
}
