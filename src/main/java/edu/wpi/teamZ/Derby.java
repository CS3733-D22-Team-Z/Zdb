package edu.wpi.teamZ;

import java.sql.*;

public class Derby {
  public static void main(String[] args) {
    System.out.println("Embedded Apache Derby Connection Testing");
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    } catch (ClassNotFoundException e) {
      System.out.println("Apache Derby Driver not found. Add the classpath to your module.");
      System.out.println("For IntelliJ do the following:");
      System.out.println("File | Project Structure, Modules, Dependency tab");
      System.out.println("Add by clicking on the green plus icon on the right of the window");
      System.out.println(
          "Select JARs or directories. Go to the folder where the database JAR is located");
      System.out.println("Click OK, now you can compile your program and run it.");
      e.printStackTrace();
      return;
    }
    System.out.println("Apache Derby driver registered!");
    Connection connection = null;

    try {
      // substitute your database name for mmyDB
      connection = DriverManager.getConnection("jdbc:derby:myDB;create=true");
    } catch (SQLException e) {
      System.out.println("Connection failed. Check output console.");
      e.printStackTrace();
      return;
    }

    if (connection != null) {
      System.out.println("Apache Derby connection established!");
    } else {
      System.out.println("Apache Derby connection established!");
    }

    // create table if not yet created
    /*try {
      Statement tableStmt = connection.createStatement();
      tableStmt.execute(
          ""
              + "CREATE TABLE Location ("
              + "nodeID VARCHAR(15),"
              + "xcoord INTEGER,"
              + "ycoord INTEGER ,"
              + "floor INTEGER,"
              + "building VARCHAR(20),"
              + "nodeType VARCHAR(5),"
              + "longName VARCHAR(50),"
              + "shortName Varchar(25),"
              + "constraint LOCATION_PK Primary Key (nodeID))");
    } catch (SQLException e) {
      System.out.println("Unable to create new table Location");
    }*/

    // insert SQL statements here
    try {
      PreparedStatement pstmt =
          connection.prepareStatement(
              "INSERT INTO Location (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName) values (?, ?, ?, ?, ?, ?, ?, ?)");
      // loop through the array to insert into DB
    } catch (SQLException e) {
      System.out.println("Insert prepared statements failed to load");
    }

    // Display location info
    try {
      Statement selectStmt = connection.createStatement();
      selectStmt.execute("SELECT * FROM Location");
    } catch (SQLException e) {
      System.out.println("Display not working");
    }

    //

    // close connection!
    // Close the connection!!!!!!
    try {
      connection.close();
    } catch (SQLException e) {
      System.out.println("Failed to close :/ what to do?");
    }
    return;
  }
}
