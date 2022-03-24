package edu.wpi.teamZ;

import static java.lang.System.exit;
import static java.lang.System.in;

import java.io.*;
import java.io.File;
import java.sql.*;
import java.util.Scanner;

public class Main {
  public static boolean done = false;

  public static void main(String[] args) throws IOException {
    // get the username and password
    Scanner scanner = new Scanner(System.in);
    System.out.println("Username: ");
    String username = scanner.nextLine();
    System.out.println("Password: ");
    String pwd = scanner.nextLine();

    // scanner.close();

    // Access Database
    Connection conn = enterDB(username, pwd);

    File f = checkCSV();
    readCSV(f, conn);

    while (!done) {
      printUI();
      takeAction(scanner, conn);
    }
    scanner.close();
    try {
      conn.close();
    } catch (SQLException e) {
      System.out.println("connection close failed. How odd.");
    }
  }

  public static File checkCSV() {

    // Get the file
    File f = new File("src/TowerLocations.csv");

    // Create new file
    // if it does not exist
    if (f.exists()) {
      System.out.println("File found.");
    } else {
      System.out.println("File does not exist in the correct location.\nPlease fix and try again.");
      exit(0);
    }
    return f;
  }

  public static void readCSV(File f, Connection connection) throws IOException {
    // File f = new File("src/TowerLocations.csv");
    FileReader fr = new FileReader(f);
    BufferedReader br = new BufferedReader(fr);
    String line;
    line = br.readLine(); // skip first line (headers)
    while ((line = br.readLine()) != null) {
      String[] args = line.split(","); // regex split into array of arg strings

      Location input =
          new Location(
              args[0], // nodeID
              Integer.parseInt(args[1]), // xcoord
              Integer.parseInt(args[2]), // ycoord
              args[3], // floor
              args[4], // building
              args[5], // nodetype
              args[6], // longName
              args[7]); // shortName

      // TODO: pass to DB
      insertData(input, connection);
      input = null;
    }
  }

  public static void printUI() {
    System.out.println("1 – Location Information");
    System.out.println("2 – Change Floor and Type");
    System.out.println("3 – Enter Location");
    System.out.println("4 – Delete Location");
    System.out.println("5 – Save Locations to CSV file");
    System.out.println("6 – Exit Program");
  }

  public static void printAll(Connection conn) {
    CallableStatement getall = null;
    try (Statement stmt = conn.createStatement()) {
      ResultSet rs = stmt.executeQuery("select * from LOCATION"); // get all records
      System.out.printf(
          " %10s | %6s | %6s | %5s | %8s | %8s | %45s | %20s\n", // this is all for the header
          "nodeID", "xcoord", "ycoord", "floor", "building", "nodeType", "Long Name", "Short Name");
      System.out.println("-".repeat(130)); // dividing bar between header and data
      while (rs.next()) {
        System.out.printf( // actual printout. Will need to be fixed should column ordering change.
            " %10s | %6d | %6d | %5s | %8s | %8s | %45s | %20s\n",
            rs.getString("NODEID"),
            Integer.parseInt(rs.getString("XCOORD")),
            Integer.parseInt(rs.getString("YCOORD")),
            rs.getString("FLOOR"),
            rs.getString("BUILDING"),
            rs.getString("NODETYPE"),
            rs.getString("LONGNAME"),
            rs.getString("SHORTNAME"));
      }
    } catch (SQLException e) {
      System.out.println("Query failed.");
    }
  }

  public static void takeAction(Scanner in, Connection conn) {
    // Scanner in = new Scanner(System.in);
    int selection = 0;
    while (selection <= 0 || selection >= 7) { // repeat for invalids
      System.out.println("Selection? ");
      String instring = in.nextLine();
      try {
        selection = Integer.parseInt(instring); // fail string inputs
      } catch (NumberFormatException ignored) {
      }
      if (selection <= 0 || selection >= 7) { // invalid input ends up here
        System.out.println("Invalid. Try again.");
      }
    }

    switch (selection) {
      case 1:
        printAll(conn);
        break;
      case 2:
        // TODO: edit info

        break;
      case 3:
        // TODO: new info
        break;
      case 4:
        // TODO: delete info
        break;
      case 5:
        // TODO: export
        break;
      case 6:
        done = true;
        break;
    }

    // in.close();
  }

  public static Connection enterDB(String user, String pwd) {
    System.out.println("Embedded Apache Derby Connection Start");
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
    }
    System.out.println("Apache Derby driver registered!");
    Connection connection = null;

    try {
      // substitute your database name for myDB
      connection =
          DriverManager.getConnection(
              "jdbc:derby:myDB;create=true" + ";user=" + user + ";password=" + pwd);

    } catch (SQLException e) {
      System.out.println("Connection failed. Check output console.");
      e.printStackTrace();
    }

    // set authentication
    /*try {
      Statement s = connection.createStatement();
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(\n"
              + "'derby.connection.requireAuthentication', 'true')");
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(\n"
              + "'derby.authentication.provider', 'BUILTIN')");
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(\n" + "'derby.user.admin', 'admin')");
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(\n"
              + "'derby.database.propertiesOnly', 'true')");
      System.out.println("Authentication initialized");
    } catch (SQLException e) {
      System.out.println("Failed to set credentials");
    }*/

    if (connection != null) {
      System.out.println("Apache Derby connection established!");
    } else {
      System.out.println("Apache Derby connection failed!");
      return null;
    }

    // create table if not yet created
    /*try {
      Statement tableStmt = connection.createStatement();
      tableStmt.execute("DROP TABLE LOCATION");
      tableStmt.execute(
          ""
              + "CREATE TABLE Location ("
              + "nodeID VARCHAR(15),"
              + "xcoord INTEGER,"
              + "ycoord INTEGER ,"
              + "floor Varchar(5),"
              + "building VARCHAR(20),"
              + "nodeType VARCHAR(5),"
              + "longName VARCHAR(50),"
              + "shortName Varchar(25),"
              + "constraint LOCATION_PK Primary Key (nodeID))");
      System.out.println("Created new table Location");
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      System.out.println("Unable to create new table Location");
    }*/

    return connection;
  }

  public static void insertData(Location info, Connection connection) {
    try {
      PreparedStatement pstmt =
          connection.prepareStatement(
              "INSERT INTO Location (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName) values (?, ?, ?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, info.getID());
      pstmt.setInt(2, info.getXcoord());
      pstmt.setInt(3, info.getYcoord());
      pstmt.setString(4, info.getFloor());
      pstmt.setString(5, info.getBuilding());
      pstmt.setString(6, info.getNodeType());
      pstmt.setString(7, info.getLongName());
      pstmt.setString(8, info.getShortName());

      // insert it
      pstmt.executeUpdate();
      connection.commit();
      // loop through the array to insert into DB
    } catch (SQLException e) {
      System.out.println("Insert prepared statements failed to load");
    }
  }
}
