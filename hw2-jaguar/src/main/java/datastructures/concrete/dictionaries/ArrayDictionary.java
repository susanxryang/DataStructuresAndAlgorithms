package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
// import misc.exceptions.NoSuchKeyException;

// import misc.exceptions.NotYetImplementedException;
// import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;

    // You may add extra fields or helper methods though!
    private int elements; // stores the number of elements

    public ArrayDictionary() {
        this.pairs = new Pair[10];
        this.elements = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if (key == null){
            for (int i = 0; i < elements; i++){
                if (pairs[i].key == null){
                    return pairs[i].value;
                }
            }
        } else {
            for (int i = 0; i < elements; i++) {
                if (key.equals(pairs[i].key)) {
                    return pairs[i].value;
                }
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        Pair<K, V> newPair = new Pair<>(key, value);
        int index = getIndex(key);
        if (index < elements){ // key found in original array
            pairs[index] = newPair;
        }
        else { // not found
            if (pairs.length == this.elements){
                Pair<K, V>[] newArr = new Pair[pairs.length * 2];          // make a new array with twice the size
                for (int i = 0; i < this.elements; i++){                   // move everything to the new array
                    newArr[i] = pairs[i];
                }
                this.pairs = newArr;
            }
            pairs[elements] = newPair;
            elements++;
        }
    }

    @Override
    public V remove(K key) {
        V result;
        int index = getIndex(key); // finds the index of this key, returns 0 if not found
        if (index < elements) {
            result = pairs[index].value;
            pairs[index] = pairs[elements - 1];
            elements--;
            return result;
        }
        throw new NoSuchKeyException();
        //return result; // return null when not found / nothing to remove?
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null){
            for (int i = 0; i < elements; i++){
                if (pairs[i].key == null){
                    return true;
                }
            }
        } else {
            for (int i = 0; i < elements; i++) {
                if (key.equals(pairs[i].key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.elements;
    }

    private int getIndex(K key) {
        if (key == null){
            for (int i = 0; i < elements; i++){
                if (pairs[i].key == null){
                    return i;
                }
            }
        } else {
            for (int i = 0; i < elements; i++) {
                if (key.equals(pairs[i].key)) {
                    return i;
                }
            }
        }
        return elements;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
