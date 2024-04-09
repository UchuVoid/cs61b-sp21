package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private static class TNode<T> {
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

    @Override
    public void addFirst(T item) {
        size++;
        TNode<T> temp = new TNode<>(sentinel, item, sentinel.next);
        sentinel.next.prev = temp;
        sentinel.next = temp;

    }

    @Override
    public void addLast(T item) {
        size++;
        TNode<T> temp = new TNode<>(sentinel.prev, item, sentinel);
        sentinel.prev.next = temp;
        sentinel.prev = temp;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        TNode<T> current = sentinel.next;
        while (current != sentinel) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (!isEmpty()) {
            size--;
            T temp = (T) sentinel.next.item;
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
            return temp;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (!isEmpty()) {
            size--;
            T temp = (T) sentinel.prev.item;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            return temp;
        }
        return null;
    }

    @Override
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

    public T getRecursive(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return (T) getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, TNode<T> currentNode) {
        if (index == 0) {
            return currentNode.item;
        }
        return (T) getRecursiveHelper(index - 1, currentNode.next);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    // 内部迭代器类
    private class LinkedListDequeIterator implements Iterator<T> {
        private int index = 0;
        private int iteratorSize = size;

        @Override
        public boolean hasNext() {
            return (iteratorSize - index) != 0;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T item = get(index);
            index++;

            return item;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<?> lld = (LinkedListDeque<?>) o;
        if (lld.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (lld.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }

}
