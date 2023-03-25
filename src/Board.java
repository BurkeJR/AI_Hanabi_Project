import java.util.ArrayList;
import java.util.Collections;

public class Board {
	public int numHints;
	public final int MAX_HINTS = 8;
	public int deckSize;
	public int numFuses;
	public final int MAX_FUSES = 3;
	public ArrayList<Integer> tableau;
	public ArrayList<Card> discards;
	
	public Board() {
		numHints = MAX_HINTS;
		numFuses = MAX_FUSES;
		deckSize = 50;
		tableau = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			tableau.add(0);
		}
		discards = new ArrayList<Card>();
	}
	
	public Board(Board b) {
		this.numHints = b.numHints;
		this.deckSize = b.deckSize;
		this.tableau = new ArrayList<Integer>(b.tableau);
		this.discards = new ArrayList<Card>(b.discards);
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "  Hints remaining: " + numHints + "\n";
		result += "  Fuses remaining: " + numFuses + "\n";
		result += "  Cards remaining in deck: " + deckSize + "\n";
		result += "  Tableau: Red " + tableau.get(Colors.RED) + ", Yellow " + tableau.get(Colors.YELLOW) +
				", Blue " + tableau.get(Colors.BLUE) + ", Green " + tableau.get(Colors.GREEN) +
				", White " + tableau.get(Colors.WHITE) + "\n";
		result += "  Discards: " + discards.toString();
		return result;
	}
	
	public void discard(Card c) {
		discards.add(c);
		Collections.sort(discards);
	}
	
	public int getTableauScore() {
		int total = 0;
		for (int i = 0; i < tableau.size(); i++) {
			total += tableau.get(i);
		}
		return total;
	}
	
	public boolean isLegalPlay(Card c) {
		return (c.value == (tableau.get(c.color) + 1));
	}
	
	public boolean play(Card c) {
		if (isLegalPlay(c)) {
			tableau.set(c.color, tableau.get(c.color) + 1);
			// Playing a 5 adds a hint.
			if (c.value == 5 && numHints < MAX_HINTS) {
				numHints++;
			}
			return true;
		}
		// Bad plays consume fuses.
		else {
			numFuses--;
			discard(c);
			return false;
		}
	}

}
