import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards;
	
	public Hand() {
		cards = new ArrayList<Card>();
	}
	
	public Hand(Hand h) {
		cards = new ArrayList<Card>(h.cards);
	}
	
	public Card get(int index) throws Exception {
		if ((index >=0) && (index < cards.size())) {
			return new Card(cards.get(index));
		}
		else {
			throw new Exception("Hand.get() - index " + index + " out of bounds for hand size " + cards.size());
		}
	}
	
	public void add(int index, Card c) throws Exception {
		if ((index >= 0) && (index <= cards.size())) {	// Note we can add past the end of the array.
			cards.add(index, c);
		}
		else {
			throw new Exception("Hand.add() - index " + index + " out of bounds for hand size " + cards.size());
		}
	}
	
	public Card remove(int index) throws Exception {
		if ((index >= 0) && (index < cards.size())) {
			return cards.remove(index);
		}
		else {
			throw new Exception("Hand.remove() - index " + index + " out of bounds for hand size " + cards.size());
		}
	}
	
	public int size() {
		return cards.size();
	}
	
	@Override
	public String toString() {
		if (cards.size() == 0) {
			return "Empty";
		}
		else if (cards.size() == 1) {
			return cards.get(0).toString();
		}
		else if (cards.size() == 2) {
			return cards.get(0).toString() + " and " + cards.get(1).toString();
		}
		else {
			String result = "";
			for (int i = 0; i < cards.size(); i++) {
				result += cards.get(i).toString();
				if (i < cards.size() - 1) {
					result += ", ";
				}
				if (i == cards.size() - 2) {
					result += "and ";
				}
			}
			return result;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Hand)) {
			return false;
		}
		Hand h = (Hand) o;
		return this.cards.equals(h.cards);
	}

}
