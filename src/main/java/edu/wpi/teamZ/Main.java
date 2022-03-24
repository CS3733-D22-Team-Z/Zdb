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

      // Uncomment if haven't added to database
      // insertData(input, connection);
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
        displayData(conn, in);
        // printAll(conn);

        break;
      case 2:
        update(conn, in);
        break;
      case 3:
        // TODO: new info
        Location newLoc = getNewLocation(in);
        insertData(newLoc, conn);
        break;
      case 4:
        // TODO: delete info
        deleteData(conn, in);
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

  public static void displayData(Connection connection, Scanner in) {
    // Ask if display all or display 1
    System.out.println(
        "Select which location you want to view using NodeID\n"
            + "If you want to view all type ALL: ");
    String option = in.nextLine();

    // Display location info
    try {
      PreparedStatement selectStmt =
          connection.prepareStatement("SELECT * FROM Location WHERE NODEID = ?");
      selectStmt.setString(1, option);
      if (option.equals("ALL")) {
        selectStmt = connection.prepareStatement("SELECT * FROM Location");
      }

      ResultSet rset = selectStmt.executeQuery();

      /*if (!rset.next()) {
        System.out.println("NodeID not valid/found");
      }*/

      String nodeID = "";
      int xcoord = 0;
      int ycoord = 0;
      String floor = "";
      String building = "";
      String nodeType = "";
      String longName = "";
      String shortName = "";

      System.out.printf(
          " %10s | %6s | %6s | %5s | %8s | %8s | %45s | %20s\n", // this is all for the header
          "nodeID", "xcoord", "ycoord", "floor", "building", "nodeType", "Long Name", "Short Name");

      System.out.println("-".repeat(130)); // dividing bar between header and data

      while (rset.next()) {
        nodeID = rset.getString("nodeID");
        xcoord = rset.getInt("xcoord");
        ycoord = rset.getInt("ycoord");
        floor = rset.getString("floor");
        building = rset.getString("building");
        nodeType = rset.getString("nodeType");
        longName = rset.getString("longName");
        shortName = rset.getString("shortName");

        System.out.printf( // actual printout. Will need to be fixed should column ordering change.
            " %10s | %6d | %6d | %5s | %8s | %8s | %45s | %20s\n",
            nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName);
      }

    } catch (SQLException e) {
      System.out.println("Display not working");
    }
  }

  public static Location getNewLocation(Scanner in) {
    System.out.println("Please give NodeID:");
    String id = in.nextLine();

    System.out.println("Please give x coordinate: ");
    int xcoord = Integer.parseInt(in.nextLine());

    System.out.println("Please give y coordinate: ");
    int ycoord = Integer.parseInt(in.nextLine());

    System.out.println("Please give the floor: ");
    String floor = in.nextLine();

    System.out.println("Please give the building of the location: ");
    String building = in.nextLine();

    System.out.println("Please give the type of location: ");
    String type = in.nextLine();

    System.out.println("Please give the long name of location: ");
    String lName = in.nextLine();

    System.out.println("Please give the abbreviation of the location: ");
    String sName = in.nextLine();

    return new Location(id, xcoord, ycoord, floor, building, type, lName, sName);
  }

  public static void update(Connection connection, Scanner in) {
    System.out.println("Enter ID of location:");
    String id = in.nextLine();
    System.out.println("Enter new floor:");
    String floor = in.nextLine();
    System.out.println("Enter new location type");
    String type = in.nextLine();
    try {
      PreparedStatement stmt =
          connection.prepareStatement("UPDATE Location SET floor=?, nodeTYPE =? WHERE nodeID =?");
      stmt.setString(1, floor);
      stmt.setString(2, type);
      stmt.setString(3, id);

      stmt.executeUpdate();
      connection.commit();

    } catch (SQLException e) {
      System.out.println("Cannot update location");
    }
  }

  public static void deleteData(Connection connection, Scanner in) {
    System.out.println("Enter ID of location:");
    String id = in.nextLine();
    // Delete using SQP
    try {
      PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM Location WHERE Nodeid=?");
      stmt3.setString(1, id);
      stmt3.execute();
      connection.commit();
    } catch (SQLException e) {
      System.out.println("ID not found");
      e.printStackTrace();
    }
  }
}
