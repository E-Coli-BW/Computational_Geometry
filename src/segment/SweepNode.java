package segment;

interface SweepNode {
  /** Get segment. */
  Segment getSegment ();

  /** Get the next node in the list. */
  SweepNode getNext ();

  /** Get the previous node in the list. */
  SweepNode getPrevious ();
  
  /** Swap the segment points of this and the next node in the list.
      Also swap the node pointers in these segments. */
  void swapWithNext ();

  /** Swap the segment points of this and the previous node in the list.
      Also swap the node pointers in these segments. */
  void swapWithPrevious ();

  /** Remove this node from the list.  Set the segment's node pointer
      to null.  Return the segment. */
  Segment remove ();
}



