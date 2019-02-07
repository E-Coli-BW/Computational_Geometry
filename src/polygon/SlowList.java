package polygon;

class SlowList implements SweepList {
  class SlowNode implements SweepNode {
    private SweepData data;
    private SlowNode prev, next;

    SlowNode (SweepData s, SlowNode prev, SlowNode next) {
      data = s;
      this.prev = prev;
      this.next = next;
      s.setNode(this);
    }

    /** Get data. */
    public SweepData getData () { return data; }

    /** Set data. */
    public void setData (SweepData data) { 
      if (this.data != null)
        this.data.setNode(null);
      this.data = data; 
      data.setNode(this);
    }

    /** Get the next node in the list. */
    public SweepNode getNext () { return next; }

    /** Get the previous node in the list. */
    public SweepNode getPrevious () { return prev; }
  
    /** Swap the data points of this and the next node in the list.
        Also swap the node pointers in these datas. */
    public void swapWithNext () {
      SweepData nexts = next.data;
      next.data = data;
      data = nexts;
      data.setNode(this);
      next.data.setNode(next);
    }

    /** Swap the data points of this and the previous node in the list.
        Also swap the node pointers in these datas. */
    public void swapWithPrevious () {
      SweepData prevs = prev.data;
      prev.data = data;
      data = prevs;
      data.setNode(this);
      prev.data.setNode(prev);
    }

    /** Remove this node from the list.  Set the data's node pointer
        to null.  Return the data. */
    public SweepData remove () {
      if (prev == null)
        head = next;
      else
        prev.next = next;
      if (next == null)
        tail = prev;
      else
        next.prev = prev;
      data.setNode(null);
      return data;
    }
  }

  private SlowNode head, tail;

  /** Return first Node in list. */
  public SweepNode getFirst () { return head; }

  /** Return last Node in list. */
  public SweepNode getLast () { return tail; }

  /** Insert data into list.
      Return Node record that points to s.
      Set s.node to point to that Node. */
  public SweepNode add (SweepData s) {
    SlowNode prev = null, next = head;
    while (next != null && s.compareTo(next.data) > 0) {
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






  
