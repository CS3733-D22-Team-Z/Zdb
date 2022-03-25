package edu.wpi.teamZ;

public class Location {
  private String nodeID;
  private int xcoord;
  private int ycoord;
  private String floor;
  private String building;
  private String nodeType;
  private String longName;
  private String shortName;

  public Location() {}

  public Location(
      String nodeID,
      int xcoord,
      int ycoord,
      String floor,
      String building,
      String nodeType,
      String longName,
      String shortName) {
    this.nodeID = nodeID;
    this.xcoord = xcoord;
    this.ycoord = ycoord;
    this.floor = floor;
    this.building = building;
    this.nodeType = nodeType;
    this.longName = longName;
    this.shortName = shortName;
  }

  public Location(String nodeID) {
    this.nodeID = nodeID;
  }

  public String getID() {
    return this.nodeID;
  }

  public int getXcoord() {
    return this.xcoord;
  }

  public int getYcoord() {
    return this.ycoord;
  }

  public String getFloor() {
    return this.floor;
  }

  public String getBuilding() {
    return this.building;
  }

  public String getNodeType() {
    return this.nodeType;
  }

  public String getLongName() {
    return this.longName;
  }

  public String getShortName() {
    return this.shortName;
  }

  public void setFloor(String name) {
    this.floor = name;
  }

  public void setNodeType(String type) {
    this.nodeType = type;
  }
}
