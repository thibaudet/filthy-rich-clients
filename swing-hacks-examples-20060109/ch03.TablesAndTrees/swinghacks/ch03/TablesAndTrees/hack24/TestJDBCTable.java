package swinghacks.ch03.TablesAndTrees.hack24;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableModel;

public class TestJDBCTable {

	public static void main(String[] args) {
		try {
			/*
			  driver, url, user, and pass can be passed in as
			  system properties "jdbctable.driver",
			  "jdbctable.url", "jdbctable.user", and
			  "jdbctable.pass", or specified in a file
			  called "jdbctable.properties" in current
			  directory
			*/
			Properties testProps = new Properties();
			String ddriver = System.getProperty("jdbctable.driver");
			String durl = System.getProperty("jdbctable.url");
			String duser = System.getProperty("jdbctable.user");
			String dpass = System.getProperty("jdbctable.pass");

			if (ddriver != null)
				testProps.setProperty("jdbctable.driver", ddriver);
			if (durl != null)
				testProps.setProperty("jdbctable.url", durl);
			if (duser != null)
				testProps.setProperty("jdbctable.user", duser);
			if (dpass != null)
				testProps.setProperty("jdbctable.pass", dpass);
			try {
				testProps.load(new FileInputStream(new File("jdbctable.properties")));
			} catch (Exception e) {
			} // ignore FNF, etc.
			System.out.println("Test Properties:");
			testProps.list(System.out);

			// now get a connection
			// note care to replace nulls with empty strings
			Class.forName(testProps.getProperty("jdbctable.driver")).newInstance();
			String url = testProps.getProperty("jdbctable.url");
			url = ((url == null) ? "" : url);
			String user = testProps.getProperty("jdbctable.user");
			user = ((user == null) ? "" : user);
			String pass = testProps.getProperty("jdbctable.pass");
			pass = ((pass == null) ? "" : pass);

			Connection conn = DriverManager.getConnection(url, user, pass);

			// create db table to use
			String tableName = createSampleTable(conn);

			// get a model for this db table and add to a JTable
			TableModel mod = new JDBCTableModel(conn, tableName);
			JTable jtable = new JTable(mod);
			JScrollPane scroller = new JScrollPane(jtable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			JFrame frame = new JFrame("JDBCTableModel demo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(scroller);
			frame.pack();
			frame.setVisible(true);

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String createSampleTable(Connection conn) throws SQLException {

		Statement statement = conn.createStatement();
		// drop table if it exists
		try {
			statement.execute("DROP TABLE EMPLOYEES");
		} catch (SQLException sqle) {
			sqle.printStackTrace(); // if table !exists
		}

		statement.execute("CREATE TABLE EMPLOYEES " + "(Name CHAR(20), Title CHAR(30), Salary INT)");
		statement.execute("INSERT INTO EMPLOYEES VALUES " + "('Jill', 'CEO', 200000 )");
		statement.execute("INSERT INTO EMPLOYEES VALUES " + "('Bob', 'VP', 195000 )");
		statement.execute("INSERT INTO EMPLOYEES VALUES " + "('Omar', 'VP', 190000 )");
		statement.execute("INSERT INTO EMPLOYEES VALUES " + "('Amy', 'Software Engineer', 50000 )");
		statement.execute("INSERT INTO EMPLOYEES VALUES " + "('Greg', 'Software Engineer', 45000 )");

		statement.close();

		return "EMPLOYEES";
	}
}
