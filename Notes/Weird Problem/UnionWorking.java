  public Polygon union (Polygon that) {
    states.clear();

    for (Edge e : this.edges) {
      e.inout = 0;
      e.checked.clear();
    }
    
    for (Edge e : that.edges) {
      e.inout = 0;
      e.checked.clear();
    }

    this.that = that;
    out = new Polygon();

    for (Vert v : this.verts)
      events.offer(v);
    for (Vert v : that.verts)
      events.offer(v);

    while (events.size() > 0) {
      Vert v = events.poll();
      
      states.add(new PState(v.p.xyz().y, null, null));
      
      if (v.getPolygon() == out) {                 //  \ /
        SweepNode iNode = v.incoming.getNode();    //  / \
        SweepNode oNode = v.outgoing.getNode();
        System.out.println("X event");
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
           
        iNode.swapWithNext();
        
        check(iNode.getPrevious(), iNode);
        check(oNode, oNode.getNext());
        
        copyEdge(v.incoming, v);
        copyEdge(v.outgoing, v);
        v.incoming.setMinY(v);
        v.outgoing.setMinY(v);
        v.incoming.inout = 3 - v.incoming.inout;
        v.outgoing.inout = 3 - v.outgoing.inout;

        states.add(new PState(v.p.xyz().y, null, null));
      }
      else if (v == v.incoming.minY())  { //   /\
        if (v == v.outgoing.minY()) {     //  /  \
          System.out.println("^ event");
 
          SweepNode left = sweep.add(v.incoming);
          SweepNode right = sweep.add(v.outgoing);
          
          if (left.getNext() != right) {
            right = v.incoming.node;
            left = v.outgoing.node;
          }
          
          check(left.getPrevious(), left);
          check(right, right.getNext());
          
          if (right.getNext() == null) {
            System.out.println("RIGHT IS NULL");
            if (v.getPolygon() == that) {
              System.out.println("this.inout " + this.inout);
              v.incoming.inout = this.getInOut();
              v.outgoing.inout = this.getInOut();
            }
            else {
              System.out.println("that.inout " + that.inout);
              v.incoming.inout = that.getInOut();
              v.outgoing.inout = that.getInOut();
            }
          }
          else {
            Edge nextEdge = (Edge) right.getNext().getData();
            
            if (nextEdge.getPolygon() == v.getPolygon()) {
              v.incoming.inout = nextEdge.inout;
              v.outgoing.inout = nextEdge.inout;
            }
            else {
              if (nextEdge.minY() == nextEdge.tail) {
                v.incoming.inout = 1;
                v.outgoing.inout = 1;
              }
              else {
                v.incoming.inout = 2;
                v.outgoing.inout = 2;
              }
            }
          }
          System.out.println("incoming inout " + v.incoming.inout);
          System.out.println("outgoing inout " + v.outgoing.inout);
          states.add(new PState(v.p.xyz().y, null, null));
        }
        if (v == v.outgoing.maxY()) {         //   o /  or  \ o
          // EXERCISE 5                       //   i \      / i
          System.out.println("< event");
          SweepNode oNode = v.outgoing.node;  
          
          copyEdge(v.outgoing, null);
          
          oNode.setData(v.incoming);
          v.incoming.inout = v.outgoing.inout;

          check(oNode.getPrevious(), oNode);
          check(oNode, oNode.getNext());
          
          states.add(new PState(v.p.xyz().y, null, null));
        }
      }
      else if (v == v.incoming.maxY()) {
        if (v == v.outgoing.minY()) {         //   i /  or  \ i
          // EXERCISE 6                       //   o \      / o
          System.out.println("> event");
          SweepNode iNode = v.incoming.node;
          
          // Part 9: before deleting v.incoming by setting its data, copy it
          copyEdge(v.incoming, null);

          iNode.setData(v.outgoing);
          

          v.outgoing.inout = v.incoming.inout;
          
          check(iNode.getPrevious(), iNode);
          check(iNode, iNode.getNext());
          
          states.add(new PState(v.p.xyz().y, null, null));
        }
        if (v == v.outgoing.maxY()) {  //  \  /

          System.out.println("V event");
          SweepNode iNode = v.incoming.node;
          SweepNode oNode = v.outgoing.node;

          if (iNode.getNext() != oNode) {
            check(oNode.getPrevious(), iNode.getNext());
          }
          else {
            check(iNode.getPrevious(), oNode.getNext());
          }
          
          copyEdge(v.incoming, null);
          copyEdge(v.outgoing, null);
          
          iNode.remove(); 
          oNode.remove(); 

          states.add(new PState(v.p.xyz().y, null, null));
        }
      }
    }


    for (Edge e : this.edges) {
      e.checked.clear();
      e.inout = 0;
    }
    
    for (Edge e : that.edges) {
      e.checked.clear();
      e.inout = 0;
    }
    
    for (Vert v: this.verts) {
      v.informEdges();
    }
    
    for (Vert v: that.verts) {
      v.informEdges();
    }

    for (Edge e: out.edges) {
      e.informVerts();
    }
    
    return out;
  }