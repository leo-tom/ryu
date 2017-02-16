import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StreamInitializer extends Filter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JScrollPane scrollpane;
	JTextArea textarea;

	public StreamInitializer(FilterConnector filcone, FilterInfoManager filmane) {
		super(filcone, filmane);
		// TODO Auto-generated constructor stub
		try{
			this.remove(panel);
			this.remove(inPanel);
			this.SouthPanel.remove(comboBox);
			Component[] com = this.outPanel.getComponents();
			for(int i = 1;i<com.length;i++){
				this.outPanel.remove(com[i]);
				com[i] = null;
			}
			textarea = new JTextArea();
			textarea.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
			textarea.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					applyText();
				}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			scrollpane = new JScrollPane(textarea);
			this.add(scrollpane, BorderLayout.CENTER);
			deleteReferences();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	private void deleteReferences(){
		panel=null;
		inPanel = null;
		comboBox = null;
	}
	public String getText(){
		return textarea.getText();
	}
	public void setText(String str){
		textarea.setText(str);
		applyText();
	}
	private void applyText(){
		if(textarea.getText().length()<20)
			label.setText("["+textarea.getText()+"]");
	}
	public Stream_ryu getStream(){
		return getOutStreamAt(0);
	}
}
