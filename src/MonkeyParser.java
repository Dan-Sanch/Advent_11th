import java.util.*;
import java.util.function.UnaryOperator;

public class MonkeyParser {
    private static final UnaryOperator<Integer> AFTER_INSPECT_OP = worry -> worry/3;

    public static Map<Integer, Monkey> parseMonkeys(String[] inputLines) {
        Map<Integer, Monkey> monkeys = new HashMap<>();
        for (int i=0; i< inputLines.length; i+=7) {
            String[] monkeyBlock = Arrays.copyOfRange(inputLines, i, i+6);
            AbstractMap.SimpleEntry<Integer, Monkey> idMonkey = parseMonkey(monkeyBlock);
            monkeys.put(idMonkey.getKey(), idMonkey.getValue());
        }

        return monkeys;
    }

    private static AbstractMap.SimpleEntry<Integer, Monkey> parseMonkey(String[] monkeyBlock) {
        int id = parse0(monkeyBlock[0]);
        Queue<Integer> items = parse1(monkeyBlock[1]);
        UnaryOperator<Integer> operation = parse2(monkeyBlock[2]);
        UnaryOperator<Integer> throwNextTest = parseTest(Arrays.copyOfRange(monkeyBlock, 3, 6));
        UnaryOperator<Integer> afterInspectOp = worry -> worry/3;

        return new AbstractMap.SimpleEntry<>(id, new Monkey(items, operation, AFTER_INSPECT_OP, throwNextTest));
    }

    private static int parse0(String line) {
        final String prefix = "Monkey ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return Integer.parseInt(line.replaceAll("\\D", "")); // parse only the integer in the string
    }

    private static Queue<Integer> parse1(String line) {
        final String prefix = "  Starting items: ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        Queue<Integer> items = new ArrayDeque<>();
        Arrays.stream(line.substring(prefix.length()).split(", "))
                .mapToInt(Integer::parseInt)
                .forEach(items::add);

        return items;
    }

    private static UnaryOperator<Integer> parse2(String line) {
        final String prefix = "  Operation: new = ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        String[] operationTokens = line.substring(prefix.length()).split(" ");

        String operand1 = operationTokens[0];
        String operand2 = operationTokens[2];
        String operator = operationTokens[1];

        return new UnaryOperator<>() {
            @Override
            public Integer apply(Integer oldInteger) {
                int first = operand1.equals("old")? oldInteger : parseInt(operand1);
                int second = operand2.equals("old")? oldInteger : parseInt(operand2);

                if (operator.equals("+")) {
                    return first + second;
                } else {
                    return first * second;
                }
            }

            private Integer parseInt(String arg) {
                if (arg == null)
                    return null;
                int i;
                try {
                    i = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException(String.format("Invalid operand: <%s>", arg));
                }

                return i;
            }
        };
    }

    private static UnaryOperator<Integer> parseTest(String[] testBlock) {
        int divisibleBy = parseDivisibleBy(testBlock[0]);
        int trueMonkey = parseIfTrue(testBlock[1]);
        int falseMonkey = parseIfFalse(testBlock[2]);

        return worryLevel ->
                worryLevel % divisibleBy == 0? trueMonkey : falseMonkey;
    }

    private static int parseDivisibleBy(String line) {
        final String prefix = "  Test: divisible by ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return Integer.parseInt(line.replaceAll("\\D", "")); // parse only the integer in the string
    }

    private static int parseIfTrue(String line) {
        final String prefix = "    If true: throw to monkey ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return Integer.parseInt(line.replaceAll("\\D", "")); // parse only the integer in the string
    }

    private static int parseIfFalse(String line) {
        final String prefix = "    If false: throw to monkey ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return Integer.parseInt(line.replaceAll("\\D", "")); // parse only the integer in the string
    }
}
