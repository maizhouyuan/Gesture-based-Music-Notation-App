package reaction;

import graphicslib.UC;
import graphicslib.G;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.DoubleBinaryOperator;

import static com.sun.tools.javac.main.Option.G;

public class Shape implements Serializable {
    public static Database DB = Database.load();    // database
    public static Collection<Shape> shapeList = DB.values();    // list backed by DB; changes to DB shown here
    public static Shape DOT = DB.get("DOT");
    public Prototype.List prototypes = new Prototype.List();
    public String name;
    public Shape(String name){this.name = name;}
    public static void saveShapeDB(){Database.save();}
/*    public static TreeMap<String, Shape> loadShapeDB(){
        TreeMap<String, Shape> res = new TreeMap<>();
        res.put("DOT", new Shape("DOT"));
        String filename = UC.ShapeDbFileName;
        // {} this is a block
        try{
            System.out.println("Attempting DB load...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            res = (TreeMap<String, Shape>) ois.readObject();
            System.out.println("Successful load");
            ois.close();
        }catch(Exception e){
            System.out.println("Load fail");
            System.out.println(e);
        }
        return res;
    }*/

/*    public static void saveShapeDB(){
        String filename = UC.ShapeDbFileName;
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(DB);
            System.out.println("Saved " + filename);
            oos.close();
        }catch(Exception e){
            System.out.println("Failed Database Save");
            System.out.println(e);
        }
    }*/
    public static Shape recognized(Ink ink){    // can return null
        if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for(Shape s: shapeList){
            int d = s.prototypes.bestDist(ink.norm);
            if(d < bestSoFar){
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    //--------------------------------Prototype--------------------------------------------
    public static class Prototype extends Ink.Norm implements Serializable {
        int nBlend = 1;    // to do the average calculation
        public void blend(Ink.Norm norm){
            blend(norm, nBlend);
            nBlend ++;
        }
        //--------------------------------List--------------------------------------------
        public static class List extends ArrayList<Prototype> implements Serializable {
//            public static Prototype bestMatch;    // a variable, side effect of bestDist
            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int bestSoFar = UC.noMatchDist;
                for(Prototype p: this){
                    int d = p.dist(norm);
                    if(d < bestSoFar){
                        bestMatch = p;
                        bestSoFar = d;
                    }
                }
                return bestSoFar;
            }
            public void train(Ink.Norm norm){
                if(bestDist(norm) < UC.noMatchDist){    // Found match, so blend
                    bestMatch.blend(norm);
                }else{    // No match, add new prototype
                    add(new Shape.Prototype());
                }
            }
            private int showNdx(int x){return x/(m + w);}    // Ndx -> index
            public boolean isShowDelete(G.VS vs){
                return vs.loc.y < m + w && showNdx(vs.loc.x) < size();
            }
            public void showDelete(G.VS vs){remove(showNdx(vs.loc.x));}

            public static Prototype bestMatch; // Side effect of bestDist


            private static int m = 10, w = 60;
            private static G.VS showBox = new G.VS(m, m, w, w);
            public void show(Graphics g){    // Show boxes across top of screen
                g.setColor(Color.ORANGE);
                for(int i = 0; i < size(); i++){
                    Prototype p = get(i);
                    int x = m + i * (m + w);
                    showBox.loc.set(x, m);
                    p.drawAt(g, showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }

            }
        }
    }
    //--------------------------------Database--------------------------------------------
    public static class Database extends HashMap<String, Shape>{
        private Database(){
            super();
            String dot = "DOT";
            put(dot, new Shape(dot));
        }
        public static Database load() {
            Database db = null;    //db is a local variable
            try{
                System.out.println("Attempting DB load...");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.ShapeDbFileName));
                db = (Database) ois.readObject();    // () -> casting
                System.out.println("Successful load");
                ois.close();
            }catch(Exception e){
                System.out.println("Load fail");
                System.out.println(e);
                db = new Database();
            }
            return db;
        }
        public static void save(){
            String filename = UC.ShapeDbFileName;
            try{
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
                oos.writeObject(DB);
                System.out.println("Saved " + filename);
                oos.close();
            }catch(Exception e){
                System.out.println("Failed Database Save");
                System.out.println(e);
            }
        }
        private Shape forceGet(String name){    // If the shape is null, force it to return a shape.
            if(! DB.containsKey(name)){DB.put(name, new Shape(name));}
            return DB.get(name);
        }
        public void addPrototype(String name){
            if (isLegal(name)){forceGet(name).prototypes.add(new Prototype());}    // Adds a prototype
        }
        public void train(String name, Ink ink){
            if(isLegal(name)){
                Shape rs = recognized(ink);    // recognize shape
                if (rs == null || !rs.name.equals(name)){
                    addPrototype(name);
                }else{
                    forceGet(name).prototypes.train(ink.norm);
                }
            }
        }
        public static boolean isLegal(String name){
            return ! name.equals("") && ! name.equals("DOT");    // We don't want to train a null or a dot.
        }
    }
}
