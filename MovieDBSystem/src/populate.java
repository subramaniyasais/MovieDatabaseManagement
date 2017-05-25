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
			//publishData(con);
			publishDataUsingPreparedStatement(con); 
			result = searchAllTuples(con); 
			showMetaDataOfResultSet(result); 
			showResultSet(result); 
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
		return stmt.executeQuery("SELECT * FROM MOVIE_ACTORS"); 
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
	 }
	
	private void publishDataUsingPreparedStatement(Connection con) throws SQLException { 
		

		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO MOVIE_ACTORS VALUES(?,?,?,?)"); 
		System.out.println("Deleting previous tuples ..."); 
		//stmt1.executeUpdate("delete from MOVIE_ACTORS"); 

		System.out.println("Inserting Data ..."); 
		try {
			File file = new File("data/movie_actors.dat");
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
		
		stmt1.close(); 
		
	
	}
}

