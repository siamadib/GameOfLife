import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic dynamic array implementation that resizes automatically as elements are added or removed.
 * <p>
 * This class mimics some functionality of Java's ArrayList but is implemented manually as required
 * for the CS310 Project 1: Game of Life assignment. It supports adding, removing, and accessing elements.
 * </p>
 *
 * @param <T> the type of elements stored in this array
 */
public class DynamicArray<T> implements Iterable<T> {

    /** The initial minimum capacity of the array. */
    private static final int INITCAP = 2;

    /** The internal storage array. */
    private T[] storage;

    /** The number of elements currently stored. */
    private int size;

    /**
     * Constructs an empty DynamicArray with the default initial capacity.
     */
    @SuppressWarnings("unchecked")
    public DynamicArray() {
        this.storage = (T[]) new Object[INITCAP];
        this.size = 0;
    }

    /**
     * Constructs an empty DynamicArray with a specified initial capacity.
     *
     * @param initCapacity the initial capacity of the array
     * @throws IllegalArgumentException if initCapacity is less than 1
     */
    @SuppressWarnings("unchecked")
    public DynamicArray(int initCapacity) {
        if (initCapacity < 1) {
            throw new IllegalArgumentException("Capacity cannot be zero or negative.");
        }
        this.storage = (T[]) new Object[initCapacity];
        this.size = 0;
    }

    /**
     * Returns the number of elements currently stored.
     *
     * @return the size of the array
     */
    public int size() {
        return size;
    }

    /**
     * Returns the total capacity of the internal storage.
     *
     * @return the capacity of the array
     */
    public int capacity() {
        return storage.length;
    }

    /**
     * Replaces the element at the specified index with a new value.
     *
     * @param index the position to replace
     * @param value the new value to insert
     * @return the old value previously stored at the index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public T set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds!");
        }
        T old = storage[index];
        storage[index] = value;
        return old;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index the position to access
     * @return the element stored at the given index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds!");
        }
        return storage[index];
    }

    /**
     * Adds an element to the end of the array, expanding capacity as needed.
     *
     * @param value the value to add
     * @return true if the element was added successfully
     */
    @SuppressWarnings("unchecked")
    public boolean add(T value) {
        if (size == storage.length) {
            int newCap = storage.length * 2;
            T[] newStorage = (T[]) new Object[newCap];
            System.arraycopy(storage, 0, newStorage, 0, storage.length);
            storage = newStorage;
        }
        storage[size++] = value;
        return true;
    }

    /**
     * Inserts an element at a specific index, shifting elements to the right as necessary.
     *
     * @param index the index at which to insert
     * @param value the element to insert
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @SuppressWarnings("unchecked")
    public void add(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds!");
        }
        if (size == storage.length) {
            int newCap = storage.length * 2;
            T[] newStorage = (T[]) new Object[newCap];
            System.arraycopy(storage, 0, newStorage, 0, storage.length);
            storage = newStorage;
        }
        for (int i = size - 1; i >= index; i--) {
            storage[i + 1] = storage[i];
        }
        storage[index] = value;
        size++;
    }

    /**
     * Removes and returns the element at a specific index.
     * Shrinks capacity if the size becomes less than one-third of capacity.
     *
     * @param index the index to remove
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds!");
        }
        T old = storage[index];
        for (int i = index; i < size - 1; i++) {
            storage[i] = storage[i + 1];
        }
        storage[size - 1] = null;
        size--;

        int cap = storage.length;
        if (size < cap / 3 && cap / 2 >= INITCAP) {
            int newCap = cap / 2;
            if (newCap < INITCAP) {
                newCap = INITCAP;
            }
            T[] newStorage = (T[]) new Object[newCap];
            System.arraycopy(storage, 0, newStorage, 0, size);
            storage = newStorage;
        }
        return old;
    }

    /**
     * Returns an iterator that traverses this DynamicArray from beginning to end.
     *
     * @return an iterator for the array
     */
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            public boolean hasNext() {
                return cursor < size;
            }

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return storage[cursor++];
            }
        };
    }

    /**
     * Returns a formatted string showing the array's contents, size, and capacity.
     *
     * @return a string representation of the DynamicArray
     */
    public String toString() {
        StringBuilder s = new StringBuilder("Dynamic array with " + size()
                + " items and a capacity of " + capacity() + ":");
        for (int i = 0; i < size(); i++) {
            s.append("\n  [").append(i).append("]: ").append(get(i));
        }
        return s.toString();
    }

    /**
     * A simple main method for testing the DynamicArray implementation.
     * Expected output includes "Yay 1" through "Yay 4" and printed element values.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        DynamicArray<Integer> ida = new DynamicArray<>();
        if ((ida.size() == 0) && (ida.capacity() == 2)) {
            System.out.println("Yay 1");
        }

        boolean ok = true;
        for (int i = 0; i < 3; i++) {
            ok = ok && ida.add(i * 5);
        }

        if (ok && ida.size() == 3 && ida.get(2) == 10 && ida.capacity() == 4) {
            System.out.println("Yay 2");
        }

        ida.add(1, -10);
        ida.add(4, 100);
        if (ida.set(1, -20) == -10 && ida.get(2) == 5 && ida.size() == 5
                && ida.capacity() == 8) {
            System.out.println("Yay 3");
        }

        if (ida.remove(0) == 0 && ida.remove(0) == -20 && ida.remove(2) == 100
                && ida.size() == 2 && ida.capacity() == 4) {
            System.out.println("Yay 4");
        }

        System.out.print("Printing values: ");
        for (Integer i : ida) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }
}
