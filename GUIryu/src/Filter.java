import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Filter extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JPanel inPanel;
	protected JPanel outPanel;
	protected JPanel panel;
	protected JPanel panel_1;
	protected JButton btnNewButton;
	protected JLabel label;
	
	//used for movement of filter
	private Point lastPoint;
	private FilterConnector filterconnector;
	private Stream_ryu[] stIn;
	private Stream_ryu[] stOut;
	private Filter me = this;
	//less calc is used to reduce frequency of updating lines
	private long lessCalc;
	private final static int HOWOFTEN = 15;
	public JButton[] inBtn;
	public JButton[] outBtn;
	
	private FilterInfo myfilter;
	private JButton btnDetails;
	private JTextArea textArea;
	protected JPanel SouthPanel;
	protected JComboBox<String> comboBox;
	private JLabel lblNewLabel;
	private FilterInfoManager filterinfoManager;
	
	protected static final String resourcesFolder = RYU.RESOURCEFOULDER;

	/**
	 * Create the panel.
	 */
	public Filter(FilterConnector filcone,FilterInfoManager filmane) {
		try{
			stIn = new Stream_ryu[8];
			stOut = new Stream_ryu[8];
			inBtn = new JButton[8];
			outBtn = new JButton[8];
			filterconnector = filcone;
			filterinfoManager = filmane;
			setPreferredSize(new Dimension(235, 128));
			setBorder(new LineBorder(new Color(0, 0, 0)));
			setLayout(new BorderLayout(0, 0));
			
			inPanel = new JPanel();
			add(inPanel, BorderLayout.WEST);
			inPanel.setLayout(new GridLayout(0, 1, 0, 0));
			
			outPanel = new JPanel();
			add(outPanel, BorderLayout.EAST);
			outPanel.setLayout(new GridLayout(0, 1, 0, 0));
			int i;
			for(i = 0;i<inBtn.length;i++){
				inBtn[i] = new JButton(Integer.valueOf(i+1).toString());
				inBtn[i].setMargin(new Insets(0, 0, 0, 0));
				inBtn[i].setPreferredSize(new Dimension(25, 25));
				inBtn[i].addActionListener(filterconnector);
				inPanel.add(inBtn[i]);
			}
			for(i = 0;i<outBtn.length;i++){
				outBtn[i] = new JButton(Integer.valueOf(i+1).toString());
				outBtn[i].setMargin(new Insets(0, 0, 0, 0));
				outBtn[i].setPreferredSize(new Dimension(25, 25));
				outBtn[i].addActionListener(filterconnector);
				outPanel.add(outBtn[i]);
			}
			
			panel = new JPanel();
			add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			
			btnDetails = new JButton("Details");
			btnDetails.addActionListener(new ActionListener() {
				FilterDetailViewer viwer;
				public void actionPerformed(ActionEvent arg0) {
					if(viwer==null)
						viwer = new FilterDetailViewer(myfilter,filmane);
					else
						viwer.setFilterInfo(myfilter);
					viwer.setVisible(true);
				}
			});
			panel.add(btnDetails, BorderLayout.NORTH);
			
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			JScrollPane scpane = new JScrollPane(textArea);
			panel.add(scpane, BorderLayout.CENTER);
			
			panel_1 = new JPanel();
			add(panel_1, BorderLayout.NORTH);
			panel_1.setLayout(new BorderLayout(0, 0));
			
			btnNewButton = new JButton("");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try{
						Dispose();
					}catch(Exception ee){
						ee.printStackTrace();
					}
					
				}
			});
			btnNewButton.setIcon(filterinfoManager.getCloseImage());
			btnNewButton.setMargin(new Insets(0, 0, 0, 0));
			btnNewButton.setPreferredSize(new Dimension(20, 20));
			btnNewButton.setFont(new Font(Font.MONOSPACED,Font.PLAIN,3));
			panel_1.add(btnNewButton, BorderLayout.EAST);
			
			label = new JLabel("===========");
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					//this makes movement smoothie
					lastPoint = null;
				}
			});
			label.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					
					Point p = e.getLocationOnScreen();
					if(lastPoint!=null){
						int x = (int) (p.getX() - lastPoint.getX());
						int y = (int) (p.getY() - lastPoint.getY());
						x += getX();
						y += getY();
						setLocation(x,y);
						UpdateAllLines();
					}
					lastPoint = p;
					JPanel panel = (JPanel) me.getParent();
					int marginx = me.getWidth()+50;
					int marginy = me.getHeight()+50;
					if(me.getX()+marginx+10 >= panel.getWidth()){
						panel.setPreferredSize(new Dimension(me.getX()+marginx, panel.getHeight()));
					}
					if(me.getY()+marginy+10 >= panel.getHeight()){
						panel.setPreferredSize(new Dimension(panel.getWidth(),me.getY()+marginy));
					}
				}
			});
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel_1.add(label, BorderLayout.CENTER);
			
			SouthPanel = new JPanel();
			add(SouthPanel, BorderLayout.SOUTH);
			SouthPanel.setLayout(new BorderLayout(0, 0));
			
			comboBox = new JComboBox<String>();
			comboBox.setModel(filterinfoManager.getDefaultComboBoxModel());
			comboBox.addPopupMenuListener(new PopupMenuListener() {
				
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					// TODO Auto-generated method stub
					BackGroundWorker.readNow();
					me.getSelectedFilter();
					me.UpdateFilterInfo();
				}
				
				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					// TODO Auto-generated method stub
					BackGroundWorker.readNow();
					me.getSelectedFilter();
					me.UpdateFilterInfo();
				}
				
				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			comboBox.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					// TODO Auto-generated method stub
					
				}
			});
			/*
			comboBox.addItemListener(new ItemListener() {
				int i = 0;
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(i%2==0){
						BackGroundWorker.readNow();
						me.getSelectedFilter();
						me.UpdateFilterInfo();
					}
					System.out.println("aa");
					i++;
				}
			});*/
			SouthPanel.add(comboBox, BorderLayout.CENTER);
			
			lblNewLabel = new JLabel(filmane.getChangeSizeImage());
			lblNewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			lblNewLabel.addMouseMotionListener(new MouseMotionListener() {
				int lastx,lasty;
				long last;
				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					try{
						if((lastx!=0&&lasty!=0)&&(last+50>e.getWhen())&&(lastx!=e.getXOnScreen()||lasty!=e.getYOnScreen())){
							Dimension size = new Dimension(me.getWidth()+(e.getXOnScreen()-lastx),
									me.getHeight()+(e.getYOnScreen()-lasty));
							if(size.getWidth()>50&&size.getHeight()>50)
								me.setSize(size);
							me.UpdateAllLines();
							me.updateUI();
						}
						last = e.getWhen();
						lastx = e.getXOnScreen();
						lasty = e.getYOnScreen();
					}catch(Exception er){
						er.printStackTrace();
					}
					
				}
			});
			SouthPanel.add(lblNewLabel, BorderLayout.EAST);
			this.updateUI();
			this.UpdateFilterInfo();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private void getSelectedFilter(){
		try{
			int index = comboBox.getSelectedIndex()-1;
			if(index>=0)
				me.myfilter = filterinfoManager.getFilterInfoAt(index);
			else{
				me.myfilter = null;
				comboBox.setModel(filterinfoManager.getDefaultComboBoxModel());
			}
			UpdateFilterInfo();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	
	public void UpdateAllLines(){
		int i = 0;
		try{
			lessCalc++;
			if(lessCalc>Long.MAX_VALUE/3)
				lessCalc = 0;
			if(lessCalc%HOWOFTEN==0)
				for(i = 0;i<stIn.length;i++){
					if(stIn[i]!=null){
						stIn[i].removeFrom((JPanel) getParent());
						stIn[i].UpdateLine();
						stIn[i].addon((JPanel) getParent());
					}
					if(stOut[i]!=null){
						stOut[i].removeFrom((JPanel) getParent());
						stOut[i].UpdateLine();
						stOut[i].addon((JPanel) getParent());
					}
				}
		}catch(Exception er){
			er.printStackTrace();
			System.err.println(""+i);
		}
		
	}
	private void Dispose(){
		try {
			BackGroundWorker.readNow();
			setVisible(false);
			for(int i = 0;i<stIn.length;i++){
				if(stIn[i]!=null){
					stIn[i].dispose();
				}
				if(stOut[i]!=null){
					stOut[i].dispose();
				}
			}
			((JComponent)me.getParent()).updateUI();
			me.getParent().remove(me);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public void setInStreamAt(Stream_ryu st,int index){
		if(index>=stIn.length)
			return;
		if(stIn[index]!=null&&stIn[index]!=st){
			stIn[index].dispose();
		}
		stIn[index] = st;
	}
	public void setOutStreamAt(Stream_ryu st,int index){
		if(index>=stOut.length)
			return;
		if(stOut[index]!=null&&stOut[index]!=st){
			stOut[index].dispose();
		}
		stOut[index] = st;
	}
	public void setNullOut(int i){stOut[i]=null;}
	public void setNullIn(int i){stIn[i]=null;}
	public Stream_ryu getInStreamAt(int i){
		if(i<stIn.length&&i>=0)
			return stIn[i];
		return null;
	}
	public Stream_ryu getOutStreamAt(int i){
		if(i<stOut.length&&i>=0)
			return stOut[i];
		return null;
	}
	public FilterInfo getFilterInfo(){return this.myfilter;}
	private void UpdateFilterInfo(){
		try{
			if(this.myfilter!=null){
				label.setText(myfilter.getName());
				ComboBoxModel<String> model = comboBox.getModel();
				for(int i = 0;i<model.getSize();i++){
					String str = model.getElementAt(i);
					if(str.equals(myfilter.getName())){
						model.setSelectedItem(str);
						break;
					}
				}
				if(myfilter.getDescription()!=null)
					textArea.setText(myfilter.getDescription());
				else
					textArea.setText(myfilter.getCode());
			}else{
				label.setText("===========");
				textArea.setText("");
			}
			this.updateUI();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void setFilterInfo(FilterInfo val){
		this.myfilter = val;
		UpdateFilterInfo();
	}
}
