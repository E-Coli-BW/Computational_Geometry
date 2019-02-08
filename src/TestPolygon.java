import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import acp.*;
import pv.*;
import polygon.Polygon;

public class TestPolygon extends JFrame implements ActionListener {
  public static void main (String[] args) {
    new TestPolygon();
  }

  TestPolygon () {
    super("TestPolygon");
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
  static protected JButton buttonC = new JButton("complement");
  static protected JButton buttonU = new JButton("union");
  static protected JButton buttonI = new JButton("intersection");
  static protected JButton buttonD = new JButton("difference");
  
  DPanel d;
  
  public void init(){
    getContentPane().setLayout(new BorderLayout());
    
    d = new DPanel();
    d.setBackground(Color.white);
    getContentPane().add(d);
    
    label = new JLabel("Click points then press step repeatedly.");
    getContentPane().add("South", label);
    
    button.addActionListener(this);
    buttonC.addActionListener(this);
    buttonU.addActionListener(this);
    buttonI.addActionListener(this);
    buttonD.addActionListener(this);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(button);
    panel.add(buttonC);
    panel.add(buttonU);
    panel.add(buttonI);
    panel.add(buttonD);
    getContentPane().add("North", panel);
  }
  
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
    
    int iState = -1;
    Polygon polygon;

    public void actionPerformed (ActionEvent e) {
      repaint();
      String command = e.getActionCommand();
      System.out.println("Command: " + command);
      if (command.equals("union")) {
        if (polygon == null)
          polygon = new Polygon(points);
        else
          polygon = polygon.union(new Polygon(points));
        points.clear();
      }
      else if (command.equals("step")) {
        iState++;
        if (polygon == null || iState == polygon.numStates())
          iState = -1;
      }
    }
    
    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){
      GO<PV2> mouse = new InputPoint(e.getX(), e.getY());
      System.out.println("mouse pressed " + e.getX() + " " + e.getY());
      System.out.println("mouse pressed " + mouse.xyz().x.approx() + " " + mouse.xyz().y.approx());
      
      points.add(mouse);
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
      
      if (points.size() > 0) {
        GO<PV2> prevP = points.get(points.size()-1);
        for (GO<PV2> p : points) {
          paintEdge(g2, Color.green, prevP.xyz(), p.xyz());
          prevP = p;
        }
      }

      if (polygon != null) {
        System.out.println("Drawing polygon.");
        polygon.draw(g2);
      }

      if (iState != -1)
        polygon.getState(iState).draw(g2);
    }
  }
}
