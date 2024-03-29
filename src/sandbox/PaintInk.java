package sandbox;

import graphicslib.G;
import graphicslib.UC;
import graphicslib.Window;
import reaction.Ink;
import reaction.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public PaintInk(){
        super("PaintInk", UC.mainWindowWidth, UC.mainWindowHeight);
    }
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLUE);
        g.drawString("points:"+Ink.BUFFER.n, 20, 20);
        // g.drawLine(0, 0, 100, 100);
        inkList.show(g);
        g.setColor(Color.RED);
        pList.show(g);
        Ink.BUFFER.show(g);
        if (inkList.size() > 1){
            int last = inkList.size() - 1;
            int dist = inkList.get(last).norm.dist(inkList.get(last - 1).norm);
            // computes distance between the last two norms
            g.setColor(dist > UC.noMatchDist? Color.RED : Color.BLACK);
            g.drawString("disc: " + dist, 600, 60);
        }
    }

    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me){
        Ink.BUFFER.up(me.getX(), me.getY());
        Ink ink = new Ink();
        inkList.add(ink);
        Shape.Prototype proto;
        if(pList.bestDist(ink.norm) < UC.noMatchDist){
            proto = Shape.Prototype.List.bestMatch;    // capture the value
            proto.blend(ink.norm);
        }else{
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        ink.norm = proto;
        repaint();
    }

    public static void main(String[] args){
        PANEL = new PaintInk();
        Window.launch();
    }
}

