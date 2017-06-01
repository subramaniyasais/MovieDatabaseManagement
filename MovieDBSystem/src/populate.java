import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;


public class populate {
	public static void main(String args[]) { 
		populate example = new populate(); 
		example.run(); 
	}

	
	public void run() { 
		Connection con = null; 
		ResultSet result = null; 
		try { 
			con = openConnection(); 
			
			
			//result = searchAllTuples(con); 
			//showMetaDataOfResultSet(result); 
			//showResultSet(result); 
			
			publish_Movie_Actors_Table(con); 
			publish_Movie_Countries(con);
			publish_Movie_Directors_Table(con);
			publish_Movie_Genres_Table(con);
			publish_Movie_Tags_Table(con);
			publish_Movie_Table(con);
			publish_Tags_Table(con);
			publish_User_TaggedMovie_Table(con);
			
			
		} catch (SQLException e) { 
			System.err.println("Errors occurs when communicating with the database server: " + e.getMessage()); 
		} catch (ClassNotFoundException e) { 
			System.err.println("Cannot find the database driver"); 
		} finally { 
			// Never forget to close database connection 
			closeConnection(con); 
		}
	}
	
	private Connection openConnection() throws SQLException, ClassNotFoundException { 
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
		return DriverManager.getConnection(dbURL, userName, password); 
	} 
	
	private void closeConnection(Connection con) { 
		try { 
			con.close(); 
		} catch (SQLException e) { 
			System.err.println("Cannot close connection: " + e.getMessage()); 
		} 
	} 
	
	
	private ResultSet searchAllTuples(Connection con) throws SQLException { 
		Statement stmt = con.createStatement(); 
		return stmt.executeQuery("SELECT * FROM MOVIE_COUNTRIES"); 
	} 
	
	private void showMetaDataOfResultSet(ResultSet result) throws SQLException { 
		ResultSetMetaData meta = result.getMetaData(); 
		for (int col = 1; col <= meta.getColumnCount(); col++) { 
			System.out.println("Column: " + meta.getColumnName(col) + "\t, Type: " + meta.getColumnTypeName(col)); 
		} 
	} 
	
	private void showResultSet(ResultSet result) throws SQLException { 
		ResultSetMetaData meta = result.getMetaData(); 
		int tupleCount = 1; 
		while (result.next()) {
			System.out.print("Tuple " + tupleCount++ + " : "); 
			for (int col = 1; col <= meta.getColumnCount(); col++) { 
				System.out.print("\"" + result.getString(col) + "\","); 
			}
			System.out.println(); 
		} 
	} 
	

	
	private void publish_Movie_Actors_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_ACTORS VALUES(?,?,?,?)"); 
		System.out.println("Deleting previous tuples ..."); 
		Statement stmt = con.createStatement(); 
		stmt.executeUpdate("delete from MOVIE_ACTORS"); 

		System.out.println("Inserting Data for Movie Actors..."); 
		try {
			File file = new File("data/movie_actors.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			boolean firstline = true;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("	");
				int rating = Integer.parseInt(details[details.length-1]);
				String actor_name = "";
				for(int i=2 ; i <= (details.length)-2 ; i++) {
					actor_name+=details[i]+ " ";
				}
				actor_name = actor_name.substring(0, actor_name.length()-1);
				
				stmt1.setString(1, details[0]); 
				stmt1.setString(2, details[1]); 
				stmt1.setString(3, actor_name); 
				stmt1.setInt(4, rating); 
				stmt1.executeUpdate(); 
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close(); 
		System.out.println("Data Populated!"); 
	}
	
	private void publish_Movie_Countries(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_COUNTRIES VALUES(?,?)"); 
		System.out.println("Deleting previous tuples ..."); 
		Statement stmt = con.createStatement(); 
		stmt.executeUpdate("delete from MOVIE_COUNTRIES"); 

		System.out.println("Inserting Data for Countries..."); 
		try {
			File file = new File("data/movie_countries.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			boolean firstline = true;
			while ((line = bufferedReader.readLine()) != null) {
				if(firstline){
					firstline = false;
					continue;
				}
				String[] details = line.split("\t");
				if(details.length > 1) {

					stmt1.setString(1, details[0]); 
					String country = "";
					if(details.length==3)
						country = details[1]+" "+details[2];
					else 
						country = details[1];

					stmt1.setString(2, country);
					stmt1.executeUpdate(); 
					
					System.out.println(details[0]+", "+country);
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close(); 
		System.out.println("Data Populated!"); 
	}
	
	private void publish_Movie_Directors_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_DIRECTORS VALUES(?,?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from MOVIE_DIRECTORS"); 

		System.out.println("Inserting Data For Directors..."); 
		try {
			File file = new File("data/movie_directors.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			boolean firstline = true;
			while ((line = bufferedReader.readLine()) != null) {
				if(firstline){
					firstline = false;
					continue;
				}
				String[] details = line.split("	");
				if(details.length>2){
					String director_name = "";
					for(int i=2 ; i < (details.length) ; i++) {
						director_name+=details[i]+ " ";
					}
					director_name = director_name.substring(0, director_name.length()-1);
					
					stmt1.setString(1, details[0]); 
					stmt1.setString(2, details[1]); 
					stmt1.setString(3, director_name);
					stmt1.executeUpdate();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close(); 
		System.out.println("Data Populated!"); 
	}
	
	private void publish_Movie_Genres_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_GENRES VALUES(?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from MOVIE_GENRES"); 

		System.out.println("Inserting Data For Genres..."); 
		try {
			File file = new File("data/movie_genres.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("	");
				if(details.length>1){
					stmt1.setString(1, details[0]); 
					stmt1.setString(2, details[1]);
					stmt1.executeUpdate();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close();
		System.out.println("Data Populated!"); 
	}
	
	private void publish_Movie_Tags_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_TAGS VALUES(?,?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from MOVIE_TAGS"); 

		System.out.println("Inserting Data for Movie Tags..."); 
		try {
			File file = new File("data/movie_tags.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("	");
				if(details.length>2){
					int weight = Integer.parseInt(details[2]);
					stmt1.setString(1, details[0]); 
					stmt1.setString(2, details[1]);
					stmt1.setInt(3, weight);
					stmt1.executeUpdate();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close(); 

		System.out.println("Data Populated!"); 
	}
	
	private void publish_Movie_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIES VALUES(?,?,?,?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from MOVIES"); 

		System.out.println("Inserting Data for Movies ..."); 
		try {
			File file = new File("data/movies.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("\t");
				if(details.length>5){
					int year = Integer.parseInt(details[5]);
					int rtAudienceNumRating;
					try {
						rtAudienceNumRating = Integer.parseInt(details[18]);
					}catch(NumberFormatException e) { 
						rtAudienceNumRating = 0;
				    }
					stmt1.setString(1, details[0]); 
					stmt1.setString(2, details[1]);
					stmt1.setInt(3, year);
					stmt1.setString(4, details[17]);
					stmt1.setInt(5, rtAudienceNumRating);
					stmt1.executeUpdate();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close();
		System.out.println("Data Populated!"); 
	}
	
	private void publish_Tags_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO TAGS VALUES(?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from TAGS"); 

		System.out.println("Inserting Data for TAGS..."); 
		try {
			File file = new File("data/tags.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("\t");
				if(details.length>1){
					stmt1.setString(1, details[0]); 
					String value = "";
					if(details.length==3)
						value = details[1]+" "+details[2];
					else 
						value = details[1];

					stmt1.setString(2, value);
					stmt1.executeUpdate(); 
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close();
		System.out.println("Data Populated!"); 
	}
	
	private void publish_User_TaggedMovie_Table(Connection con) throws SQLException { 

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO USER_TAGGEDMOVIES VALUES(?,?,?,?,?,?,?,?,?)"); 
		Statement stmt = con.createStatement(); 
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from USER_TAGGEDMOVIES"); 

		System.out.println("Inserting Data ..."); 
		try {
			File file = new File("data/user_taggedmovies.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] details = line.split("\t");
				if(details.length>8){
					int date_day = Integer.parseInt(details[3]);
					int date_month = Integer.parseInt(details[4]);
					int date_year = Integer.parseInt(details[5]);
					int date_hour = Integer.parseInt(details[6]);
					int date_min = Integer.parseInt(details[7]);
					int date_sec = Integer.parseInt(details[8]);
					
					stmt1.setString(1, details[0]); 
					stmt1.setString(2, details[1]);
					stmt1.setString(3, details[2]); 
					stmt1.setInt(4, date_day);
					stmt1.setInt(5, date_month); 
					stmt1.setInt(6, date_year);
					stmt1.setInt(7, date_hour); 
					stmt1.setInt(8, date_min); 
					stmt1.setInt(9, date_sec);
					stmt1.executeUpdate();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stmt.close();
		stmt1.close();
		System.out.println(" Data Populated!"); 
	}
	
/*	
	private void publishData(Connection con) throws SQLException { 

		Statement stmt = con.createStatement(); 
		
		System.out.println("Deleting previous tuples ..."); 
		stmt.executeUpdate("delete from MOVIE_ACTORS"); 

		System.out.println("Inserting Data ..."); 
		try {
			File file = new File("data/movie_actors.dat");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			boolean firstline = true;
			while ((line = bufferedReader.readLine()) != null) {
				if(firstline){
					firstline = false;
					continue;
				}
				String[] details = line.split("	");
				int rating = Integer.parseInt(details[details.length-1]);
				String actor_name = "";
				for(int i=2 ; i <= (details.length)-2 ; i++) {
					actor_name+=details[i]+ " ";
				}
				actor_name = actor_name.substring(0, actor_name.length()-1);
				String temp="insert into MOVIE_ACTORS VALUES (\""+details[0]+"\", \""+details[1]+"\", \""+actor_name+"\", "+rating+")";
				System.out.println(temp);
				stmt.executeUpdate(temp); 
				
				//stringBuffer.append(line);
				//stringBuffer.append("\n");
			}
			fileReader.close();
			System.out.println("Contents of file:");
			System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stmt.close(); 
	 }*/
}

