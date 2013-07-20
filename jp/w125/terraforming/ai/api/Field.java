package jp.w125.terraforming.ai.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Field {
  public final int radius;
  public final HashMap<Point, Tile> tiles; // NOTE: it is a mutable collection

  public Field(int radius) {
    this.radius = radius;
    this.tiles = new HashMap<Point, Tile>();
  }

  public List<Point> getPointsWithRobots(int targetPlayerId) {
    List<Point> result = new ArrayList<Point>();
    for (Entry<Point, Tile> pointAndTile : tiles.entrySet()) {
      Point point = pointAndTile.getKey();
      Tile tile = pointAndTile.getValue();
      if (tile.playerId == targetPlayerId && tile.robot > 0) {
        result.add(point);
      }
    }
    return result;
  }

  public List<Point> getPointsWithoutInstallations() {
    List<Point> result = new ArrayList<Point>();
    for (Entry<Point, Tile> pointAndTile : tiles.entrySet()) {
      Point point = pointAndTile.getKey();
      Tile tile = pointAndTile.getValue();
      if (tile.installation == null) {
        result.add(point);
      }
    }
    return result;
  }

  public int getScore(int targetPlayerId) {
    int score = 0;
    for (Tile tile : tiles.values()) {
      score += tile.getScore(targetPlayerId);
    }
    return score;
  }

  public int aroundResource(Point point) {
    Tile tile = tiles.get(point);
    if(tile == null) return 0;

    int resources = tiles.get(point).resource;
    for (Direction direction : Direction.values()) {
      Tile aroundTile = tiles.get(point.move(direction));
      if(aroundTile != null
          && aroundTile.playerId == tile.playerId){
        resources += aroundTile.resource;
      }
    }
    return resources;
  }
  
  public int aroundSettlement(Point point){
    return 0;
  }

}
