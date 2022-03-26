package edu.wpi.teamZ;

import static java.lang.System.exit;
import static java.lang.System.in;

import java.io.*;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
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
    while (conn == null) {
      conn = enterDB(username, pwd);
      if (conn == null) {
        System.out.println("Username and password are not correct");
        System.out.println("Username: ");
        username = scanner.nextLine();
        System.out.println("Password: ");
        pwd = scanner.nextLine();
      }
    }

    // Initialize hashmap
    HashMap<String, Location> locationObjects = new HashMap<>();

    File f = checkCSV();
    readCSV(f, conn, locationObjects);

    while (!done) {
      printUI();
      takeAction(scanner, conn, locationObjects);
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

  public static void readCSV(File f, Connection connection, HashMap<String, Location> map)
      throws IOException {
    // File f = new File("src/TowerLocations.csv");
    FileReader fr = new FileReader(f);
    BufferedReader br = new BufferedReader(fr);
    String line;
    line = br.readLine(); // skip first line (headers)
    while ((line = br.readLine()) != null) {
      System.out.println(line);
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
      insertData(input, connection, map);
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

  public static void dataToCSV(Connection conn, Scanner in, HashMap<String, Location> map) {
    System.out.println(
        "Enter a filepath from home to save to, including filename (Default to Downloads\\output.csv with \"ENTER\"): ");
    System.out.println(
        "Example for folder \"outfolder\" on Desktop: \\Desktop\\outfolder\\filename.csv");
    String path = in.nextLine();
    if (path.compareToIgnoreCase("") == 0) {
      path = System.getProperty("user.home") + "\\Downloads\\output.csv";
    } else {
      path = System.getProperty("user.home") + path;
    }
    // path = path.replaceAll("\\\\", "\\");
    File writeTo = new File(path);
    FileWriter writer = null;
    try {
      writer = new FileWriter(writeTo);
    } catch (IOException e) {
      System.out.println("Error occurred writing file.");
    }
    if (writer != null) {
      try (Statement stmt = conn.createStatement()) {
        //        ResultSet rs = stmt.executeQuery("select * from LOCATION"); // get all records
        ResultSet rs = stmt.executeQuery("SELECT NODEID FROM Location"); // Changed from above -AB

        String nodeID = "";
        int xcoord = 0;
        int ycoord = 0;
        String floor = "";
        String building = "";
        String nodeType = "";
        String longName = "";
        String shortName = "";

        writer.write(
            String.join(
                    ",",
                    "nodeID",
                    "xcoord",
                    "ycoord",
                    "floor",
                    "building",
                    "nodeType",
                    "longName",
                    "shortName")
                + "\n");
        while (rs.next()) {
          //          writer.write(
          //              String.join(
          //                      ",",
          //                      rs.getString("NODEID"),
          //                      rs.getString("XCOORD"),
          //                      rs.getString("YCOORD"),
          //                      rs.getString("FLOOR"),
          //                      rs.getString("BUILDING"),
          //                      rs.getString("NODETYPE"),
          //                      rs.getString("LONGNAME"),
          //                      rs.getString("SHORTNAME"))
          //                  + "\n");

          nodeID = rs.getString("nodeID");
          //          xcoord = rs.getInt("xcoord");
          //          ycoord = rs.getInt("ycoord");
          //          floor = rs.getString("floor");
          //          building = rs.getString("building");
          //          nodeType = rs.getString("nodeType");
          //          longName = rs.getString("longName");
          //          shortName = rs.getString("shortName");

          Location temp = map.get(nodeID);

          // Get info
          xcoord = temp.getXcoord();
          ycoord = temp.getYcoord();
          floor = temp.getFloor();
          building = temp.getBuilding();
          nodeType = temp.getNodeType();
          longName = temp.getLongName();
          shortName = temp.getShortName();

          writer.write(
              String.join(
                      ",",
                      nodeID,
                      Integer.toString(xcoord),
                      Integer.toString(ycoord),
                      floor,
                      building,
                      nodeType,
                      longName,
                      shortName)
                  + "\n");
        }
      } catch (Exception e) {
        if (e instanceof SQLException) {
          System.out.println("Query failed.");
        } else if (e instanceof IOException) {
          System.out.println("File write failed.");
        } else {
          System.out.println("Not a valid ID.");
        }
        e.printStackTrace();
      }

      try {
        writer.close();
      } catch (Exception e) {
        if (e instanceof SQLException) {
          System.out.println("Connection failed. Check output console.");
        } else if (e instanceof IOException) {
          System.out.println("File writer close failed.");
        } else {
          System.out.println("Not a valid ID.");
        }
        e.printStackTrace();
      }
    }
  }

  public static void takeAction(Scanner in, Connection conn, HashMap<String, Location> map) {
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
        displayData(conn, in, map);
        // printAll(conn);

        break;
      case 2:
        update(conn, in, map);
        break;
      case 3:
        Location newLoc = getNewLocation(in);
        insertData(newLoc, conn, map);
        break;
      case 4:
        // TODO: delete info
        deleteData(conn, in, map);
        break;
      case 5:
        dataToCSV(conn, in, map);
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

    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println("Connection failed. Check output console.");
      } else {
        System.out.println("Not a valid ID");
      }
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

    try {
      Statement tableStmt = connection.createStatement();
      tableStmt.execute("DROP TABLE Location");
      tableStmt.execute(
          ""
              + "CREATE TABLE Location ("
              + "nodeID VARCHAR(15),"
              + "xcoord INTEGER,"
              + "ycoord INTEGER ,"
              + "floor VARCHAR(10),"
              + "building VARCHAR(20),"
              + "nodeType VARCHAR(5),"
              + "longName VARCHAR(50),"
              + "shortName Varchar(50),"
              + "constraint LOCATION_PK Primary Key (nodeID))");
      System.out.println("Created new table Location");
    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println(((SQLException) e).getSQLState());
        System.out.println("Unable to create new table Location");
      } else {
        System.out.println("Not a valid ID");
      }
      e.printStackTrace();
    }

    return connection;
  }

  public static void insertData(
      Location info, Connection connection, HashMap<String, Location> map) {
    map.put(info.getID(), info);
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
    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println("Insert prepared statements failed to load");
      } else {
        System.out.println("Not a valid ID");
      }
      e.printStackTrace();
    }
  }

  public static void displayData(Connection connection, Scanner in, HashMap<String, Location> map) {
    // Ask if display all or display 1
    System.out.println(
        "Select which location you want to view using NodeID\n"
            + "If you want to view all type ALL, \"cancel\" to cancel:");
    String option = in.nextLine();
    if (option.compareToIgnoreCase("cancel") == 0) {
      return;
    }
    // Display location info
    try {
      PreparedStatement selectStmt =
          connection.prepareStatement("SELECT NODEID FROM Location WHERE NODEID = ?");
      selectStmt.setString(1, option);
      if (option.equals("ALL")) {
        selectStmt = connection.prepareStatement("SELECT NODEID FROM Location");
      } else {
        option = databaseID(connection, option, in);
        if (option == null) {
          return;
        }
        selectStmt = connection.prepareStatement("SELECT NODEID FROM Location WHERE NODEID = ?");
        selectStmt.setString(1, option);
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
        //        xcoord = rset.getInt("xcoord");
        //        ycoord = rset.getInt("ycoord");
        //        floor = rset.getString("floor");
        //        building = rset.getString("building");
        //        nodeType = rset.getString("nodeType");
        //        longName = rset.getString("longName");
        //        shortName = rset.getString("shortName");

        Location temp = map.get(nodeID);

        // Get info
        xcoord = temp.getXcoord();
        ycoord = temp.getYcoord();
        floor = temp.getFloor();
        building = temp.getBuilding();
        nodeType = temp.getNodeType();
        longName = temp.getLongName();
        shortName = temp.getShortName();

        System.out.printf( // actual printout. Will need to be fixed should column ordering change.
            " %10s | %6d | %6d | %5s | %8s | %8s | %45s | %20s\n",
            nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName);
      }

    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println("Display not working");
      } else {
        System.out.println("Not a valid ID");
      }
      e.printStackTrace();
    }
  }

  public static Location getNewLocation(Scanner in) {
    System.out.println("Please give NodeID:");
    String id = in.nextLine();

    /*
    System.out.println("Please give y coordinate or cancel to cancel: ");
    String sycoord = in.nextLine();
    if (sycoord.compareToIgnoreCase("cancel") == 0) {
      return null;
    }
    int ycoord = Integer.parseInt(sycoord);


    System.out.println("Please give the floor or cancel to cancel: ");
    String floor = in.nextLine();
    if (floor.compareToIgnoreCase("cancel") == 0) {
      return null;
    }

    System.out.println("Please give the building of the location or cancel to cancel: ");
    String building = in.nextLine();
    if (building.compareToIgnoreCase("cancel") == 0) {
      return null;
    }

    System.out.println("Please give the type of location or cancel to cancel: ");
    String type = in.nextLine();
    if (type.compareToIgnoreCase("cancel") == 0) {
      return null;
    }

    System.out.println("Please give the long name of location or cancel to cancel: ");
    String lName = in.nextLine();
    if (lName.compareToIgnoreCase("cancel") == 0) {
      return null;
    }

    System.out.println("Please give the abbreviation of the location or cancel to cancel: ");
    String sName = in.nextLine();
    if (sName.compareToIgnoreCase("cancel") == 0) {
      return null;
    }*/

    return new Location(id);
  }

  public static void update(Connection connection, Scanner in, HashMap<String, Location> map) {
    System.out.println("Enter ID of location or cancel to cancel:");
    String id = in.nextLine();
    if (id.compareToIgnoreCase("cancel") == 0) {
      return;
    }
    id = databaseID(connection, id, in); // test if ID is in database
    if (id == null) {
      return;
    }
    System.out.println("Enter new floor or cancel to cancel:");
    String floor = in.nextLine();
    if (floor.compareToIgnoreCase("cancel") == 0) {
      return;
    }
    System.out.println("Enter new location type or cancel to cancel:");
    String type = in.nextLine();
    if (type.compareToIgnoreCase("cancel") == 0) {
      return;
    }
    try {
      PreparedStatement stmt =
          connection.prepareStatement("UPDATE Location SET floor=?, nodeTYPE =? WHERE nodeID =?");
      stmt.setString(1, floor);
      stmt.setString(2, type);
      stmt.setString(3, id);

      stmt.executeUpdate();
      connection.commit();

      Location temp = map.get(id);
      temp.setFloor(floor);
      temp.setNodeType(type);
      map.put(id, temp);

    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println("Cannot update location");
      } else {
        System.out.println("Not a valid ID");
      }
      e.printStackTrace();
    }
  }

  public static void deleteData(Connection connection, Scanner in, HashMap<String, Location> map) {
    System.out.println("Enter ID of location or cancel to cancel:");
    String id = in.nextLine();
    if (id.compareToIgnoreCase("cancel") == 0) {
      return;
    }
    id = databaseID(connection, id, in);
    if (id == null) {
      return;
    }
    // Delete using SQP
    try {
      PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM Location WHERE Nodeid=?");
      stmt3.setString(1, id);
      stmt3.execute();
      connection.commit();
      map.remove(id);
    } catch (Exception e) {
      if (e instanceof SQLException) {
        System.out.println("ID not found");
      } else {
        System.out.println("Not a valid ID");
      }
      e.printStackTrace();
      System.out.println("NodeID " + id + (" has been deleted\n"));
    }

    map.remove(id);
  }

  public static String databaseID(Connection connection, String id, Scanner in) {
    Boolean uniqueID = true;
    while (uniqueID) { // Test ID is in database
      try {
        PreparedStatement stmt =
            connection.prepareStatement("SELECT COUNT(*) FROM Location WHERE Nodeid=?");
        stmt.setString(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          if (rs.getInt(1) == 0) {
            System.out.println(
                "Node ID does not exists please enter another Node ID or cancel to cancel:");
            id = in.nextLine();
            if (id.compareToIgnoreCase("cancel") == 0) {
              return null;
            }
          } else {
            uniqueID = false;
          }
        }
        connection.commit();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return id;
  }
}
