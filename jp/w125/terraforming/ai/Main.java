package jp.w125.terraforming.ai;

import java.util.Scanner;

import jp.w125.terraforming.ai.api.*;

public class Main {

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    Game game = parseGame(scanner);
    // AI ai = (game.myId == 0) ? new WandHouse()
    // : ((game.myId == 1) ? new WandFactory() : new WandAI());
    AI ai = new WandFactory();

    if (args.length == 1) {
      if (args[0].equals("robot")) {
        ai = new WandAI();
      } else if (args[0].equals("house")) {
        ai = new WandHouse();
      }
    }

    System.out.println(ai.name());

    while (scanner.hasNext()) {
      game = parseGame(scanner);

      ai.command(game);

      System.out.println("finish");
    }
    scanner.close();
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
      boolean isHole = false;

      String landformName = scanner.next();
      String capitalizedlandformName = landformName.substring(0, 1)
          .toUpperCase() + landformName.substring(1);
      Landform landform = Landform.valueOf(capitalizedlandformName);

      String instName = scanner.next();
      String capitalizedInstName = instName.substring(0, 1).toUpperCase()
          + instName.substring(1);
      Installation inst = null;
      if (capitalizedInstName.equals("Hole")) {
        isHole = true;
      } else {
        inst = Installation.valueOf(capitalizedInstName);
      }

      Tile tile = new Tile(playerId, robot, resource, isHole, landform, inst);
      field.tiles.put(point, tile);
    }
    if (!scanner.next().equals("EOS")) {
      throw new RuntimeException("EOS should be retrieved.");
    }
    return game;
  }
}
