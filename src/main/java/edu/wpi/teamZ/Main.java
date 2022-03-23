package edu.wpi.teamZ;

import static java.lang.System.exit;

import java.io.*;
import java.io.File;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws IOException {

    File f = checkCSV();
    readCSV(f);

    while (true) {
      printUI();
      takeAction();
    }

    // App.launch(App.class, args);
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

  public static void readCSV(File f) throws IOException {
    // File f = new File("src/TowerLocations.csv");
    FileReader fr = new FileReader(f);
    BufferedReader br = new BufferedReader(fr);
    String line;
    while ((line = br.readLine()) != null) {
      String[] args = line.split(","); // regex split into array of arg strings

      /*Location input =
      new Location(
          args[0], // nodeID
          Integer.parseInt(args[1]), // xcoord
          Integer.parseInt(args[2]), // ycoord
          args[3], // floor
          args[4], // building
          args[5], // nodetype
          args[6], // longName
          args[7]); // shortName*/
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

  public static void takeAction() {
    Scanner in = new Scanner(System.in);
    int selection = 0;
    while (selection <= 0 || selection >= 7) {
      System.out.println("Selection? ");
      selection = in.nextInt();
      if (selection <= 0 || selection >= 7) {
        System.out.println("Invalid. Try again.");
      }
    }

    switch (selection) {
      case 1:
        // TODO: print info
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
        exit(0);
        break;
    }
  }
}
