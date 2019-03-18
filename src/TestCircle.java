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
import circle.SmallestCircle;

public class TestCircle extends JFrame implements ActionListener {
  public static void main (String[] args) {
    new TestCircle();
  }

  TestCircle () {
    super("TestCircle");
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
  static protected JButton button2 = new JButton("two");
  static protected JButton button3 = new JButton("three");
  static protected JButton buttonI = new JButton("inside");
  static protected JButton buttonS = new JButton("smallest");
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
    button2.addActionListener(this);
    button3.addActionListener(this);
    buttonI.addActionListener(this);
    buttonS.addActionListener(this);
    buttonR.addActionListener(this);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(button);
    panel.add(button2);
    panel.add(button3);
    panel.add(buttonI);
    panel.add(buttonS);
    panel.add(buttonR);
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
    
    Circle circle = null;
    SmallestCircle smallest = new SmallestCircle();
    // Huller huller = new Fast();
    // Huller huller = new Medium();
    // Huller huller = new Slow();
    int iState = -1;
    String command = "two";    

    Random random = new Random(1);

    public void actionPerformed (ActionEvent e) {
      repaint();
      command = e.getActionCommand();
      System.out.println("Command: " + command);
      if (command.equals("step")) {
        iState++;
        if (circle == null || iState == smallest.numStates())
          iState = -1;
      }
      if (command.equals("randomize")) {
        for (int i = points.size() - 1; i > 0; i--) {
          int j = random.nextInt(i + 1);
          GO<PV2> temp = points.get(i);
          points.set(i, points.get(j));
          points.set(j, temp);
        }
        if (points.size() >= 2)
          circle = smallest.smallestCircle(points);
      }
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
      if (command.equals("two")) {
        while (points.size() > 2)
          points.remove(0);
        if (points.size() == 2)
          circle = new Circle(points.get(0), points.get(1));
        else
          circle = null;
      }
      else if (command.equals("three")) {
        while (points.size() > 3)
          points.remove(0);
        if (points.size() == 3)
          circle = new Circle(points.get(0), points.get(1), points.get(2));
        else
          circle = null;
      }
      else if (command.equals("inside")) {
        if (circle == null)
          System.out.println("circle is null");
        else if (circle.contains(points.get(points.size()-1)))
          System.out.println("inside");
        else
          System.out.println("outside");
      }
      else if (points.size() >= 2)
        circle = smallest.smallestCircle(points);
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
      
      int i = 0;
      for (GO<PV2> p : points)
        drawPoint(g2, p.xyz(), "" + i++, 4, Color.black);

      if (circle != null)
        circle.draw(g2, Color.green);

      if (0 <= iState && iState < smallest.numStates()) {
        System.out.println("iState " + iState);
        smallest.getState(iState).draw(g2);
      }
    }
  }
}
