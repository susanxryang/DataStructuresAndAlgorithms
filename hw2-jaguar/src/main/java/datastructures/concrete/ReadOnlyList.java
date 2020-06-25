package datastructures.concrete;

import datastructures.interfaces.IList;

import java.util.Iterator;

/**
 * ReadOnlyList provides a read-only view of another IList.
 *
 * The list can't be modified through an instance of this class, and any method that mutates the
 * list (add, remove, set, insert, delete) will throw an UnsupportedOperationException instead.
 * Note that anyone with a reference to the original list can still modify it (which will affect
 * this list since it's only a view), so the list is not truly immutable.
 */
public class ReadOnlyList<T> implements IList<T> {
    private IList<T> list;

    /**
     * Creates a read-only view of the list.
     */
    public ReadOnlyList(IList<T> list) {
        this.list = list;
    }

    /**
     * @throws UnsupportedOperationException This method is not supported on a ReadOnlyList.
     */
    @Override
    public void add(T item) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException This method is not supported on a ReadOnlyList.
     */
    @Override
    public T remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    /**
     * @throws UnsupportedOperationException This method is not supported on a ReadOnlyList.
     */
    @Override
    public void set(int index, T item) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException This method is not supported on a ReadOnlyList.
     */
    @Override
    public void insert(int index, T item) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException This method is not supported on a ReadOnlyList.
     */
    @Override
    public T delete(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(T item) {
        return list.indexOf(item);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean contains(T other) {
        return list.contains(other);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<T> iterator = list.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
