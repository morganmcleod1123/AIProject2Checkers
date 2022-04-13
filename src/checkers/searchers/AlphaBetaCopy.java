package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.ToIntFunction;

// CheckersSearcher provided by Professor Gabriel Ferrer

public class AlphaBetaCopy extends CheckersSearcher {
    private int numNodes = 0;
    private final int win = Integer.MAX_VALUE;
    private final int lose = win * -1;

    public AlphaBetaCopy(ToIntFunction<Checkerboard> e) {
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
        } else if (depth >= this.getDepthLimit() && !board.getLastMove().isCapture()){
            return Optional.of(new Duple<>(getEvaluator().applyAsInt(board),board.getLastMove()));
        } else {
            Optional<Duple<Integer, Move>> best = Optional.empty();
            // implement ordering heuristic here. sort a list from board.getNextBoards() in ascending order.
            // Then go through them.
            ArrayList<Optional<Duple<Integer, Move>>> outcomeList = new ArrayList<>();
            for (Checkerboard alternative: board.getNextBoards()){
                numNodes ++;
                Optional<Duple<Integer, Move>> outcome;
                int outcomeInt;
                Move outcomeMove;
                if (board.getCurrentPlayer() != alternative.getCurrentPlayer()){
                    outcome = selectMove(alternative, depth + 1, -beta, -alpha);
                    outcomeInt = outcome.get().getFirst() * -1;
                    outcomeMove = alternative.getLastMove();
                } else {
                    outcome = selectMove(alternative, depth +1, alpha, beta);
                    outcomeInt = outcome.get().getFirst();
                    outcomeMove = alternative.getLastMove();
                }
                outcomeList.add(Optional.of(new Duple<>(outcomeInt, outcomeMove)));
            }
            // Sort outcomeList in descending order
            Collections.sort(outcomeList, Collections.reverseOrder());
            for(Optional<Duple<Integer, Move>> outcome : outcomeList){
                if(best.isEmpty() || outcome.get().getFirst() > best.get().getFirst()){
                    best = Optional.of(outcome.get());
                }
                alpha = Math.max(outcome.get().getFirst(), alpha);
                if(alpha >= beta){
                    break;
                }
            }
            return best;
        }
    }
}
