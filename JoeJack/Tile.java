package JoeJack;

public class Tile {
  public final int playerId;
  public final int robot;
  public final int resource;
  public final boolean isHole;
  public final Landform landform;
  public final Installation installation;

  public Tile(int playerId, int robot, int resource, boolean isHole, Landform landform,
      Installation installation) {
    this.playerId = playerId;
    this.robot = robot;
    this.resource = resource;
    this.isHole = isHole;
    this.landform = landform;
    this.installation = installation;
  }

  public int getScore(int targetPlayerId) {
    if (playerId != targetPlayerId) {
      return 0;
    }
    if (installation == Installation.None) {
      return 1;
    }
    return installation.score;
  }
	public boolean canBuildInstallation(Installation installation, Field field, int playerId) {
		Point point = field.getPointFromTile(this);
		return (
		    (installation == Installation.Bridge) == isHole && this.installation == Installation.None
				&& field.getNumAvailableResources(point, playerId) >= installation.materialCost && robot >= installation.robotCost);
	}

	public boolean canMoveInto(int playerId) {
		if (this.playerId != playerId && installation != Installation.None) {
			return false;
		} else {
			return true;
		}
	}
}
