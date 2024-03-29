package sandbox;

import graphicslib.G;
import graphicslib.UC;
import graphicslib.Window;
import reaction.Shape;
import reaction.Ink;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends Window {
    // Define several string variables, fixed strings
    public static final String UNKNOWN = "<- this name is currently unknown";
    public static final String KNOWN = "<- this is a known shape";
    public static final String ILLEGAL = "<- this name is NOT a legal shape name";
    public static String curName = "";     // the name that user types in
    public static String curState = ILLEGAL;    // current name doesn't have anything in it

    public static Shape.Prototype.List pList = new Shape.Prototype.List();

    public ShapeTrainer(){
        super("shapeTrainer", 1000, 700);
    }

    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.black);
        g.drawString(curName, 600, 30);
        g.drawString(curState, 700, 30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if(pList != null){pList.show(g);}
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
        Ink ink = new Ink();
        if (pList != null && pList.isShowDelete(ink.vs)){
            pList.showDelete(ink.vs);
            repaint();
            return;
        }
        Shape.DB.train(curName, ink);
        setState();
        repaint();
    }

    /*public void mouseReleased(MouseEvent me){
        Ink.BUFFER.up(me.getX(), me.getY());
        Ink ink = new Ink();
        Shape.Prototype proto;
        if(pList == null){
            Shape s = new Shape(curName);
            Shape.DB.put(curName, s);
            pList = s.prototypes;
        }
        if(pList.bestDist(ink.norm) < UC.noMatchDist){
            proto = Shape.Prototype.List.bestMatch;
            proto.blend(ink.norm);
        }else{
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        setState();    // Possibly unknown converted to known
        repaint();
    }*/
    public void keyTyped(KeyEvent ke){
        char c = ke.getKeyChar();
        System.out.println("typed: " + c);
        curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName + c;
        // 0x0D: return character, 0x0A: new line character
        if(c == 0x0D || c == 0x0A){Shape.saveShapeDB();}
        setState();
        repaint();
    }

    public void setState(){
        curState = (curName.equals("") || curName.equals("DOT")) ? ILLEGAL : UNKNOWN;
        if(curState == UNKNOWN) {
            if(Shape.DB.containsKey(curName)){    // look-up
                curState = KNOWN;
                pList = Shape.DB.get(curName).prototypes;
            }else{
                pList = null;
            }
        }
    }
    public static void main(String[] args){
        PANEL = new ShapeTrainer();
        Window.launch();
    }
}
