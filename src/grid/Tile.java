package grid;

public class Tile {
	private int x, y;

	public enum Type {
		EMPTY, RESOURCE
	}

	private Type type = Type.EMPTY;
	private String resource = null; // "A", "T", "G", "C" if it's a resource tile

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setResource(String code) {
		this.type = Type.RESOURCE;
		this.resource = code;
	}

	public boolean isResource() {
		return type == Type.RESOURCE;
	}

	public String getResource() {
		return resource;
	}

	public Type getType() {
		return type;
	}
}
