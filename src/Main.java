import utils.FileReaderTools;

import java.util.*;
import java.util.stream.IntStream;

public class Main {
    private static final String INPUT_FILE_NAME = "resources\\monkey stats.txt";

    public static void main(String[] args) {
        String[] inputLines = FileReaderTools.readFileAsArray(INPUT_FILE_NAME);

        Map<Integer, Monkey<Integer>> monkeys = MonkeyParser.parseMonkeys(inputLines);
        new ThrowManager<>(monkeys);

        solveProblem1(monkeys);
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
        System.out.println(String.format("Monkey business level: %d", getMonkeyBusiness(monkeys)));
    }


    private static Integer getMonkeyBusiness(Map<Integer, Monkey<Integer>> monkeys) {
        int highest1 = 0;
        int highest2 = 0;
        int secondCompare;

        for(Monkey<Integer> monkey : monkeys.values()) {
            int inspectCount = monkey.getItemInspectCount();

            if (inspectCount > highest1) {
                secondCompare = highest1;
                highest1 = inspectCount;
            } else {
                secondCompare = inspectCount;
            }

            if (secondCompare > highest2)
                highest2 = secondCompare;
        }

        return highest1 * highest2;
    }
}

class ThrowManager<T extends Number> implements Monkey.ThrowListener<T>{
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