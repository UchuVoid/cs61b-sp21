package deque;

import java.util.Iterator; // 引入 Iterator 类

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] arr;
    private int front;
    private int end;

    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        end = 0;
        front = 0;
    }

    private int minusOne(int index) {
        index--;
        while (index < 0) {
            index += arr.length;
        }
        return index;
    }

    private int addOne(int index) {
        index++;
        while (index >= arr.length) {
            index -= arr.length;
        }
        return index;
    }


    private T[] reSize(int num) {
        return (T[]) new Object[num];
    }

    private T[] copyArr(T[] temp) {
        if (front >= 0) {
            System.arraycopy(arr, 0, temp, 0, front);
        }
        for (int i = 0; i < arr.length - end - 1; i++) {
            temp[temp.length - 1 - i] = arr[arr.length - 1 - i];
        }
        end = temp.length - (arr.length - end);
        arr = temp;
        return temp;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        if (size == 0 && end == front) {
            end = minusOne(end);
        }
        size++;
        if (size == arr.length) {
            T[] temp = reSize(arr.length * 2);
            copyArr(temp);
        }
        arr[front] = item;
        front = addOne(front);
    }

    @Override
    public void addLast(T item) {
        if (size == 0 && end == front) {
            front = addOne(front);
        }
        size++;
        if (size == arr.length) {
            T[] temp = reSize(arr.length * 2);
            copyArr(temp);
        }
        arr[end] = item;
        end = minusOne(end);
    }

    @Override
    public T removeFirst() {
        if (size != 0) {
            front = minusOne(front);
            T temp = arr[front];
            arr[front] = null;
            size--;
            if (size > 4 && size < arr.length / 4) {
                T[] t = reSize(arr.length / 4);
                arr = copyArr(t);
            }
            return temp;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size != 0) {
            end = addOne(end);
            T temp = arr[end];
            arr[end] = null;
            size--;
            return temp;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (index < size) {
            return arr[minusOne(front - index)];
        } else {
            return null;
        }
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    // 内部迭代器类
    private class ArrayDequeIterator implements Iterator<T> {
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
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            T item1 = get(i);
            T item2 = other.get(i);
            if (!item1.equals(item2)) {
                return false;
            }
        }
        return true;
    }
}
