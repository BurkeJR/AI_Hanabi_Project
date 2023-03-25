
public class Colors {
	public static final int RED = 0;
	public static final int YELLOW = 1;
	public static final int BLUE = 2;
	public static final int GREEN = 3;
	public static final int WHITE = 4;
	
	public static boolean isSuit(int x) {
		return (x == RED) || (x == YELLOW) || (x == BLUE) || (x == GREEN) || (x == WHITE);
	}
	
	public static String suitColor(int x) {
		if (x == RED) { return "Red";}
		if (x == YELLOW) { return "Yellow";}
		if (x == BLUE) { return "Blue";}
		if (x == GREEN) { return "Green";}
		if (x == WHITE) { return "White";}
		return "INVALID";
	}

}
