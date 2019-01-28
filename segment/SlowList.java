package segment;

class SlowList implements SweepList {
  class SlowNode implements SweepNode {
    private Segment segment;
    private SlowNode prev, next;

    SlowNode (Segment s, SlowNode prev, SlowNode next) {
      segment = s;
      this.prev = prev;
      this.next = next;
      s.node = this;
    }

    /** Get segment. */
    public Segment getSegment () { return segment; }

    /** Get the next node in the list. */
    public SweepNode getNext () { return next; }

    /** Get the previous node in the list. */
    public SweepNode getPrevious () { return prev; }
  
    /** Swap the segment points of this and the next node in the list.
        Also swap the node pointers in these segments. */
    public void swapWithNext () {
      Segment nexts = next.segment;
      next.segment = segment;
      segment = nexts;
      segment.node = this;
      next.segment.node = next;
    }

    /** Swap the segment points of this and the previous node in the list.
        Also swap the node pointers in these segments. */
    public void swapWithPrevious () {
      Segment prevs = prev.segment;
      prev.segment = segment;
      segment = prevs;
      segment.node = this;
      prev.segment.node = prev;
    }

    /** Remove this node from the list.  Set the segment's node pointer
        to null.  Return the segment. */
    public Segment remove () {
      if (prev == null)
        head = next;
      else
        prev.next = next;
      if (next == null)
        tail = prev;
      else
        next.prev = prev;
      segment.node = null;
      return segment;
    }
  }

  private SlowNode head, tail;

  /** Return first Node in list. */
  public SweepNode getFirst () { return head; }

  /** Return last Node in list. */
  public SweepNode getLast () { return tail; }

  /** Insert segment into list.
      Return Node record that points to s.
      Set s.node to point to that Node. */
  public SweepNode add (Segment s) {
    SlowNode prev = null, next = head;
    while (next != null && s.compareTo(next.segment) > 0) {
      prev = next;
      next = next.next;
    }
    SlowNode node = new SlowNode(s, prev, next);
    if (prev == null)
      head = node;
    else
      prev.next = node;
    if (next == null)
      tail = node;
    else
      next.prev = node;
    return node;
  }
}






  
