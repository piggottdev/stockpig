package dev.pig.old;

import dev.pig.old.evaluation.ChessGameEvaluator;
import dev.pig.old.chess.ChessGame;
import dev.pig.old.chess.ChessMove;
import dev.pig.old.game.MoveDivideEnumerator;
import dev.pig.old.game.MoveEnumerator;

import java.util.Scanner;

public class Stockpig {

    private static final String NEW = "new";
    private static final String FEN = "fen";
    private static final String MOVE = "move";
    private static final String UNDO = "undo";
    private static final String PERFT = "perft";
    private static final String DIVIDE = "divide";
    private static final String EVAL = "eval";
    private static final String EXIT = "exit";

    public static void main(String[] args) {
        mainLoop();
    }

    private static void mainLoop() {

        final Scanner in = new Scanner(System.in);
        boolean exit = false;

        ChessGame game = ChessGame.standard();

        while(!exit) {

            System.out.println(game.debugString());
            System.out.println("=>");

            final String input = in.nextLine();
            final String[] command = input.split(" ");

            switch (command[0]) {
                case NEW:
                    game = ChessGame.standard();
                    break;
                case FEN:
                    try {
                        game = ChessGame.fromFen(input.substring(4));
                    } catch (Exception ex) {
                        System.out.println("Invalid Fen");
                    }
                    break;
                case MOVE:
                    for(ChessMove move : game.getLegalMoves()) {
                        if (move.toString().equals(command[1])) game.applyMove(move);
                    }
                    break;
                case UNDO:
                    game.undoMove();
                    break;
                case PERFT:
                    try {
                        System.out.println(new MoveEnumerator<>(game, Integer.parseInt(command[1])).getNodes() + " nodes analysed");
                    } catch (Exception ex) {
                        System.out.println("Invalid Depth");
                    }
                    break;
                case DIVIDE:
                    try {
                        final MoveDivideEnumerator<ChessMove> test = new MoveDivideEnumerator<>(game, Integer.parseInt(command[1]));
                        System.out.println(test.getNodes() + " nodes analysed");
                        System.out.println(test.getDivide().toString());
                    } catch (Exception ex) {
                        System.out.println("Invalid Depth");
                    }
                    break;
                case EVAL:
                    System.out.println("Material = " + ChessGameEvaluator.position().evaluate(game));
                    break;
                case EXIT:
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown Command");
            }
        }
    }

}
