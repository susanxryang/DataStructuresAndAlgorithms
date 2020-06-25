package datastructures;

import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.InvalidElementException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * See spec for details on what kinds of tests this class should include.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrayHeap extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        assertEquals(1, heap.size());
        assertFalse(heap.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testBasicAddReflection() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        Comparable<Integer>[] array = getArray(heap);
        assertEquals(3, array[0]);
    }

    @Test(timeout=SECOND)
    public void testUpdateDecrease() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{1, 2, 3, 4, 5});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }

        IntWrapper newValue = new IntWrapper(0);
        heap.replace(values[2], newValue);

        assertEquals(newValue, heap.removeMin());
        assertEquals(values[0], heap.removeMin());
        assertEquals(values[1], heap.removeMin());
        assertEquals(values[3], heap.removeMin());
        assertEquals(values[4], heap.removeMin());
    }

    @Test(timeout=SECOND)
    public void testUpdateIncrease() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{0, 2, 4, 6, 8});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }

        IntWrapper newValue = new IntWrapper(5);
        heap.replace(values[0], newValue);

        assertEquals(values[1], heap.removeMin());
        assertEquals(values[2], heap.removeMin());
        assertEquals(newValue, heap.removeMin());
        assertEquals(values[3], heap.removeMin());
        assertEquals(values[4], heap.removeMin());
    }

    @Test(timeout=SECOND)
    public void testRemoveMin() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{2, 4, 3, 0, 5});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }

        assertEquals(values[3], heap.removeMin());
        assertEquals(4, heap.size());

        assertEquals(values[0], heap.removeMin());
        assertEquals(3, heap.size());

        assertEquals(values[2], heap.removeMin());
        assertEquals(2, heap.size());

        assertEquals(values[1], heap.removeMin());
        assertEquals(1, heap.size());

        assertEquals(values[4], heap.removeMin());
        assertEquals(0, heap.size());

        try {
            heap.removeMin();
            // We didn't throw an exception? Fail now.
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is ok
        }

    }

    @Test(timeout=SECOND)
    public void testPeek() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{0, 4, 2, 8, 1});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }
        assertEquals(5, heap.size());
        assertTrue(!heap.isEmpty());

        assertEquals(values[0], heap.peekMin());
        assertEquals(5, heap.size());

        heap.removeMin();
        assertEquals(values[4], heap.peekMin());

        heap.removeMin();
        assertEquals(values[2], heap.peekMin());

        heap.removeMin();
        assertEquals(2, heap.size());
        assertEquals(values[1], heap.peekMin());

        heap.removeMin();
        assertEquals(1, heap.size());
        assertEquals(values[3], heap.peekMin());

        assertEquals(values[3], heap.removeMin());
        assertEquals(0, heap.size());
    }


    @Test//(timeout= 10 * SECOND)
    public void stressTest() {
        int limit = 10000;
        IntWrapper[] values = new IntWrapper[limit];
        IPriorityQueue<IntWrapper> heap = this.makeInstance();
        assertFalse(heap.contains(new IntWrapper(-1)));

        for (int i = 0; i < values.length; i++){
            values[i] = new IntWrapper(i);
        }
        for (int j = values.length - 1; j >= 0; j--){
            heap.add(values[j]);
            assertTrue(heap.contains(values[j]));
        }
        assertEquals(10000, heap.size());

        for (int i = 0; i < values.length; i++){
            try {
                heap.add(values[i]);
                fail("InvalidElementException");
            } catch (InvalidElementException ex) {
                // do nothing
            }
        }

        for (int i = 0; i < limit; i++) {
            heap.remove(values[i]);
            assertFalse(heap.contains(values[i]));
        }
    }

    @Test(timeout = SECOND)
    public void testInsertDuplicateInt() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        try {
            heap.add(3);
            // We didn't throw an exception? Fail now.
            fail("Expected InvalidElementException");
        } catch (InvalidElementException ex) {
            // Do nothing: this is ok
        }
    }

    @Test(timeout = SECOND)
    public void testInsertDuplicateIntWrapper() {
        IPriorityQueue<IntWrapper> heap = this.makeInstance();
        heap.add(new IntWrapper(3));
        try {
            heap.add(new IntWrapper(3));
        } catch (InvalidElementException ex) {
            fail("InvalidElementException");
        }
        assertEquals(3, heap.removeMin().val);
    }

    @Test(timeout=SECOND)
    public void testInsertNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.add(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex){
            //do nothing
        }
    }

    @Test(timeout = SECOND)
    public void testRemove() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{1, 4, 3, 0, 5});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }
        heap.remove(values[2]);
        assertEquals(4, heap.size());
        try {
            heap.remove(new IntWrapper(4));
            fail("Expected InvalidElementException");
        } catch (InvalidElementException ex) {
            //do nothing
        }
        heap.remove(values[1]);
        assertEquals(3, heap.size());
    }



        @Test(timeout=SECOND)
        public void testRemoveNullException() {
            IPriorityQueue<Integer> heap = this.makeInstance();
            try {
                heap.removeMin();
                fail("Expected EmptyContainerException");
            } catch (EmptyContainerException ex){
                //do nothing
            }
        }


        @Test(timeout=SECOND)
        public void testPeekNullException() {
            IPriorityQueue<Integer> heap = this.makeInstance();
            try {
                heap.peekMin();
                fail("Expected EmptyContainerException");
            } catch (EmptyContainerException ex){
                //do nothing
            }
        }


        // below are the additional tests

        @Test(timeout=SECOND)
        public void testReplace() {
            IntWrapper[] values = IntWrapper.createArray(new int[]{1, 2, 3, 4, 5, 6});
            IPriorityQueue<IntWrapper> heap = this.makeInstance();

            for (IntWrapper value : values) {
                heap.add(value);
            }

            IntWrapper newValue = new IntWrapper(10);
            heap.replace(values[2], newValue);
            assertEquals(6, heap.size());
        }


        @Test(timeout = SECOND)
        public void testMatchStress() {
            int limit = 10;
            IPriorityQueue<Integer> heap = this.makeInstance();

            for (int j = 0; j < limit; j++){
                heap.add(j);
                assertTrue(heap.contains(j));
            }

            Comparable<Integer>[] array = getArray(heap);
            String heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            System.out.println();
            assertEquals("0123456789", heapArr);

            assertEquals(10, heap.size());
            for (int i = 0; i < limit; i++){
                try {
                    heap.add(i);
                    fail("InvalidElementException");
                } catch (InvalidElementException ex) {
                    // do nothing
                }
            }

            assertEquals(0, heap.removeMin());
            assertFalse(heap.contains(0));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("152349678", heapArr);
            assertEquals(9, heap.size());


            heap.remove(2);
            assertFalse(heap.contains(2));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("15834967", heapArr);
            assertEquals(8, heap.size());


            heap.add(0);
            assertTrue(heap.contains(0));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("018349675", heapArr);
            assertEquals(9, heap.size());


            for (int i = limit; i < limit * 2; i++){
                heap.add(i);
            }
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("01834967510111213141516171819", heapArr);
            assertEquals(19, heap.size());

            heap.add(-1);
            assertTrue(heap.contains(-1));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("-118309675101112131415161718194", heapArr);
            assertEquals(20, heap.size());

            heap.add(20);
            heap.add(21);
            heap.add(22);
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("-118309675101112131415161718194202122", heapArr);
            assertEquals(23, heap.size());


            heap.remove(1);
            assertFalse(heap.contains(1));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("-15830967221011121314151617181942021", heapArr);
            assertEquals(22, heap.size());

            heap.add(-10);
            assertTrue(heap.contains(-10));
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++){
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("-10-18305672210111213141516171819420219", heapArr);
            assertEquals(23, heap.size());
        }

        @Test(timeout = SECOND)
        public void removeBottomAndTop() {
            int limit = 25;
            IPriorityQueue<Integer> heap = this.makeInstance();

            for (int j = 0; j < limit; j++) {
                heap.add(j);
                assertTrue(heap.contains(j));
            }

            heap.add(-2);
            Comparable<Integer>[] array = getArray(heap);
            String heapArr = "";
            for (int i = 0; i < heap.size(); i++) {
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            //System.out.println();
            assertEquals("-20234517891011121314151617181920212223246", heapArr);

            heap.remove(0);
            array = getArray(heap);
            heapArr = "";
            for (int i = 0; i < heap.size(); i++) {
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            assertEquals("-2123456789101112131415161718192021222324", heapArr);
        }

        @Test(timeout=SECOND)
        public void testCheckLastChild() {
            IPriorityQueue<Integer> heap = this.makeInstance();
            heap.add(0);
            heap.add(4);
            heap.add(3);
            heap.add(2);
            heap.add(1);
            heap.add(20);
            heap.add(19);
            heap.add(18);
            heap.add(17);

            heap.remove(0);
            assertEquals(8, heap.size());


            Comparable<Integer>[] array = getArray(heap);
            String heapArr = "";
            for (int i = 0; i < heap.size(); i++) {
                //System.out.print(array[i]);
                heapArr += array[i];
            }
            assertEquals("143217201918", heapArr);
        }



    /**
     * A comparable wrapper class for ints. Uses reference equality so that two different IntWrappers
     * with the same value are not necessarily equal--this means that you may have multiple different
     * IntWrappers with the same value in a heap.
     */
    public static class IntWrapper implements Comparable<IntWrapper> {
        private final int val;

        public IntWrapper(int value) {
            this.val = value;
        }

        public static IntWrapper[] createArray(int[] values) {
            IntWrapper[] output = new IntWrapper[values.length];
            for (int i = 0; i < values.length; i++) {
                output[i] = new IntWrapper(values[i]);
            }
            return output;
        }

        @Override
        public int compareTo(IntWrapper o) {
            return Integer.compare(val, o.val);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return this.val;
        }

        @Override
        public String toString() {
            return Integer.toString(this.val);
        }
    }

    /**
     * A helper method for accessing the private array inside a heap using reflection.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> Comparable<T>[] getArray(IPriorityQueue<T> heap) {
        return getField(heap, "heap", Comparable[].class);
    }

}
