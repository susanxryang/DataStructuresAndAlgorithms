package problems;

import datastructures.LinkedIntList;
// import misc.exceptions.NotYetImplementedException;

// IntelliJ will complain that this is an unused import, but you should use ListNode variables
// in your solution, and then this error should go away.

import datastructures.LinkedIntList.ListNode;

//import java.util.List;

/**
 * Parts b.iii, b.iv, and b.v should go here.
 *
 * (Implement reverse3, firstLast, and shift as described by the spec
 *  See the spec on the website for picture examples and more explanation!)
 *
 * REMEMBER THE FOLLOWING RESTRICTIONS:
 * - do not construct new ListNode objects (though you may have as many ListNode variables as you like).
 * - do not call any LinkedIntList methods
 * - do not construct any external data structures like arrays, queues, lists, etc.
 * - do not mutate the .data field of any nodes, change the list only by modifying links between nodes.
 * - Your solution should run in linear time with respect to the number of elements of the linked list.
 */

public class LinkedIntListProblems {

    // Reverses the 3 elements in the LinkedIntList (assume there are only 3 elements).
    // make the end of the list head, ethe middle mid, and have the front point to null
    public static void reverse3(LinkedIntList list) {
        ListNode head = list.front.next.next;
        ListNode mid = list.front.next;
        list.front.next = null;
        mid.next = list.front;
        head.next = mid;
        list.front = head;
    }

    // move
    public static void shift(LinkedIntList list) {
        if (list.front != null && list.front.next != null && list.front.next.next != null){
            ListNode tail = list.front;
            ListNode otherCurr = list.front.next;
            int count = 0;
            while (tail.next != null && tail.next.next != null){
                tail = tail.next.next;
                count++;
            }
            ListNode last = tail.next;
            if (last != null){
                last.next = null;
            }
            tail.next = otherCurr;

            ListNode curr = list.front;

            while (count > 1){
                curr.next = curr.next.next;
                otherCurr.next = curr.next.next;
                curr = curr.next;
                otherCurr = otherCurr.next;
                count--;
            }
            curr.next = curr.next.next;
            //this line is where the problem is
            otherCurr.next = last;
        }
    }

    public static void firstLast(LinkedIntList list) {
        ListNode curr = list.front;

        while (curr.next != null){
            curr = curr.next;
        }
        curr.next = list.front;
        list.front = list.front.next;
        curr.next.next = null;
    }
}
