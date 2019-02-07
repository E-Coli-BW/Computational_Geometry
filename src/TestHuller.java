import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import acp.*;
import pv.*;
import hull.*;

public class TestHuller extends JFrame implements ActionListener {
  TestHuller (String name) { super(name); }

  static protected JLabel label;
  static protected JButton button = new JButton("step");
  
  DPanel d;
  
  public void init(){
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    d.setBackground(Color.white);
    getContentPane().add(d);
    
    label = new JLabel("Click points then press step repeatedly.");
    getContentPane().add("South", label);
    
    getContentPane().add("North", button);
    button.addActionListener(this);
  }
  
  public void actionPerformed (ActionEvent e) {
    d.actionPerformed(e);
  }

  void init2 () {
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    // JApplet applet = new TestHuller();
    // getContentPane().add("Center", applet);
    // applet.init();
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    //set background Color of the playground
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

  public static void main(String s[]) {
    TestHuller f = new TestHuller("TestHuller");
    f.init2();
  }
  
  class DPanel extends JPanel implements MouseListener, MouseMotionListener {
    ArrayList<GO<PV2>> points = new ArrayList<GO<PV2>>();
    
    public DPanel(){
      setBackground(Color.white);
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    //Here you can test your Slow, Medium, Medium2 and Fast methods!
    //Huller huller = new Slow();
    //Huller huller = new Medium();
    Huller huller = new Fast();
    //Huller huller = new Medium2();
    //Huller huller = new Fast2();
    int iState = -1;
    List<GO<PV2>> hull;

    public void actionPerformed (ActionEvent e) {
      repaint();
      iState = (iState+1) % (huller.numStates()+1);
    }
    
    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), -e.getY());
      System.out.println("mouse pressed " + e.getX() + " " + e.getY());
      System.out.println("mouse pressed " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      
      GO<PV2> a = new GO<PV2>(PV2.constant(0, 0));
      GO<PV2> b = new GO<PV2>(PV2.constant(3, 4));
      GO<PV2> c = new GO<PV2>(PV2.constant(6, 8));
      int s = AreaABC.sign(a, b, c);
      System.out.println("test area sign " + s);

      points.add(mouse);
      hull = huller.hull(points);
      if (hull != null) {
        System.out.println("points.size() " + points.size());
        System.out.println("hull.size() " + hull.size());
      }
      iState = -1;
      repaint();
    }
    
    public void mouseDragged (MouseEvent e) {
    }
    
    public void mouseMoved (MouseEvent e) {
    }
    
    // Handles the event of a user releasing the mouse button.
    public void mouseReleased(MouseEvent e){}
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
      g2.draw(new Line2D.Double(p.x.approx(), -p.y.approx(), q.x.approx(), -q.y.approx()));
    }
    
    private void drawPoint(Graphics2D g, PV2 p, String label, int size, Color color) {
      g.setColor(color);
      
      float offset = size / 2.0f;
      g.fillRect((int)(p.x.approx() - offset),(int)(-p.y.approx() - offset),size,size);
      g.drawString(label, (int)(p.x.approx() + offset), (int)(-p.y.approx() + offset));
    }
		
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke(1.0f));
      
      for (GO<PV2> p : points)
        drawPoint(g2, p.xyz(), "", 4, Color.orange);

      System.out.println("iState " + iState);
      if (0 <= iState && iState < huller.numStates())
        huller.getState(iState).draw(g2);
      else if (hull != null) {
        GO<PV2> prev = hull.get(hull.size()-1);
        for (int i = 0; i < hull.size(); i++) {
          GO<PV2> next = hull.get(i);
          paintEdge(g2, Color.green, prev.xyz(), next.xyz());
          prev = next;
        }
      }
    }
  }
}
