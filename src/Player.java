import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Collections;
/**
 * This is the only class you should edit.
 * @author You
 *
 */
public class Player {
	// Add whatever variables you want. You MAY NOT use static variables, or otherwise allow direct communication between
	// different instances of this class by any means; doing so will result in a score of 0.

	ArrayList<Integer> knownColors;
	ArrayList<Integer> knownValues;
	ArrayList<Boolean> hasHintedColor;
	ArrayList<Boolean> hasHintedValue;
	ArrayList<Integer> playableIndexes;
	ArrayList<Integer> discardableIndexes;
	ArrayList<Integer> lastHintIndices;
	ArrayList<Card> deck;

	boolean wasHinted, numHint, colorHint;
	int numCardsChangedByHint, indexOfSingleCardHint;
	int turn;
	
	// Delete this once you actually write your own version of the class.
	private static Scanner scn = new Scanner(System.in);
	
	/**
	 * This default constructor should be the only constructor you supply.
	 */
	public Player() {
		knownColors = new ArrayList<>(Arrays.asList(-1,-1,-1,-1,-1)); //All unknown
		knownValues = new ArrayList<>(Arrays.asList(0,0,0,0,0)); //All unknown
		hasHintedColor = new ArrayList<>(Arrays.asList(false,false,false,false,false)); //No hints given
		hasHintedValue = new ArrayList<>(Arrays.asList(false,false,false,false,false)); //No hints given
		wasHinted = false;
		numHint = false;
		colorHint = false;
		numCardsChangedByHint = 0;
		indexOfSingleCardHint = -1;
		playableIndexes = new ArrayList<>();
		discardableIndexes = new ArrayList<>();
		lastHintIndices = new ArrayList<>();

		//Make deck for counting cards
		deck = new ArrayList<>();
		// Loads deck with three of each 1, two of each 2-3-4, and one of each 5.
		for (int i = 0; i < 5; i++) {
			deck.add(new Card(i, 1));
			deck.add(new Card(i, 1));
			deck.add(new Card(i, 1));
			deck.add(new Card(i, 2));
			deck.add(new Card(i, 2));
			deck.add(new Card(i, 3));
			deck.add(new Card(i, 3));
			deck.add(new Card(i, 4));
			deck.add(new Card(i, 4));
			deck.add(new Card(i, 5));
		}

		turn = 0; //Set turn to 0
	}
	
	/**
	 * This method runs whenever your partner discards a card.
	 * @param startHand The hand your partner started with before discarding.
	 * @param discard The card he discarded.
	 * @param disIndex The index from which he discarded it.
	 * @param draw The card he drew to replace it; null, if the deck is empty.
	 * @param drawIndex The index to which he drew it.
	 * @param finalHand The hand your partner ended with after redrawing.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerDiscard(Hand startHand, Card discard, int disIndex, Card draw, int drawIndex, 
			Hand finalHand, Board boardState) {
		hasHintedValue.remove(disIndex);
		hasHintedColor.remove(disIndex);
		hasHintedValue.add(false);
		hasHintedColor.add(false);


		deck.remove(draw); //Removes card they drew from possible unknowns
	}
	
	/**
	 * This method runs whenever you discard a card, to let you know what you discarded.
	 * @param discard The card you discarded.
	 * @param boardState The state of the board after play.
	 */
	public void tellYourDiscard(Card discard, Board boardState) {
		deck.remove(discard);
	}
	
	/**
	 * This method runs whenever your partner played a card
	 * @param startHand The hand your partner started with before playing.
	 * @param play The card she played.
	 * @param playIndex The index from which she played it.
	 * @param draw The card she drew to replace it; null, if the deck was empty.
	 * @param drawIndex The index to which she drew the new card.
	 * @param finalHand The hand your partner ended with after playing.
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerPlay(Hand startHand, Card play, int playIndex, Card draw, int drawIndex,
			Hand finalHand, boolean wasLegalPlay, Board boardState) {
		hasHintedValue.remove(playIndex);
		hasHintedColor.remove(playIndex);
		hasHintedValue.add(false);
		hasHintedColor.add(false);

		deck.remove(draw); //Removes card they drew from possible unknowns
	}
	
	/**
	 * This method runs whenever you play a card, to let you know what you played.
	 * @param play The card you played.
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellYourPlay(Card play, boolean wasLegalPlay, Board boardState) {
		deck.remove(play);
	}
	
	/**
	 * This method runs whenever your partner gives you a hint as to the color of your cards.
	 * @param color The color hinted, from Colors.java: RED, YELLOW, BLUE, GREEN, or WHITE.
	 * @param indices The indices (from 0-4) in your hand with that color.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellColorHint(int color, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		numCardsChangedByHint = indices.size();
		wasHinted = true;
		colorHint = true;
		if (numCardsChangedByHint == 1) {
			indexOfSingleCardHint = indices.get(0);
		} else {
			lastHintIndices = indices;
		}
		for (Integer i : indices) {
			knownColors.set(i, color);
		}

	}
	
	/**
	 * This method runs whenever your partner gives you a hint as to the numbers on your cards.
	 * @param number The number hinted, from 1-5.
	 * @param indices The indices (from 0-4) in your hand with that number.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellNumberHint(int number, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		numCardsChangedByHint = indices.size();
		wasHinted = true;
		numHint = true;
		if (numCardsChangedByHint == 1) {
			indexOfSingleCardHint = indices.get(0);
		} else {
			lastHintIndices = indices;
		}
		for (Integer i : indices) {
			knownValues.set(i, number);
		}
	}
	
	/**
	 * This method runs when the game asks you for your next move.
	 * @param yourHandSize How many cards you have in hand.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The current state of the board.
	 * @return A string encoding your chosen action. Actions should have one of the following formats; in all cases,
	 *  "x" and "y" are integers.
	 * 	a) "PLAY x y", which instructs the game to play your card at index x and to draw a card back to index y. You
	 *     should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Illegal plays will consume a fuse; at 0 fuses, the game ends with a score of 0.
	 *  b) "DISCARD x y", which instructs the game to discard the card at index x and to draw a card back to index y.
	 *     You should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Discarding returns one hint if there are fewer than the maximum number available.
	 *  c) "NUMBERHINT x", where x is a value from 1-5. This command informs your partner which of his cards have a value
	 *     of the chosen number. An error will result if none of his cards have that value, or if no hints remain.
	 *     This command consumes a hint.
	 *  d) "COLORHINT x", where x is one of the RED, YELLOW, BLUE, GREEN, or WHITE constant values in Colors.java.
	 *     This command informs your partner which of his cards have the chosen color. An error will result if none of
	 *     his cards have that color, or if no hints remain. This command consumes a hint.
	 */
	public String ask(int yourHandSize, Hand partnerHand, Board boardState) throws Exception {
		infer(boardState);

		if (turn == 0) {
			//Only runs on first turn, removes all cards from our deck in partner's hand
			for (int i = 0; i < 5; i++) {
				deck.remove(partnerHand.get(i));
			}
		}
		turn++;

		if (wasHinted && numCardsChangedByHint == 1) {
			wasHinted = false; //reset val
			numCardsChangedByHint = 0; //reset val
			int index = indexOfSingleCardHint; //store val locally, reset class
			indexOfSingleCardHint = -1; //reset val
			//Was hinted only one color or value
			if (colorHint) {
				colorHint = false; //reset val
				//Remove hinted number from known Cards and add replacement to back of hand
				int color = knownColors.get(index);

				knownColors.remove(index);
				knownValues.remove(index);
				knownValues.add(0);
				knownColors.add(-1);

				if (boardState.tableau.get(color) != 5) {
					// if the color isn't complete, then play
					return playMsg(index);
				}
				else {
					return discardMsg(index);
				}
			}
			if (numHint) {
				numHint = false; //reset val
				int value = knownValues.get(index);

				knownColors.remove(index);
				knownValues.remove(index);
				knownValues.add(0);
				knownColors.add(-1);

				int playableSpots = 0;
				for (int spot : boardState.tableau) {
					playableSpots += (spot == value - 1) ? 1 : 0;
				}
				if (playableSpots > 0) {
					return playMsg(index);
				}
				else if (value != 5) {
					return discardMsg(index);
				}
			}
		}
		wasHinted = false;
		colorHint = false;
		numHint = false;


		if (playableIndexes.size() != 0) {
			//Play from playables
			int index = playableIndexes.get(0);
			knownColors.remove(index);
			knownValues.remove(index);
			knownValues.add(0);
			knownColors.add(-1);
			return playMsg(index);
			//Just play nearest thing in playables
		}

		//TODO: check for any infrences (maybe done when we get hints or they play) so we can just use list above





		//TODO: check for hintable cards

		String hint = hint(boardState, partnerHand);//Return an empty string if no hint to give

		if (!hint.equals("")) {
			return hint;
		}


		if (discardableIndexes.size() != 0 && boardState.numHints < 6) {
			//Discard from here
			int index = discardableIndexes.get(0);
			knownColors.remove(index);
			knownValues.remove(index);
			knownValues.add(0);
			knownColors.add(-1);
			return discardMsg(discardableIndexes.get(0));
			//Discard nearest thing in discardables
		}


		//TODO: Check if we should gamble
		ArrayList<Card> playable = getPossiblePlayableCards(boardState);

		if (shouldGamble(boardState, playable)) {
			int index = gamble(playable);
			knownColors.remove(index);
			knownValues.remove(index);
			knownValues.add(0);
			knownColors.add(-1);
			return playMsg(index);
		}


		//Discard first non 5 card
		for (int i = 0; i < knownValues.size(); i++) {
			if (knownValues.get(i) == 5) {
				continue;
			}
			knownColors.remove(i);
			knownValues.remove(i);
			knownValues.add(0);
			knownColors.add(-1);
			return discardMsg(i);
		}

		/* This statement will be reached only if we have no hints, no safely playable or discardable
		 * cards, and we know that all of our cards are 5s. It is unlikely that this will happen, but 
		 * we need to account for it so that we return something in that case. 
		 */
		return discardMsg(0);
	}


	/**
	 * Checks for any cards that are discardable
	 * Also checks if we guarantee we can play a card
	 * Call every time we wish to know things
	 * Alters playableIndexes and discardableIndexes
	 * Checks if any valued card in hand is less than minimum tableau value
	 * Also looks at cards that we know color of, and
	 *
	 * @param boardState Board to infer based off of
	 */
	private void infer(Board boardState) {
		playableIndexes.clear();
		discardableIndexes.clear();

		int minTableau = 5;
		for (Integer i : boardState.tableau) {
			minTableau = i < minTableau ? i : minTableau;
		}
		//Get minimum value in tableau


		//Adds index of all values where the known value is <= minimum tableau value
		for (int i = 0; i < knownValues.size(); i++) {
			if (knownValues.get(i) == 0) {
				continue;
			}
			if (knownValues.get(i) <= minTableau) {
				discardableIndexes.add(i);
			}
		}


		//Goes through each color on tableau
		//Loops through what we know of hand
		//If card with color
		for (int i = 0; i < boardState.tableau.size(); i++) {
			if (!knownColors.contains(i)) {
				continue;
			}

			//Loop through what we know of our hand
			for (int j = 0; j < knownColors.size(); j++) {
				if (knownColors.get(j) != i) {
					continue;
				}
				//Same color

				if (knownValues.get(j) == -1) {
					//We only know color
					continue;
				}

				if (knownValues.get(j) == boardState.tableau.get(i) + 1) {
					//Card is 1 greater than corresponding value in tableau
					playableIndexes.add(j);
					continue;
				}

				if (knownValues.get(j) <= boardState.tableau.get(i)) {
					//Card is less than or equal to corresponding stack
					//So is discard-able
					discardableIndexes.add(j);
				}
			}
		}
	}

	private boolean shouldGamble(Board boardState, ArrayList<Card> playable) {
		if (boardState.numFuses <= 1) {
			// we don't have enough lives to gamble
			return false;
		}

		if (boardState.deckSize < boardState.numFuses) {
			/* There are less cards in the deck than fuses. 
			 * So we should just play something since discarding will bring us closer
			 * to the end of the game while not increasing our score. We can guess
			 * without losing and have the possibility of gaining a point. 
			 */
			return true;
		}

		// get proportion of playable cards that will give us points to total playable cards
		int total = boardState.deckSize + 10; // 10 because each player has 5 cards

		// if we have at least a 50% chance of getting a point from a guess, then gamble
		if (playable.size()/total >= 0.5) {
			gamble(playable);
		}

		return false;
	}

	/** 
	 * returns the index of the card we should play on a gamble
	 */
	public int gamble(ArrayList<Card> playable) {
		// chances of each of our cards being something playable 
		double chances[] = {0, 0, 0, 0, 0};
		
		/*
		* If we already know what some cards are, then remove them from playable since the other cards in
		* our hand can't be those. If it could have been played, we wouldn't have gotten to gamble.
		* So we can remove this card from our gamble candidates. 
		*/
		for (int card = 0; card < knownColors.size(); card++) {
			if (knownColors.get(card) != -1 && knownValues.get(card) != 0) {
				playable.remove(new Card(knownColors.get(card), knownValues.get(card)));
				chances[card] = -1; // set to -1 so we know not to bother with this card 
			}
		}

		// find the chance of that each of our cards could be one of the playable cards 
		for (int card = 0; card < knownColors.size(); card++) {
			// skip over cards we already know about 
			if (chances[card] != -1) {
				// we know the color
				if (knownColors.get(card) != -1) {
					// chance = number of next playable cards of this color / total number of playable cards of this color
					chances[card] = countColorInList(knownColors.get(card), playable) / (countColorInList(knownColors.get(card), deck) + 0.0);
				}
				// we know the number
				else if (knownValues.get(card) != 0) {
					// chance = number of next playable cards of this number / total number of playable cards of this number
					chances[card] = countValuesInList(knownValues.get(card), playable) / (countValuesInList(knownValues.get(card), deck) + 0.0);
				}
				// we know nothing about the card
				else {
					// chance = the number of playable cards / cards left in the deck
					chances[card] = playable.size() / deck.size();
				}
			}
		}

		// play the card with the highest chance. if tied, play the newest card (the one closer to the end)
		int max = 4;
		for (int i = 3; i <= 0; i--) {
			if (chances[i] > chances[max]) {
				max = i;
			}
		}

		return max;
	}

	public ArrayList<Card> getPossiblePlayableCards(Board boardState) {
		ArrayList<Card> playable = new ArrayList<>();

		for (int color = 0; color < 5; color++) {
			// get the cards that can be played on each color
			Card card = new Card(color, boardState.tableau.get(color) + 1);
			int count = Collections.frequency(deck, card);

			for (int i = 0; i < count; i++) {
				// add the card the number of times it is in the remaining deck
				playable.add(card);
			}
		}

		return playable;
	}

	private int countColorInList(int color, ArrayList<Card> list) {
		int count = 0;
		for (Card card : list) {
			if (card.color == color) {
				count++;
			}
		}
		return count;
	}

	private int countValuesInList(int value, ArrayList<Card> list) {
		int count = 0;
		for (Card card : list) {
			if (card.value == value) {
				count++;
			}
		}
		return count;
	}

	private String playMsg(int x) {
		//System.out.println("PLAY " + x + " " + (knownColors.size() - 1));
		return "PLAY " + x + " " + (knownColors.size() - 1);
	}

	private String discardMsg(int x) {
		//System.out.println("DISCARD " + x + " " + (knownColors.size() - 1));
		return "DISCARD " + x + " " + (knownColors.size() - 1);
	}

	private String colorHintMsg(int x) {
		//System.out.println("COLORHINT " + x);
		return "COLORHINT " + x;
	}

	private String numHintMsg(int x) {
		//System.out.println("NUMBERHINT " + x);
		return "NUMBERHINT " + x;
	}

	private String hint(Board boardState, Hand partnerHand) throws Exception {
		if (boardState.numHints == 0) {
			return "";
		}

		int maxTableau = 0;
		int minTableau = 5;

		//Get max and mins
		for (Integer i : boardState.tableau) {
			if (i > maxTableau) {
				maxTableau = i;
			}
			if (i < minTableau) {
				minTableau = i;
			}
		}

		ArrayList<Integer> partnerHandVals = new ArrayList<>();
		ArrayList<Integer> partnerHandColors = new ArrayList<>();
		ArrayList<Card> partnerHandCards = new ArrayList<>();

		for (int i = 0; i < partnerHand.size(); i++) {
			Card c = partnerHand.get(i);
			partnerHandColors.add(c.color);
			partnerHandVals.add(c.value);
			partnerHandCards.add(c);
		}

		boolean discardOnes = minTableau > 0 && partnerHandVals.stream().filter(i -> i == 1).count() > 1;
		boolean discardTwos = minTableau > 1 && partnerHandVals.stream().filter(i -> i == 2).count() > 1;
		boolean discardThrees = minTableau > 2 && partnerHandVals.stream().filter(i -> i == 3).count() > 1;

		boolean playAllOnes = maxTableau < 1 && partnerHandVals.stream().filter(i -> i == 1).count() > 1;
		boolean playAllTwos = minTableau == maxTableau && minTableau == 1 && partnerHandVals.stream().filter(i -> i == 2).count() > 1;
		boolean playAllThrees = minTableau == maxTableau && minTableau == 2 && partnerHandVals.stream().filter(i -> i == 3).count() > 1;

		if (partnerHandVals.contains(5) && partnerHandVals.indexOf(5) == 0 && !hasHintedValue.get(0)) {
			hasHintedValue.set(0, true);
			return numHintMsg(5);
		}

		//Hint for all of one number
		if (playAllOnes && !hasHintedValue.get(partnerHandVals.indexOf(1))) {
			hasHintedValue.set(partnerHandVals.indexOf(1), true);
			return numHintMsg(1);
		} else if (playAllTwos && !hasHintedValue.get(partnerHandVals.indexOf(2))) {
			hasHintedValue.set(partnerHandVals.indexOf(2), true);
			return numHintMsg(2);
		} else if (playAllThrees && !hasHintedValue.get(partnerHandVals.indexOf(3))) {
			hasHintedValue.set(partnerHandVals.indexOf(3), true);
			return numHintMsg(3);
		}

		int index = hasPlayableCard(partnerHandCards, boardState);

		//If can play card, check if have only 1;
		if (index != -1) {
			Card playablePartnerCard = partnerHandCards.get(index);

			if (partnerHandColors.stream().filter(i -> i == playablePartnerCard.color).count() == 1 && !hasHintedValue.get(index)) {
				hasHintedValue.set(index, true);
				return numHintMsg(partnerHandVals.get(index));
			} else if (partnerHandVals.stream().filter(i -> i == playablePartnerCard.value).count() == 1 && !hasHintedColor.get(index)) {
				hasHintedColor.set(index, true);
				return colorHintMsg(partnerHandColors.get(index));
			}

			//Just Hint it anyways, favoring hinting value
			if (!hasHintedValue.get(index)) {
				hasHintedValue.set(index, true);
				return numHintMsg(partnerHandVals.get(index));
			} else if (!hasHintedColor.get(index)) {
				hasHintedColor.set(index, true);
				return colorHintMsg(partnerHandColors.get(index));
			}
		}

		if (boardState.numHints < 3) {
			return "";
		}


		if (hasHintedValue.contains(true) && !hasHintedColor.get(hasHintedValue.indexOf(true))) {
			//Have hinted values, need to hint colors
			hasHintedColor.set(hasHintedValue.indexOf(true), true);
			return colorHintMsg(partnerHandColors.get(hasHintedValue.indexOf(true)));
		} else if (hasHintedColor.contains(true) && !hasHintedValue.get(hasHintedColor.indexOf(true))) {
			//have hinted colors, need to hint values
			hasHintedValue.set(hasHintedColor.indexOf(true), true);
			return numHintMsg(partnerHandVals.get(hasHintedColor.indexOf(true)));
		}

		Tuple tuple = getBigHint(partnerHandVals, partnerHandColors);

		boolean hintHum = tuple.hintNum;
		int biggestHint = tuple.biggestHint;
		int biggestHintIndex = tuple.biggestHintIndex;

		if (hintHum && biggestHint > 2 && !hasHintedValue.get(biggestHintIndex)) {
			hasHintedValue.set(biggestHintIndex, true);
			return numHintMsg(partnerHandVals.get(biggestHintIndex));
		} else if (biggestHint > 2 && !hasHintedColor.get(biggestHintIndex)) {
			hasHintedColor.set(biggestHintIndex, true);
			return colorHintMsg(partnerHandColors.get(biggestHintIndex));
		}

		//Hint for all of one number for discard
		if (discardOnes && !hasHintedValue.get(partnerHandVals.indexOf(1))) {
			hasHintedValue.set(partnerHandVals.indexOf(1), true);
			return numHintMsg(1);
		} else if (discardTwos && !hasHintedValue.get(partnerHandVals.indexOf(2))) {
			hasHintedValue.set(partnerHandVals.indexOf(2), true);
			return numHintMsg(2);
		} else if (discardThrees && !hasHintedValue.get(partnerHandVals.indexOf(3))) {
			hasHintedValue.set(partnerHandVals.indexOf(3), true);
			return numHintMsg(3);
		}


		return "";
	}

	/**
	 * Used to check if partner has a card that is obviously playable and hintable directly
	 * @param partnerCards list of cards in partners hand
	 * @param boardState Board
	 * @return index of playable card if can play, -1 otherwise
	 */
	private int hasPlayableCard(ArrayList<Card> partnerCards, Board boardState) {
		for (int i = 0; i < boardState.tableau.size(); i++) {
			Card c = new Card(i,boardState.tableau.get(i) + 1); //Gets the playable card for that color
			if (partnerCards.contains(c)) {
				return partnerCards.indexOf(c);
			}
		}

		return -1;
	}

	private Tuple getBigHint(ArrayList<Integer> partnerHandVals, ArrayList<Integer> partnerHandColors) {
		int[] valsSize = {0,0,0,0,0};
		int[] colorSize = {0,0,0,0,0};

		for (int i = 0; i < partnerHandColors.size(); i++) {
			valsSize[partnerHandVals.get(i) - 1]++;
			colorSize[partnerHandColors.get(i)]++;
		}

		int maxVal = -1;
		int maxValIndex = 0;
		int maxColor = -1;
		int maxColorIndex = 0;

		for (int i =0; i < valsSize.length; i++) {
			if (valsSize[i] > maxVal) {
				maxVal = valsSize[i];
				maxValIndex = i;
			}
			if (colorSize[i] > maxColor) {
				maxColor = colorSize[i];
				maxColorIndex = i;
			}
		}

		if (maxColor > maxVal) {
			return new Tuple(false, maxVal, maxColorIndex);
		}
		return new Tuple(true, maxColor, maxValIndex);


	}


	private class Tuple {
		public boolean hintNum;
		public int biggestHint;

		public int biggestHintIndex;
		public Tuple (boolean hintNum, int biggestHint, int biggestHintIndex) {
			this.hintNum = hintNum;
			this.biggestHint = biggestHint;
			this.biggestHintIndex = biggestHintIndex;
		}
	}

}
