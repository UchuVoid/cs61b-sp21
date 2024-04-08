package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public MaxArrayDeque() {
        super();
    }

    //怎么访问父类的数组
    public T max() {
        if (!super.isEmpty()) {
            T max = super.get(0);
            for(int i=0;i<super.size();i++) {
                if (comparator.compare(super.get(i), max) > 0) {
                    max = super.get(i);
                }
            }
            return max;
        }
        return null;
    }

    public T max(Comparator<T> c) {
        if (!super.isEmpty()) {
            T max = super.get(0);
            for(int i=0;i<super.size();i++) {
                if (c.compare(super.get(i), max) > 0) {
                    max = super.get(i);
                }
            }
            return max;
        }
        return null;
    }
}
