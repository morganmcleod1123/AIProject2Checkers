package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class AlphaBeta extends CheckersSearcher {
    private int numNodes = 0;
    private int win = Integer.MAX_VALUE;
    private int lose = win *= -1;

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
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board, int depth, int alpha, int beta ){
        PlayerColor protagonist = board.getCurrentPlayer();
        PlayerColor adversary = protagonist.opponent();
        if (board.playerWins(protagonist)){
            Optional<Duple<Integer, Move>> proWin = Optional.of(new Duple<>(win,board.getLastMove()));
            return proWin;
        } else if (board.playerWins(adversary)) {
            Optional<Duple<Integer, Move>> advWin = Optional.of(new Duple<>(lose, board.getLastMove()));
            return advWin;
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
                alpha = Math.max(outcome, alpha);
                if(alpha >= beta){
                    break;
                }
                if(best.isEmpty() || outcome > best.get().getFirst()){
                    best = Optional.of(new Duple<>(outcome, alternative.getLastMove()));
                }
            }
            return best;
        }
    }
}
