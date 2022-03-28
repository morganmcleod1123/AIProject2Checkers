package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

// CheckersSearcher provided by Professor Gabriel Ferrer

public class AlphaBeta extends CheckersSearcher {
    private int numNodes = 0;
    private final int win = Integer.MAX_VALUE;
    private final int lose = win * -1;

    public AlphaBeta(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return selectMove(board, 0, lose, win);
    }

    // selectMove returns an integer representing the value of a move in checkers as well as the move itself.
    //      It utilizes a variant of the minimax algorithm called NegaMax. The values of the nodes in the
    //      game tree are determined by a board evaluation function that supplies an integer value to a given board
    //      position. selectMove also utilizes alpha-beta pruning to avoid considering provably useless nodes
    //      in the game tree.

    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board, int depth, int alpha, int beta ){
        PlayerColor protagonist = board.getCurrentPlayer();
        PlayerColor adversary = protagonist.opponent();
        if (board.playerWins(protagonist)){
            return Optional.of(new Duple<>(win,board.getLastMove()));
        } else if (board.playerWins(adversary)) {
            return Optional.of(new Duple<>(lose, board.getLastMove()));
        } else if (depth >= this.getDepthLimit()){
            return Optional.of(new Duple<>(getEvaluator().applyAsInt(board),board.getLastMove()));
        } else {
            Optional<Duple<Integer, Move>> best = Optional.empty();
            for (Checkerboard alternative: board.getNextBoards()){
                numNodes ++;
                int outcome;
                if (board.getCurrentPlayer() != alternative.getCurrentPlayer()){
                    outcome = selectMove(alternative, depth + 1, -beta, -alpha).get().getFirst() * -1;
                } else {
                    outcome = selectMove(alternative, depth +1, alpha, beta).get().getFirst();
                }
                if(best.isEmpty() || outcome > best.get().getFirst()){
                    best = Optional.of(new Duple<>(outcome, alternative.getLastMove()));
                }
                alpha = Math.max(outcome, alpha);
                if(alpha >= beta){
                    break;
                }
            }
            return best;
        }
    }
}
