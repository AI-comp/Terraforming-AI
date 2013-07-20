package jp.w125.terraforming.ai;

import jp.w125.terraforming.ai.api.Direction;
import jp.w125.terraforming.ai.api.Game;
import jp.w125.terraforming.ai.api.Installation;
import jp.w125.terraforming.ai.api.Point;

public abstract class AI {
  public String name() {
    return "AI";
  }

  public void command(Game game) {

  }

  protected static void move(Point point, Direction dir, int robot) {
    if (robot > 0) {
      System.out.println("move " + point.x + " " + point.y + " " + dir.command
          + " " + robot);
    }
  }

  protected static void build(Point point, Installation inst) {
    System.out.println("build " + point.x + " " + point.y + " "
        + inst.name().toLowerCase());
  }

}
