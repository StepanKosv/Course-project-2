import org.junit.jupiter.api.Test;

import reddit_collect_pascage.ArraySet;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ArraySetTest {
    private final Random rand = new Random(42);

    @Test
    void testAddAndContains() {
        ArraySet<Integer> as = new ArraySet<>();
        as.add(1); as.add(2); as.add(3);
        assertTrue(as.contains(2));
        assertFalse(as.contains(4));
        assertEquals(3, as.size());
        assertEquals(3, as.arrSize());
    }

    @Test
    void testLazyRemoveAndAlignTrigger() {
        ArraySet<Integer> as = new ArraySet<>();
        // Наполняем
        for (int i = 0; i < 10; i++) as.add(i);
        assertEquals(10, as.arrSize());

        // Удаляем половину. Массив остаётся прежним (ленивое удаление)
        for (int i = 0; i < 5; i++) as.remove(i);
        assertEquals(5, as.size());
        assertEquals(10, as.arrSize()); // Stale элементы всё ещё в arr

        // Удаляем ещё, чтобы сработал инвариант: arr.size() > 2 * set.size()
        for (int i = 5; i < 8; i++) as.remove(i); // Осталось 2 элемента (8, 9)
        assertEquals(2, as.size());
        // 10 > 4 * 2 -> align() должен сработать внутри первого remove()
        //arrSize = 4 <=2*2
        assertEquals(4, as.arrSize()); // Массив пересобран
    }

    @Test
    void testGetRandomValidity() {
        ArraySet<Integer> as = new ArraySet<>();
        for (int i = 0; i < 20; i++) as.add(i);
        // Создаём много stale-элементов
        for (int i = 0; i < 15; i++) as.remove(i);
        
        // getRandom должен вернуть только живые элементы
        for (int k = 0; k < 50; k++) {
            Integer item = as.getRandom(rand);
            assertNotNull(item);
            assertTrue(as.contains(item), "getRandom вернул удалённый элемент");
        }
    }

    @Test
    void testPopRandom() {
        ArraySet<Integer> as = new ArraySet<>();
        for (int i = 0; i < 5; i++) as.add(i);
        
        Integer popped = as.popRandom(rand);
        assertNotNull(popped);
        assertFalse(as.contains(popped));
        assertEquals(4, as.size());
    }

    @Test
    void testFullClear() {
        ArraySet<Integer> as = new ArraySet<>();
        for (int i = 0; i < 100; i++) as.add(i);
        for (int i = 0; i < 100; i++) as.remove(i);
        
        assertEquals(0, as.size());
        assertEquals(0, as.arrSize());
        assertTrue(as.isEmpty());
    }
}