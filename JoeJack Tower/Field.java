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

	public List<Point> getPointsWithEnemyInitial(int myId) {
		List<Point> result = new ArrayList<Point>();
		for (Entry<Point, Tile> pointAndTile : tiles.entrySet()) {
			Point point = pointAndTile.getKey();
			Tile tile = pointAndTile.getValue();
			if (tile.installation == Installation.Initial && tile.playerId != myId) {
				result.add(point);
			}
		}
		return result;
	}

	public List<Point> getPointsInRadius(Point center, int radius, boolean includeCenter) {
		ArrayList<Point> result = new ArrayList<Point>();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				if (dx * dy <= 0 || Math.abs(dx) + Math.abs(dy) <= radius) {
					if (includeCenter || dx != 0 || dy != 0) {
						int x = center.x + dx;
						int y = center.y + dy;
						Point point = new Point(x, y);
						if (tiles.containsKey(point)) {
							result.add(point);
						}
					}
				}
			}
		}
		return result;
	}

	public int getDistance(Point point1, Point point2) {
		for (int radius = 1; radius < 100; radius++) {
			if (getPointsInRadius(point1, radius, true).contains(point2)) {
				return radius;
			}
		}
		return Integer.MAX_VALUE;
	}

	public int getNumConstructableNeighborTiles(Point center) {
		int result = 0;
		for (Point neighborPoint : getPointsInRadius(center, 1, true)) {
			Tile neighborTile = tiles.get(neighborPoint);
			if (neighborTile.installation == null && !neighborTile.isHole) {
				result++;
			}
		}
		return result;
	}

	public Point getPointFromTile(Tile tile) {
		for (Point point : tiles.keySet()) {
			if (tiles.get(point).equals(tile)) {
				return point;
			}
		}
		return null;
	}

	public int getNumAvailableResources(Point point, int playerId) {
		int result = 0;
		for (Point neighborPoint : getPointsInRadius(point, 1, true)) {
			if (tiles.get(neighborPoint).playerId == playerId) {
				result += tiles.get(neighborPoint).resource;
			}
		}
		return result;
	}
}
