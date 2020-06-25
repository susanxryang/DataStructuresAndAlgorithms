package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    private Node<T> current;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        insert(Math.max(0, size), item);
    }

    @Override
    public T remove() {
        if (front == null){
            throw new EmptyContainerException();
        }
        Node<T> curr = back;
        if (back.equals(front)){                    // where there is only one element
            front = null;
            back = front;
        } else {
            back = back.prev;
            if (back.next != null) {
                back.next = null;
            }
        }
        size--;
        return curr.data;
    }

    @Override
    public T get(int index) {
        Node<T> curr = front;
        if (index < 0 || index >= this.size()){
            throw new IndexOutOfBoundsException();
        }

        if (index < (size / 2 + 1)){
            for (int i = 0; i < index; i++) {
                curr = curr.next;
            }
            return curr.data;

        } else {
            curr = back;
            for (int i = size - 1; i > index; i--) {
                curr = curr.prev;
            }
            return curr.data;
        }
    }

    @Override
    public void set(int index, T item) {
        Node<T> curr = front;
        if (index < 0 || index >= this.size()){
            throw new IndexOutOfBoundsException();
        }
        if (index == 0){ // if we're changing the first one
            front = new Node<>(null, item, front.next);
            if (front.next != null){
                front.next.prev = front;
            } else {
                back = front;
            }
        } else {
            for (int i = 0; i < index - 1; i++) {
                curr = curr.next;
            }
            Node temp = new Node<>(curr, item, curr.next.next);
            curr.next = temp;
            if (temp.next != null){
                curr.next.next.prev = temp;
            } else {
                back = temp;
            }

        }
    }

    @Override
    public void insert(int index, T item) {
        Node<T> curr = front;
        if (index < 0 || index > this.size()) {
            throw new IndexOutOfBoundsException();
        } else if (index == 0 && size() == 0){
            current = new Node<>(front, item, back);
            front = current;
            back = current;
        }
        else if (index == 0) {
            curr = new Node<>(null, item, null);
            curr.next = front;
            front.prev = curr;
            front = curr;
        } else if (index == size()){
            back.next = new Node<>(back, item, null);
            back = back.next;
        } else if (index < (size / 2 + 1)){
            for (int i = 0; i < index - 1; i++) {
                curr = curr.next;
            }
            curr.next = new Node<>(curr, item, curr.next);
            curr.next.next.prev = curr.next;
        } else {
            curr = back;
            for (int i = size - 1; i > index; i--) {
                curr = curr.prev;
            }
            curr.prev = new Node<>(curr.prev, item, curr);
            curr.prev.prev.next = curr.prev;
        }
        size++;
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size()){
            throw new IndexOutOfBoundsException();
        }
        if (size == 1 || index == size - 1) {
            return remove();
        } else {
            T result = front.data;
            if (index == 0) {
                front = front.next;
                front.prev = null;
            } else {
                current = front;
                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
                current.prev.next = current.next;
                current.next.prev = current.prev;
                result = current.data;
            }
            size--;
            return result;
        }
    }

    @Override
    public int indexOf(T item) {
        //printForward();
        current = front;
        for (int i = 0; i < size(); i++) {
            if (item == null && current.data == item){
                return i;
            }else if (current.data.equals(item)){
                return i;
            }
            else {
                current = current.next;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T other) {
        current = front;

        for (int i = 0; i < size; i++){
            if (other == null){
                if (current.data == other){
                    return true;
                } else {
                    current = current.next;
                }

            } else if (other instanceof String){
                if (!current.data.equals(other)){
                    current = current.next;
                } else {
                    return true;
                }
            } else if (other instanceof Integer){
                if (current.data != (other)){
                    current = current.next;
                } else {
                    return true;
                }
            }
            else {
                return true;
            }
        }
        return false;
    }

    // private void printBackward(){
    //     Node<T> curr = back;
    //     while (curr != null){
    //         System.out.print(curr.data);
    //         curr = curr.prev;
    //     }
    //     System.out.println();
    // }
    //
    // private void printForward(){
    //
    //     Node<T> curr = front;
    //     while (curr != null){
    //         System.out.print(curr.data);
    //         curr = curr.next;
    //     }
    //     System.out.println();
    // }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return (current != null);
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            T temp = current.data;
            current = current.next;
            return temp;
        }
    }
}
