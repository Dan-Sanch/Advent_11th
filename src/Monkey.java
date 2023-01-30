import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Monkey<T extends Number> {
    private final Queue<T> items;
    private final UnaryOperator<T> worryOperation;
    private final UnaryOperator<T> afterInspectWorryOperation;
    private final Function<T, Integer> throwToNextOperation;
    private final Collection<ThrowListener<T>> throwListeners = new ArrayList<>();
    private int itemInspectCount = 0;

    public Monkey(Queue<T> items, UnaryOperator<T> worryOperation, UnaryOperator<T> afterInspectWorryOperation, Function<T, Integer> throwToNextOperation) {
        this.items = new ArrayDeque<>(items);
        this.worryOperation = worryOperation;
        this.afterInspectWorryOperation = afterInspectWorryOperation;
        this.throwToNextOperation = throwToNextOperation;
    }

    public void receiveItem(T item) {
        items.add(item);
    }

    public void registerThrowListener(ThrowListener<T> listener) {
        throwListeners.add(listener);
    }

    public int getItemInspectCount() {
        return itemInspectCount;
    }

    public void playRound() {
        while(items.peek() != null) {
            T itemWorry = items.poll();
            T newItemWorry = inspectItem(itemWorry);
            newItemWorry = afterInspectWorryOperation.apply(newItemWorry);
            throwItem(newItemWorry);
        }
    }

    private T inspectItem(T itemWorry) {
        itemInspectCount++;
        return worryOperation.apply(itemWorry);
    }

    private void throwItem(T itemWorry) {
        Integer throwToId = throwToNextOperation.apply(itemWorry);
        throwListeners.forEach(listener -> listener.thrownToMonkey(itemWorry, throwToId));
    }

    public interface ThrowListener<W extends Number> {
        void thrownToMonkey(W item, Integer monkeyId);
    }
}
