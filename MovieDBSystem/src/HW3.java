import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
            frame.setSize(1800, 1200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
		}
		catch(IOException ex) {
			
		}
	}
}

class MainClass extends JFrame {
	String SUBHEADINGCOLOR = "#99b3ff";
	JList<String> genreList;
	JList<String> contryList;
	JList<String> castList;
	JList<String> directorList;
	JList<String> tagList;
	JList<String> movResultList;
	JList<String> usrResultList;
	JButton executeMovieQueryButton;
	JButton executeUserQueryButton;
	
	JTextArea queryarea;
	ArrayList<String> selectedGenreList = new ArrayList();
	ArrayList<String> selectedCountryList =new ArrayList();
	ArrayList<String> selectedCastList =new ArrayList();
	ArrayList<String> selectedDirectorList =new ArrayList();
	ArrayList<String> selectedTagList =new ArrayList();
	ArrayList<String> selectedMovieResultList =new ArrayList();
	
	JComboBox fromYear;
	JComboBox toYear;
	JComboBox ANDOR;
	JComboBox weightRel;
	JTextField weightVal;
	
	int tabsLimit = 15;
	static PreparedStatement ps = null;
    static Connection con = null;
    static ResultSet rs = null, RetrivedGenreMid = null;
    DefaultListModel addGenreList = new DefaultListModel();
    DefaultListModel addCountryList = new DefaultListModel();
    DefaultListModel addCastList = new DefaultListModel();
    DefaultListModel addDirectorList = new DefaultListModel();
    DefaultListModel addTagList = new DefaultListModel();
    DefaultListModel addMovResList = new DefaultListModel();
    DefaultListModel addUsrResList = new DefaultListModel();
    
    DefaultListModel yearList = new DefaultListModel();
	
	public static void connect() /*throws Exception*/
    {
        try
        {
        	// Load the Oracle database driver 
    		DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 
    		/* 
    		Here is the information needed when connecting to a database 
    		server. These values are now hard-coded in the program. In 
    		general, they should be stored in some configuration file and 
    		read at run time. 
    		*/ 
    		String host = "localhost"; 
    		String port = "1521"; 
    		String dbName = "xe"; 
    		String userName = "scott"; 
    		String password = "tiger"; 
    		// Construct the JDBC URL 
    		String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
    		con = DriverManager.getConnection(dbURL, userName, password); 
        	
            if(con != null)
            {
                System.out.println("Connected to Database");
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
	
	private void populateGenre() {
		
		String GetGenre = "SELECT DISTINCT MG.Genre FROM MOVIE_GENRES MG\n";
		queryarea.setText(GetGenre);
		try {
			ResultSet rs11 = null;
			ps=con.prepareStatement(GetGenre);
			rs11 = ps.executeQuery(GetGenre);

			while(rs11.next())
			{
				if(!addGenreList.contains(rs11.getString("Genre")))
				{
					addGenreList.addElement(rs11.getString("Genre"));
				}
			}
			ps.close();
			rs11.close();
		} catch(Exception ex) {
			System.out.println(ex);
		}
		genreList.setModel(addGenreList);
		
		String GetMinMaxYear = "Select MIN(M.MYEAR) AS MINY, MAX(M.MYEAR) AS MAXY FROM MOVIES M";
		try {
			ResultSet rs11 = null;
			ps=con.prepareStatement(GetMinMaxYear);
			rs11 = ps.executeQuery(GetMinMaxYear);

			while(rs11.next())
			{
				int minY = Integer.parseInt(rs11.getString("MINY"));
				int maxY = Integer.parseInt(rs11.getString("MAXY"));
				for(int i=minY; i<=maxY; i++) {
					fromYear.addItem(i);
					toYear.addItem(i);
				}
			}
			ps.close();
			rs11.close();
		} catch(Exception ex) {
			System.out.println(ex);
		}
		toYear.setSelectedIndex(toYear.getItemCount()-1);
		
		MouseListener mouseListener = new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 1)
				{
					selectedGenreList = (ArrayList<String>) genreList.getSelectedValuesList();
					//System.out.println(selectedGenreList);
					if(selectedGenreList.size()!=0)
			        {
						addTagList.clear();
						addCountryList.clear();
						addCastList.clear();
						String FinalSubQuery = "SELECT MG.MOVIEID FROM Movie_Genres MG";
			            FinalSubQuery = (selectedGenreList.size()>0)?FinalSubQuery+" WHERE":FinalSubQuery;
						for(int i=0;i<selectedGenreList.size();i++)
			            {
							FinalSubQuery += " MG.GENRE = '"+selectedGenreList.get(i)+"' "+ANDOR.getSelectedItem()+"";
			            }
						if(selectedGenreList.size()>0){ 
							FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
						}
						
						FinalSubQuery = "SELECT DISTINCT M.MOVIEID FROM MOVIES M WHERE M.MOVIEID IN \n("+FinalSubQuery+") AND MYEAR>="+fromYear.getSelectedItem()+" AND MYEAR<="+toYear.getSelectedItem()+"";
						
						FinalSubQuery = " SELECT DISTINCT MC.Country FROM Movie_Countries MC WHERE MC.MOVIEID IN \n("+FinalSubQuery+")";
						queryarea.setText(FinalSubQuery);
						try {
							ResultSet rs12 = null;
							ps=con.prepareStatement(FinalSubQuery);
							rs12 = ps.executeQuery(FinalSubQuery);

							while(rs12.next())
							{
								if(!addCountryList.contains(rs12.getString("Country")))
								{
									addCountryList.addElement(rs12.getString("Country"));
								}
							}
							ps.close();
							rs12.close();
						} catch(Exception ex) {
							System.out.println(ex);
						}
						contryList.setModel(addCountryList);
			        }
				}
			}
		};
		genreList.addMouseListener(mouseListener);
		
		ItemListener itemListener = new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedGenreList = (ArrayList<String>) genreList.getSelectedValuesList();
				//System.out.println(selectedGenreList);
				if(selectedGenreList.size()!=0)
		        {
					addTagList.clear();
					addCountryList.clear();
					addCastList.clear();
					
					
					String FinalSubQuery = "SELECT MG.MOVIEID FROM Movie_Genres MG";
		            FinalSubQuery = (selectedGenreList.size()>0)?FinalSubQuery+" WHERE":FinalSubQuery;
					for(int i=0;i<selectedGenreList.size();i++)
		            {
						FinalSubQuery += " MG.GENRE = '"+selectedGenreList.get(i)+"' "+ANDOR.getSelectedItem()+"";
		            }
					if(selectedGenreList.size()>0){ 
						FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
					}
					
					FinalSubQuery = "SELECT DISTINCT M.MOVIEID FROM MOVIES M WHERE M.MOVIEID IN \n("+FinalSubQuery+") AND MYEAR>="+fromYear.getSelectedItem()+" AND MYEAR<="+toYear.getSelectedItem()+"";
					
					FinalSubQuery = " SELECT DISTINCT MC.Country FROM Movie_Countries MC WHERE MC.MOVIEID IN \n("+FinalSubQuery+")";
					queryarea.setText(FinalSubQuery);
					try {
						ResultSet rs12 = null;
						ps=con.prepareStatement(FinalSubQuery);
						rs12 = ps.executeQuery(FinalSubQuery);

						while(rs12.next())
						{
							if(!addCountryList.contains(rs12.getString("Country")))
							{
								addCountryList.addElement(rs12.getString("Country"));
							}
						}
						ps.close();
						rs12.close();
					} catch(Exception ex) {
						System.out.println(ex);
					}
					contryList.setModel(addCountryList);
		        }
			}
		};
		fromYear.addItemListener(itemListener);
		toYear.addItemListener(itemListener);
		
	}
	
	private void populateCast() {
		MouseListener mouseListenerCountry = new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 1)
				{
					selectedCountryList = (ArrayList<String>) contryList.getSelectedValuesList();
					//System.out.println(selectedGenreList);
					if(selectedCountryList.size()!=0)
			        {
						addTagList.clear();
						addCastList.clear();
			            String FinalSubQuery = "SELECT MG.MOVIEID FROM Movie_Genres MG";
			            FinalSubQuery = (selectedGenreList.size()>0)?FinalSubQuery+" WHERE":FinalSubQuery;
						for(int i=0;i<selectedGenreList.size();i++)
			            {
							FinalSubQuery += " MG.GENRE = '"+selectedGenreList.get(i)+"' "+ANDOR.getSelectedItem()+"";
			            }
						if(selectedCountryList.size()>0){ 
							FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
						}
						if(selectedCountryList.size()>0){ 
			            	FinalSubQuery += "\nINTERSECT\nSELECT MC.MOVIEID FROM MOVIE_COUNTRIES MC WHERE";
			            }
						for(int i=0;i<selectedCountryList.size();i++)
			            {
							FinalSubQuery += " MC.COUNTRY = '"+selectedCountryList.get(i)+"' "+ANDOR.getSelectedItem()+"";
			            }
						if(selectedCountryList.size()>0){ 
							FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
						}
						FinalSubQuery = "SELECT DISTINCT M.MOVIEID FROM MOVIES M WHERE M.MOVIEID IN \n("+FinalSubQuery+") AND MYEAR>="+fromYear.getSelectedItem()+" AND MYEAR<="+toYear.getSelectedItem()+"";
						
						String ActorsSubQuery = "SELECT DISTINCT MA.ACTORNAME FROM MOVIE_ACTORS MA WHERE MA.MOVIEID IN \n("+FinalSubQuery+") ORDER BY MA.ACTORNAME";
						String DirectorsSubQuery = "SELECT DISTINCT MD.DIRECTORNAME FROM MOVIE_DIRECTORS MD WHERE MD.MOVIEID IN \n("+FinalSubQuery+")";
						
						queryarea.setText(ActorsSubQuery);
						try {
							ResultSet rs12 = null;
							ps=con.prepareStatement(ActorsSubQuery);
							rs12 = ps.executeQuery(ActorsSubQuery);

							while(rs12.next())
							{
								if(!addCastList.contains(rs12.getString("ACTORNAME")))
								{
									addCastList.addElement(rs12.getString("ACTORNAME"));
								}
							}
							ps.close();
							rs12.close();
						} catch(Exception ex) {
							System.out.println(ex);
						}
						castList.setModel(addCastList);
						
						addDirectorList.clear();
						try {
							ResultSet rs12 = null;
							ps=con.prepareStatement(DirectorsSubQuery);
							rs12 = ps.executeQuery(DirectorsSubQuery);

							while(rs12.next())
							{
								if(!addDirectorList.contains(rs12.getString("DIRECTORNAME")))
								{
									addDirectorList.addElement(rs12.getString("DIRECTORNAME"));
								}
							}
							ps.close();
							rs12.close();
						} catch(Exception ex) {
							System.out.println(ex);
						}
						directorList.setModel(addDirectorList);
						
			        }
				}
			}
		};
		contryList.addMouseListener(mouseListenerCountry);

	}

	private void populateTagIDs() {
		MouseListener mouseListenerTags = new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 1)
				{
					if(castList.getSelectedValuesList().isEmpty()) {
						selectedCastList = new ArrayList();
					} else {
						selectedCastList = (ArrayList<String>) castList.getSelectedValuesList();
					}
					if(directorList.getSelectedValuesList().isEmpty()) {
						selectedDirectorList = new ArrayList();
					} else {
						selectedDirectorList = (ArrayList<String>) directorList.getSelectedValuesList();
					}
					
					addTagList.clear();
		            String FinalSubQuery = "SELECT MG.MOVIEID FROM Movie_Genres MG";
		            FinalSubQuery = (selectedGenreList.size()>0)?FinalSubQuery+" WHERE":FinalSubQuery;
					for(int i=0;i<selectedGenreList.size();i++)
		            {
						FinalSubQuery += " MG.GENRE = '"+selectedGenreList.get(i)+"' "+ANDOR.getSelectedItem()+"";
		            }
					if(selectedCountryList.size()>0){ 
						FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
					}
					if(selectedCountryList.size()>0){ 
		            	FinalSubQuery += "\nINTERSECT\nSELECT MC.MOVIEID FROM MOVIE_COUNTRIES MC WHERE";
		            }
					for(int i=0;i<selectedCountryList.size();i++)
		            {
						FinalSubQuery += " MC.COUNTRY = '"+selectedCountryList.get(i)+"' "+ANDOR.getSelectedItem()+"";
		            }
					if(selectedCountryList.size()>0){ 
						FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
					}
					if(selectedCastList.size()>0){ 
		            	FinalSubQuery += "\nINTERSECT\nSELECT MA.MOVIEID FROM MOVIE_ACTORS MA WHERE";
		            }
					for(int i=0;i<selectedCastList.size();i++)
		            {
						FinalSubQuery += " MA.ACTORNAME = '"+selectedCastList.get(i)+"' "+ANDOR.getSelectedItem()+"";
		            }
					if(selectedCastList.size()>0){ 
						FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
					}
					if(selectedDirectorList.size()>0){ 
		            	FinalSubQuery += "\nINTERSECT\nSELECT MD.MOVIEID FROM MOVIE_DIRECTORS MD WHERE";
		            }
					for(int i=0;i<selectedDirectorList.size();i++)
		            {
						FinalSubQuery += " MD.DIRECTORNAME = '"+selectedDirectorList.get(i)+"' "+ANDOR.getSelectedItem()+"";
		            }
					if(selectedDirectorList.size()>0){ 
						FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
					}
					FinalSubQuery = "SELECT DISTINCT M.MOVIEID FROM MOVIES M WHERE M.MOVIEID IN \n("+FinalSubQuery+") AND MYEAR>="+fromYear.getSelectedItem()+" AND MYEAR<="+toYear.getSelectedItem()+"";
					
					String TagsSubQuery = "SELECT DISTINCT T.ID, T.VALUE FROM TAGS T, MOVIE_TAGS MT WHERE T.ID=MT.TAGID AND MT.MOVIEID IN \n("+FinalSubQuery+")";
					
					queryarea.setText(TagsSubQuery);
					try {
						ResultSet rs12 = null;
						ps=con.prepareStatement(TagsSubQuery);
						rs12 = ps.executeQuery(TagsSubQuery);

						while(rs12.next())
						{
							if(!addTagList.contains(rs12.getString("ID")+"   "+rs12.getString("VALUE")))
							{
								addTagList.addElement(rs12.getString("ID")+"   "+rs12.getString("VALUE"));
							}
						}
						ps.close();
						rs12.close();
					} catch(Exception ex) {
						System.out.println(ex);
					}
					tagList.setModel(addTagList);
				
				}
			}
		};
		contryList.addMouseListener(mouseListenerTags);
		castList.addMouseListener(mouseListenerTags);
		directorList.addMouseListener(mouseListenerTags);

	}
	
	private void executeMovieQueryButtonActionPerformed(ActionEvent e){
		queryarea.setText("");
		addMovResList.clear();
		selectedMovieResultList.clear();
		if(castList.getSelectedValuesList().isEmpty()) {
			selectedCastList = new ArrayList();
		} else {
			selectedCastList = (ArrayList<String>) castList.getSelectedValuesList();
		}
		if(directorList.getSelectedValuesList().isEmpty()) {
			selectedDirectorList = new ArrayList();
		} else {
			selectedDirectorList = (ArrayList<String>) directorList.getSelectedValuesList();
		}
		
		if(tagList.getSelectedValuesList().isEmpty()) {
			selectedTagList = new ArrayList();
		} else {
			selectedTagList = (ArrayList<String>) tagList.getSelectedValuesList();
		}
		
        String FinalSubQuery = "SELECT MG.MOVIEID FROM Movie_Genres MG";
        FinalSubQuery = (selectedGenreList.size()>0)?FinalSubQuery+" WHERE":FinalSubQuery;
		for(int i=0;i<selectedGenreList.size();i++)
        {
			FinalSubQuery += " MG.GENRE = '"+selectedGenreList.get(i)+"' "+ANDOR.getSelectedItem()+"";
        }
		if(selectedCountryList.size()>0){ 
			FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
		}
		if(selectedCountryList.size()>0){ 
        	FinalSubQuery += "\nINTERSECT\nSELECT MC.MOVIEID FROM MOVIE_COUNTRIES MC WHERE";
        }
		for(int i=0;i<selectedCountryList.size();i++)
        {
			FinalSubQuery += " MC.COUNTRY = '"+selectedCountryList.get(i)+"' "+ANDOR.getSelectedItem()+"";
        }
		if(selectedCountryList.size()>0){ 
			FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
		}
		if(selectedCastList.size()>0){ 
        	FinalSubQuery += "\nINTERSECT\nSELECT MA.MOVIEID FROM MOVIE_ACTORS MA WHERE";
        }
		for(int i=0;i<selectedCastList.size();i++)
        {
			FinalSubQuery += " MA.ACTORNAME = '"+selectedCastList.get(i)+"' "+ANDOR.getSelectedItem()+"";
        }
		if(selectedCastList.size()>0){ 
			FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
		}
		//Director
		if(selectedDirectorList.size()>0){ 
        	FinalSubQuery += "\nINTERSECT\nSELECT MD.MOVIEID FROM MOVIE_DIRECTORS MD WHERE";
        }
		for(int i=0;i<selectedDirectorList.size();i++)
        {
			FinalSubQuery += " MD.DIRECTORNAME = '"+selectedDirectorList.get(i)+"' "+ANDOR.getSelectedItem()+"";
        }
		if(selectedDirectorList.size()>0){ 
			FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
		}
		//Tag
		if(selectedTagList.size()>0){ 
        	FinalSubQuery += "\nINTERSECT\nSELECT MT.MOVIEID FROM TAGS T, MOVIE_TAGS MT WHERE MT.TAGID=T.ID AND";
        }
		for(int i=0;i<selectedTagList.size();i++)
        {
			String[] tagsR = selectedTagList.get(i).split("   ");
			FinalSubQuery += " T.ID = '"+tagsR[0]+"' "+ANDOR.getSelectedItem()+"";
        }
		FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
//		System.out.println( "AND MT.TAGWEIGHT"+weightRel.getSelectedItem()+""+weightVal.getText());
		if(!weightVal.getText().isEmpty()) {
			FinalSubQuery += " AND MT.TAGWEIGHT"+weightRel.getSelectedItem()+""+weightVal.getText();
		} else {
			FinalSubQuery = FinalSubQuery.substring(0, FinalSubQuery.length()-3);
		}
		
		FinalSubQuery = "SELECT DISTINCT M.MOVIEID FROM MOVIES M WHERE M.MOVIEID IN \n("+FinalSubQuery+") AND MYEAR>="+fromYear.getSelectedItem()+" AND MYEAR<="+toYear.getSelectedItem()+"";
		
		String MovieResultSubQuery = "SELECT M.MOVIEID, M.TITLE, MG.GENRE, M.MYEAR, MC.COUNTRY, M.RTAUDIENCERATING, M.RTAUDIENCENUMRATING FROM MOVIES M, MOVIE_GENRES MG, MOVIE_COUNTRIES MC WHERE M.MOVIEID=MG.MOVIEID AND M.MOVIEID=MC.MOVIEID AND M.MOVIEID IN \n("+FinalSubQuery+")";
		
		queryarea.setText(MovieResultSubQuery);
		try {
			ResultSet rs12 = null;
			ps=con.prepareStatement(MovieResultSubQuery);
			rs12 = ps.executeQuery(MovieResultSubQuery);

			while(rs12.next())
			{
				if(!addMovResList.contains(rs12.getString("MOVIEID")+"   "+rs12.getString("TITLE")+"   "+rs12.getString("GENRE")+"   "+rs12.getString("MYEAR")+"   "+rs12.getString("COUNTRY")+"   "+rs12.getString("RTAUDIENCERATING")+"   "+rs12.getString("RTAUDIENCENUMRATING")))
				{
					addMovResList.addElement(rs12.getString("MOVIEID")+"   "+rs12.getString("TITLE")+"   "+rs12.getString("GENRE")+"   "+rs12.getString("MYEAR")+"   "+rs12.getString("COUNTRY")+"   "+rs12.getString("RTAUDIENCERATING")+"   "+rs12.getString("RTAUDIENCENUMRATING"));
					selectedMovieResultList.add(rs12.getString("MOVIEID"));
				}
			}
			ps.close();
			rs12.close();
		} catch(Exception ex) {
			System.out.println(ex);
		}
		movResultList.setModel(addMovResList);
	
	}
	
	private void executeUserQueryButtonActionPerformed(ActionEvent e){
		
		addUsrResList.clear();
		selectedMovieResultList.clear();
		if(movResultList.getSelectedValuesList().isEmpty()) {
			selectedMovieResultList = new ArrayList();
		} else {
			selectedMovieResultList = (ArrayList<String>) movResultList.getSelectedValuesList();
		}
		
		String UserResultSubQuery = "SELECT DISTINCT U.USERID FROM USER_TAGGEDMOVIES U ";
		
		if(selectedMovieResultList.size()>0) {
			UserResultSubQuery += "WHERE ";
		}
		for(int i=0;i<selectedMovieResultList.size();i++)
        {
			String[] temp = selectedMovieResultList.get(i).split("   ");
			UserResultSubQuery += " U.MOVIEID = '"+temp[0]+"' "+ANDOR.getSelectedItem()+"";
        }

		UserResultSubQuery = UserResultSubQuery.substring(0, UserResultSubQuery.length()-3);
		queryarea.setText(UserResultSubQuery);
		try {
			ResultSet rs12 = null;
			ps=con.prepareStatement(UserResultSubQuery);
			rs12 = ps.executeQuery(UserResultSubQuery);

			while(rs12.next())
			{
				if(!addUsrResList.contains(rs12.getString("USERID")))
				{
					addUsrResList.addElement(rs12.getString("USERID"));
				}
			}
			ps.close();
			rs12.close();
		} catch(Exception ex) {
			System.out.println(ex);
		}
		usrResultList.setModel(addUsrResList);
	}


	MainClass() throws IOException {
		
		genreList = new JList<>();
		JLabel genrelabel = new JLabel("Genres");
		Font f = genrelabel.getFont();
		genrelabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		
		JPanel genresTitle = new JPanel();
		genresTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		genresTitle.add(genrelabel);
		JScrollPane genresContent = new JScrollPane();
		JSplitPane genresPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, genresTitle, genresContent);
		genresPane.setDividerLocation(40);
		genresPane.setDividerSize(2);
		genresPane.setEnabled(false);
		genresContent.setViewportView(genreList);
		
		
		fromYear = new JComboBox();
		toYear = new JComboBox();
		JLabel yearlabel = new JLabel("Movie Year");
		yearlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel movieyearTitle = new JPanel();
		movieyearTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		movieyearTitle.add(yearlabel);
		JPanel MovieYearContent= new JPanel();
		MovieYearContent.add(new JLabel("From Year: "));
		MovieYearContent.add(fromYear);
		MovieYearContent.add(new JLabel("To Year: "));
		MovieYearContent.add(toYear);
		JSplitPane movieYearPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, movieyearTitle, MovieYearContent);
		movieYearPane.setDividerLocation(40);
		movieYearPane.setEnabled(false);
		
		JSplitPane firstColumnPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, genresPane, movieYearPane);
		firstColumnPane.setDividerLocation(500);
		firstColumnPane.setEnabled(false);
		

		contryList = new JList<>();
		JLabel countrylabel = new JLabel("Country");
		countrylabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel countryTitle = new JPanel();
		countryTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		countryTitle.add(countrylabel);
		JScrollPane countryContent = new JScrollPane();
		JSplitPane countryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, countryTitle, countryContent);
		countryPane.setDividerLocation(40);
		countryPane.setEnabled(false);
		countryContent.setViewportView(contryList);
		
		castList = new JList<>();
		JLabel castlabel = new JLabel("Cast");
		castlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel castTitle = new JPanel();
		castTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		castTitle.add(castlabel);
		JScrollPane castContent = new JScrollPane();
		JSplitPane castPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, castTitle, castContent);
		castPane.setDividerLocation(40);
		castPane.setEnabled(false);
		castContent.setViewportView(castList);
		
		directorList = new JList<>();
		JLabel directorlabel = new JLabel("Director");
		directorlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel directorTitle = new JPanel();
		directorTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		directorTitle.add(directorlabel);
		JScrollPane directorContent = new JScrollPane();
		JSplitPane directorPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, directorTitle, directorContent);
		directorPane.setDividerLocation(40);
		directorPane.setEnabled(false);
		directorContent.setViewportView(directorList);
		
		JSplitPane thirdColumnPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, castPane, directorPane);
		thirdColumnPane.setDividerLocation(500);
		thirdColumnPane.setEnabled(false);
		
		
		tagList = new JList<>();
		JLabel idslabel = new JLabel("Tag Ids and Values");
		idslabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel idsTitle = new JPanel();
		idsTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		idsTitle.add(idslabel);
		JScrollPane idsContent = new JScrollPane();
		JSplitPane idsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, idsTitle, idsContent);
		idsPane.setDividerLocation(40);
		idsPane.setEnabled(false);
		idsContent.setViewportView(tagList);
		
		weightRel = new JComboBox();
		weightRel.addItem("=");
		weightRel.addItem("<");
		weightRel.addItem(">");
		weightVal = new JTextField(10);
		JLabel weightlabel = new JLabel("Tag Weight");
		weightlabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel weightTitle = new JPanel();
		weightTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		weightTitle.add(weightlabel);
		JPanel weightContent = new JPanel();
		//weightContent.add(new JLabel("Relation: "));
		weightContent.add(weightRel);
		weightContent.add(new JLabel("Value: "));
		weightContent.add(weightVal);
		JSplitPane weightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, weightTitle, weightContent);
		weightPane.setDividerLocation(40);
		weightPane.setEnabled(false);
		
		JSplitPane fourthColumnPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, idsPane, weightPane);
		fourthColumnPane.setDividerLocation(500);
		fourthColumnPane.setEnabled(false);
		
		executeMovieQueryButton = new JButton();
		executeMovieQueryButton.setText("Execute Movie Query");
		executeMovieQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	executeMovieQueryButtonActionPerformed(evt);
            }
        });
		
		executeUserQueryButton = new JButton();
		executeUserQueryButton.setText("Execute User Query");
		executeUserQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	executeUserQueryButtonActionPerformed(evt);
            }
        });
		

		movResultList = new JList<>();
		JLabel movReslabel = new JLabel("Movie Result");
		movReslabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel MovResTitle = new JPanel();
		MovResTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		MovResTitle.add(movReslabel);
		JScrollPane movieResult = new JScrollPane();
		JSplitPane movResultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, MovResTitle, movieResult);
		movResultPane.setDividerLocation(40);
		movResultPane.setEnabled(false);
		movieResult.setViewportView(movResultList);
		
		
		
		JPanel queryPane = new JPanel();
		queryarea = new JTextArea(10,100);
		queryPane.add(new JScrollPane(queryarea));
		queryPane.add(executeMovieQueryButton);
		queryPane.add(executeUserQueryButton);
		
		
		usrResultList = new JList<>();
		JLabel usrReslabel = new JLabel("User Result");
		usrReslabel.setFont(new Font(f.getFontName(), Font.PLAIN, 20));
		JPanel UsrResTitle = new JPanel();
		UsrResTitle.setBackground(Color.decode(SUBHEADINGCOLOR));
		UsrResTitle.add(usrReslabel);
		JScrollPane userResult = new JScrollPane();
		JSplitPane usrResultPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, UsrResTitle, userResult);
		usrResultPane.setDividerLocation(40);
		usrResultPane.setEnabled(false);
		userResult.setViewportView(usrResultList);
		
		
		JPanel movieAttrFields = new JPanel();
		ANDOR = new JComboBox();
		ANDOR.addItem("OR");
		ANDOR.addItem("AND");
		movieAttrFields.add(new JLabel("Search Between Attribute Values: "));
		movieAttrFields.add(ANDOR);
		
		JSplitPane attr1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstColumnPane, countryPane);
		attr1.setResizeWeight(.4d);
		attr1.setEnabled(false);
		JSplitPane attr2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, thirdColumnPane, fourthColumnPane);
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
		
		JSplitPane rightView = new JSplitPane(JSplitPane.VERTICAL_SPLIT, movResultPane, usrResultPane);
		rightView.setResizeWeight(.7d);
		rightView.setEnabled(false);
		
		JSplitPane HomeScreen = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftView, rightView);
		HomeScreen.setResizeWeight(.1d);
		HomeScreen.setEnabled(false);
		
		getContentPane().add(HomeScreen);
		connect();
		
		populateGenre();
		populateCast();
		populateTagIDs();
	}
}
