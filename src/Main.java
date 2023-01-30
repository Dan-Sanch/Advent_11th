import utils.FileReaderTools;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
    private static final String INPUT_FILE_NAME = "resources\\monkey stats_tst.txt";

    public static void main(String[] args) {
        String[] inputLines = FileReaderTools.readFileAsArray(INPUT_FILE_NAME);

        Map<Integer, Monkey<Integer>> monkeys = MonkeyParser.parseMonkeys(inputLines);
        new ThrowManager<>(monkeys);
        solveProblem1(monkeys);

        Map<Integer, Monkey<BigInteger>> bigMonkeys = BigMonkeyParser.parseMonkeys(inputLines);
        new ThrowManager<>(bigMonkeys);
        solveProblem2(bigMonkeys);
    }

    private static void solveProblem2(Map<Integer,Monkey<BigInteger>> monkeys) {
        int rounds = 1000;
        IntStream.range(0, rounds).forEach(i -> {
            // Order in which monkeys play a round is important, and dependent on their ID
            monkeys.keySet().stream().sorted().forEach(id ->
                    monkeys.get(id).playRound()
            );
            System.out.println(i);
        });

        monkeys.keySet().stream().sorted().forEach(id ->
                System.out.println(String.format("Monkey %d inspected items %d times.", id, monkeys.get(id).getItemInspectCount()))
        );
        System.out.println(String.format("Monkey business level: %d", new MonkeyBusinessCalculator<>(monkeys).getMonkeyBusiness()));
    }

    private static void solveProblem1(Map<Integer, Monkey<Integer>> monkeys) {
        int rounds = 20;
        IntStream.range(0, rounds).forEach(i ->
                // Order in which monkeys play a round is important, and dependent on their ID
                monkeys.keySet().stream().sorted().forEach(id ->
                        monkeys.get(id).playRound()
                )
        );

        monkeys.keySet().stream().sorted().forEach(id ->
                System.out.println(String.format("Monkey %d inspected items %d times.", id, monkeys.get(id).getItemInspectCount()))
        );
        System.out.println(String.format("Monkey business level: %d", new MonkeyBusinessCalculator<>(monkeys).getMonkeyBusiness()));
    }
}

class ThrowManager<T> implements Monkey.ThrowListener<T>{
    private final Map<Integer, Monkey<T>> monkeys;

    public ThrowManager(Map<Integer, Monkey<T>> monkeys) {
        this.monkeys = monkeys;
        monkeys.values().forEach(monkey -> monkey.registerThrowListener(this));
    }

    @Override
    public void thrownToMonkey(T item, Integer monkeyId) {
        Monkey<T> targetMonkey = monkeys.get(monkeyId);
        targetMonkey.receiveItem(item);
    }
}