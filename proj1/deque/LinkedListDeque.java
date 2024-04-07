package deque;

public class LinkedListDeque<T> implements Deque<T> {
    static private class TNode<T> {
        private T item;
        private TNode prev;
        private TNode next;

        public TNode(TNode<T> prev, T item, TNode<T> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private TNode<T> sentinel;
    private int size = 0;

    public LinkedListDeque() {
        sentinel = new TNode<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item) {
        size++;
        TNode<T> temp = new TNode<>(sentinel, item, sentinel.next);
        sentinel.next.prev = temp;
        sentinel.next = temp;

    }

    public void addLast(T item) {
        size++;
        TNode<T> temp = new TNode<>(sentinel.prev, item, sentinel);
        sentinel.prev.next = temp;
        sentinel.prev = temp;
    }

    public boolean isEmpty() {
        return size == 0 ? true : false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        TNode<T> current = sentinel.next;
        while (current != sentinel) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println("");
    }

    public T removeFirst() {
        if (!isEmpty()) {
            size--;
            T temp = (T) sentinel.next.item;
            sentinel.next = sentinel.next.next;
            return temp;
        }
        return null;
    }

    public T removeLast() {
        if (!isEmpty()) {
            size--;
            T temp = (T) sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            return temp;
        }
        return null;
    }

    public T get(int index) {
        TNode<T> current = sentinel.next;
        int i = 0;
        while (current != null) {
            if (i == index) {
                return current.item;
            }
            i++;
            current = current.next;

        }
        return null;
    }

    public LinkedListDeque(LinkedListDeque other) {
        sentinel = new TNode<T>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
        for (int i = 0; i < other.size; i++) {
            addLast((T) other.get(i));
        }
    }

    public T getRecursize(int index, TNode<T> temp) {
        if (index == 0) {
            return temp.item;
        }
        getRecursize(index - 1, temp.next);
        return null;
    }
}
