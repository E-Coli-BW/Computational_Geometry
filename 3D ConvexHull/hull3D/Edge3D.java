package hull3D;

import acp.*;
import pv.*;

public class Edge3D {
  public Triangle3D t;
  public int i;

  public Edge3D() {
    this.t = null;
    this.i = -1;
  }

  public Edge3D(Triangle3D t, int i) {
    this.t = t;
    this.i = i;
  }

  public Vertex3D tail() { return t.v[(i+1)%3]; }
  public Vertex3D head() { return t.v[(i+2)%3]; }

  public Triangle3D next() { return t.t[i]; }

  public Edge3D twin() { return new Edge3D(next(), next().find(t)); }
  public Edge3D fnext() { return new Edge3D(t, (i+1)%3); }
  public Edge3D vprev() { return twin().fnext(); }

  public boolean equals(Object o) {
    if(!(o instanceof Edge3D)) return false;
    return (((Edge3D)o).t == this.t) && (((Edge3D)o).i == this.i);
  } 

}
