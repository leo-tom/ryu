import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerListModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FilterMaker extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FilterInfo basedOn;
	private FilterInfoManager filterinfomanager;
	private FilterMaker me =this;
	
	private JTextArea Code;
	private JTextField NameT;
	private JTextField NameInCT;
	private JTextField VersionT;
	private JTextArea IncludesT;
	private JSpinner AcceptOutT;
	private JSpinner AcceptInT;
	private JTextArea DescriptionT;
	private JTextArea UrlT;
	private JButton btnBaseOn;
	private JButton btnSaveAt;
	
	private JFileChooser chooser;
	private File myLibFoulder = new File(RYU.MYLIB);

	/**
	 * @wbp.parser.constructor
	 */
	public FilterMaker(FilterInfo basedOn,FilterInfoManager manager) {
		super();
		Initialize();
		this.basedOn = basedOn;
		this.filterinfomanager = manager;
		setFilterInfoVal();
	}

	public FilterMaker(FilterInfoManager manager) {
		super();
		filterinfomanager = manager;
		Initialize();
	}
	private void save(){
		try{
			String[] includes = new String[IncludesT.getLineCount()];
			String str = IncludesT.getText();
			int n = 0;
			StringBuffer stBuilder = new StringBuffer();
			char newLine = System.lineSeparator().charAt(0);
			boolean isWin = System.lineSeparator().length()>1?true:false;
			int i;
			for(i = 0;i<includes.length&&n<str.length();n++){
				char c = str.charAt(n);
				if(c==newLine){
					includes[i++] = stBuilder.toString();
					if(isWin){n++;}
					stBuilder.delete(0, stBuilder.length());
				}else{
					stBuilder.append(c);
				}
			}
			includes[i] = stBuilder.toString();
			if(chooser==null){
				chooser =new JFileChooser();
			}
			chooser.setFileFilter(new FileNameExtensionFilter("Filter file", "fi"));
			chooser.setCurrentDirectory(myLibFoulder);
			chooser.setSelectedFile(new File(myLibFoulder+"/"+NameT.getText()+".fi"));
			if(chooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				File savingto = chooser.getSelectedFile();
				LibxmlWriter xmlWriter = new LibxmlWriter(new FilterInfo[]{
						new FilterInfo.Builder(NameT.getText())
						.setNameInC(NameInCT.getText())
						.setCode(Code.getText())
						.setDescription(DescriptionT.getText())
						.setVersion(VersionT.getText())
						.setUrl(UrlT.getText())
						.setAcceptableInputStream((Integer)AcceptInT.getValue())
						.setAcceptableOutputStream((Integer)AcceptOutT.getValue())
						.addIncludes(includes)
						.setFamilly(FamillyT.getText())
						.build()
				});
				xmlWriter.BuildinBack(savingto);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void setFilterInfoVal(){
		IncludesT.setText("");
		if(basedOn==null){
			Code.setText("");
			NameInCT.setText("");
			NameT.setText("");
			VersionT.setText("");
			AcceptInT.setValue(0);
			AcceptOutT.setValue(0);
			DescriptionT.setText("");
			return;
		}
		UrlT.setText(basedOn.getUrl()==null?null:basedOn.getUrl().toString());
		Code.setText(basedOn.getCode());
		NameT.setText(basedOn.getName());
		NameInCT.setText(basedOn.getNameInC());
		VersionT.setText(basedOn.getVersion());
		AcceptInT.setValue(basedOn.getAcceptableInputStream());
		AcceptOutT.setValue(basedOn.getAcceptableOutputStream());
		DescriptionT.setText(basedOn.getDescription());
		FamillyT.setText(basedOn.getFamilly());
		for(String str : basedOn.getIncludes()){
			IncludesT.append(str);
			IncludesT.append(System.lineSeparator());
		}
	}
	public void SetFilterInfo(FilterInfo info){
		this.basedOn = info;
		setFilterInfoVal();
	}
	private JSplitPane splitPane_1;
	private JTextField FamillyT;

	private void Initialize(){
		setTitle("Filter Maker");
		setSize(new Dimension(500, 350));
		setLocation(new Point(500, 200));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(300, 200));
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setSize(new Dimension(300, 100));
		panel_1.add(scrollPane);
		
		Code = new JTextArea();
		Code.setTabSize(4);
		Code.setFont(new Font("Monospaced", Font.PLAIN, 12));
		Code.setToolTipText("Code");
		JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Open..");
		menu.add(item);
		Code.setComponentPopupMenu(menu);
		item.addActionListener(new ActionListener() {
					String newLine = System.lineSeparator();
					@Override
					public void actionPerformed(ActionEvent arg0) {
						try{
							if(chooser==null)chooser = new JFileChooser();
							if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
								File selected = chooser.getSelectedFile();
								BufferedReader reader = new BufferedReader(new FileReader(selected));
								String appending;
								StringBuilder builder = new StringBuilder();
								while((appending=reader.readLine())!=null){
									builder.append(appending);
									builder.append(newLine);
								}
								Code.setText(builder.toString());
								reader.close();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
		});
		scrollPane.setViewportView(Code);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		DescriptionT = new JTextArea();
		item = new JMenuItem("Open..");
		item.addActionListener(new ActionListener() {
			String newLine = System.lineSeparator();
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
						File selected = chooser.getSelectedFile();
						BufferedReader reader = new BufferedReader(new FileReader(selected));
						String appending;
						while((appending=reader.readLine())!=null){
							DescriptionT.append(appending);
							DescriptionT.append(newLine);
						}
						reader.close();
					}
				}catch(Exception er){
					er.printStackTrace();
				}
			}
		});
		menu = new JPopupMenu();
		menu.add(item);
		DescriptionT.setComponentPopupMenu(menu);
		scrollPane_2.setViewportView(DescriptionT);
		JPanel panelDescription = new JPanel();
		panelDescription.setLayout(new BorderLayout(0, 0));
		panelDescription.add(scrollPane_2);
		DescriptionT.setToolTipText("Description");
		
		
		splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setLeftComponent(panel);
		splitPane_1.setRightComponent(panelDescription);
		splitPane.setRightComponent(splitPane_1);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));
		
		btnBaseOn = new JButton("Base on");
		btnBaseOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FilterInfoChooser chooser = new FilterInfoChooser(filterinfomanager,me);
				chooser.setVisible(true);
			}
		});
		panel_2.add(btnBaseOn);
		
		btnSaveAt = new JButton("Save at");
		btnSaveAt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				save();
			}
		});
		panel_2.add(btnSaveAt);
		
		
		NameT = new JTextField();
		NameT.setToolTipText("Name");
		panel.add(NameT);
		NameT.setColumns(10);
		
		NameInCT = new JTextField();
		NameInCT.setToolTipText("Name in C");
		panel.add(NameInCT);
		NameInCT.setColumns(10);
		
		VersionT = new JTextField();
		VersionT.setToolTipText("Version");
		panel.add(VersionT);
		VersionT.setColumns(10);
		SpinnerListModel model1 = new SpinnerListModel(new Integer[]{
				new Integer(0),
				new Integer(1),
				new Integer(2),
				new Integer(3),
				new Integer(4),
				new Integer(5),
				new Integer(6),
				new Integer(7),
				new Integer(8),
		});
		SpinnerListModel model2 = new SpinnerListModel(new Integer[]{
				new Integer(0),
				new Integer(1),
				new Integer(2),
				new Integer(3),
				new Integer(4),
				new Integer(5),
				new Integer(6),
				new Integer(7),
				new Integer(8),
		});
		AcceptInT = new JSpinner();
		AcceptInT.setToolTipText("Acceptable input streams");
		AcceptInT.setModel(model1);
		panel.add(AcceptInT);
		
		AcceptOutT = new JSpinner();
		AcceptOutT.setToolTipText("Acceptable output streams");
		AcceptOutT.setModel(model2);
		panel.add(AcceptOutT);
		
		FamillyT = new JTextField();
		FamillyT.setToolTipText("Familly");
		panel.add(FamillyT);
		FamillyT.setColumns(100);
		
		UrlT = new JTextArea();
		UrlT.setToolTipText("URL");
		panel.add(UrlT);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollPane_1);
		
		IncludesT = new JTextArea();
		scrollPane_1.setViewportView(IncludesT);
		IncludesT.setToolTipText("Includes");
		
		
	
	}
	
}
