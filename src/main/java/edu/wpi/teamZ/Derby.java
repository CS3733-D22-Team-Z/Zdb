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

    // insert SQL statements here

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
