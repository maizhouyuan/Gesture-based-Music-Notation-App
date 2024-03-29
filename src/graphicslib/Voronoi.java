package graphicslib;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class Voronoi extends Window {
    public static List list = new List();
    public Voronoi(){
        super("Voronoi", 1000, 700);
    }
    public void paintComponent(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(100, 100, 100, 100);
        list.drawC(g);
        list.drawDots(g);
    }
    public void mousePressed(MouseEvent me){
        list.add(new Dot(me.getX(), me.getY()));
        repaint();

    }
    public static void main(String[] args){
        PANEL = new Voronoi();
        Window.launch();
    }
    public static Random RND = new Random();
    public static int rnd(int n){
        return RND.nextInt(n);
    }
    public static Color rndColor(){
        return new Color(rnd(256), rnd(256), rnd(256));
    }
    //---------------------------------DOT---------------------------------//
    public static class Dot extends Point{
        public Color color = rndColor();
        public Dot(int x, int y){
            this.x = x;
            this.y = y;
        }
        public void dot (Graphics g){
            g.setColor(color);
            g.fillOval(x-5, y-5, 10, 10);
        }
    }
    //---------------------------------LIST---------------------------------//
    public static class List extends ArrayList<Dot> {

        public void drawC(Graphics g){
            for(int r = 200; r>0; r--){
                for(Dot d: this){
                    g.setColor(d.color);
                    g.fillOval(d.x - r, d.y - r, 2*r, 2*r);
                }
            }

        }
        public void drawDots(Graphics g){
            for(Dot d: this){
                d.dot(g);
            }
        }
    }
}
