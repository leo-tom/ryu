import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LookAndFeelListener implements ActionListener{
	LookAndFeelInfo info;
	SettingReader settingreader;

	public LookAndFeelListener(LookAndFeelInfo info) {
		this.info = info;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		try{
			if(settingreader==null) settingreader = new SettingReader();
			settingreader.ReadSetting();
			settingreader.SetValue("DEFAULTLOOKANDFEEL", info.getClassName());
			settingreader.WriteMySelf();
			UIManager.setLookAndFeel(info.getClassName());
		}catch(Exception er){
			er.printStackTrace();
		}
	}
}
