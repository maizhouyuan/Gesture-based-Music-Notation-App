package sandbox;

import graphicslib.G;
import graphicslib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends Window {
    public static int clicks = 0;
    public static Path path = new Path();
    public static Pic pic = new Pic();

    public Paint(){super("Paint", 1000, 700);}



    @Override
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(G.rndColor());
        g.fillOval(100, 100, 200, 100);
        g.drawLine(300, 200, 500, 100);
        g.setColor(Color.BLACK);
        String msg = "Hello World"; int x = 150; int y = 400;
        g.drawString(msg + clicks, x, y);
        g.setColor(Color.RED);
        pic.draw(g);
        g.fillOval(x-1, y-1, 2, 2);
        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent();
        int d = fm.getDescent();
        int w = fm.stringWidth(msg);
        g.drawRect(x, y - a, w, a + d);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        clicks++;
        // path.clear();
        path = new Path();
        pic.add(path);
        path.add(me.getPoint());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        path.add(me.getPoint());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new Paint();
        Window.launch();
    }

    //--------------------------------Path--------------------------------------------

    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g) {
            for(int i = 1; i<size(); i ++) {
                Point p = get(i - 1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }
    }
    public static class Pic extends ArrayList<Path> {
        public void draw(Graphics g) {for(Path p: this) {p.draw(g);}}
    }
}
