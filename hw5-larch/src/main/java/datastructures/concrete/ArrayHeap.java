package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.InvalidElementException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private IDictionary<T, Integer> map;

    // Feel free to add more fields and constants.
    private int capacity = 100;
    private int size;

    public ArrayHeap() {
        this.heap = makeArrayOfT(capacity);
        this.size = 0;
        map = new ChainedHashDictionary<>();
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * upwards from a given index, if necessary.
     */
    private void percolateUp(int index) {
        // find parent

        int parent = (index - 1)/NUM_CHILDREN;
        if (parent < 0){
            map.put(heap[index], index);
            return;
        } else if (heap[parent].compareTo(heap[index]) <= 0){
            map.put(heap[index], index);
            return;
        } else { // swap the parent and child
            swap(parent, index);
            percolateUp(parent);
        }

    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * downwards from a given index, if necessary.
     */
    private void percolateDown(int index) {
        int minIndex = index;
        // Compare child
        int child1 = index * NUM_CHILDREN + 1;
        int child2 = index * NUM_CHILDREN + 2;
        int child3 = index * NUM_CHILDREN + 3;
        int child4 = index * NUM_CHILDREN + 4;

        if (child1 < this.size && this.heap[child1].compareTo(this.heap[minIndex]) < 0) {
            minIndex = child1;
        }
        if (child2 < this.size && this.heap[child2].compareTo(this.heap[minIndex]) < 0) {
            minIndex = child2;
        }
        if (child3 < this.size && this.heap[child3].compareTo(this.heap[minIndex]) < 0) {
            minIndex = child3;
        }
        if (child4 < this.size && this.heap[child4].compareTo(this.heap[minIndex]) < 0) {
            minIndex = child4;
        }
        //when curIndex is the min value.
        if (minIndex == index) {
            map.put(heap[index], index);
            return;
        }
        swap(minIndex, index);
        percolateDown(minIndex);
    }

    /**
     * A method stub that you may replace with a helper method for determining
     * which direction an index needs to percolate and percolating accordingly.
     */
    private void percolate(int index) {
        int parent = (index - 1) / 4;
        if (index == 0 || this.heap[parent].compareTo(this.heap[index]) < 0) {
            percolateDown(index);
        } else if (this.heap[parent].compareTo(this.heap[index]) > 0){
            percolateUp(index);
        }
    }

    /**
     * A method stub that you may replace with a helper method for swapping
     * the elements at two indices in the 'heap' array.
     */
    private void swap(int a, int b) {
        T temp = this.heap[a];
        this.heap[a] = this.heap[b];
        map.put(heap[b], a);

        this.heap[b] = temp;
        map.put(temp, b);
    }

    @Override
    public T removeMin() {
        if (size == 0){
            throw new EmptyContainerException();
        } else {
            map.remove(heap[0]);
            T min = heap[0];
            T last = heap[size - 1];
            heap[0] = last;
            this.heap[size - 1] = null;
            size--;
            percolateDown(0);
            return min;
        }
    }

    @Override
    public T peekMin(){
        if (size == 0){
            throw new EmptyContainerException();
        }
        return heap[0];
    }

    @Override
    public void add(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        }
        if (this.contains(item)){
            throw new InvalidElementException();
        } else {
            heap[size] = item;
            size++;
            percolateUp(size - 1);
            this.resize();
        }
    }

    @Override
    public boolean contains(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        } else {
            return map.containsKey(item);
        }
    }

    @Override
    public void remove(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        } else if (!this.contains(item)){
            throw new InvalidElementException();
        } else {
            int index = map.remove(item);
            size--;
            if (index == size){
                heap[size] = null;
            } else {
                T last = heap[size];
                map.put(last, index);
                heap[size] = null;
                heap[index] = last;
                percolate(index);
            }
        }
    }

    @Override
    public void replace(T oldItem, T newItem) {
        if (this.contains(newItem)){
            throw new InvalidElementException();
        }
        if (!this.contains(oldItem)){
            throw new InvalidElementException();
        }
        else {
            int index = map.remove(oldItem);
            heap[index] = newItem;
            map.put(newItem, index);
            percolate(index);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    private int getIndex(T item){
        return map.get(item);
    }

    private void resize() {
        if (size >= capacity - 1){
            this.capacity = this.capacity * 2;
            T[] newHeap = makeArrayOfT(capacity);
            int i = 0;
            for (T item : heap) {
                newHeap[i] = item;
                i++;
            }
            this.heap = newHeap;
        }
    }
}
