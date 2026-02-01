package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.Piece;
import dev.pig.stockpig.chess.bitboard.File;
import dev.pig.stockpig.chess.bitboard.Rank;
import dev.pig.stockpig.chess.bitboard.Square;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * View for the chess board.
 * Registers square press event with controller.
 */
final class BoardView extends JPanel {

    private final SquareView[] squares = new SquareView[64];

    BoardView() {
        super(new GridLayout(0, 9));
        setBackground(Look.BACKGROUND_COLOUR);

        Rank.forEach(rank -> {
            final JLabel rankLabel = new JLabel(rank.toString(), SwingConstants.CENTER);
            rankLabel.setForeground(Look.TEXT_COLOUR);
            add(rankLabel);

            File.forEach(file -> {
                final Square square = Square.of(file, rank);
                final SquareView squareView = new SquareView(square.ordinal());
                this.squares[square.ordinal()] = squareView;
                add(squareView);
            });
        });
        add(new JLabel(""));
        File.forEach(file -> {
            final JLabel fileLabel = new JLabel(file.toString().toUpperCase(), SwingConstants.CENTER);
            fileLabel.setForeground(Look.TEXT_COLOUR);
            add(fileLabel);
        });
    }

    /**
     * Forward controller to all square views to register square events.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        for (final SquareView squareView : this.squares) {
            squareView.addController(controller);
        }
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Reset and draw all pieces in the list, list should have length of 64,
     * empty squares should use {@link Piece#EMPTY}.
     * @param pieces pieces
     */
    void redrawPieces(final List<Piece> pieces) {
        for (int i = 0; i < 64; i++) {
            this.squares[i].setPiece(pieces.get(i));
        }
    }

    /**
     * Reset all squares with extra colouring to their defaults.
     */
    void clearColours() {
        for (int i = 0; i < 64; i++) {
            this.squares[i].clearColour();
        }
    }

    /**
     * Mark and colour the squares as tinted, only indexes within the list will be tinted.
     * @param squares squares
     */
    void tintSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].tint();
        }
    }

    /**
     * Mark and colour the squares as selected, only indexes within the list will be selected.
     * @param squares squares
     */
    void selectSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].select();
        }
    }

    /**
     * Mark and colour the squares as selected, only indexes within the list will be highlighted.
     * @param squares squares
     */
    void highlightSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].highlight();
        }
    }
}
