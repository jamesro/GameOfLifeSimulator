package com.james.gol.model;

public class StandardRule implements SimulationRule {

    @Override
    public CellState getNextState(int x, int y, Board board) {
        int aliveNeighbours;

        aliveNeighbours = countAliveNeighbours(x, y, board);
        if (board.getState(x, y) == CellState.ALIVE) {
            // game of life rules for a living cell
            if (aliveNeighbours < 2) {
                return CellState.DEAD;
            } else if (aliveNeighbours <= 3) {
                return CellState.ALIVE;
            } else {
                return CellState.DEAD;
            }
        } else {
            // game of life rules for a dead cell
            if (aliveNeighbours == 3) {
                return CellState.ALIVE;
            }
        }

        return CellState.DEAD;
    }

    public int countAliveNeighbours(int x, int y, Board board) {
        int count = 0;

        count += countCell(x-1,y-1, board);
        count += countCell(x,y-1, board);
        count += countCell(x+1,y-1, board);

        count += countCell(x-1,y, board);
        count += countCell(x+1,y, board);

        count += countCell(x-1,y+1, board);
        count += countCell(x,y+1, board);
        count += countCell(x+1,y+1, board);

        return count;
    }

    private int countCell(int x, int y, Board board) {
        return board.getState(x, y) == CellState.ALIVE ? 1 : 0;
    }

}
