import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Hanabi {
	private Board boardState;
	private ArrayList<Player> players;
	private ArrayList<Hand> hands;
	private ArrayList<Card> deck;

	private int currentPlayer;
	private int otherPlayer;
	private boolean chatty;

	/**
	 * The basic constructor.
	 * @param chatty True to print out all useful information about the game; false for speed of play.
	 */
	public Hanabi(boolean chatty) {
		boardState = new Board();

		players = new ArrayList<Player>();
		players.add(new Player());
		players.add(new Player());

		hands = new ArrayList<Hand>();
		hands.add(new Hand());
		hands.add(new Hand());

		currentPlayer = 0;
		otherPlayer = 1;

		deck = new ArrayList<Card>();

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

		shuffle();

		// Deals five cards to both players.
		for (int i = 0; i < 5; i++) {
			try {
				Card c = dealCard();
				hands.get(0).add(0, c);
				c = dealCard();
				hands.get(1).add(0, c);
			}
			catch (Exception e) {
				System.out.println(e);
				return;
			}

		}

		currentPlayer = 0;
		otherPlayer = 1;

		this.chatty = chatty;
	}

	/**
	 * Called once to play a full game of Hanabi.
	 * @return 0 if the players run out of fuses or there is an exception; tableau sum otherwise.
	 */
	public int play() {
		
		if (chatty) {
			System.out.println("Player 0 hand: " + hands.get(0));
			System.out.println("Player 1 hand: " + hands.get(1));
			System.out.println("Board state: \n" + boardState);
		}

		while (true) {

			try {
				String response = players.get(currentPlayer).ask(hands.get(currentPlayer).size(), 
						hands.get(otherPlayer), boardState);
				parseAndHandleResponse(response);
				if (gameEnded()) {
					if (boardState.numFuses <= 0) {
						if (chatty) {
							System.out.println("Ran out of fuses; Score: 0");
						}
						return 0;
					}
					if (chatty) {
						System.out.println("Game ended; Score: " + boardState.getTableauScore());
					}
					return boardState.getTableauScore();
				}
				switchPlayers();
			}
			catch (Exception e) {
				System.out.println(e);
				if (chatty) {
					System.out.println("Error; Score: 0");
				}
				return 0;
			}
		}

	}

	/**
	 * Shuffle the deck using the Fisher-Yates shuffling algorithm.
	 */
	public void shuffle() {
		Random rand = new Random();
		for (int i = deck.size() - 1; i >= 1; i--) {
			int j = rand.nextInt(i + 1);
			Card temp = deck.get(j);
			deck.set(j, deck.get(i));
			deck.set(i, temp);
		}
	}

	/**
	 * Interprets responses from a Player.ask() call and performs appropriate Player.tell...() calls
	 * @param response The String provided by a Player.ask() call
	 * @throws Exception In case of malformed Strings
	 */
	public void parseAndHandleResponse(String response) throws Exception {
		Scanner scn = new Scanner(response);
		if (!scn.hasNext()) {
			scn.close();
			throw new Exception("Hanabi.parseAndHandleResponse() - Empty response string");
		}
		String command = scn.next();
		
		if (command.equals("PLAY")) {
			// Error if no play index specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Play with no play index provided");
			}
			// Error if play index out of range.
			int playIndex = scn.nextInt();
			if (playIndex < 0 || playIndex >= hands.get(currentPlayer).size()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Play index " + playIndex + 
						"is outside player's hand size of " + hands.get(currentPlayer).size());
			}
			// Error if no play index specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Play with no draw index provided");
			}
			// Error if draw index out of range.
			int drawIndex = scn.nextInt();
			if (drawIndex < 0 || drawIndex >= hands.get(currentPlayer).size()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Draw index " + drawIndex + 
						"is outside player's hand size of " + hands.get(currentPlayer).size());
			}
			// No errors.
			Hand oldHand = new Hand(hands.get(currentPlayer));
			Card play = hands.get(currentPlayer).remove(playIndex);
			boolean legalPlay = boardState.play(play);
			Card draw = null;
			if (deck.size() > 0) {
				draw = dealCard();
				hands.get(currentPlayer).add(drawIndex, draw);
			}
			Hand newHand = new Hand(hands.get(currentPlayer));
			if (chatty) {
				System.out.print("Player " + currentPlayer + " plays " + play + " from position "
						+ playIndex);
				if (draw != null) {
					System.out.println(" and draws a card to index " + drawIndex);
				}
				else {
					System.out.println(" and cannot draw because the deck is empty.");
				}
				if (!legalPlay) {
					System.out.println("The play is illegal; removing a fuse.");
				}
				System.out.println("Player 0 hand: " + hands.get(0));
				System.out.println("Player 1 hand: " + hands.get(1));
				System.out.println("Board state: \n" + boardState);
			}
			players.get(otherPlayer).tellPartnerPlay(oldHand, play, playIndex, draw, drawIndex, newHand, 
					legalPlay, boardState);
			players.get(currentPlayer).tellYourPlay(play, legalPlay, boardState);

		}
		
		else if (command.equals("DISCARD")) {
			// Error if no discard index specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Discard with no discard index provided");
			}
			// Error if discard index out of range.
			int discardIndex = scn.nextInt();
			if (discardIndex < 0 || discardIndex >= hands.get(currentPlayer).size()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Discard index " + discardIndex + 
						"is outside player's hand size of " + hands.get(currentPlayer).size());
			}
			// Error if no draw index specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Discard with no draw index provided");
			}
			// Error if draw index out of range.
			int drawIndex = scn.nextInt();
			if (drawIndex < 0 || drawIndex >= hands.get(currentPlayer).size()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Draw index " + drawIndex + 
						"is outside player's hand size of " + hands.get(currentPlayer).size());
			}
			// No errors.
			Hand oldHand = new Hand(hands.get(currentPlayer));
			Card discard = hands.get(currentPlayer).remove(discardIndex);
			boardState.discard(discard);
			if (boardState.numHints < boardState.MAX_HINTS) {
				boardState.numHints++;
			}
			Card draw = null;
			if (deck.size() > 0) {
				draw = dealCard();
				hands.get(currentPlayer).add(drawIndex, draw);
			}
			Hand newHand = new Hand(hands.get(currentPlayer));
			if (chatty) {
				System.out.print("Player " + currentPlayer + " discards " + discard + " from position "
						+ discardIndex);
				if (draw != null) {
					System.out.println(" and draws a card to index " + drawIndex);
				}
				else {
					System.out.println(" and cannot draw because the deck is empty.");
				}
				System.out.println("Player 0 hand: " + hands.get(0));
				System.out.println("Player 1 hand: " + hands.get(1));
				System.out.println("Board state: \n" + boardState);
			}
			players.get(otherPlayer).tellPartnerDiscard(oldHand, discard, discardIndex, draw, drawIndex, newHand, 
					boardState);
			players.get(currentPlayer).tellYourDiscard(discard, boardState);
		}
		
		else if (command.equals("NUMBERHINT")) {
			// Error if out of hints.
			if (boardState.numHints <= 0) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Numberhint with no hints remaining");
			}
			// Error if no number specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Numberhint with no number provided");
			}
			// Error if specified number not in other player's hand.
			int number = scn.nextInt();
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < hands.get(otherPlayer).size(); i++) {
				if (hands.get(otherPlayer).get(i).value == number) {
					indices.add(i);
				}
			}
			if (indices.size() == 0) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Numberhint with number " + number + 
						" not in other player's hand.");
			}
			// No errors. Tell other player.
			if (chatty) {
				System.out.println("Player " + currentPlayer + " hints the number " + number + " for indices "
						+ indices);
				System.out.println("Player 0 hand: " + hands.get(0));
				System.out.println("Player 1 hand: " + hands.get(1));
				System.out.println("Board state: \n" + boardState);
			}
			// And decrement hints.
			boardState.numHints--;
			players.get(otherPlayer).tellNumberHint(number, indices, hands.get(currentPlayer), boardState);

		}

		else if (command.equals("COLORHINT")) {
			// Error if out of hints.
			if (boardState.numHints <= 0) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Colorhint with no hints remaining");
			}
			// Error if no color specified.
			if (!scn.hasNextInt()) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Colorhint with no color provided");
			}
			// Error if specified color is not a real color
			int color = scn.nextInt();
			if ((color != Colors.RED) && (color != Colors.YELLOW) && (color != Colors.GREEN) && (color != Colors.BLUE)
					&& (color != Colors.WHITE)) {
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Colorhint with bad color " + color);
			}
			// Error if specified color not in other player's hand.
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < hands.get(otherPlayer).size(); i++) {
				if (hands.get(otherPlayer).get(i).color == color) {
					indices.add(i);
				}
			}
			if (indices.size() == 0) {
				scn.close();
				scn.close();
				throw new Exception("Hanabi.parseAndHandleResponse() - Colorhint with color " + color + 
						" not in other player's hand.");
			}
			// No errors. Tell other player.
			if (chatty) {
				System.out.println("Player " + currentPlayer + " hints the color " + Colors.suitColor(color) +
						" for indices " + indices);
				System.out.println("Player 0 hand: " + hands.get(0));
				System.out.println("Player 1 hand: " + hands.get(1));
				System.out.println("Board state: \n" + boardState);
			}
			// And decrement hints.
			boardState.numHints--;
			players.get(otherPlayer).tellColorHint(color, indices, hands.get(currentPlayer), boardState);

		}
		// Bad command.
		else {
			scn.close();
			throw new Exception("Hanabi.parseAndHandleResponse() - illegal command " + command);
		}
		scn.close();
	}

	/**
	 * Swaps who the current player is.
	 */
	public void switchPlayers() {
		int temp = currentPlayer;
		currentPlayer = otherPlayer;
		otherPlayer = temp;
	}

	/**
	 * Removes the last card from the deck and updates boardState for reduced deck size.
	 * @return The card removed
	 * @throws Exception if the deck is empty
	 */
	public Card dealCard() throws Exception {
		if (deck.isEmpty()) {
			throw new Exception("Hanabi.dealCard() - Dealing from an empty deck");
		}
		Card c = deck.remove(deck.size() - 1);
		boardState.deckSize--;
		return c;
	}

	/**
	 * Checks for endgame conditions
	 * @return True if players are out of fuses, out of cards, or have finished tableau; false otherwise.
	 */
	public boolean gameEnded() {
		return (boardState.numFuses <= 0) || (deck.size() == 0) || (boardState.getTableauScore() == 25);

	}

}
