package deque;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.checkerframework.checker.units.qual.A;

public class ArrayDeque<T> {

    private int size = 0;
    private T[] arr = (T[]) new Object[8];
    private int front = 0;
    private int end = 0;

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
        T[] temp = (T[]) new Object[num];
        return temp;
    }

    public ArrayDeque() {

    }

    public ArrayDeque(T item) {
        arr[front] = item;
        front = minusOne(front);
        size++;
    }

    public int size() {
        return size;
    }

    public void addFirst(T item) {
        if (size == 0) {
            end = minusOne(end);
        }
        size++;
        if (size == arr.length) {
            T[] temp = reSize(arr.length * 2);
            for (int i = 0; i < front; i++) {
                temp[i] = arr[i];
            }
            for (int i = 0; i < end; i++) {
                temp[temp.length - 1 - i] = arr[arr.length - 1 - i];
            }
            arr = temp;
        }
        arr[front] = item;
        front = addOne(front);
    }

    public void addLast(T item) {
        if (size == 0) {
            front = addOne(front);
        }
        size++;
        if (size == arr.length) {
            T[] temp = reSize(arr.length * 2);
            for (int i = 0; i < front; i++) {
                temp[i] = arr[i];
            }
            for (int i = 0; i < arr.length - end - 1; i++) {
                temp[temp.length - 1 - i] = arr[arr.length - 1 - i];
            }
            end = temp.length - (arr.length - end);
            arr = temp;
        }
        arr[end] = item;
        end = minusOne(end);
    }

    public T removeFirst() {
        if (size != 0) {
            front = minusOne(front);
            T temp = arr[front];
            size--;
            return temp;
        }
        return null;
    }

    public T removeLast() {
        if (size != 0) {
            end = addOne(end);
            T temp = arr[end];
            size--;
            return temp;

        }
        return null;
    }

    public T get(int index) {
        return arr[minusOne(front - index)];
    }

    public void printDeque() {
        for(int i=0;i<size;i++){
            System.out.print(get(i)+" ");
        }
        System.out.println("");
    }

    public boolean isEmpty() {
        return size == 0 ? true : false;
    }
    public ArrayDeque(ArrayDeque other){
        front=0;
        end=0;
        for(int i=0;i<other.size;i++){
            addLast((T)other.get(i));
        }
    }

}
