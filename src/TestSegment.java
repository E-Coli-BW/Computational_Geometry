import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import acp.*;
import pv.*;
import segment.*;

public class TestSegment extends JFrame implements ActionListener {
  public static void main(String s[]) {
    new TestSegment();
  }

  TestSegment () { 
    super("TestSegment");

    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    d.setBackground(Color.white);
    getContentPane().add(d);
    
    label = new JLabel("Click points then press step repeatedly.");
    getContentPane().add("South", label);
    
    getContentPane().add("North", button);
    button.addActionListener(this);
    pack();
    setSize(new Dimension(550,250));
    setVisible(true);
  }

  DPanel d;
  static protected JLabel label;
  static protected JButton button = new JButton("step");
  
  public void actionPerformed (ActionEvent e) {
    d.actionPerformed(e);
  }

  class DPanel extends JPanel implements MouseListener, MouseMotionListener {
    ArrayList<GO<PV2>> points = new ArrayList<GO<PV2>>();
    
    public DPanel(){
      setBackground(Color.white);
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    public void actionPerformed (ActionEvent e) {
      if (++iState == intersecter.numStates())
        iState = -1;
      repaint();
    }
    
    List<Segment> segments = new ArrayList<Segment>();
    GO<PV2> pressed;

    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse pressed " + e.getX() + " " + e.getY());
      System.out.println("mouse pressed " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      
      pressed = mouse;
    }
    
    public void mouseDragged (MouseEvent e) {
    }
    
    public void mouseMoved (MouseEvent e) {
    }
    
    int iState = -1;
    Intersecter intersecter = new Slow();
    // Intersecter intersecter = new Fast();
    List<GO<PV2>> intersections;

    // Handles the event of a user releasing the mouse button.
    public void mouseReleased(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse released " + e.getX() + " " + e.getY());
      System.out.println("mouse released " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      
      segments.add(new Segment(pressed, mouse));
      intersections = intersecter.intersect(segments);
      iState = -1;
      repaint();
    }

    public void mouseClicked(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    
    void paintEdge (Graphics2D g2, Color color, int i, int j) {
      g2.setPaint(color);
      PV2 p = points.get(i).xyz();
      PV2 q = points.get(j).xyz();
      g2.draw(new Line2D.Double(p.x.approx(), p.y.approx(), q.x.approx(), q.y.approx()));
    }
    
    void paintEdge (Graphics2D g2, Color color, PV2 p, PV2 q) {
      g2.setPaint(color);
      g2.draw(new Line2D.Double(p.x.approx(), p.y.approx(), q.x.approx(), q.y.approx()));
    }
    
    private void drawPoint(Graphics2D g, PV2 p, String label, int size, Color color) {
      g.setColor(color);
      
      float offset = size / 2.0f;
      g.fillRect((int)(p.x.approx() - offset),(int)(p.y.approx() - offset),size,size);
      g.drawString(label, (int)(p.x.approx() + offset), (int)(p.y.approx() + offset));
    }
		
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke(1.0f));
      
      for (Segment s : segments)
        paintEdge(g2, Color.blue, s.tail.xyz(), s.head.xyz());

      if (iState >= 0)
        intersecter.getState(iState).draw(g2);
      else if (intersections != null)
        for (GO<PV2> p : intersections)
          drawPoint(g2, p.xyz(), "", 4, Color.green);
    }
  }
}
