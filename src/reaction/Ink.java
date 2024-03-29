package reaction;

import graphicslib.G;
import graphicslib.I;
import graphicslib.UC;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show, Serializable {
    // public static G.VS TEMP = new G.VS(100, 100, 100, 100);    // for debugging
    public static Buffer BUFFER = new Buffer();
    public Norm norm;    // normalized coordinator
    public G.VS vs;
    public static final int K = UC.normSampleSize;
    public Ink(){
//        super(K);
//        BUFFER.subSample(this);
//        G.V.T.set(BUFFER.bBox, TEMP);
//        this.transform();
        norm = new Norm();
        vs = BUFFER.bBox.getNewVS();
    }

    @Override
    public void show(Graphics g) {
        // g.setColor(Color.RED);
        // g.fillRect(100, 100, 100, 100);
        // draw(g);
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
    }

    //-------------------------LIST-----------------------------------------
    public static class List extends ArrayList<Ink> implements I.Show, Serializable{
        @Override
        public void show(Graphics g) {
            for(Ink ink: this){ink.show(g);}    // this is a parameter that don't show up
        }
    }

    //-------------------------BUFFER-----------------------------------------
    // hold sth for a while
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.inkBufferMax;
        public int n;    // How many points in the Buffer
        public G.BBox bBox= new G.BBox();
        private Buffer(){super(MAX);}
        public void clear(){n = 0;}    // Clear Buffer
        public void add(int x, int y){
            if(n < MAX){points[n++].set(x, y); bBox.add(x, y);}    // n++: updating n
        }
        public void subSample(G.PL res){
            int K = res.size();
            for(int i = 0; i < K; i++){
                res.points[i].set(points[i*(n-1)/(K-1)]);    // linear function
            }
        }
        @Override
        public boolean hit(int x, int y) {return true;}

        @Override
        public void dn(int x, int y) {clear(); add(x, y); bBox.set(x, y);}

        @Override
        public void up(int x, int y) {

        }

        @Override
        public void drag(int x, int y) {add(x, y);}

        @Override
        public void show(Graphics g) {
            drawN(g, n);
            // bBox.draw(g);    // Show the BBox for debugging(making sure it's working)
        }
    }

    //-------------------------Norm-----------------------------------------

    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;

        //Norm Coordinate System, coordinate box for transform
        public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);
        public Norm(){
            super(N);    // how big the poly line is
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bBox, NCS);
            this.transform();
        }

        public void drawAt(Graphics g, G.VS vs){
            // Set up the transform
            G.V.T.set(NCS, vs);
            for(int i = 1; i < N; i++){
                g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
            }
        }

        public int dist(Norm norm){
            int res = 0;
            for(int i = 0; i < N; i++){
                int dx = points[i].x - norm.points[i].x;    //Delta x
                int dy = points[i].y - norm.points[i].y;
                res += dx * dx + dy * dy;
            }
            return res;
        }

        public void blend(Norm norm, int nBlend){
            for(int i = 0; i < N; i++){
                points[i].blend(norm.points[i], nBlend);
            }
        }
    }
}
