import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Tower {
	final static int DistanceBetweenTowerAndEnemyInitial = 2;
	final static int NumRobotsToDefendConstruction = 10;
	Random random;

	public static void main(String[] args) {
		Tower ai = new Tower();
		ai.run();
	}

	private void run() {
		random = new Random("aicomp".hashCode());

		Scanner scanner = new Scanner(System.in);
		System.out.println("JoeJack Tower");

		while (scanner.hasNext()) {
			Game game = parseGame(scanner);
			processTurn(game);
			System.out.println("finish");
		}
		scanner.close();
	}

	private boolean buildTower(final Game game) {
		for (Point enemyInitialPoint : game.field.getPointsWithEnemyInitial(game.myId)) {
			List<Point> pointsToBuild = game.field.getPointsInRadius(enemyInitialPoint,
					DistanceBetweenTowerAndEnemyInitial, false);
			for (Point point : pointsToBuild) {
				if (game.field.tiles.containsKey(point)) {
					Tile tile = game.field.tiles.get(point);
					if (tile.canBuildInstallation(Installation.Attack, game.field, game.myId)) {
						build(point, Installation.Attack);
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean buildBridge(final Game game) {
		for (Point point : game.field.getPointsWithRobots(game.myId)) {
			Tile tile = game.field.tiles.get(point);
			if (tile.isHole && tile.canBuildInstallation(Installation.Bridge, game.field, game.myId)) {
				build(point, Installation.Bridge);
				return true;
			}
		}
		return false;
	}

	private void setNumRequiredRobotsForInstallation(final Game game, Map<Point, Integer> numRequiredRobots,
			List<Point> pointsToBuildInstallation, Installation installation) {
		for (Point pointToBuildInstallation : pointsToBuildInstallation) {
			Utility.AddIntToMap(numRequiredRobots, pointToBuildInstallation, installation.robotCost);
			for (Point neighborPoint : game.field.getPointsInRadius(pointToBuildInstallation, 1, false)) {
				Tile neighborTile = game.field.tiles.get(neighborPoint);
				if (!neighborTile.isHole && neighborTile.installation == null) {
					Utility.AddIntToMap(numRequiredRobots, neighborPoint, NumRobotsToDefendConstruction);
				}
			}
		}
	}

	private void setNumRequiredRobotsForBridge(final Game game, Map<Point, Integer> numRequiredRobots) {
		List<Point> pointsToBuildBridge = new ArrayList<Point>();
		for (Point point : game.field.getPointsWithRobots(game.myId)) {
			Tile tile = game.field.tiles.get(point);
			if (tile.isHole) {
				pointsToBuildBridge.add(point);
			}
		}

		setNumRequiredRobotsForInstallation(game, numRequiredRobots, pointsToBuildBridge, Installation.Bridge);
	}

	private void setNumRequiredRobotsForTower(final Game game, Map<Point, Integer> numRequiredRobots) {
		List<Point> pointsToBuildTower = new ArrayList<Point>();
		for (final Point enemyInitialPoint : game.field.getPointsWithEnemyInitial(game.myId)) {
			List<Point> candidatePoints = game.field.getPointsInRadius(enemyInitialPoint,
					DistanceBetweenTowerAndEnemyInitial, false);
			Collections.sort(candidatePoints, new Comparator<Point>() {
				private int getScore(Point point) {
					Point center = new Point(0, 0);
					Point myInitial = game.field.getPointWithMyInitial(game.myId);
					int score = 0;
					score += game.field.getDistance(point, center);
					score -= game.field.getDistance(point, myInitial);
					score -= game.field.getNumNeighborTilesWithInstallation(center, Installation.Attack, 3);
					return score;
				}

				@Override
				public int compare(Point point1, Point point2) {
					int score1 = getScore(point1);
					int score2 = getScore(point2);
					if (score1 == score2) {
						return 0;
					} else if (score1 > score2) {
						return -1;
					} else {
						return 1;
					}
				}
			});

			for (Point candidatePoint : candidatePoints) {
				Tile candidateTile = game.field.tiles.get(candidatePoint);
				if (!candidateTile.isHole
						&& candidateTile.installation == null
						&& game.field.getNumNeighborTilesWithInstallation(candidatePoint, null, 1) >= Installation.Attack.materialCost) {
					pointsToBuildTower.add(candidatePoint);
					break;
				}
			}
		}

		setNumRequiredRobotsForInstallation(game, numRequiredRobots, pointsToBuildTower, Installation.Attack);
	}

	private int getNumRobotsToMove(Point point, Tile tile, Map<Point, Integer> numRequiredRobots) {
		int numRobotsToMove;
		if (numRequiredRobots.containsKey(point)) {
			int surplus = tile.robot - numRequiredRobots.get(point);
			numRobotsToMove = Math.max(0, surplus);
		} else {
			numRobotsToMove = tile.robot;
		}
		return numRobotsToMove;
	}

	void moveRobots(final Game game, Map<Point, Integer> numRequiredRobots, Set<Point> pointsWithMovedRobots) {
		for (Point currentPoint : game.field.getPointsWithRobots(game.myId)) {
			Tile currentTile = game.field.tiles.get(currentPoint);

			if (pointsWithMovedRobots.contains(currentPoint)) {
				continue;
			}

			int numRobotsToMove = getNumRobotsToMove(currentPoint, currentTile, numRequiredRobots);
			if (numRobotsToMove == 0) {
				continue;
			}

			List<List<Point>> shortestPathToDestinations = new ArrayList<List<Point>>();

			for (Entry<Point, Integer> destination : numRequiredRobots.entrySet()) {
				Point destinationPoint = destination.getKey();
				Tile destinationTile = game.field.tiles.get(destinationPoint);
				int numLackingRobots = destination.getValue() - destinationTile.robot;
				if (numLackingRobots <= 0) {
					continue;
				}

				Algorithm algorithm = new Algorithm();
				List<Point> shortestPath = algorithm.getShortestPath(currentPoint, destinationPoint, false, game.field,
						game.myId);
				if (shortestPath.isEmpty()) {
					shortestPath = algorithm.getShortestPath(currentPoint, destinationPoint, true, game.field,
							game.myId);
				}

				if (shortestPath.size() > 0) {
					shortestPathToDestinations.add(shortestPath);
				}
			}

			if (shortestPathToDestinations.size() > 0) {
				Collections.sort(shortestPathToDestinations, new Comparator<List<Point>>() {
					@Override
					public int compare(List<Point> left, List<Point> right) {
						if (left.size() == right.size()) {
							return 0;
						} else if (left.size() < right.size()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				assert (numRobotsToMove <= currentTile.robot);
				move(currentPoint, currentPoint.getDirection(shortestPathToDestinations.get(0).get(0)), numRobotsToMove);
				pointsWithMovedRobots.add(currentPoint);
			}
		}
	}

	void moveRemainingRobots(final Game game, Map<Point, Integer> numRequiredRobots, Set<Point> pointsWithMovedRobots) {
		for (Point currentPoint : game.field.getPointsWithRobots(game.myId)) {
			if (!pointsWithMovedRobots.contains(currentPoint)) {
				List<Point> candidateAlternatePoints = game.field.getPointsInRadius(currentPoint, 1, false);
				List<Point> alternatePoints = new ArrayList<Point>();
				for (Point candidateAlternatePoint : candidateAlternatePoints) {
					Tile candidateAlternateTile = game.field.tiles.get(candidateAlternatePoint);
					if (!candidateAlternateTile.isHole && candidateAlternateTile.installation == null) {
						alternatePoints.add(candidateAlternatePoint);
					}
				}

				Tile currentTile = game.field.tiles.get(currentPoint);
				int numRobotsToMove = getNumRobotsToMove(currentPoint, currentTile, numRequiredRobots);

				if (numRobotsToMove > 0 && candidateAlternatePoints.size() > 0) {
					Point alternatePoint = candidateAlternatePoints
							.get(random.nextInt(candidateAlternatePoints.size()));
					assert (numRobotsToMove <= currentTile.robot);
					move(currentPoint, currentPoint.getDirection(alternatePoint), numRobotsToMove);
					pointsWithMovedRobots.add(currentPoint);
				}
			}
		}
	}

	private void processTurn(final Game game) {
		if (buildTower(game)) {
			return;
		}
		if (buildBridge(game)) {
			return;
		}

		Set<Point> pointsWithMovedRobots = new HashSet<Point>();
		Map<Point, Integer> numRequiredRobots = new HashMap<Point, Integer>();

		setNumRequiredRobotsForBridge(game, numRequiredRobots);
		setNumRequiredRobotsForTower(game, numRequiredRobots);
		moveRobots(game, numRequiredRobots, pointsWithMovedRobots);

		moveRemainingRobots(game, numRequiredRobots, pointsWithMovedRobots);
	}

	private void move(Point point, Direction dir, int robot) {
		System.out.println("move " + point.x + " " + point.y + " " + dir.command + " " + robot);
	}

	private void build(Point point, Installation inst) {
		System.out.println("build " + point.x + " " + point.y + " " + inst.name().toLowerCase());
	}

	private Game parseGame(Scanner scanner) {
		if (!scanner.next().equals("START")) {
			throw new RuntimeException("START should be retrieved.");
		}

		int turn = scanner.nextInt();
		int maxTurn = scanner.nextInt();
		int myId = scanner.nextInt();
		int radius = scanner.nextInt();
		Game game = new Game(turn, maxTurn, myId, new Field(radius));
		Field field = game.field;

		int nTiles = scanner.nextInt();
		for (int i = 0; i < nTiles; i++) {
			Point point = new Point(scanner.nextInt(), scanner.nextInt());

			int playerId = scanner.nextInt();
			int robot = scanner.nextInt();
			int resource = scanner.nextInt();
			String instName = scanner.next();
			String capitalizedName = instName.substring(0, 1).toUpperCase() + instName.substring(1);
			boolean isHole = false;
			Installation inst = null;
			if (capitalizedName.equals("Hole")) {
				isHole = true;
			} else if (!capitalizedName.equals("None")) {
				inst = Installation.valueOf(capitalizedName);
			}
			Tile tile = new Tile(playerId, robot, resource, isHole, inst);
			field.tiles.put(point, tile);
		}
		if (!scanner.next().equals("EOS")) {
			throw new RuntimeException("EOS should be retrieved.");
		}
		return game;
	}
}
