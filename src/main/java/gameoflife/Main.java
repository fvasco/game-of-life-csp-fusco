package gameoflife;

import gameoflife.ui.WindowOutput;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        execute(ExecutionArgs.parse(args));
    }

    public static void execute(ExecutionArgs args) throws IOException {
        GameOfLife gameOfLife = GameOfLife.create(args);
        gameOfLife.start();

        System.out.println(args);
        System.out.println(gameOfLife.getDimensions());
        WindowOutput.runUI(args, gameOfLife);
    }
}
