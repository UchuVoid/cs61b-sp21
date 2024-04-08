package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {

    // 创建一个整数比较器，用于测试
    private Comparator<Integer> comparator = Comparator.naturalOrder();

    @Test
    public void testMax() {
        // 使用整数比较器创建 MaxArrayDeque 对象
        MaxArrayDeque<Integer> lld1 = new MaxArrayDeque<>(comparator);
        lld1.addFirst(2000);
        // 向队列中添加元素
        for (int i = 0; i < 9; i++) {
            lld1.addFirst(i);
        }
        lld1.addFirst(200);
        lld1.addLast(100);

        // 测试 max 方法
        assertEquals(2000, (int) lld1.max());
    }

    @Test
    public void testParametriMax() {
        MaxArrayDeque<String> lld2 = new MaxArrayDeque<>();
        lld2.addFirst("aa");
        lld2.addFirst("b");
        lld2.addFirst("caa");
        lld2.addFirst("b");
        lld2.addFirst("dasd");
        lld2.addFirst("f");
        lld2.addFirst("fb");
        Comparator<String> comparator = Comparator.naturalOrder();
        assertEquals("fb", (String) lld2.max(comparator));
    }
}