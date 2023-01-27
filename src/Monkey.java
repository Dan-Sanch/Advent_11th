import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.function.UnaryOperator;

public class Monkey {
    private final Queue<Integer> items;
    private final UnaryOperator<Integer> worryOperation;
    private final UnaryOperator<Integer> afterInspectWorryOperation;
    private final UnaryOperator<Integer> throwToNextOperation;
    private final Collection<ThrowListener> throwListeners = new ArrayList<>();
    private int itemInspectCount = 0;

    public Monkey(Queue<Integer> items, UnaryOperator<Integer> worryOperation, UnaryOperator<Integer> afterInspectWorryOperation, UnaryOperator<Integer> throwToNextOperation) {
        this.items = new ArrayDeque<>(items);
        this.worryOperation = worryOperation;
        this.afterInspectWorryOperation = afterInspectWorryOperation;
        this.throwToNextOperation = throwToNextOperation;
    }

    public void receiveItem(Integer item) {
        items.add(item);
    }

    public void registerThrowListener(ThrowListener listener) {
        throwListeners.add(listener);
    }

    public int getItemInspectCount() {
        return itemInspectCount;
    }

    public void playRound() {
        while(items.peek() != null) {
            Integer itemWorry = items.poll();
            Integer newItemWorry = inspectItem(itemWorry);
            newItemWorry = afterInspectWorryOperation.apply(newItemWorry);
            throwItem(newItemWorry);
        }
    }

    private Integer inspectItem(Integer itemWorry) {
        itemInspectCount++;
        return worryOperation.apply(itemWorry);
    }

    private void throwItem(Integer itemWorry) {
        Integer throwToId = throwToNextOperation.apply(itemWorry);
        throwListeners.forEach(listener -> listener.thrownToMonkey(itemWorry, throwToId));
    }

    public interface ThrowListener {
        void thrownToMonkey(Integer item, Integer monkeyId);
    }
}
