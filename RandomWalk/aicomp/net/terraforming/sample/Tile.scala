package aicomp.net.terraforming.sample

case class Tile(ownerId: Int, robots: Int, materials: Int, instStr: String)
{
  val isHole = instStr == "hole"
  val isNone = instStr == "none"
  val inst = Installation.buildables.find(_.name == instStr).map{v => v}.getOrElse(null) 

  val isOwnerEmpty = ownerId == -1
  def isOwned(playerId: Int) = ownerId == playerId
  def isOwnedByEnemy(playerId: Int) = !isOwned(playerId) && !isOwnerEmpty 
}
