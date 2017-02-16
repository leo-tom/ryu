import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class FilterConnector implements ActionListener,MouseListener{
	Filter filterup;
	Stream_ryu addingStream;
	Info lastInfo;
	private class Info{
		public static final int streamInfoIn = 1;
		public static final int streamInfoOut = 2;
		public int streamInfo;
		public int streamIndex;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			//filterup can be downer filter and filterDown can also be upper filter
			if(filterup==null&&addingStream==null&&lastInfo==null){
				BackGroundWorker.readNow();
				filterup = (Filter)((JButton)e.getSource()).getParent().getParent();
				addingStream = new Stream_ryu();
				Info info = findButton((JButton) e.getSource(),filterup);
				lastInfo = info;
				if(info!=null){
					if(info.streamInfo==Info.streamInfoOut){
						filterup.setOutStreamAt(addingStream,info.streamIndex);
					}else{
						filterup.setInStreamAt(addingStream, info.streamIndex);
					}
				}
				filterup.getParent().setBackground(Color.lightGray);
			}else{
				Filter filterDown = (Filter)((JButton)e.getSource()).getParent().getParent();
				Info info = findButton((JButton) e.getSource(),filterDown);
				if(info.streamInfo==lastInfo.streamInfo){
					//reset all
					filterDown.getParent().setBackground(null);
					if(lastInfo.streamInfo==Info.streamInfoIn)
						filterup.setInStreamAt(null, lastInfo.streamIndex);
					else
						filterup.setOutStreamAt(null, lastInfo.streamIndex);
					lastInfo = null;
					filterup = null;
					addingStream = null;
					return;
				}
				if(info!=null && addingStream != null &&filterup!=null){
					if(info.streamInfo==Info.streamInfoIn){
						//filter down is downer filter
						filterDown.setInStreamAt(addingStream, info.streamIndex);
						addingStream.connect(filterup,lastInfo.streamIndex,filterDown,info.streamIndex);
					}else{
						//filter down is upper filter
						filterDown.setOutStreamAt(addingStream, info.streamIndex);
						addingStream.connect(filterDown, info.streamIndex, filterup,lastInfo.streamIndex);
					}
					addingStream.addon((JPanel)filterDown.getParent());
				}
				filterDown.getParent().setBackground(null);
				lastInfo = null;
				filterup = null;
				addingStream = null;
			}
		}catch(Exception er){
			er.printStackTrace();
		}
		
	}
	private Info findButton(JButton button,Filter from){
		Info info = new Info(); 
		int i = 0;
		for(JButton btn : from.inBtn){
			if(btn.equals(button)){
				info.streamIndex = i;
				info.streamInfo = Info.streamInfoIn;
				return info;
			}
			i++;
		}
		i = 0;
		for(JButton btn : from.outBtn){
			if(btn.equals(button)){
				info.streamIndex = i;
				info.streamInfo = Info.streamInfoOut;
				return info;
			}
			i++;
		}
		return null;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		/*
		filterup = null;
		addingStream = null;
		lastInfo = null;
		*/
	}

}
