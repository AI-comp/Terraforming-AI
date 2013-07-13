import java.util.List;
import java.util.Random;
import java.util.Scanner;

import aicomp.net.terraforming.sample.Direction;
import aicomp.net.terraforming.sample.Field;
import aicomp.net.terraforming.sample.Game;
import aicomp.net.terraforming.sample.Installation;
import aicomp.net.terraforming.sample.Point;
import aicomp.net.terraforming.sample.Tile;

public class MySweetHouse {
  public static void main(String[] args) {
    Random rand = new Random("aicomp".hashCode());

    Scanner scanner = new Scanner(System.in);
    System.out.println("Ouchi_daisuki");

    while (scanner.hasNext()) {
      Game game = parseGame(scanner);
      if (game.turn % 3 != 0) {
        List<Point> points = game.field.getPointsWithRobots(game.myId);
        for (Point point : points) {
        	Tile tile = game.field.tiles.get(point);
        	if(tile.robot == 0 || (tile.isHole && tile.installation != Installation.Bridge)) continue;
        	expandMovement(tile, point, game);
        }
      } else {
        List<Point> points = game.field.getPointsWithRobots(game.myId);
        boolean finished = false;
        for (Point point : points) {
          Tile tile = game.field.tiles.get(point);
          if (checkBuildable(tile, game, point)) {
            finished = true;
            break;
          }
        }
        if(!finished) {
        for (Point point : points) {
          Tile tile = game.field.tiles.get(point);
          expandMovement(tile, point, game);
        }
        }
      }
      System.out.println("finish");
    }
    scanner.close();
  }

  private static void move(Point point, String dir, int robot) {
    System.out.println("move " + point.x + " " + point.y + " " + dir + " " + robot);
  }

  private static void build(Point point, Installation inst) {
    System.out.println("build " + point.x + " " + point.y + " " + inst.name().toLowerCase());
  }

  private static Game parseGame(Scanner scanner) {
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
  
  private static boolean checkBuildable(Tile tile, Game game, Point point) {
	  int material = tile.resource;
	  Tile around;
	  int[] dx = {1,1,0,-1,-1,0};
	  int[] dy = {0,-1,-1,0,1,1};
	  for(int i = 0; i< 6; i++){
		  around = game.field.tiles.get(new Point(point.x + dx[i], point.y + dy[i]));
		  if(around != null && around.playerId == game.myId) {
			  material += around.resource;
		  }
	  }
	  if(game.turn % 9 == 0 && tile.robot >= 15 && material >= 4 && tile.isHole  && tile.installation == null){
		  build(point, Installation.values()[2]);
		  return true;
	  }
	  if (!tile.isHole && tile.robot >= 10 && material >= 4 && tile.installation == null){
		  build(point, Installation.values()[6]);
		  return true;
	  }
	  return false;
  }
  
  private static String getDirection(int i) {
	  switch(i) {
	  case 0: return "l";
	  case 1: return "r";
	  case 2: return "ul";
	  case 3: return "ur";
	  case 4: return "dl";
	  case 5: return "dr";
	  default: return "ul";
	  }
}
  
  private static void expandMovement(Tile tile, Point point, Game game) {
	  Tile around;
	  int min = -1;
	  int myMin = -1;
	  String decition = "ul";
	  String mydecition = "ul";
	  int[] dx = {-1,0,0,1,-1,0};
	  int[] dy = {0,1,-1,-1,1,1};
	  for(int i = 0; i< 6; i++){
		  around = game.field.tiles.get(new Point(point.x + dx[i], point.y + dy[i]));
		  if(around == null) continue;
		  if(around.playerId == -1) {
			  move(point, getDirection(i), (int)(tile.robot * 0.4) + 1);
			  return;
		  } else if(around.playerId == game.myId && tile.robot > around.robot){
			  if(myMin == -1 || myMin > around.robot){
				  myMin = around.robot;
				  mydecition = getDirection(i);
			  }
		  } else if(around.playerId != game.myId && tile.robot > around.robot){
			  if(around.installation != Installation.House && (min == -1 || min > around.robot)){
				  min = around.robot;
				  decition = getDirection(i);
			  }
		  }
		  if(min != -1) {
			  move(point, decition, (int)(tile.robot * 0.4) + 1);
		  } else if(myMin != -1) {
			  move(point, mydecition, (int)(tile.robot * 0.4) + 1);
		  }
	  }
  }
}
