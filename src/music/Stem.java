package music;

import graphicslib.UC;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem>{
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam  = null;    // Default for stem is no Beam
    public Stem(Staff staff, Head.List heads, boolean up) {
        this.staff = staff;
        // staff.sys.stems.addStem(this);    // this is done in the Time class
        isUp = up;
        for (Head h: heads){h.unStem(); h.stem = this;}
        this.heads = heads;
        staff.sys.stems.addStem(this);
        setWrongSides();

        addReaction(new Reaction("E-E") {    // Increment flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;    // first head on the stem
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2)/2) + 100;    // (y1 + y2)/2: midpoint where the stem was
                // +100: Allow sys."E-E" to win
            }

            @Override
            public void act(Gesture g) {Stem.this.incFlag();}
        });

        addReaction(new Reaction("W-W") {    // Decrement flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;    // first head on the stem
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2)/2);    // (y1 + y2)/2: midpoint where the stem was
            }

            @Override
            public void act(Gesture g) {Stem.this.decFlag();}
        });
    }
    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up){    // factory method
        Head.List heads = new Head.List();
        for (Head h: time.heads){
            int yH = h.y();
            if (yH > y1 && yH < y2){heads.add(h);}
        }
        if (heads.size() == 0){return null;}    // No stem created if there is no heads
        Beam b = internalStem(staff.sys, time.x, y1, y2);
        Stem res = new Stem(staff, heads, up);
        if (b != null){b.addStem(res); res.nFlag = 1;}
        return res;
    }

    private static Beam internalStem(Sys sys, int x, int y1, int y2) {    // returns non-null IF we find a beam crossed by line
        // helper function for finding the stem user crosses
        for (Stem s : sys.stems){
            if (s.beam !=null && s.x()<x && s.yLo()<y2 && s.yHi()>y1){
                int bx = s.beam.first().x(), by =s.beam.first().yBeamEnd();
                int ex = s.beam.last().x(), ey = s.beam.last().yBeamEnd();
                if (Beam.verticalLineCrossesSegment(x, y1, y2, bx, by, ex, ey)){return s.beam;}
            }
        }
        return null;
    }

    public void show(Graphics g){
        if (nFlag >= -1 && heads.size() > 0) {
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();    // Flag == Beam
            g.drawLine(x, yH, x, yB);
            if (nFlag > 0 && beam == null) {
                if (nFlag == 1){(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x, yB);}
                if (nFlag == 2){(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x, yB);}
                if (nFlag == 3){(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x, yB);}
                if (nFlag == 4){(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x, yB);}
            }
        }
    }
    public Head firstHead(){return heads.get(isUp? heads.size()-1 : 0);}    // Either the first or the last
    public Head lastHead(){return heads.get(isUp? 0 : heads.size()-1);}
    public int yFirstHead(){Head h = firstHead(); return h.staff.yLine(h.line);}
    public boolean isInternalStem(){
        return beam != null && beam.stems != null && this != beam.first() && this != beam.last();
    }
    public int yBeamEnd(){
        if (isInternalStem()){beam.setMasterBeam(); return Beam.yOfX(x());}
        Head h = lastHead();
        int line = h.line;
        line += isUp? - 7 : 7;    // Up stem or down stem
        //increase more if more flags has been added
        //if flag is more than 2, give room for the extra flag(s)
        int flagInc = nFlag > 2? 2*(nFlag-2) : 0;
        line += isUp? - flagInc : flagInc;
        //Head towards center
        if ((isUp && line > 4) || (!isUp && line < 4)) {line = 4;}
        return h.staff.yLine(line);
    }
    public int x(){Head h = firstHead(); return h.time.x + (isUp? h.W() : 0);}
    public int yLo() {return isUp ? yBeamEnd() : yFirstHead();}
    public int yHi() {return isUp ? yFirstHead() : yBeamEnd();}

    public void deleteStem() {
        staff.sys.stems.remove(this);
        deleteMass();}

    public void setWrongSides() {    // Stub - fixed
        Collections.sort(heads);
        int i, last, next;
        if (isUp){
            i = heads.size() - 1; last = 0; next = -1;
        }else{
            i = 0;
            last = heads.size() - 1;
            next = 1;
        }
        Head pH = heads.get(i);     // previous head
        pH.wrongSide = false;
        while (i != last){
            i += next;
            Head nH = heads.get(i);   // next head
            nH.wrongSide = pH.staff == nH.staff && (Math.abs(nH.line - pH.line) <= 1 && ! pH.wrongSide);
            pH = nH;
        }
    }

    @Override
    public int compareTo(Stem s) {return x() - s.x();}

    //--------------------------------List--------------------------------------------
    public static class List extends ArrayList<Stem> {
        public int yMin = 1_000_000, yMax = -1_000_000;
        public void addStem(Stem s){
            add(s);
            // Every time you add a stem, yMin and yMax are tracking and keeping the values
            if (s.yLo() < yMin){yMin = s.yLo();}
            if (s.yHi() < yMin){yMax = s.yHi();}
        }
        public boolean fastReject(int y1, int y2){return false;}//y2 < yMin || y1 > yMax;}
        public void sort(){Collections.sort(this);}
        public Stem.List allIntersecters(int x1, int y1, int x2, int y2){
            Stem.List res = new Stem.List();
            System.out.println("AllIntersectors list.size="+this.size());
            for (Stem s : this){
                int x = s.x(), y = Beam.yOfX(x, x1, y1, x2, y2);
                System.out.println("x:"+x+" x1:"+x1 +" x2:"+x2);
                System.out.println("y:"+y+" y1:"+y1 +" y2:"+y2);
                System.out.println("y:"+y+" yL:"+s.yLo() +" yH:"+s.yHi());
                if (x > x1 && x < x2 && y > s.yLo() && y < s.yHi()) {
                    res.add(s);
                }
            }
            System.out.println("found:" + res.size());
            return res;
        }
    }
}
