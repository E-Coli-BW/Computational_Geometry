import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;

import acp.*;
import pv.*;
import polygon.Polygon;

public class TestMinkowski extends JFrame implements ActionListener {
  public static void main (String[] args) {
    new TestMinkowski();
  }

  TestMinkowski () {
    super("TestMinkowski");
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    init();
    pack();
    setSize(new Dimension(550,250));
    setVisible(true);
  }

  static protected JLabel label;
  static protected JButton button, buttonU, buttonR, button180;
  
  DPanel d;
  
  public void init(){
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    d.setBackground(Color.white);
    getContentPane().add(d);
    
    label = new JLabel("Press and drag to make segments then click step repeatedly.");
    getContentPane().add("South", label);
    
    button = new JButton("step");
    button.addActionListener(this);
    buttonR = new JButton("robot");
    buttonR.addActionListener(this);
    button180 = new JButton("rot180");
    button180.addActionListener(this);
    buttonU = new JButton("obstacle");
    buttonU.addActionListener(this);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(button);
    panel.add(buttonR);
    panel.add(button180);
    panel.add(buttonU);
    getContentPane().add("North", panel);
  }
  
  public void actionPerformed (ActionEvent e) {
    if (e.getSource() == buttonU) {
      d.union();
    }
    else if (e.getSource() == buttonR) {
      d.makeRobot();
    }
    else if (e.getSource() == button180) {
      d.rot180();
    }
    else if (e.getSource() == buttonU) {
      d.union();
    }
    else
      d.actionPerformed(e);
  }

  class DPanel extends JPanel implements MouseListener, MouseMotionListener {
    ArrayList<GO<PV2>> points = new ArrayList<GO<PV2>>();
    
    public DPanel(){
      setBackground(Color.white);
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    Polygon subdiv;
    boolean stepping = false;
    int iFace, iLoop;
    //Polygon.Edge edge;
    GO<PV2> trans = new InputPoint(0,0);
    boolean rotate180;
    Polygon robot, minkowskiSum;

    public void rot180 () {
      rotate180 = !rotate180;
      repaint();
    }

    public void union () {
      System.out.println("union was called");
      if (subdiv == null)
        subdiv = new Polygon(points);
      else {
        Polygon s2 = new Polygon(points);
        subdiv = subdiv.union(s2);
      }
      points.clear();
      if (robot != null)
        minkowskiSum = robot.sum(subdiv);
      repaint();
    }

    public void makeRobot () {
      System.out.println("robot was called " + points.size());
      trans = points.get(0);
      for (int i = 0; i < points.size(); i++)
        points.set(i, new AminusB(trans, points.get(i)));
      robot = new Polygon(points);
      points.clear();
      if (subdiv != null)
        minkowskiSum = robot.sum(subdiv);
      repaint();
    }

    public void actionPerformed (ActionEvent e) {
      /*
      if (!stepping) {
        iFace = 0;
        iLoop = 0;
        edge = subdiv.faces.get(iFace).loops.get(iLoop);
        stepping = true;
        repaint();
        return;
      }

      edge = edge.next;
      if (edge == subdiv.faces.get(iFace).loops.get(iLoop)) {
        iLoop++;
        if (iLoop == subdiv.faces.get(iFace).loops.size()) {
          iFace++;
          if (iFace == subdiv.faces.size()) {
            stepping = false;
            repaint();
            return;
          }
          iLoop = 0;
        }
        edge = subdiv.faces.get(iFace).loops.get(iLoop);
      }
      */
      repaint();
    }
    
    GO<PV2> pressed;

    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse pressed " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      pressed = mouse;
    }
    
    public void mouseDragged (MouseEvent e) {
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      trans = new AplusB(trans, new AminusB(mouse, pressed));;
      pressed = mouse;
      repaint();
    }
    
    public void mouseMoved (MouseEvent e) {
    }
    
    // Handles the event of a user releasing the mouse button.
    public void mouseReleased(MouseEvent e) {
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse released " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      trans = new AplusB(trans, new AminusB(mouse, pressed));;
      repaint();
    }

    public void mouseClicked(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse clicked " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      points.add(mouse);
      repaint();
    }

    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    
    void drawEdge (Graphics2D g2, Color color, GO<PV2> p, GO<PV2> q) {
      if (false)
        System.out.println("drawEdge " + color + " " +
                           p.xyz().x.approx() + " " + p.xyz().y.approx() + " " +
                           q.xyz().x.approx() + " " + q.xyz().y.approx() + " ");
      g2.setPaint(color);
      g2.draw(new Line2D.Double(p.xyz().x.approx(), p.xyz().y.approx(), q.xyz().x.approx(), q.xyz().y.approx()));
    }
    
    private void drawPoint(Graphics2D g, GO<PV2> p, String label, int size, Color color) {
      g.setColor(color);
      
      float offset = size / 2.0f;
      g.fillRect((int)(p.xyz().x.approx() - offset),(int)(p.xyz().y.approx() - offset),size,size);
      g.drawString(label, (int)(p.xyz().x.approx() + offset), (int)(p.xyz().y.approx() + offset));
    }
		
    void drawCircle (Graphics2D g2, Color color, GO<PV2> c, double r) {
      g2.setColor(color);
      g2.drawArc((int) (c.xyz().x.approx() - r),
                 (int) (c.xyz().y.approx() - r),
                 (int) (2 * r), (int) (2 * r),
                 0, 360);
    }

    int count = 0;

    public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke(1.0f));

      int test = 0;
      if (test == 1) {
        if (points.size() == 0 && count == 0) {
          points.add(new InputPoint(566.0, 396.0));
          points.add(new InputPoint(531.0, 429.0));
          points.add(new InputPoint(575.0, 464.0));
          points.add(new InputPoint(555.0, 429.0));
          count++;
        }
        if (points.size() == 0 && count == 1) {
          points.add(new InputPoint(711.0, 372.0));
          points.add(new InputPoint(715.0, 193.0));
          points.add(new InputPoint(966.0, 191.0));
          points.add(new InputPoint(979.0, 563.0));
          points.add(new InputPoint(686.0, 578.0));
          points.add(new InputPoint(701.0, 453.0));
          points.add(new InputPoint(749.0, 452.0));
          points.add(new InputPoint(751.0, 535.0));
          points.add(new InputPoint(928.0, 521.0));
          points.add(new InputPoint(923.0, 237.0));
          points.add(new InputPoint(755.0, 229.0));
          points.add(new InputPoint(752.0, 362.0));
          count++;
        }
        if (points.size() == 0 && count == 2) {
          points.add(new InputPoint(772.0, 253.0));
          points.add(new InputPoint(767.0, 513.0));
          points.add(new InputPoint(911.0, 509.0));
          points.add(new InputPoint(910.0, 254.0));
          count++;
        }
      }

      /*
      for (GO<PV2> p : points)
        drawPoint(g2, p, "", 4, Color.black);

      if (points.size() > 1) {
        GO<PV2> pPrev = points.get(points.size() - 1);
        for (GO<PV2> p : points) {
          drawEdge(g2, Color.red, pPrev, p);
          pPrev = p;
        }
      }
      */

      if (points.size() > 0) {
        GO<PV2> prevP = points.get(points.size()-1);
        for (GO<PV2> p : points) {
          drawEdge(g2, Color.green, prevP, p);
          prevP = p;
        }
      }

      if (subdiv != null) {
        // System.out.println("Drawing subdivision");
        subdiv.draw(g2);
      }

      if (robot != null) {
        if (rotate180)
          robot.draw(g2, Color.blue, trans.xyz(), 1);
        else
          robot.draw(g2, Color.blue, trans.xyz(), -1);

        if (minkowskiSum != null) {
          minkowskiSum.draw(g2, Color.red);
        }
      }

        /*
          for (Polygon.Edge edge : minkowskiSum.edges) {
            GO<PV2> a = edge.vert.p;
            GO<PV2> b = edge.twin.vert.p;
            drawEdge(g2, Color.blue, a, b);
          }
        */

      /*
      if (stepping) {
        drawEdge(g2, Color.blue,
                 edge.vert.p,
                 edge.twin.vert.p);
      }
      */
    }
  }
}
