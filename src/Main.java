import utils.FileReaderTools;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class Main {
    private static final String INPUT_FILE_NAME = "resources\\monkey stats.txt";

    public static void main(String[] args) {
        String[] inputLines = FileReaderTools.readFileAsArray(INPUT_FILE_NAME);

        Map<Integer, Monkey> monkeys = MonkeyParser.parseMonkeys(inputLines);
        ThrowManager.manage(monkeys);

        solveProblem1(monkeys);
    }

    private static void solveProblem1(Map<Integer, Monkey> monkeys) {
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


    private static Integer getMonkeyBusiness(Map<Integer, Monkey> monkeys) {
        int highest1 = 0;
        int highest2 = 0;
        int secondCompare;

        for(Monkey monkey : monkeys.values()) {
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

class ThrowManager implements Monkey.ThrowListener{
    private final Map<Integer, Monkey> monkeys;

    private ThrowManager(Map<Integer, Monkey> monkeys) {
        this.monkeys = monkeys;
        monkeys.values().forEach(monkey -> monkey.registerThrowListener(this));
    }

    public static void manage(Map<Integer, Monkey> monkeys) {
        new ThrowManager(monkeys);
    }

    @Override
    public void thrownToMonkey(Integer item,Integer monkeyId) {
        Monkey targetMonkey = monkeys.get(monkeyId);
        targetMonkey.receiveItem(item);
    }
}