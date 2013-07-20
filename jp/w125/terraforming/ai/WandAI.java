package jp.w125.terraforming.ai;

import java.util.ArrayList;
import java.util.List;

import jp.w125.terraforming.ai.api.*;

public class WandAI extends AI {

  public String name() {
    return "WandRobot";
  }

  public void command(Game game) {

    List<Point> points = game.field.getPointsWithRobots(game.myId);
    for (Point point : points) {
      Tile tile = game.field.tiles.get(point);
      // Direction choiceDir = null;
      ArrayList<Direction> validDirections = new ArrayList<Direction>();
      for (Direction direction : Direction.values()) {
        Tile movedTile = game.field.tiles.get(point.move(direction));
        if (movedTile == null)
          continue;
        if (movedTile.isHole) {
          continue;
        }
        if (movedTile.playerId != game.myId
            && movedTile.playerId != -1
            && !movedTile.installation.equals(Installation.None)) {
          continue;
        }
        validDirections.add(direction);
      }

      if (validDirections.size() > 0) {
        for (Direction direction : validDirections) {
          move(point, direction, tile.robot / validDirections.size());
        }
      }

    }
  }
}
