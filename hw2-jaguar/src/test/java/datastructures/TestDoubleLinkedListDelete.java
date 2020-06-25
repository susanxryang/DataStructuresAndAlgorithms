package datastructures;

import datastructures.interfaces.IList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import datastructures.concrete.DoubleLinkedList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class should contain all the tests you implement to verify that
 * your 'delete' method behaves as specified. You should give your tests
 * with a timeout of 1 second.
 *
 * This test extends the BaseTestDoubleLinkedList class. This means that
 * you can use the helper methods defined within BaseTestDoubleLinkedList.
 * @see BaseTestDoubleLinkedList
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDoubleLinkedListDelete extends BaseTestDoubleLinkedList {
    @Test(timeout=SECOND)
    public void testExample() {
        IList<String> list = makeBasicList();
        // Feel free to modify or delete this dummy test.
        assertTrue(true);
        assertEquals(3, 3);
    }

    @Test(timeout=SECOND)
    public void testDeleteMiddleElement(){
        //arrange
        IList<String> list = makeBasicList();
        list.add("x");
        list.add("y");
        list.add("z");

        //act
        list.delete(1);

        //assert
        this.assertListValidAndMatches(new String[] {"a", "c", "x", "y", "z"}, list);

        //act
        list.delete(3);

        //assert
        this.assertListValidAndMatches(new String[] {"a", "c", "x", "z"}, list);
    }

    @Test(timeout=SECOND)
    public void testDeleteIndexOfAndDeleteMiddle(){
        //arrange
        IList<String> list = makeBasicList();
        //act
        list.delete(1);
        //assert
        assertEquals(-1, list.indexOf("b"));
    }

    @Test(timeout=SECOND)
    public void testDeleteUpdatesSize(){
        //arrange
        IList<String> list = makeBasicList();
        list.add("x");

        //act, assert
        assertEquals("b", list.delete(1));
        assertEquals(3, list.size());
        assertEquals("x", list.delete(2));
        assertEquals(2, list.size());
    }

    @Test(timeout=SECOND)
    public void testDeleteFrontElement(){
        //arrange
        IList<String> list = makeBasicList();
        list.add("x");
        list.add("y");
        list.add("z");
        //act, assert
        assertEquals("a", list.delete(0));
        this.assertListValidAndMatches(new String[] {"b", "c", "x", "y", "z"}, list);
    }

    @Test(timeout=SECOND)
    public void testDeleteBackElement(){
        //arrange
        IList<String> list = makeBasicList();
        list.add("x");
        list.add("y");
        list.add("z");
        //act, assert
        assertEquals("z", list.delete(5));
        this.assertListValidAndMatches(new String[] {"a", "b", "c", "x", "y"}, list);
    }

    //dont know what that means3
    @Test(timeout = SECOND)
    public void testDeleteDuplicates(){
        //arrange
        IList<String> list = makeBasicList();
        list.add("c");
        list.add("b");
        list.add("a");
        //act
        assertEquals("a", list.delete(0));
        assertEquals("a", list.delete(4));
        //assert
        this.assertListValidAndMatches(new String[] {"b", "c", "c", "b"}, list);

    }

    @Test(timeout = SECOND)
    public void testDeleteSingleElementList(){
        //arrange
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);

        //act
        assertEquals(1, list.delete(0));
        //assert
        assertTrue(list.isEmpty());

        //act
        assertFalse(list.contains(1));
        //assert
        assertEquals(-1, list.indexOf(1));
    }

    @Test(timeout = SECOND)
    public void testDeleteOutOfBoundsThrowsException(){
        //arrage
        IList<String> list = makeBasicList();

        //act, assert
        try {
            list.delete(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }

        try {
            list.delete(3);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }


    // Above are some examples of provided assert methods from JUnit,
    // but in these tests you will also want to use a custom assert
    // we have provided you in BaseTestDoubleLinkedList called
    // assertListValidAndMatches. It will check many properties of
    // your DoubleLinkedList so you will want to use it frequently.
    // For usage examples, you can refer to TestDoubleLinkedList,
    // and refer to BaseTestDoubleLinkedList for the method comment.
}
