import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import acp.*;
import pv.*;
import circle.Circle;
import delaunay.Delaunay;

public class TestDelaunay extends JFrame implements ActionListener {
  public static void main (String[] args) {
    new TestDelaunay();
  }

  TestDelaunay () {
    super("TestDelaunay");
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    init();
    pack();
    setSize(new Dimension(550,250));
    setVisible(true);
  }

  static protected JLabel label;
  static protected JButton button = new JButton("step");
  static protected JButton buttonV = new JButton("voronoi");
  static protected JButton buttonR = new JButton("randomize");
  
  DPanel d;
  
  public void init(){
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    d.setBackground(Color.white);
    getContentPane().add(d);
    
    label = new JLabel("Click points then press step repeatedly.");
    getContentPane().add("South", label);
    
    button.addActionListener(this);
    buttonV.addActionListener(this);
    buttonR.addActionListener(this);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(button);
    panel.add(buttonR);
    panel.add(buttonV);
    getContentPane().add("North", panel);
  }
  
  public void actionPerformed (ActionEvent e) {
    d.actionPerformed(e);
  }

  /*
  public static void mainX(String s[]) {
    JFrame f = new JFrame("TestDelaunay");
    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    JApplet applet = new TestDelaunay();
    f.getContentPane().add("Center", applet);
    applet.init();
    f.pack();
    f.setSize(new Dimension(550,250));
    f.setVisible(true);
  }
  */

  class DPanel extends JPanel implements MouseListener, MouseMotionListener {
    ArrayList<GO<PV2>> points = new ArrayList<GO<PV2>>();
    
    public DPanel(){
      setBackground(Color.white);
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    Delaunay delaunay = new Delaunay();
    // Huller huller = new Fast();
    // Huller huller = new Medium();
    // Huller huller = new Slow();
    int iState = -1;
    boolean showVoronoi = false;

    Random random = new Random(1);

    public void actionPerformed (ActionEvent e) {
      repaint();
      String command = e.getActionCommand();
      System.out.println("Command: " + command);
      if (command.equals("step")) {
        iState++;
        if (iState == delaunay.numStates())
          iState = -1;
      }
      if (command.equals("randomize")) {
        for (int i = points.size() - 1; i > 0; i--) {
          int j = random.nextInt(i);
          GO<PV2> temp = points.get(i);
          points.set(i, points.get(j));
          points.set(j, temp);
        }
        if (points.size() >= 1)
          delaunay.triangulate(points);
      }
      if (command.equals("voronoi"))
        showVoronoi = !showVoronoi;
    }
    
    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      // System.out.println("mouse pressed " + e.getX() + " " + e.getY());
      // System.out.println("mouse pressed " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      
      points.add(mouse);
      /*
      if (points.size() == 2)
        circle = new Circle(points.get(0), points.get(1));
      else if (points.size() >= 3)
        circle = new Circle(points.get(points.size()-3),
                            points.get(points.size()-2),
                            points.get(points.size()-1));
      */
      delaunay.triangulate(points);
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
      
      int ip = 0;
      for (GO<PV2> p : points)
        drawPoint(g2, p.xyz(), "" + ip++, 4, Color.black);

      if (0 <= iState && iState < delaunay.numStates()) {
        System.out.println("iState " + iState);
        delaunay.getState(iState).draw(g2);
      }
      else
        delaunay.draw(g2);

      if (showVoronoi && delaunay.triangles.size() > 0) {
        Delaunay.Triangle root = delaunay.triangles.get(0);
        
        for (Delaunay.Triangle t : delaunay.triangles) {
          if (t.c != null)
            continue;
          boolean bounding = false;
          for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
              if (t.v[i] == root.v[j])
                bounding = true;
          if (bounding)
            continue;
          Circle c1 = new Circle(t.v[0],
                                 t.v[1],
                                 t.v[2]);
          for (int i = 0; i < 3; i++) {
            if (t.t[i] == null)
              continue;
            Delaunay.Triangle t2 = t.t[i];
            Circle c2 = new Circle(t2.v[0],
                                   t2.v[1],
                                   t2.v[2]);
            paintEdge(g2, Color.green, c1.center(), c2.center());
          }
        }
      }
    }
  }
}
