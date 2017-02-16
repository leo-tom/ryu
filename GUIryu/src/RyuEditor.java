import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Segment;
import javax.swing.text.StyleContext;

public class RyuEditor extends JTextPane{
	private static final long serialVersionUID = 1L;
	/*                                                                       
         ,--.                                                                                        
       ,--.'|              ___                                                                       
   ,--,:  : |            ,--.'|_                   ,--,               ,--,                           
,`--.'`|  ' :   ,---.    |  | :,'                ,'_ /|             ,--.'|         ,---,             
|   :  :  | |  '   ,'\   :  : ' :           .--. |  | :   .--.--.   |  |,      ,-+-. /  |  ,----._,. 
:   |   \ | : /   /   |.;__,'  /          ,'_ /| :  . |  /  /    '  `--'_     ,--.'|'   | /   /  ' / 
|   : '  '; |.   ; ,. :|  |   |           |  ' | |  . . |  :  /`./  ,' ,'|   |   |  ,"' ||   :     | 
'   ' ;.    ;'   | |: ::__,'| :           |  | ' |  | | |  :  ;_    '  | |   |   | /  | ||   | .\  . 
|   | | \   |'   | .; :  '  : |__         :  | | :  ' ;  \  \    `. |  | :   |   | |  | |.   ; ';  | 
'   : |  ; .'|   :    |  |  | '.'|        |  ; ' |  | '   `----.   \'  : |__ |   | |  |/ '   .   . | 
|   | '`--'   \   \  /   ;  :    ;        :  | : ;  ; |  /  /`--'  /|  | '.'||   | |--'   `---`-'| | 
'   : |        `----'    |  ,   /         '  :  `--'   \'--'.     / ;  :    ;|   |/       .'__/\_: | 
;   |.'                   ---`-'          :  ,      .-./  `--'---'  |  ,   / '---'        |   :    : 
'---'                                      `--`----'                 ---`-'                \   \  /  
                                                                                            `--`-'   
	 */
	RyuEditor me = this;
	StyleContext context = new StyleContext();
	DefaultStyledDocument doc = new DefaultStyledDocument(context);
	HashMap<String, Integer> SpecialTexts = new HashMap<String,Integer>();
	int lastOffset = -1;
	public RyuEditor() {
		super();
		this.setDocument(doc);
		try{
			readAllTexts();
		}catch (Exception e) {
			e.printStackTrace();
		}
		doc.addDocumentListener(new DocumentListener() {
			Segment txt = new Segment();
			@Override
			public void removeUpdate(DocumentEvent e) {
				//Updated(e);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				//Updated(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@SuppressWarnings("unused")
			private void Updated(DocumentEvent e){
				int off = e.getOffset();
				int len = e.getLength();
				lastOffset = off;
				try {
					doc.getText(off, len, txt);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
					return;
				}
				String str = txt.toString();
			}
		});
	}
	private void readAllTexts() throws IOException,FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(RYU.RYUSIMBOLS));
		String str;
		while((str=reader.readLine())!=null){
			SpecialTexts.put(str, Integer.valueOf(str.hashCode()));
		}
		reader.close();
	}
	public void InsertStr(String str){
		int offset = this.getCaretPosition();
		try {
			doc.insertString(offset, str, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}
