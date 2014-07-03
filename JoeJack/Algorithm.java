package JoeJack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Algorithm {
	private class NodeForShortestPath {
		private int cost;
		private Point previousPoint;

		public NodeForShortestPath(int cost, Point previousPoint) {
			this.cost = cost;
			this.previousPoint = previousPoint;
		}
	}

	public List<Point> getShortestPath(Point source, Point destination, boolean useHole, Field field, int playerId) {
		LinkedList<Point> queue = new LinkedList<Point>();
		Map<Point, NodeForShortestPath> nodes = new HashMap<Point, NodeForShortestPath>();
		queue.add(source);
		nodes.put(source, new NodeForShortestPath(0, null));
		while (!queue.isEmpty()) {
			Point currentPoint = queue.removeFirst();
			for (Point neighborPoint : field.getPointsInRadius(currentPoint, 1, false)) {
				if (!nodes.containsKey(neighborPoint)) {
					Tile neighborTile = field.tiles.get(neighborPoint);
					boolean canGo = true;
					if (neighborTile.installation != null && neighborTile.playerId != playerId) {
						canGo = false;
					}
					if (neighborTile.isHole) {
						if (useHole) {
							if (field.getNumNeighborTilesWithInstallation(neighborPoint, null, 1) < Installation.Bridge.materialCost) {
								canGo = false;
							}
						} else {
							canGo = false;
						}
					}
					if (canGo) {
						nodes.put(neighborPoint,
								new NodeForShortestPath(nodes.get(currentPoint).cost + 1, currentPoint));
						queue.addLast(neighborPoint);
					}
				}
			}
		}
		return constructShortestPath(destination, nodes);
	}

	private List<Point> constructShortestPath(Point destination, Map<Point, NodeForShortestPath> nodes) {
		List<Point> result = new ArrayList<Point>();
		if (nodes.containsKey(destination)) {
			Point currentPoint = destination;
			while (nodes.get(currentPoint).previousPoint != null) {
				result.add(currentPoint);
				currentPoint = nodes.get(currentPoint).previousPoint;
			}
			Collections.reverse(result);
		}
		return result;
	}
}
