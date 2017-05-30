import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class HW3 extends JFrame{
	public static void main(String[] args) {
		try {
			JFrame frame = new MainClass();
			frame.setTitle("IMDB Movie Database Query System");
            frame.setVisible(true);
            frame.setSize(2000, 1200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
		}
		catch(IOException ex) {
			
		}
	}
}

class MainClass extends JFrame {
	int tabsLimit = 15;
	
	MainClass() throws IOException {
		
		JLabel genrelabel = new JLabel("Genres");
		Font f = genrelabel.getFont();
		genrelabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		
		JPanel genresTitle = new JPanel();
		genresTitle.setBackground(Color.decode("#99b3ff"));
		genresTitle.add(genrelabel);
		JScrollPane genresContent = new JScrollPane();
		JSplitPane genresPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, genresTitle, genresContent);
		genresPane.setDividerLocation(40);
		genresPane.setDividerSize(2);
		genresPane.setEnabled(false);
		
		JLabel yearlabel = new JLabel("Movie Year");
		yearlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel movieyearTitle = new JPanel();
		movieyearTitle.setBackground(Color.decode("#99b3ff"));
		movieyearTitle.add(yearlabel);
		JPanel t1= new JPanel();
		JScrollPane MovieYearContent = new JScrollPane();
		t1.add(MovieYearContent);
		JSplitPane movieYearPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, movieyearTitle, t1);
		movieYearPane.setDividerLocation(40);
		movieYearPane.setEnabled(false);
		
		JSplitPane firstColumnPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, genresPane, movieYearPane);
		firstColumnPane.setDividerLocation(500);
		//firstColumnPane.setEnabled(false);
		
		
		JLabel countrylabel = new JLabel("Country");
		countrylabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel countryTitle = new JPanel();
		countryTitle.setBackground(Color.decode("#99b3ff"));
		countryTitle.add(countrylabel);
		JScrollPane countryContent = new JScrollPane();
		JSplitPane countryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, countryTitle, countryContent);
		countryPane.setDividerLocation(40);
		countryPane.setEnabled(false);
		
		JLabel castlabel = new JLabel("Cast");
		castlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel castTitle = new JPanel();
		castTitle.setBackground(Color.decode("#99b3ff"));
		castTitle.add(castlabel);
		JScrollPane castContent = new JScrollPane();
		JSplitPane castPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, castTitle, castContent);
		castPane.setDividerLocation(40);
		castPane.setEnabled(false);
		
		JLabel idslabel = new JLabel("Tag Ids and Values");
		idslabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel idsTitle = new JPanel();
		idsTitle.setBackground(Color.decode("#99b3ff"));
		idsTitle.add(idslabel);
		JScrollPane idsContent = new JScrollPane();
		JSplitPane idsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, idsTitle, idsContent);
		idsPane.setDividerLocation(40);
		idsPane.setEnabled(false);
		
		JPanel movieResult = new JPanel();
		JPanel queryPane = new JPanel();
		JPanel userResult = new JPanel();
		
		JPanel movieAttrFields = new JPanel();
		
		
		JSplitPane attr1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstColumnPane, countryPane);
		attr1.setResizeWeight(.5d);
		attr1.setEnabled(false);
		JSplitPane attr2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, castPane, idsPane);
		attr2.setResizeWeight(.5d);
		attr2.setEnabled(false);
		JSplitPane movieAttribute = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, attr1, attr2);
		movieAttribute.setResizeWeight(.5d);
		movieAttribute.setEnabled(false);
		
		JPanel movieAttrTitle = new JPanel();
		JLabel mtitlelabel = new JLabel("Movie Attributes");
		mtitlelabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		movieAttrTitle.add(mtitlelabel);
		JSplitPane attrValues_sub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, movieAttrTitle, movieAttribute);
		attrValues_sub.setResizeWeight(.01d);
		attrValues_sub.setEnabled(false);
		
		JSplitPane attrValues = new JSplitPane(JSplitPane.VERTICAL_SPLIT, attrValues_sub, movieAttrFields);
		attrValues.setResizeWeight(.9d);
		attrValues.setEnabled(false);
		
		JSplitPane leftView = new JSplitPane(JSplitPane.VERTICAL_SPLIT, attrValues, queryPane);
		leftView.setResizeWeight(.7d);
		leftView.setEnabled(false);
		
		JSplitPane rightView = new JSplitPane(JSplitPane.VERTICAL_SPLIT, movieResult, userResult);
		rightView.setResizeWeight(.7d);
		rightView.setEnabled(false);
		
		JSplitPane HomeScreen = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftView, rightView);
		HomeScreen.setResizeWeight(.6d);
		HomeScreen.setEnabled(false);
		
		getContentPane().add(HomeScreen);
	}
}
