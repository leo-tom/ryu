import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class FilterInfoManager {
	private ArrayList<FilterInfo> filterinfos;
	private DefaultListModel<FilterInfo> list;
	private TreeModel treemodel;
	private ImageIcon closeImage;
	private ImageIcon sizeChangeImage;
	private File mylib;
	
	private ActionListener action = null;
	
	public void setAction(ActionListener action) {
		this.action = action;
	}
	public FilterInfoManager(DefaultListModel<FilterInfo> val){
		filterinfos = new ArrayList<FilterInfo>();
		treemodel = new DefaultTreeModel(new DefaultMutableTreeNode("Filters"));
		list = val;
		mylib = null;
		for(int i = 0;i<filterinfos.size();i++){
			list.addElement(filterinfos.get(i));
		}
	}
	private void RemoveAll(){
		treemodel = new DefaultTreeModel(new DefaultMutableTreeNode("Filters"));
		filterinfos.clear();
	}
	public void ReadLibs(){
		RemoveAll();
		_ReadAllLibs();
		filterinfos.sort(new Comparator<FilterInfo>() {
			@Override
			public int compare(FilterInfo o1, FilterInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		if(action!=null){
			action.actionPerformed(null);
		}
	}
	public void addFilterInfo(FilterInfo info){
		try {
			filterinfos.add(info);
			list.addElement(info);
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) treemodel.getRoot();
			String familly = info.getFamilly();
			for(int i = 0;i<root.getChildCount();i++){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt(i);
				String gotInfo = (String) node.getUserObject();
				if(gotInfo.equals(familly)){
					int cou = node.getChildCount();
					int index;
					for(index = 0;index<cou;index++){
						String s = node.getChildAt(index).toString();
						if(s.compareTo(info.toString())>0){
							node.insert(new DefaultMutableTreeNode(info), index);
							return;
						}
					}
					node.insert(new DefaultMutableTreeNode(info), index);
					return;
				}
			}
			DefaultMutableTreeNode adding = new DefaultMutableTreeNode(info.getFamilly());
			root.add(adding);
			adding.add(new DefaultMutableTreeNode(info));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public TreeModel getTreemodel() {
		return treemodel;
	}
	private LibxmlParser parser;
	public void Readxml(File f) throws ParserConfigurationException, IOException, SAXException{
		if(parser==null)parser = new LibxmlParser();
		parser.ParseThis(f);
		for(FilterInfo info : parser.GetFilterInfo()){
			addFilterInfo(info);
		}
	}
	private void _ReadAllLibs(){
		try{
			if(mylib==null){
				mylib = new File(RYU.MYLIB);
				if(!mylib.exists()){
					mylib.mkdir();
				}
			}
			File[] files = mylib.listFiles();
			if(files!=null)
				for(File f : files)
					Readxml(f);
			File shareLib = new File(RYU.SHAREDLIBFOULDER);
			if(!shareLib.exists()){JOptionPane.showMessageDialog(null, "There is no shared Library foulder at \'/etc/ryu/lib\'");}
			for(File f : shareLib.listFiles()){
				Readxml(f);
			}
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public ImageIcon getChangeSizeImage(){
		try{
			if(sizeChangeImage==null){
				sizeChangeImage = new ImageIcon(RYU.SIZECHANGEICON);
			}
		}catch(Exception er){
			er.printStackTrace();
		}
		return sizeChangeImage;
	}
	public ImageIcon getCloseImage(){
		try{
			if(closeImage==null){
				closeImage = new ImageIcon(RYU.CLOSEICON);
			}
		}catch(Exception er){
			er.printStackTrace();
		}
		return closeImage;
	}
	public int getLength(){return filterinfos.size();}
	public ListModel<FilterInfo> getListModel(){
		return list;
	}
	public DefaultComboBoxModel<String> getDefaultComboBoxModel(){
		DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>();
		comboModel.addElement("");
		for(int i = 0;i<filterinfos.size();i++)
			comboModel.addElement(filterinfos.get(i).getName());
		return comboModel;
	}
	public FilterInfo getFilterInfoAt(int i){return filterinfos.get(i);}
	public FilterInfo[] getFilterbyName(String name){
		FilterInfo[] infos = null;
		try
		{
			ArrayList<FilterInfo> listinfo = new ArrayList<FilterInfo>();
			for(int i=0;i<filterinfos.size();i++){
				FilterInfo filinfo = filterinfos.get(i);
				if(filinfo.getName().equals(name)){
					listinfo.add(filinfo);
				}
			}
			Object[] objcts = listinfo.toArray();
			infos = new FilterInfo[objcts.length];
			for(int i = 0;i<objcts.length;i++){
				if(objcts[i] instanceof FilterInfo)
					infos[i] = (FilterInfo)objcts[i];
			}
		}
		catch(Exception er){
			er.printStackTrace();
		}
		return infos;
	}
	public FilterInfo[] getFilterInfos(){
		return (FilterInfo[]) filterinfos.toArray(new FilterInfo[0]);
	}
	public FilterInfo getSelected() {
		return null;
	}
}
