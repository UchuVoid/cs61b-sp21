package deque;

public class ArrayDeque<T> implements Deque<T> {

    private int size = 0;
    private T[] arr = (T[]) new Object[8];
    private int front = 0;
    private int end = 0;

    public T[] getArr() {
        return arr;
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

    private int addOne(int index, int length) {
        index++;
        while (index >= length) {
            index -= length;
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

    public ArrayDeque() {

    }

    public ArrayDeque(T item) {
        arr[front] = item;
        front = minusOne(front);
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        if (size == 0) {
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
        if (size == 0) {
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
            size--;
            return temp;
        }
        return null;
    }

    @Override
    public T get(int index) {
        return arr[minusOne(front - index)];
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    public ArrayDeque(ArrayDeque other) {
        front = 0;
        end = 0;
        for (int i = 0; i < other.size; i++) {
            addLast((T) other.get(i));
        }
    }

}
