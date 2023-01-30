import java.util.Map;

public class MonkeyBusinessCalculator<T extends  Number> {
    private final Map<Integer, Monkey<T>> monkeys;

    public  MonkeyBusinessCalculator(Map<Integer, Monkey<T>> monkeys) {
        this.monkeys = monkeys;
    }

    public Long getMonkeyBusiness() {
        long highest1 = 0;
        long highest2 = 0;
        long secondCompare;

        for(Monkey<T> monkey : monkeys.values()) {
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
