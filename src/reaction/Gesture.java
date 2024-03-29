package reaction;

import graphicslib.G;
import graphicslib.I;

import java.util.ArrayList;

public class Gesture {
    private static List UNDO = new List();
    public Shape shape;
    public G.VS vs;
    private Gesture(Shape shape, G.VS vs){
        this.shape = shape;
        this.vs = vs;
    }
    public static Gesture getNew(Ink ink){    // Can return null, New can never fail but getNew can fail
        Shape s = Shape.recognized(ink);
        return (s == null) ? null : new Gesture(s, ink.vs);
    }
    public void redoGesture(){    // Don't add to UNDO (it's already on it)
        Reaction r = Reaction.best(this);
        if(r != null){r.act(this);}
    }
    public void doGesture(){
        Reaction r = Reaction.best(this);
        if(r != null){UNDO.add(this); r.act(this);}
    }
    public static void undo(){
        if(UNDO.size() > 0){
            UNDO.remove(UNDO.size() - 1);
            Layer.nuke();    // Eliminates all the messes
            Reaction.nuke();    // Clear by shape map, then reload initial reactions
            UNDO.redo();
        }
    }
    // Splitting screen up, make areas(drag area, note-taking area etc.) do their own thing
    public static I.Area AREA = new I.Area(){    // much like BUFFER, one single gesture area
        // I is an interface, which has no enough pieces
        // build an anonymous class -> java thing, no name no file, just give me a function
        public boolean hit(int x, int y){return true;};
        public void dn(int x, int y){Ink.BUFFER.dn(x, y);}
        public void drag(int x, int y){Ink.BUFFER.drag(x, y);}
        public void up(int x, int y){
            Ink.BUFFER.add(x, y);
            Ink ink = new Ink();    // local variable
            Gesture gest = Gesture.getNew(ink);    // Can fail if unrecognized
            Ink.BUFFER.clear();
            if (gest != null){
                System.out.println(gest.shape.name);
                if (gest.shape.name.equals("N-N")) {
                    undo();
                }else{
                    gest.doGesture();
                }
            }
//                Reaction r = Reaction.best(gest);    // Can fail if no reaction wanted
//                if (r != null){
//                    r.act(gest);
//                }
        }
    };
    //--------------------------------List--------------------------------------------
    public static class List extends ArrayList<Gesture>{

        public void redo() {
            for(Gesture g: this){g.redoGesture();}
        }
    }
}
