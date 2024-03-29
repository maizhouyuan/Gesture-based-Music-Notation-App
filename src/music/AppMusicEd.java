package music;

import graphicslib.G;
import graphicslib.UC;
import graphicslib.Window;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AppMusicEd extends Window {
    static{new Layer("BACK"); new Layer("NOTE"); new Layer("FORE");}
    public static Page PAGE;
    public AppMusicEd() {
        super("music editor", UC.mainWindowWidth, UC.mainWindowHeight);
        Reaction.initialReactions.addReaction(new Reaction("E-E"){
            public int bid(Gesture g){return 10;}
            public void act(Gesture g){
                int y = g.vs.yM();
                Sys.Fmt sysFmt = new Sys.Fmt();
                PAGE = new Page(sysFmt);
                PAGE.margins.top = y;
                PAGE.addNewSys();
                PAGE.addNewStaff(0);
                this.disable();
            }
        });
    }
    // Polygon usage:
    // https://docs.oracle.com/javase/8/docs/api/java/awt/Polygon.html
    static int[]xPoly = {100, 200, 200, 100};
    static int[]yPoly = {50, 70, 80, 60};
    static Polygon poly = new Polygon(xPoly, yPoly, 4);
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.black);
        Layer.ALL.show(g);    // List of all layers, that has a show routine
        if (PAGE != null){
//            Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top + 4*8);
            int H = 32;
//            Glyph.HEAD_Q.showAt(g, H, 200, PAGE.margins.top + 4*H);
//            g.setColor(Color.RED);
//            g.drawRect(200, PAGE.margins.top + 3*H, 24*H/10, 2*H);
        }
        int H = 8, x1 = 100, x2 = 200;
        /* Beam.setMasterBeam(x1, 100 + G.rnd(100), x2, 100 + G.rnd(100));
        Beam.drawBeamStack(g, 0, 1, x1, x2, H);
        g.setColor(Color.ORANGE);
        Beam.drawBeamStack(g, 1, 3, x1 + 10, x2 - 10, H); */
//        Beam.setPoly(100, 100 + G.rnd(100), 200, G.rnd(200), 8);
//        g.fillPolygon(Beam.poly);
//        yPoly[3]++;
//        poly.ypoints[3]++;
        Ink.BUFFER.show(g);
    }

    public void mousePressed(MouseEvent me){
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me){
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new AppMusicEd();
        Window.launch();
    }
}
