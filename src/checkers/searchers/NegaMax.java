package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.ToIntFunction;

public class NegaMax extends CheckersSearcher {
    private int numNodes = 0;
    private int win = Integer.MAX_VALUE;
    private int lose = win *= -1;

    public NegaMax(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return selectMove(board, 0);
    }
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board, int depth){
        // Set up protagonist and adversary
        PlayerColor protagonist = board.getCurrentPlayer();
        PlayerColor adversary = protagonist.opponent();
        // If either player wins, return win
        if (board.playerWins(protagonist)){
            Optional<Duple<Integer, Move>> proWin = Optional.of(new Duple<>(win,board.getLastMove()));
            return proWin;
        } else if (board.playerWins(adversary)) {
            Optional<Duple<Integer, Move>> advWin = Optional.of(new Duple<>(lose, board.getLastMove()));
            return advWin;
        }
        // If you are currently at the limits of your depth, stop recurring and return score for current board.
        // This is where you are actually getting the score value that fuels "outcome" below.
        else if (depth >= this.getDepthLimit()){
            return Optional.of(new Duple<>(getEvaluator().applyAsInt(board), board.getLastMove()));
        } else {
            Optional<Duple<Integer, Move>> best = Optional.empty();
            for (Checkerboard alternative: board.getNextBoards()){
                numNodes ++;
                // If the current player is not the same after a round, set negation to -1, else set it to 1.
                int negation = board.getCurrentPlayer() != alternative.getCurrentPlayer() ? -1 : 1;
                // We negate the successors, because they represent moves from our adversary
                int outcome = selectMove(alternative, depth + 1).get().getFirst() * negation;
                // If best is empty, any move is better. If our outcome is greater than the current best score,
                // we want to use outcome, since we are looking at this through the perspective of the protagonist.
                if(best.isEmpty() || outcome > best.get().getFirst()){
                    best = Optional.of(new Duple<>(outcome,alternative.getLastMove()));
                }
            }
            return best;
        }
    }
}
