import util.parsing.combinator._
import aicomp.net.terraforming.sample._
import scala.util.Random

object StringifyParser extends RegexParsers {
  val num = "-?[0-9]+".r ^^ { _.toInt }
  val str = "[a-zA-Z]+".r
  val eol = "\n"

  val gameStringify = num ~ num ~ num ~ fieldStringify ^^ {
    case currentTurn ~ maxTurn ~ playerId ~ field =>
      new Game(currentTurn, maxTurn, playerId, field)
  }

  val fieldStringify = num ~ num ~ rep(tileStringify) ^^ {
    case radius ~ tileSize ~ tiles =>
      new Field(radius, tileSize, tiles.toMap)
  }

  val tileStringify = num ~ num ~ num ~ num ~ num ~ str ^^ {
    case x ~ y ~ ownerId ~ robots ~ resource ~ inst =>
      (new Point(x, y), new Tile(ownerId, robots, resource, inst))
  }

  def parse(input: String) = parseAll(gameStringify, input)
}

case class Phase(start: Int, end: Int)
object Phase {
  val generateRobot    = Phase(0  , 75)
  val generateMaterial = Phase(75 , 125)
  val build            = Phase(125, 190)
  val spurt            = Phase(190, 201)

  val all = List(generateRobot, generateMaterial, build, spurt)
}

object Main {
  var game: Game = null 
  def tiles = game.field.tiles
  def field = game.field

  val random = new Random()
  var builded = false

  def build(point: Point, inst: Installation) {
    builded = true
    System.out.println("build " + point.stringify() + " " + inst);
  }

  def move(point: Point, dir: Direction, amount: Int) {
    System.out.println("move " + point.stringify() + " " + dir.toString() + " " + amount)
  }

  def getPhase() = {
    Phase.all.find(phase => (phase.start <= game.currentTurn && game.currentTurn < phase.end))
    .map{v=>v}.getOrElse(Phase.spurt)
  }

  def getBuildTarget(point: Point) = {
    getBuildTargets(point).filter(target => field.canBuildInstallation(point, target, game.playerId)).headOption.map(v => Some(v)).getOrElse(None)
  }

  def getBuildTargets(point: Point) = {
    val cityTown = List(/*Installation.city,*/ Installation.town)
    val townBuildable = field.hasSufficientMaterialAmount(point, Installation.town, game.playerId)

    getPhase() match {
      case Phase.generateMaterial => if(townBuildable) cityTown else List(Installation.pit)
      case Phase.generateRobot    => if(townBuildable) cityTown else List(Installation.factory)
      case Phase.build => List(/*Installation.city,*/ Installation.town)
      case Phase.spurt => List(/*Installation.city,*/ Installation.town, Installation.house)
    }
  }

  def randomBuild() {
    val ownTiles = field.ownedTiles(game.playerId)
    val ownHoleTiles = ownTiles.filter(kv => kv._2.isHole && field.canBuildInstallation(kv._1, Installation.bridge, game.playerId))

    random.shuffle(ownHoleTiles).headOption.map {hole =>
      build(hole._1, Installation.bridge)
    }
    .getOrElse {
      for((point, tile) <- field.ownedTiles(game.playerId)) {
        getBuildTarget(point).map{inst => build(point, inst)}
      }
    }
  }

  def randomMove() = {
    val movableTiles = field.ownedTiles(game.playerId).filter(kv => (kv._2.robots > 0 && !kv._2.isHole))
    def isInAttackRange(point: Point) = !field.aroundInstallationTiles(point, Installation.attack).isEmpty

    if(!movableTiles.isEmpty) {
      movableTiles.foreach{kv => {
        val (point, tile) = kv
        val movableDirs = Direction.all.filter(dir => field.canEnter(point, dir))
        val safeMovableDirs = movableDirs.filter(dir => !isInAttackRange(point + dir))

        if(!movableDirs.isEmpty) {
          val notOwnTiles = movableDirs.filter(dir => tiles(point + dir).isOwnerEmpty)
          val enemyTiles = safeMovableDirs.filter(dir => tiles(point + dir).isOwnedByEnemy(game.playerId))

          val highPriorityTiles = safeMovableDirs.filter(dir => { 
            val dest = (point + dir)
            val materials = field.aroundMaterialAmount(point, game.playerId);
            val requireCost = getBuildTargets(dest).map(_.materialCost).min
            tiles(dest).isNone && materials >= requireCost
          })

          val dir = 
          if     (!notOwnTiles.isEmpty) random.shuffle(notOwnTiles).head
          else if(!highPriorityTiles.isEmpty) random.shuffle(highPriorityTiles).head
          else if(!enemyTiles.isEmpty) enemyTiles.sortWith((e1, e2) => tiles(e1 + point).robots < tiles(e2 + point).robots).head
          else safeMovableDirs.sortWith((e1, e2) => tiles(e1 + point).robots < tiles(e2 + point).robots).head

          val amount = if(tile.robots > 1) random.shuffle(tile.robots/2 to tile.robots).head else 1

          move(point, dir, amount)
        }
      }
    }}
  }

  def main(args: Array[String]) {
    var line = ""

    System.out.println("Random Walk")
    while ({ line = readLine(); line ne null }) {
      if (line != "START") Iterator.continually(readLine()).takeWhile(_ != "START")
      val commands = Iterator.continually(readLine()).takeWhile(_ != "EOS").toList

      val result = StringifyParser.parse(commands.mkString("\n"))
      if ( result.successful ){
        game = result.get

        builded = false
        randomBuild()
        if(!builded) randomMove();
      }

      System.out.println("finish")
    }
  }
}

