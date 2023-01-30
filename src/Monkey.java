import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Monkey<WorryType> {
    private final Queue<WorryType> items;
    private final UnaryOperator<WorryType> worryOperation;
    private final UnaryOperator<WorryType> afterInspectWorryOperation;
    private final Function<WorryType, Integer> throwToNextOperation;
    private final Collection<ThrowListener<WorryType>> throwListeners = new ArrayList<>();
    private int itemInspectCount = 0;

    public Monkey(Queue<WorryType> items, UnaryOperator<WorryType> worryOperation, UnaryOperator<WorryType> afterInspectWorryOperation, Function<WorryType, Integer> throwToNextOperation) {
        this.items = new ArrayDeque<>(items);
        this.worryOperation = worryOperation;
        this.afterInspectWorryOperation = afterInspectWorryOperation;
        this.throwToNextOperation = throwToNextOperation;
    }

    public void receiveItem(WorryType item) {
        items.add(item);
    }

    public void registerThrowListener(ThrowListener<WorryType> listener) {
        throwListeners.add(listener);
    }

    public int getItemInspectCount() {
        return itemInspectCount;
    }

    public void playRound() {
        while(items.peek() != null) {
            WorryType itemWorry = items.poll();
            WorryType newItemWorry = inspectItem(itemWorry);
            newItemWorry = afterInspectWorryOperation.apply(newItemWorry);
            throwItem(newItemWorry);
        }
    }

    private WorryType inspectItem(WorryType itemWorry) {
        itemInspectCount++;
        return worryOperation.apply(itemWorry);
    }

    private void throwItem(WorryType itemWorry) {
        Integer throwToId = throwToNextOperation.apply(itemWorry);
        throwListeners.forEach(listener -> listener.thrownToMonkey(itemWorry, throwToId));
    }

    public interface ThrowListener<W> {
        void thrownToMonkey(W item, Integer monkeyId);
    }
}
