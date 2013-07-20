package jp.w125.terraforming.ai.api;

public class Point {
  public final int x;
  public final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Point))
      return false;
    Point other = (Point) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    return true;
  }

  public Point move(Direction direction) {
    return new Point(x + direction.x, y + direction.y);
  }

  public int length() {
    return (x * y < 0) ? Math.max(Math.abs(x), Math.abs(y)) : Math.abs(x)
        + Math.abs(y);
  }

  public int length(Point point) {
    return new Point(point.x - x, point.y - y).length();
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
