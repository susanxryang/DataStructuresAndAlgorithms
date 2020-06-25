package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;

import java.util.Iterator;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    // However, feel free to add more fields and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    private IDictionary<T, Integer> map; // generic items and integer representatives
    private int size;

    public ArrayDisjointSet() {
        size = 0;
        map = new ChainedHashDictionary<>();
        pointers = new int[20];
    }

    @Override
    public void makeSet(T item) {
        if (map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        resize();
        map.put(item, size);
        pointers[size] = -1;
        size++;
    }

    private void resize() {
        if (size == pointers.length) {
            int[] newPointers = new int[2 * pointers.length];
            for (int i = 0; i < pointers.length; i++) {
                newPointers[i] = pointers[i];
            }
            this.pointers = newPointers;
        }
    }

    @Override
    public int findSet(T item) {
        // first find
        // store the ones on the path
        // do compression
        if (!map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        IList<Integer> arr = new DoubleLinkedList<Integer>();

        int rep = map.get(item); // gets the index rep of this item
        int parent = pointers[rep]; // finds the pointer for parent of this item

        while (parent >= 0){ //while the parent has pointer greater than 0, keep finder its parent
            arr.add(rep);
            rep = parent; // gets rep for the parent
            parent = pointers[parent]; // new parent is
        }

        Iterator<Integer> iter = arr.iterator();
        for (int i = 0; i < arr.size() - 1; i++) {
            pointers[iter.next()] = rep;
        }
        return rep;
    }

    @Override
    public void union(T item1, T item2) {
        if (!map.containsKey(item1) || !map.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        int rootId1 = findSet(item1);
        int rootId2 = findSet(item2);
        if (rootId1 == rootId2) {
            return;
        }

        int rank1 = pointers[rootId1];
        int rank2 = pointers[rootId2];

        int highRank = rootId1;
        int lowRank = rootId2;

        if (rank1 == rank2){ // if they have the same rank
            pointers[highRank] -= 1; // changes one of the rank to be +1
            rank1--;
        }

        if (rank1 > rank2) {
            highRank = rootId2;
            lowRank = rootId1;
        }
        pointers[lowRank] = highRank;
    }
}
