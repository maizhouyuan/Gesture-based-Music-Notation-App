package sandbox;

import graphicslib.G;
import graphicslib.G.VS;
import graphicslib.I;
import graphicslib.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window implements ActionListener {
    // public static G.VS vs = new VS(100, 100, 200, 300);
    // public static Color color = G.rndColor();
    public static I.Area curArea;
    public static Square.List list = new Square.List();
    public static Square BACKGROUND = new Square(0, 0) {
        public void dn(int x, int y) {square = new Square(x, y);list.add(square);}
        public void drag(int x, int y) {square.resize(x, y);}
    };
    static {
        BACKGROUND.size.set(3000, 3000);
        BACKGROUND.c = Color.white;
        list.add(BACKGROUND);
    }
    public static Square square;
    public boolean dragging = false;
    public static G.V mouseDelta = new G.V(0, 0);// Overwritten in mousePressed
    public static Timer timer;
    public static G.V pressedLoc = new G.V(0, 0);
    public Squares() {
        super("squares", 1000, 700);
        timer = new Timer(30, this);
        timer.setInitialDelay(5000);
        // timer.start();
    }
    public void paintComponent(Graphics g) {
        G.fillBack(g);
        // vs.fill(g, color);
        list.draw(g);
    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        // if(vs.hit(me.getX(), me.getY())) {color = G.rndColor();}
        curArea = list.hit(x, y);
        curArea.dn(x, y);
        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        curArea.drag(x, y);
        repaint();
    }
    public void mouseReleased(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        curArea.up(x, y);
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new Squares();
        Window.launch();
    }

    @Override
    public void actionPerformed(ActionEvent e) {repaint();}

    //------------------------------------Square------------------------------------------------------------------
    public static class Square extends G.VS implements I.Area {
        public Color c = G.rndColor();
        public G.V dv = new G.V(0, 0);// new G.V(G.rnd(20)-10, G.rnd(20)-10);
        public Square(int x, int y) {super(x, y, 100, 100);}
        public void draw(Graphics g) {fill(g, c);moveAndBounce();}
        public void resize(int x, int y) {
            if (x > loc.x && y > loc.y) {
                size.set(x-loc.x, y-loc.y);
            }
        }
        public void move(int x, int y) {loc.set(x, y);}
        public void moveAndBounce() {
            loc.add(dv);
            if (loc.x < 0 && dv.x < 0) {dv.x = -dv.x;}
            if (loc.x > 1000 && dv.x > 0) {dv.x = -dv.x;}
            if (loc.y < 0 && dv.y < 0) {dv.y = -dv.y;}
            if (loc.y > 700 && dv.y > 0) {dv.y = -dv.y;}
        }

        @Override
        public void dn(int x, int y) {mouseDelta.set(loc.x-x, loc.y-y);}

        @Override
        public void up(int x, int y) {}

        @Override
        public void drag(int x, int y) {loc.set(mouseDelta.x+x, mouseDelta.y+y);}

        //-----------------------------------List--------------------------------------------------------------------
        public static class List extends ArrayList<Square> {
            public void draw(Graphics g) {for (Square s:this) {s.draw(g);}}
            public Square hit(int x, int y) {
                Square res = null;
                for (Square s: this) {
                    if (s.hit(x, y)) {
                        res = s;
                    }
                }
                return res;
            }
        }
    }
}
