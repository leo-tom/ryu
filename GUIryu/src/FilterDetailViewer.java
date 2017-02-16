import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JSplitPane;

public class FilterDetailViewer extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea Url;
	private JTextField acceptOut;
	private JTextField acceptI;
	private JTextField version;
	private JTextField nameinc;
	private JTextField filtername;
	private FilterInfo filterinfo;
	private FilterInfoManager filterinfomanager;
	private JTextArea Include;
	private JTextArea Description;
	private JTextArea Code;
	private JTextField FamillyT;
	public FilterDetailViewer(FilterInfo val,FilterInfoManager manager){
		filterinfomanager = manager;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				setVisible(false);
			}
		});
		filterinfo = val;
		this.setBounds(300, 300, 500, 400);;
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		Font font = new Font(Font.MONOSPACED,Font.PLAIN, 12);
		
		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMinimumSize(new Dimension(100, 10));
		panel_2.setPreferredSize(new Dimension(200, 10));
		splitPane.setLeftComponent(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setMinimumSize(new Dimension(10, 10));
		
		Code = new JTextArea();
		Code.setTabSize(4);
		Code.setToolTipText("Source Code");
		Code.setFont(font);
		Code.setEditable(true);
		scrollPane.setViewportView(Code);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		
		JPanel panel_4 = new JPanel();
		splitPane_1.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel_4.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblFilterName = new JLabel("Filter Name");
		lblFilterName.setFont(font);
		lblFilterName.setBorder(null);
		panel.add(lblFilterName);
		
		filtername = new JTextField("");
		filtername.setToolTipText("Name of Filter");
		filtername.setEditable(false);
		filtername.setBorder(null);
		panel.add(filtername);
		filtername.setColumns(10);
		
		JLabel lblNameInC = new JLabel("Name in C");
		lblNameInC.setFont(font);
		panel.add(lblNameInC);
		
		nameinc = new JTextField("");
		nameinc.setToolTipText("Name that is used in C");
		nameinc.setEditable(false);
		nameinc.setBorder(null);
		panel.add(nameinc);
		nameinc.setColumns(10);
		
		JLabel lblVersion = new JLabel("Version");
		lblVersion.setFont(font);
		panel.add(lblVersion);
		
		version = new JTextField();
		version.setToolTipText("Version");
		version.setEditable(false);
		version.setBorder(null);
		panel.add(version);
		version.setColumns(10);
		
		JLabel lblAcceptableInputStream = new JLabel("Acceptable In");
		lblAcceptableInputStream.setFont(font);
		panel.add(lblAcceptableInputStream);
		
		acceptI = new JTextField();
		acceptI.setToolTipText("Acceptable Input Stream");
		acceptI.setEditable(false);
		acceptI.setBorder(null);
		panel.add(acceptI);
		acceptI.setColumns(10);
		
		JLabel lblAcceptableOutputStream = new JLabel("Acceptable Out");
		lblAcceptableOutputStream.setFont(font);
		panel.add(lblAcceptableOutputStream);
		
		acceptOut = new JTextField();
		acceptOut.setToolTipText("Acceptable Output Stream");
		acceptOut.setEditable(false);
		acceptOut.setBorder(null);
		panel.add(acceptOut);
		acceptOut.setColumns(10);
		
		JLabel lblInclude = new JLabel("Includes");
		lblInclude.setFont(font);
		panel.add(lblInclude);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel.add(scrollPane_2);
		
		Include = new JTextArea();
		Include.setToolTipText("Headers Including");
		scrollPane_2.setViewportView(Include);
		Include.setEditable(false);
		
		JLabel lblUrl_1 = new JLabel("URL");
		lblUrl_1.setFont(font);
		panel.add(lblUrl_1);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		panel.add(scrollPane_3);
		
		Url = new JTextArea();
		Url.setToolTipText("URL");
		scrollPane_3.setViewportView(Url);
		Url.setEditable(false);
		Url.setBorder(null);
		Url.setColumns(10);
		
		JLabel lblFamilly = new JLabel("Familly");
		lblFamilly.setFont(new Font("Monospaced", Font.PLAIN, 12));
		panel.add(lblFamilly);
		
		FamillyT = new JTextField();
		FamillyT.setEditable(false);
		FamillyT.setBorder(null);
		panel.add(FamillyT);
		FamillyT.setColumns(10);
		
		JPanel panel_5 = new JPanel();
		splitPane_1.setRightComponent(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_5.add(scrollPane_1);
		
		Description = new JTextArea();
		Description.setToolTipText("Discription");
		Description.setBorder(null);
		Description.setEditable(false);
		scrollPane_1.setViewportView(Description);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnReRead = new JButton("Re Read");
		btnReRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InitVal();
			}
		});
		panel_1.add(btnReRead);
		
		JButton btnMakeNewFilter = new JButton("Make new filter based on this filter");
		btnMakeNewFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				FilterMaker maker = new FilterMaker(filterinfo,filterinfomanager);
				maker.setVisible(true);
			}
		});
		panel_1.add(btnMakeNewFilter);
		
		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		InitVal();
	}
	private void InitVal(){
		if(filterinfo==null)return;
		Url.setText(filterinfo.getUrl()==null?"":filterinfo.getUrl().toString());
		acceptOut.setText(filterinfo.getAcceptableOutputStream()+"");
		acceptI.setText(filterinfo.getAcceptableInputStream()+"");
		version.setText(filterinfo.getVersion());
		for(String str : filterinfo.getIncludes()){
			Include.append(str);
			Include.append(System.lineSeparator());
		}
		Description.setText(filterinfo.getDescription());
		Code.setText(filterinfo.getCode());
		filtername.setText(filterinfo.getName());
		nameinc.setText(filterinfo.getNameInC());
		FamillyT.setText(filterinfo.getFamilly());
		setTitle(filterinfo.getName());
	}
	public void setFilterInfo(FilterInfo val){
		filterinfo = val;
		InitVal();
	}
}
