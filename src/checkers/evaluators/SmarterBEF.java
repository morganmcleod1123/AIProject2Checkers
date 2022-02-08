package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.Piece;
import checkers.core.PlayerColor;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.ToIntFunction;

// Help with Checkers Strategy: https://hobbylark.com/board-games/Checkers-Strategy-Tactics-How-To-Win

// Basic Idea: It is good strategy to have good positioning, and good positioning entails taking control of the
// center of the board and grouping your pieces together.

public class SmarterBEF implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor protagonist = c.getCurrentPlayer();
        int protagPieces = c.numPiecesOf(protagonist);
        ArrayList<Optional<Piece>> neighborList = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            int row = c.getRow(i);
            int col = c.getCol(i);
            if(c.pieceAt(row, col).isPresent()){
                if(c.pieceAt(row, col).get().getColor() == protagonist){
                    if((row != 0 && row != 7) && ((col != 0 && col != 7))){
                        ArrayList<Optional<Piece>> candidates = new ArrayList<>();
                        if(c.pieceAt(row - 1, col - 1).isPresent()){ candidates.add(c.pieceAt(row - 1, col - 1)); }
                        if(c.pieceAt(row - 1, col + 1).isPresent()){ candidates.add(c.pieceAt(row - 1, col - 1)); }
                        if(c.pieceAt(row + 1, col - 1).isPresent()){ candidates.add(c.pieceAt(row - 1, col - 1)); }
                        if(c.pieceAt(row + 1, col + 1).isPresent()){ candidates.add(c.pieceAt(row - 1, col - 1)); }
                        for(Optional<Piece> candidate : candidates){
                            if(!neighborList.contains(candidate)){
                                neighborList.add(candidate);
                            }
                        }
                    }
                }
            }
        }
        int neighborCount = neighborList.size();
        int positioning = 0;
        if(neighborCount >= (protagPieces * 0.75)){
            positioning = protagPieces / 4;
        } else if (neighborCount >= (protagPieces * 0.5) && neighborCount < (protagPieces * 0.75)){
            positioning = protagPieces / 8;
        }
        return (c.numPiecesOf(c.getCurrentPlayer()) - c.numPiecesOf(c.getCurrentPlayer().opponent()) + positioning);
    }
}
