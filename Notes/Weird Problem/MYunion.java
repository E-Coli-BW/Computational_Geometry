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
      System.out.println("WHILING...");

      
      if (v.getPolygon() == out) {                    //  \ /
        SweepNode iNode = v.incoming.getNode();       //  / \
        SweepNode oNode = v.outgoing.getNode();
        // EXERCISE 3  X
        // v is intersection of a this edge with a that edge.
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        iNode.swapWithNext();
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        check(iNode.getPrevious(),iNode);
        check(oNode,oNode.getNext());
        copyEdge(v.incoming, v);
        copyEdge(v.outgoing, v);
        v.incoming.setMinY(v);
        v.outgoing.setMinY(v);
        v.incoming.inout = 3 - v.incoming.inout;
        v.outgoing.inout = 3 - v.outgoing.inout;

        states.add(new PState(v.p.xyz().y, null, null));

      }

          
      // EXERCISE 4  ^
      if (v.incoming.minY() == v.outgoing.minY()){
        // if we dont add them to sweep list, there will be NullPointer Exception
        SweepNode iNode = sweep.add(v.incoming);
        SweepNode oNode = sweep.add(v.outgoing);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        System.out.println("Meet for first time, lines ADDED");

        if(iNode.getNext()!=oNode){
          oNode=v.incoming.node;
          iNode=v.outgoing.node;
        }



          check(iNode.getPrevious(),iNode);
          check(oNode,oNode.getNext());  



        if (oNode.getNext() == null) {
          if (v.getPolygon() == that) {
              v.incoming.inout = this.getInOut();
              v.outgoing.inout = this.getInOut();
            }
          else {
              v.incoming.inout = that.getInOut();
              v.outgoing.inout = that.getInOut();
            }
          }
        else {
            Edge nextEdge = (Edge) oNode.getNext().getData();
            
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

        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

      }
      // EXERCISE 5  \/
      if (v.incoming.maxY() == v.outgoing.maxY() ){

        SweepNode iNode = v.incoming.getNode();    
        SweepNode oNode = v.outgoing.getNode();
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        if(iNode.getNext()!=oNode){

          check(oNode.getPrevious(), iNode.getNext());


        }
        else{

          check(iNode.getPrevious(), oNode.getNext());
     
        }

        copyEdge(v.incoming, null);
        copyEdge(v.outgoing, null);

        iNode.remove();
        oNode.remove();


        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
      // EXERCISE 6  i on o
      if (v.incoming.maxY() == v.outgoing.minY() ){
        // SweepNode iNode = sweep.add(v.incoming);
        SweepNode iNode = v.incoming.getNode();
        copyEdge(v.incoming, null); 
        
        iNode.setData(v.outgoing);
        v.outgoing.inout = v.incoming.inout;
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        check(iNode.getPrevious(), iNode);
        check(iNode, iNode.getNext());

        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
      // EXERCISE 7  o on i
      if (v.incoming.minY() == v.outgoing.maxY() ){
        SweepNode oNode = v.outgoing.getNode();
        copyEdge(v.outgoing, null);

        check(oNode.getPrevious(), oNode.getNext());
        oNode.setData(v.incoming);

        v.incoming.inout = v.outgoing.inout;
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        check(oNode.getPrevious(), oNode);
        check(oNode, oNode.getNext());

        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
    }


    for (Edge e : this.edges){
      e.checked.clear();
      e.inout = 0;
    }
    
    for (Edge e : that.edges){
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