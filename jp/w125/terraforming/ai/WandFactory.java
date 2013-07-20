package jp.w125.terraforming.ai;

import java.util.ArrayList;
import java.util.List;

import jp.w125.terraforming.ai.api.*;

public class WandFactory extends AI {

  public String name() {
    return "WandFactory";
  }

  public boolean buildCommand(Game game) {
    List<Point> points = game.field.getPointsWithRobots(game.myId);
    for (Point point : points) {
      Tile tile = game.field.tiles.get(point);
      if (tile.playerId != game.myId)
        continue;
      if (!tile.installation.equals(Installation.None))
        continue;
      if (tile.robot < Installation.House.robotCost)
        continue;
      if (game.field.aroundResource(point) < Installation.House.materialCost) {
        continue;
      }
      if (game.turn < 60 && tile.robot >= Installation.Robotmaker.robotCost) {
        build(point, Installation.Robotmaker);
        return true;
      }
      if(game.turn >= 60) {
        build(point, Installation.House);
        return true;
      }
    }
    return false;
  }

  public void moveCommand(Game game) {
    List<Point> points = game.field.getPointsWithRobots(game.myId);
    for (Point point : points) {
      Tile tile = game.field.tiles.get(point);

      ArrayList<Direction> validDirections = new ArrayList<Direction>();
      for (Direction direction : Direction.values()) {
        Point movedPoint = point.move(direction);
        Tile movedTile = game.field.tiles.get(movedPoint);
        if (movedTile == null)
          continue;
        if (movedTile.isHole) {
          continue;
        }
        if (movedTile.playerId != game.myId && movedTile.playerId != -1
            && !movedTile.installation.equals(Installation.None)) {
          continue;
        }
        if (tile.installation.equals(Installation.None)
            && game.field.aroundResource(point) >= Installation.House.materialCost
            && !movedTile.installation.equals(Installation.None)) {
          continue;
        }
        if (game.turn > 60 && movedTile.robot > tile.robot
            && movedTile.robot > Installation.House.robotCost) {
          continue;
        }
        if (game.turn < 60
            && tile.installation.equals(Installation.None)
            && game.field.aroundResource(point) >= Installation.Robotmaker.materialCost
            && tile.robot < Installation.Robotmaker.robotCost) {
          continue;
        }
        validDirections.add(direction);
      }

      if (validDirections.size() > 0) {
        for (Direction direction : validDirections) {
          if (tile.robot / validDirections.size() > 0) {
            move(point, direction, tile.robot / validDirections.size());
          }
        }
      }
    }

  }

  public void command(Game game) {
    if (!buildCommand(game)) {
      moveCommand(game);
    }
  }
}
