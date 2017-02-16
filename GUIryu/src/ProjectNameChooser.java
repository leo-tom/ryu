import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ProjectNameChooser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel lblTypeProjectName;

	public static String show(String showingText){
		ProjectNameChooser dialog = null;
		try {
			dialog = new ProjectNameChooser();
			if(showingText!=null){
				dialog.lblTypeProjectName.setText(showingText);
			}
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dialog.textField.getText();
	}
	/**
	 * Create the dialog.
	 */
	private ProjectNameChooser() {
		setBounds(100, 100, 324, 159);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			lblTypeProjectName = new JLabel("Type project name");
			lblTypeProjectName.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblTypeProjectName, BorderLayout.NORTH);
		}
		{
			textField = new JTextField();
			contentPanel.add(textField, BorderLayout.CENTER);
			textField.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
