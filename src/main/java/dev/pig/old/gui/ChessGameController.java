package dev.pig.old.gui;

import dev.pig.old.chess.Bitboard;
import dev.pig.old.chess.ChessGame;
import dev.pig.old.chess.ChessMove;
import dev.pig.old.chess.Piece;
import dev.pig.old.search.ChessGameMoveSearcher;

public class ChessGameController  {

    private static final String WHITE_TURN = "White Turn";
    private static final String BLACK_TURN = "Black Turn";
    private static final String WHITE_CHECKMATE = "Checkmate - White Wins";
    private static final String BLACK_CHECKMATE = "Checkmate - Black Wins";
    private static final String STALEMATE = "Stalemate - Draw";

    private final ChessGameView view;
    private ChessGame game = ChessGame.standard();

    private int indexSelected = -1;

    public ChessGameController(final ChessGameView view) {
        this.view = view;
        init();
    }

    private void init() {
        initActionListeners();
        update();
    }

    private void initActionListeners() {
        this.view.toolBar.newGame.addActionListener(e -> newGame());
        this.view.toolBar.undo.addActionListener(e -> undo());
        this.view.fen.fen.addActionListener(e -> fen(e.getActionCommand()));
        this.view.ai.generate.addActionListener(e -> aiMove());

        this.view.toolBar.none.addActionListener(e -> update());
        this.view.toolBar.threatened.addActionListener(e -> update());
        this.view.toolBar.movable.addActionListener(e -> update());
        this.view.toolBar.pins.addActionListener(e -> update());

        for (int index = 0; index < this.view.board.squares.length; index++) {
            final int finalIndex = index;
            this.view.board.squares[index].addActionListener(e -> square(finalIndex));
        }
    }

    private void newGame() {
        this.game = ChessGame.standard();
        update();
    }

    private void undo() {
        this.game.undoMove();
        update();
    }

    private void fen(final String fen) {
        try {
            this.game = ChessGame.fromFen(fen);
            update();
        } catch (Exception ignored) {}
    }

    private void aiMove() {
        final int depth = this.view.ai.depth.getValue();
        final int qDepth = this.view.ai.qDepth.getValue();
        final ChessMove move = ChessGameMoveSearcher.quiescence(depth, qDepth).search(this.game);
        this.game.applyMove(move);
        update();
    }

    private void square(final int index) {
        if (this.indexSelected == -1) {
            selectSquare(index);
        } else {
            makeMove(index);
        }
    }

    private void selectSquare(final int index) {
        boolean isMovable = false;
        for (final ChessMove move : this.game.getLegalMoves()) {
            if (Bitboard.INDEX[index] == move.getFrom()) {
                isMovable = true;
                this.view.board.squares[Long.numberOfTrailingZeros(move.getTo())].highlightMovable();
            }
        }
        if (isMovable) {
            this.view.board.squares[index].select();
            this.indexSelected = index;
        }
    }

    private void makeMove(final int index) {
        for (final ChessMove move : this.game.getLegalMoves()) {
            if (Bitboard.INDEX[this.indexSelected] == move.getFrom() && Bitboard.INDEX[index] == move.getTo()) {
                this.game.applyMove(move);
            }
        }
        this.indexSelected = -1;
        update();
    }

    private void update() {
        this.view.toolBar.turn.setText(
                this.game.isGameOver() ?
                        (this.game.getWinner() == 0 ? STALEMATE : (this.game.getWinner() == 1 ? WHITE_CHECKMATE : BLACK_CHECKMATE)) :
                        this.game.isWhiteTurn() ? WHITE_TURN : BLACK_TURN
        );

        this.view.fen.fen.setText(this.game.toFen());

        for (int index = 0; index < this.view.board.squares.length; index++) {
            this.view.board.squares[index].reset();
            final int piece = this.game.getPieceAtIndex(index);
            if (piece != Piece.UNOCCUPIED) {
                this.view.board.squares[index].setIcon(Piece.isWhite(piece) ?
                        Look.WHITE_ICONS[Piece.getTypeOnly(piece) - 1] : Look.BLACK_ICONS[Piece.getTypeOnly(piece) - 1]);
            }
        }

        if (!this.view.toolBar.none.isSelected()) {
            if (this.view.toolBar.threatened.isSelected()) {
                Bitboard.forEachBit(this.game.getThreatenedSquares(), bit -> this.view.board.squares[Bitboard.toIndex(bit)].debug());
            }
            if (this.view.toolBar.movable.isSelected()) {
                Bitboard.forEachBit(this.game.getMovableSquares(), bit -> this.view.board.squares[Bitboard.toIndex(bit)].debug());
            }
            if (this.view.toolBar.pins.isSelected()) {
                Bitboard.forEachBit(this.game.getPinSquares(), bit -> this.view.board.squares[Bitboard.toIndex(bit)].debug());
            }
        }

        if (this.game.isCheck()) {
            this.view.board.squares[Long.numberOfTrailingZeros(this.game.getPieceBitboard(Piece.KING | Piece.getTeam(this.game.isWhiteTurn())))].check();
        }
    }

}
