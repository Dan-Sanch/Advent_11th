import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class BigMonkeyParser {
    private static final UnaryOperator<BigInteger> AFTER_INSPECT_OP = worry -> worry;

    public static Map<Integer, Monkey<BigInteger>> parseMonkeys(String[] inputLines) {
        Map<Integer, Monkey<BigInteger>> monkeys = new HashMap<>();
        for (int i=0; i< inputLines.length; i+=7) {
            String[] monkeyBlock = Arrays.copyOfRange(inputLines, i, i+6);
            AbstractMap.SimpleEntry<Integer, Monkey<BigInteger>> idMonkey = parseMonkey(monkeyBlock);
            monkeys.put(idMonkey.getKey(), idMonkey.getValue());
        }

        return monkeys;
    }

    private static AbstractMap.SimpleEntry<Integer, Monkey<BigInteger>> parseMonkey(String[] monkeyBlock) {
        int id = parseId(monkeyBlock[0]);
        Queue<BigInteger> items = parseItems(monkeyBlock[1]);
        UnaryOperator<BigInteger> operation = parseOperation(monkeyBlock[2]);
        Function<BigInteger, Integer> throwNextTest = parseThrowOperation(Arrays.copyOfRange(monkeyBlock, 3, 6));

        return new AbstractMap.SimpleEntry<>(id, new Monkey<>(items, operation, AFTER_INSPECT_OP, throwNextTest));
    }

    private static int parseId(String line) {
        final String prefix = "Monkey ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return Integer.parseInt(line.replaceAll("\\D", "")); // parse only the integer in the string
    }

    private static Queue<BigInteger> parseItems(String line) {
        final String prefix = "  Starting items: ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        Queue<BigInteger> items = new ArrayDeque<>();
        Arrays.stream(line.substring(prefix.length()).split(", "))
                .map(BigInteger::new)
                .forEach(items::add);

        return items;
    }

    private static UnaryOperator<BigInteger> parseOperation(String line) {
        final String prefix = "  Operation: new = ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        String[] operationTokens = line.substring(prefix.length()).split(" ");

        return new UnaryOperator<>() {
            final BigInteger bigOperand1 = isNum(operationTokens[0])? new BigInteger(operationTokens[0]) : BigInteger.ZERO;
            final BigInteger bigOperand2 = isNum(operationTokens[2])? new BigInteger(operationTokens[2]) : BigInteger.ZERO;
            final String operator = operationTokens[1];

            @Override
            public BigInteger apply(BigInteger oldValue) {
                BigInteger first = bigOperand1.equals(BigInteger.ZERO)? oldValue : bigOperand1;
                BigInteger second = bigOperand2.equals(BigInteger.ZERO)? oldValue : bigOperand2;

                if (operator.equals("+")) {
                    return first.add(second);
                } else {
                    return first.multiply(second);
                }
            }

            private boolean isNum(String operandToken) {
                try {
                    Integer.parseInt(operandToken);
                } catch (NumberFormatException nfe) {
                    return false;
                }
                return true;
            }
        };
    }

    private static Function<BigInteger, Integer> parseThrowOperation(String[] testBlock) {
        return new Function<>() {
            final BigInteger divisibleBy = new BigInteger(parseDivisibleBy(testBlock[0]));
            final int trueMonkey = parseIfTrue(testBlock[1]);
            final int falseMonkey = parseIfFalse(testBlock[2]);

            @Override
            public Integer apply(BigInteger worryLevel) {
                return worryLevel.remainder(divisibleBy).equals(BigInteger.ZERO)? trueMonkey : falseMonkey;
            }
        };
    }

    private static String parseDivisibleBy(String line) {
        final String prefix = "  Test: divisible by ";
        if (!line.startsWith(prefix)) {
            throw new RuntimeException(String.format("Expected <%s>, found <%s>", prefix, line));
        }

        return line.replaceAll("\\D", ""); // parse only the integer in the string
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
