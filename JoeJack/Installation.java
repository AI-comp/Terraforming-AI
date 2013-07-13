public enum Installation {
  Initial(0, 0, 3),
  Factory(30, 4, 3),
  Bridge(15, 4, 3),
  Attack(25, 5, 3),
  Pit(20, 4, 3),
  House(10, 4, 3),
  Town(10, 9, 3);

  public final int robotCost;
  public final int materialCost;
  public final int score;

  private Installation(int robotCost, int materialCost, int score) {
    this.robotCost = robotCost;
    this.materialCost = materialCost;
    this.score = score;
  }
}
