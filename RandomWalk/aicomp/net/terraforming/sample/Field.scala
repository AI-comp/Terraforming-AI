package aicomp.net.terraforming.sample


case class Field(radius: Int, tileSize: Int, tiles: Map[Point, Tile]) {
  def ownedTiles(playerId: Int) : Map[Point, Tile] = tiles.filter{kv => kv._2.ownerId == playerId}

  def aroundMaterialAmount(point: Point, playerId: Int) = {
    val materials = point.aroundPoints().map{p => {
      tiles.get(p).map(tile => if(tile.isOwned(playerId)) tile.materials else 0
      ).getOrElse(0)
    }}.foldLeft(0)(_+_)

    (materials + tiles(point).materials)
  }

  def aroundInstallationTiles(point: Point, inst: Installation, length: Int = 1) = {
    point.aroundPoints(length).filter(tiles.get(_).map(v=>true).getOrElse(false))
                              .map(p => (p, tiles(p))).filter(kv => kv._2.inst == inst)
  }

  def canEnter(point: Point, dir: Direction) = tiles.get(point + dir).map(_ => true).getOrElse(false)

  def canBuildInstallation(point: Point, inst: Installation, playerId: Int) : Boolean = {
    val tile = tiles(point)
    val cond = if(inst == Installation.bridge) tile.isHole else tile.isNone
    (cond && tile.robots >= inst.robotCost && aroundMaterialAmount(point, playerId) >= inst.materialCost)
  }

  def hasSufficientMaterialAmount(point: Point, inst: Installation, playerId: Int) = 
    aroundMaterialAmount(point, playerId) >= inst.materialCost
}
