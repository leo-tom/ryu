import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class LibMaker extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton Remove;
	private FilterInfoManager filterinfomanager;
	private LibMaker me = this;
	private JList<FilterInfo> ListDown;
	private DefaultListModel<FilterInfo> UsingfilterInfos;
	private JList<FilterInfo> ListUp;
	private DefaultListModel<FilterInfo> AllFilterInfos;
	
	private JFileChooser chooser;
	/**
	 * Create the frame.
	 */
	public LibMaker(FilterInfoManager manager) {
		filterinfomanager = manager;
		Initialize();
		AllFilterInfos = new DefaultListModel<FilterInfo>();
		for(FilterInfo filin : filterinfomanager.getFilterInfos()){
			AllFilterInfos.addElement(filin);
		}
		ListUp.setModel(AllFilterInfos);
		UsingfilterInfos = new DefaultListModel<FilterInfo>();
		ListDown.setModel(UsingfilterInfos);
	}
	private void Initialize(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		ListDown = new JList<FilterInfo>();
		scrollPane.setViewportView(ListDown);
		
		Remove = new JButton("Remove");
		Remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Remove();
			}
		});
		panel.add(Remove, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setMinimumSize(new Dimension(50, 150));
		panel_1.add(scrollPane_1, BorderLayout.CENTER);
		
		ListUp = new JList<FilterInfo>();
		scrollPane_1.setViewportView(ListUp);
		
		JButton Add = new JButton("Add");
		Add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Add();
			}
		});
		panel_1.add(Add, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				me.dispose();
			}
		});
		panel_2.add(btnExit);
		
		JButton btnNewButton_1 = new JButton("Save");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Save();
			}
		});
		panel_2.add(btnNewButton_1);
	}
	private void Add(){
		int index = ListUp.getSelectedIndex();
		if(index>=0){
			UsingfilterInfos.addElement(AllFilterInfos.getElementAt(index));
			AllFilterInfos.remove(index);
		}
		try{
			ListUp.setSelectedIndex(index);
		}catch (Exception e) {
			ListUp.setSelectedIndex(0);
		}
		
	}
	private void Remove(){
		int index = ListDown.getSelectedIndex();
		if(index>=0){
			AllFilterInfos.addElement(UsingfilterInfos.getElementAt(index));
			UsingfilterInfos.remove(index);
		}
	}
	private File dir; 
	private void Save(){
		if(chooser==null)chooser = new JFileChooser();
		if(dir==null)dir = new File(RYU.MYLIB);
		chooser.setCurrentDirectory(dir);
		if(chooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			File savingTo = chooser.getSelectedFile();
			FilterInfo[] filterinfos = new FilterInfo[UsingfilterInfos.size()];
			for(int i = 0;i<filterinfos.length;i++)
				filterinfos[i] = UsingfilterInfos.getElementAt(i);
			LibxmlWriter writer = new LibxmlWriter(filterinfos);
			writer.BuildinBack(savingTo);
		}
	}
}
