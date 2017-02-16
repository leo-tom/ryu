import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class Stream_ryu extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<JSeparator> lines;
	Filter up;
	Filter down;
	int indexUp;
	int indexDown;
	Color colour;
	public Stream_ryu() {
		try{
			lines  = new ArrayList<JSeparator>();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void addon(JPanel panel){
		if(panel==null)
			return;
		try{
			for(Object sp : lines.toArray()){
				JSeparator separator = (JSeparator)sp;
				separator.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						if(e.isShiftDown())
							ChangeColour();
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {}

					
				});
				panel.add(separator);
			}
			panel.updateUI();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(int i=0;i<lines.size();i++){
			result = prime * result + lines.get(i).hashCode();
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Stream_ryu))
			return false;
		Stream_ryu st = (Stream_ryu)obj;
		if(st==this)
			return true;
		if(this.hashCode()==st.hashCode())
			return true;
		return false;
	}
	private void ChangeColour() {
		int randColour = (new Random()).nextInt();
		colour = new Color(randColour);
		for(int i=0;i<lines.size();i++){
			JSeparator sepa = lines.get(i);
			sepa.setForeground(colour);
			sepa.updateUI();
		}
	}
	public Filter getUp(){return up;}
	public Filter getDown(){return down;}
	public void removeFrom(JPanel panel){
		if(panel==null)
			return;
		try{
			for(int i = 0;i<lines.size();i++)
				panel.remove(lines.get(i));
			lines.clear();
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void connect(Filter up,int indexUp,Filter down,int indexDown){
		//up means that line from up starts from right hand side of a component
		//down means that line from down starts from left side of a component
		if(up==null||down==null)
			return;
		this.down = down;
		this.up = up;
		this.indexUp = indexUp;
		this.indexDown = indexDown;
		UpdateLine();
	}
	/**
	 * @param up the up to set
	 */
	public void setUp(Filter up) {
		this.up = up;
	}
	/**
	 * @param down the down to set
	 */
	public void setDown(Filter down) {
		this.down = down;
	}
	/**
	 * @return the indexUp
	 */
	public int getIndexUp() {
		return indexUp;
	}
	/**
	 * @param indexUp the indexUp to set
	 */
	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}
	/**
	 * @return the indexDown
	 */
	public int getIndexDown() {
		return indexDown;
	}
	/**
	 * @param indexDown the indexDown to set
	 */
	public void setIndexDown(int indexDown) {
		this.indexDown = indexDown;
	}
	private void UpdateLineBack(){
		try{
			if(colour==null){
				int randColour = (new Random()).nextInt();
				colour = new Color(randColour);
			}
			JSeparator line = new JSeparator(JSeparator.HORIZONTAL);
			int firstLineSize = 50 + indexUp*2;
			int x = up.getX()+up.getWidth();
			int y = 5+up.getY()+(up.outBtn[indexUp].getLocationOnScreen().y-up.getLocationOnScreen().y);
			int width = firstLineSize;
			int height = 5;
			line.setBounds(x,y,width,height);
			line.setForeground(colour);
			lines.add(line);
			line = new JSeparator(JSeparator.VERTICAL);
			x += firstLineSize;
			height = y-1;
			y = 1;
			width =5;
			line.setBounds(x, y, width, height);
			line.setForeground(colour);
			lines.add(line);
			line = new JSeparator(JSeparator.HORIZONTAL);
			width = x - down.getX() + firstLineSize;
			x = x - width;
			line.setBounds(x, y, width, height);
			line.setForeground(colour);
			lines.add(line);
			line = new JSeparator(JSeparator.VERTICAL);
			width = 5;
			height = (int) (5+down.getY()+(down.inBtn[this.indexDown].getLocationOnScreen().getY() - down.getLocationOnScreen().getY()));
			line.setBounds(x, y, width, height);
			line.setForeground(colour);
			lines.add(line);
			line = new JSeparator(JSeparator.HORIZONTAL);
			width = firstLineSize;
			y = height;
			height = 5;
			line.setBounds(x, y, width, height);
			line.setForeground(colour);
			lines.add(line);
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void UpdateLine(){
		if(up==null||down==null)
			return;
		if(this.up.getX()>this.down.getX()){
			UpdateLineBack();
			return;
		}
		try{
			if(colour==null){
				int randColour = (new Random()).nextInt();
				colour = new Color(randColour);
			}
			int divided = (up.getHeight()-10)/8;
			int sizeOfFirstline = (down.getX()-up.getX())/10 + indexUp*2;
			int heightDifference = down.inBtn[indexDown].getLocationOnScreen().y - up.outBtn[indexUp].getLocationOnScreen().y;
			Dimension dime = new Dimension(down.getX(), down.getY()+divided*indexDown+5);
			//making left to write
			//this is first line that is lined from upper filter
			JSeparator line = new JSeparator(JSeparator.HORIZONTAL);
			line.setBounds(up.getX()+up.getWidth(), 5+up.getY()+(up.outBtn[indexUp].getLocationOnScreen().y-up.getLocationOnScreen().y), sizeOfFirstline, 1);
			line.setForeground(colour);
			lines.add(line);
			//line to match  two components' height difference
			int x = line.getX()+line.getWidth();
			int y = line.getY();
			int width = 1;
			int height = heightDifference<0?heightDifference*-1:heightDifference;/*
			if(heightDifference<0){
				height -= 5+divided*indexDown;
			}else{
				height += 5+divided*indexDown;
			}
			*/
			if(heightDifference<0)
				y = line.getY() - height;
			line = new JSeparator(JSeparator.VERTICAL);
			line.setBounds(x, y, width,height);
			line.setForeground(colour);
			lines.add(line);
			//line for reducing width difference
			line = new JSeparator(JSeparator.HORIZONTAL);
			y = heightDifference<0?y:y + height;
			width = dime.width - (x+width);
			height = 1;
			line.setBounds(x, y, width, height);
			line.setForeground(colour);
			lines.add(line);
			
		}catch(Exception er){
			er.printStackTrace();
		}
	}
	public void dispose(){
		if(up != null){
			up.setNullOut(indexUp);
			up = null;
		}
		if(down != null){
			down.setNullIn(indexDown);
			down = null;
		}
		if(lines!=null&&lines.size()>0){
			JSeparator s = lines.get(0);
			if(s!=null&&s.getParent() instanceof JPanel){
				removeFrom((((JPanel)s.getParent())));
			}
			lines = null;
		}
		
	}
}
