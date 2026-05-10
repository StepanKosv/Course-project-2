package reddit_collect_pascage;
import java.util.*;

/**
 * Коллекция, сочетающая быструю проверку вхождения (Set) и эффективный доступ по индексу (Array).
 * Реализует ленивое удаление: элементы остаются в массиве до тех пор, пока 
 * array.size() > 2 * set.size(), после чего массив пересобирается.
 */
public class ArraySet<T> {
    private final Set<T> set;
    private final List<T> arr;

    public ArraySet() {
        this.set = new HashSet<>();
        this.arr = new ArrayList<>();
    }

    public boolean add(T item) {
        if (item == null) return false;
        if (set.add(item)) {
            arr.add(item);
            return true;
        }
        return false;
    }

    public boolean remove(T item) {
        if (set.remove(item)) {
            // Ленивое удаление: элемент остаётся в arr до вызова align()
            align();
            return true;
        }
        return false;
    }

    public boolean contains(T item) {
        return item != null && set.contains(item);
    }

    /**
     * Возвращает случайный элемент. Если попался удалённый (stale) элемент, 
     * пересобирает массив и повторяет попытку.
     */
    public T getRandom(Random rand) {
        align();
        if (arr.isEmpty()) return null;

        int idx = rand.nextInt(arr.size());
        T item = arr.get(idx);

        // Защита от stale-элементов
        while (!set.contains(item)) {
            align();
            if (arr.isEmpty()) return null;
            idx = rand.nextInt(arr.size());
            item = arr.get(idx);
        }
        return item;
    }

    /** Возвращает и удаляет случайный элемент */
    public T popRandom(Random rand) {
        T item = getRandom(rand);
        if (item != null) {
            set.remove(item); // Логическое удаление, физическое в arr произойдёт при align()
        }
        return item;
    }

    /** 
     * Вспомогательная функция: проверяет, не разросся ли массив слишком сильно.
     * Пересоздаёт массив, если arr.size() > 2 * set.size()
     */
    public void align() {
        if (arr.size() > 2 * set.size()) {
            arr.clear();
            arr.addAll(set);
        }
    }

    // === Helper methods for tests & debugging ===
    public int size() { return set.size(); }
    public int arrSize() { return arr.size(); }
    public boolean isEmpty() { return set.isEmpty(); }
}