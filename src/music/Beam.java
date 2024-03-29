package music;

import reaction.Mass;

import java.awt.*;

public class Beam extends Mass {
    public static Polygon poly;
    static{int[] foo = {0, 0, 0, 0}; poly = new Polygon(foo, foo, 4);}
    public Stem.List stems = new Stem.List();
    public Beam(Stem f, Stem l){
        super("NOTE");
        f.nFlag = 1;
        l.nFlag = 1;
        addStem(f);
        addStem(l);
    }

    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bx, int by, int ex, int ey) {
        if (x < bx || x > ex){return false;}
        int y = yOfX(x, bx, by, ex, ey);
        if (y1 < y2){return (y1 < y && y < y2);}else{return (y2 < y && y < y1);}
    }

    public Stem first(){return stems.get(0);}
    public Stem last(){return stems.get(stems.size() - 1);}
    public void deleteBeam(){
        for (Stem s : stems){s.beam = null;}    // Remove stem associations with beam
        deleteMass();    // Remove beam from layers
    }
    public void addStem(Stem s){
        if (s.beam == null){
            stems.add(s); s.beam = this;
            stems.sort();    // Stems should be sorted by increasing x value
        }
    }

    @Override
    public void show(Graphics g) {
        // STUB fixed!
        g.setColor(Color.black);
        drawBeamGroup(g);
    }
    private void drawBeamGroup(Graphics g){
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.H(); int sH = firstStem.isUp? H:-H;    // signed h
        int nPrev = 0, nCurr = firstStem.nFlag, nNext = stems.get(1).nFlag;
        int pX;    // X coordinate of previous stem
        int cX = firstStem.x();
        int bX = cX + 3*H;    // First beamlet leans forward cX to bX
        if (nCurr > nNext) {drawBeamStack(g, nNext, nCurr, cX, bX, sH);}    // beamlets on first stem point right
        for (int curr = 1; curr < stems.size(); curr++){
            Stem sCurr = stems.get(curr);    // Current stem
            pX = cX;
            cX = sCurr.x();
            nPrev = nCurr; nCurr = nNext; nNext = (curr < stems.size() - 1) ? stems.get(curr + 1).nFlag : 0;
            int nBack = Math.min(nPrev, nCurr);
            drawBeamStack(g, 0, nBack, pX, cX, sH);    // draw beams back to previous stem.
            if (nCurr > nPrev && nCurr > nNext){    // we have beamlets on this stem.
                if (nPrev < nNext){    // beamlets lean toward side with more beams.
                    bX = cX + 3*H;
                    drawBeamStack(g, nNext, nCurr, cX, bX, sH);
                }else{
                    bX = cX - 3*H;
                    drawBeamStack(g, nPrev, nCurr, bX, cX, sH);
                }
            }
        }
    }

    public void setMasterBeam(){
        mx1 = first().x();
        my1 = first().yBeamEnd();
        mx2 = last().x();
        my2 = last().yBeamEnd();
    }
    public static void setPoly(int x1, int y1, int x2, int y2, int h){
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints;
        a[0] = y1; a[1] = y2; a[2] = y2 + h; a[3] = y1 + h;
    }
    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h){
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1; i < n2; i++){
            setPoly(x1, y1 + i*2*h, x2, y2 + i*2*h, h);
            g.fillPolygon(poly);
        }
    }

    //--------------------------------Math Functions--------------------------------------------
    public static int yOfX(int x, int x1, int y1, int x2, int y2){
        int dy = y2-y1, dx = x2-x1;
        if (dx == 0){System.out.println("x:" + x + " dy:" + dy);}
        return (x-x1)*dy/dx + y1;    // Examine with x=x1 and x=x2 - works everytime!
    }
    public static int mx1, my1, mx2, my2; // Coordinates for the Master Beam
    // Make sure the buffer remain intact - BUFFER WITH CARE AND CAUTION
    public static int yOfX(int x){return yOfX(x, mx1, my1, mx2, my2);}
    public static void setMasterBeam(int x1, int y1, int x2, int y2){
        mx1 = x1; my1 = y1; mx2 = x2; my2 = y2;
    }
}
