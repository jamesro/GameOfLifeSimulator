package com.james;

public class Simulation {

    int width;
    int height;
    int[][] board;

    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[width][height];
    }

    public void printBoard() {
        System.out.println("---");
        for (int y = 0; y < height; y++) {
            String line = "|";
            for (int x = 0; x < width; x++) {
                if (this.board[x][y] == 0) {  // 0 is dead, 1 is alive
                    line += ".";
                } else {
                    line += "*";
                }
            }
            line += "|";
            System.out.println(line);
        }
        System.out.println("---\n");
    }

    public void setState(int x, int y, int state) {
        if (x < 0 || x >= width) {
            return;
        }
        if (y < 0 || y >= height) {
            return;
        }
        this.board[x][y] = state;
    }
    public void setAlive(int x, int y) {
        setState(x, y, 1);
    }

    public void setDead(int x, int y) {
        setState(x, y, 0);
    }

    public int countAliveNeighbours(int x, int y) {
        int count = 0;

        count += getState(x-1,y-1);
        count += getState(x,y-1);
        count += getState(x+1,y-1);

        count += getState(x-1,y);
        count += getState(x+1,y);

        count += getState(x-1,y+1);
        count += getState(x,y+1);
        count += getState(x+1,y+1);

        return count;
    }

    public int getState(int x, int y) {
        if (x < 0 || x >= width) {
            return 0;
        }
        if (y < 0 || y >= height) {
            return 0;
        }

        return this.board[x][y];
    }

    public void step() {
        int aliveNeighbours;
        int[][] newBoard = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                aliveNeighbours = countAliveNeighbours(x, y);
                if (getState(x, y) == 1) {
                    // game of life rules for a living cell
                    if (aliveNeighbours < 2) {
                        newBoard[x][y] = 0;
                    } else if (aliveNeighbours <= 3) {
                        newBoard[x][y] = 1;
                    } else {
                        newBoard[x][y] = 0;
                    }
                } else {
                    // game of life rules for a dead cell
                    if (aliveNeighbours == 3) {
                        newBoard[x][y] = 1;
                    }
                }
            }
        }
        this.board = newBoard;
    }
}
