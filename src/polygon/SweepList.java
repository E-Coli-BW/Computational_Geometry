package polygon;

/** List of segments ordered by x. */
interface SweepList {
  /** Insert segment into list.
      Return Node record that points to s.
      Set s.node to point to that Node. */
  SweepNode add (SweepData s);

  /** Return first Node in list. */
  SweepNode getFirst ();

  /** Return last Node in list. */
  SweepNode getLast ();
}
