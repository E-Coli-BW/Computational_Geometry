package segment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

import pv.PV2;

public class Drawer {
  public static void drawPoint(Graphics2D g, PV2 p, Color color, String label) {
    g.setColor(color);
    int size = 8;
    float offset = size / 2.0f;
    g.fillRect((int)(p.x.approx() - offset),(int)(p.y.approx() - offset),size,size);
    g.drawString(label, (int)(p.x.approx() + offset), (int)(p.y.approx() + offset));
  }
  
  public static void drawEdge (Graphics2D g, PV2 p, PV2 q, Color color, String label) {
    g.setPaint(color);
    g.draw(new Line2D.Double(p.x.approx(), p.y.approx(), q.x.approx(), q.y.approx()));
    PV2 m = p.plus(q).over(2);
    g.drawString(label, (int) m.x.approx(), (int) m.y.approx());
  }
}
