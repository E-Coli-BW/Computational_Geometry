package polygon;

interface SweepData {
  /** Get node associated with this data. */
  SweepNode getNode ();

  /** Set node associated with this data. */
  void setNode(SweepNode node);

  int compareTo (SweepData that);
}
