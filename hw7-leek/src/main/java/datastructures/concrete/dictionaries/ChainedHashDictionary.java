package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    private final double lambda;
    // You MUST use this field to store the contents of your dictionary.
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    // You're encouraged to add extra fields (and helper methods) though!
    private int originalLength = 5;
    //private int size = 50;
    private int full = 0;

    public ChainedHashDictionary() {
        this(0.75);
    }

    public ChainedHashDictionary(double lambda) {
        this.lambda = lambda;
        chains = makeArrayOfChains(originalLength);
        for (int i = 0; i < chains.length; i++) {
            chains[i] = new ArrayDictionary<K, V>();
        }
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[arraySize];
    }

    @Override
    public V get(K key) {
        if (!this.containsKey(key)){
            throw new NoSuchKeyException("cant get");
        }
        int bucket = this.getCode(key);
        V value = this.chains[bucket].get(key);
        return value;
    }

    @Override
    public void put(K key, V value) {
        if (!containsKey(key)){
            full++;
        }
        this.resize();
        IDictionary<K, V> bucket = chains[this.getCode(key)];
        bucket.put(key, value);
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)){
            throw new NoSuchKeyException("cant remove");
        }
        full--;
        int code = this.getCode(key);
        V value = this.chains[code].remove(key);
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        if (chains[getCode(key)] == null){
            return false;
        }
        int bucket = this.getCode(key);
        return this.chains[bucket].containsKey(key);
    }

    @Override
    public int size() {
        return this.full;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    private int getCode(K key) {
        int code;
        if (key == null) {
            code = 0;
        } else {
            code = Math.abs(key.hashCode() % this.chains.length);
        }
        return code;
    }

    private void resize(){
        if (full/chains.length < lambda){
            return;
        }
        int newLength = chains.length * 2;
        IDictionary<K, V>[] original = chains;
        this.chains = makeArrayOfChains(newLength);
        for (int i = 0; i < chains.length; i++){
            chains[i] = new ArrayDictionary<K, V>();
        }
        for (int i = 0; i < original.length; i++) {
            for (KVPair<K, V> pair : original[i]) {
                int code = this.getCode(pair.getKey());
                chains[code].put(pair.getKey(), pair.getValue());
            }
        }
        // for (int code = 0; code < length; code++) {
        //     IDictionary<K, V> bucket = chains[code];
        //     newChains[code] = bucket;
        //
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private Iterator<KVPair<K, V>> iter;
        private int index = 0;


        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            iter = chains[index].iterator();
            while (!iter.hasNext() && !this.hasNext() && index < chains.length - 1) {
                index++;
            }
            iter = chains[index].iterator();
        }

        @Override
        public boolean hasNext() {
            if (iter.hasNext()) {
                return true;
            } else {
                int i = index + 1;
                while (i < chains.length) {
                    if (chains[i] != null && !chains[i].isEmpty()) {
                        return true;
                    }
                    i++;
                }
                return false;
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else if (iter.hasNext()) {
                return iter.next();
            } else {
                index++;
                while (index < chains.length - 1 && chains[index].isEmpty()) {
                    index++;
                }
                iter = chains[index].iterator();
                return iter.next();
            }
        }

    }
}
