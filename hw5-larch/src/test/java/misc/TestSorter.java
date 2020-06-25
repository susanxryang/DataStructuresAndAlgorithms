package misc;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.exceptions.InvalidElementException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * See spec for details on what kinds of tests this class should include.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSorter extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }

    @Test(timeout = 10 * SECOND)
    public void testStress() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 50000; i++) {
            list.add(i);
        }
        IList<Integer> top = Sorter.topKSort(20000, list);
        assertEquals(20000, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(30000 + i, top.get(i));
        }
    }

    @Test(timeout = SECOND)
    public void testAdd() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(8);
        list.add(9);
        list.add(57);
        list.add(3);
        list.add(0);
        list.add(92);
        list.add(16);
        list.add(43);
        list.add(34);
        list.add(13);
        //0,3,8,9,13,16,34,43,57,92

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());
        assertEquals(16, top.get(0));
        assertEquals(34, top.get(1));
        assertEquals(43, top.get(2));
        assertEquals(57, top.get(3));
        assertEquals(92, top.get(4));
    }

    @Test(timeout = SECOND)
    public void testKNegative() {
        IList<Integer> list = new DoubleLinkedList<>();
        try {
            IList<Integer> top = Sorter.topKSort(-1, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            //do nothing
        }
    }

    @Test(timeout = SECOND)
    public void testGetLargeK() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(8);
        list.add(9);
        list.add(3);
        list.add(0);
        list.add(13);
        list.add(1);
        //0,1,3,8,9,13

        IList<Integer> top = Sorter.topKSort(10, list);
        assertEquals(6, top.size());
        assertEquals(0, top.get(0));
        assertEquals(1, top.get(1));
        assertEquals(3, top.get(2));
        assertEquals(8, top.get(3));
        assertEquals(9, top.get(4));
        assertEquals(13, top.get(5));
    }


    // below are the added tests
    @Test(timeout = SECOND)
    public void testKZero() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(8);
        list.add(0);
        list.add(3);
        list.add(3);
        IList<Integer> top = Sorter.topKSort(0, list);
        assertEquals(0, top.size());
        assertTrue(top.isEmpty());

    }

    @Test(timeout = SECOND)
    public void testMutatesInputLargeK() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(8);
        list.add(9);
        list.add(3);
        list.add(0);
        list.add(13);
        list.add(1);
        //0,1,3,8,9,13

        IList<Integer> top = Sorter.topKSort(10, list);
        String listString = "";
        for (int i = 0; i < list.size(); i++){
            listString += list.get(i);
        }
        assertEquals(listString, "8930131");
    }

    @Test(timeout = SECOND)
    public void testMutatesInput() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(8);
        list.add(9);
        list.add(3);
        list.add(0);
        list.add(13);
        list.add(1);
        list.add(14);
        list.add(11);
        list.add(18);
        //0,1,3,8,9,13

        IList<Integer> top = Sorter.topKSort(5, list);
        String listString = "";
        for (int i = 0; i < list.size(); i++){
            listString += list.get(i);
        }
        assertEquals(listString, "8930131141118");
    }



    // added tests
    private <T> void assertListMatches(T[] expected, IList<T> actual) {
        assertEquals(expected.length, actual.size());
        assertEquals(expected.length == 0, actual.isEmpty());
        for (int i = 0; i < expected.length; i++) {
            try {
                assertEquals("Item at index " + i + " does not match", expected[i], actual.get(i));
            } catch (Exception ex) {
                String errorMessage = String.format(
                        "Got %s when getting item at index %d (expected '%s')",
                        ex.getClass().getSimpleName(), i, expected[i]);
                throw new AssertionError(errorMessage, ex);
            }
        }
    }

    @Test(timeout=SECOND)
    public void testInputEmpty() {
        IList<Integer> list = new DoubleLinkedList<>();
        IList<Integer> top = Sorter.topKSort(0, list);
        assertListMatches(new Integer[] {}, top);
        top = Sorter.topKSort(3, list);
        this.assertListMatches(new Integer[] {}, top);
    }


    @Test(timeout=SECOND)
    public void testInputSameElement() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(1);
        list.add(1);
        IList<Integer> top = Sorter.topKSort(0, list);
        this.assertListMatches(new Integer[] {}, top);
        try {
            top = Sorter.topKSort(3, list);
            fail("Expected InvalidElementException");
        } catch (InvalidElementException ex) {
            //do nothing
        }
    }

    @Test(timeout=SECOND)
    public void testInputNegative() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(-1);
        list.add(-3);
        list.add(-5);
        list.add(-7);
        list.add(-9);
        IList<Integer> top = Sorter.topKSort(0, list);
        this.assertListMatches(new Integer[] {}, top);
        top = Sorter.topKSort(3, list);
        this.assertListMatches(new Integer[] {-5, -3, -1}, top);
        top = Sorter.topKSort(5, list);
        this.assertListMatches(new Integer[] {-1, -3, -5, -7, -9}, list);
        this.assertListMatches(new Integer[] {-9, -7, -5, -3, -1}, top);
        top = Sorter.topKSort(7, list);
        this.assertListMatches(new Integer[] {-9, -7, -5, -3, -1}, top);
        this.assertListMatches(new Integer[] {-1, -3, -5, -7, -9}, list);
    }
}
