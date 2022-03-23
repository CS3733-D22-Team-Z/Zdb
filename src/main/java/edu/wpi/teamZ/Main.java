package edu.wpi.teamZ;

import static java.lang.System.exit;

import java.io.*;
import java.io.File;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws IOException {

    checkCSV();

    while (true) {
      printUI();
      takeAction();
    }

    // App.launch(App.class, args);
  }

  public static void checkCSV() throws IOException {
    try {
      // Get the file
      File f = new File("src/TowerLocations.csv");

      // Create new file
      // if it does not exist
      if (f.createNewFile()) System.out.println("File created");
      else System.out.println("File already exists");
    } catch (Exception e) {
      System.err.println(e);
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

  public static void takeAction() throws IOException {
    Scanner in = new Scanner(System.in);
    int selection = 0;
    while (selection <= 0 || selection >= 7) { // repeat for invalids
      System.out.println("Selection? ");
      String instring = in.nextLine();
      try {
        selection = Integer.parseInt(instring);//fail string inputs
      } catch (NumberFormatException ignored) {
      }
      if (selection <= 0 || selection >= 7) { // invalid input ends up here
        System.out.println("Invalid. Try again.");
      }
    }

    File f = new File("src/TowerLocations.csv");
    FileReader fr = new FileReader(f);
    BufferedReader br = new BufferedReader(fr);
    String line;

    switch (selection) {
      case 1:
        // print info
        while ((line = br.readLine()) != null) {
          // process the line
          System.out.println(line);
        }
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

    in.close();
  }

}
