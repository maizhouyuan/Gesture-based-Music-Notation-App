package music;

import graphicslib.UC;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;


public class Rest extends Duration {
    public Staff staff;
    public Time time;
    public int line = 4;    // Middle 3rd line
    public Rest(Staff staff, Time time){
        this.staff = staff;
        this.time = time;
        addReaction(new Reaction("E-E") {    // Add a flag to the Rest, up to four flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}    // Crossed
                return Math.abs(y - Rest.this.staff.yLine(4));    // 4: Middle line
            }

            @Override
            public void act(Gesture g) {Rest.this.incFlag();}    // Increment flag
        });
        addReaction(new Reaction("W-W") {    // Decrement flag
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}    // Crossed
                return Math.abs(y - Rest.this.staff.yLine(4));    // 4: Middle line
            }

            @Override
            public void act(Gesture g) {Rest.this.decFlag();}    // Decrement flag
        });
        addReaction(new Reaction("DOT") {    // Cycle Dots
            @Override
            public int bid(Gesture g) {
                int xr = Rest.this.time.x, yr = Rest.this.y();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xr || x > xr + 40 || y < yr - 40 || y > yr + 40) {return UC.noBid;}    // Dots are supposed to be on the right
                return Math.abs(x - xr) + Math.abs(y - yr);
            }

            @Override
            public void act(Gesture g) {cycleDot();}
        });
    }
    public int y() {return staff.yLine(line);}
    public void show(Graphics g) {
        int H = staff.H(), y = y();
        if (nFlag == -2){Glyph.REST_W.showAt(g, H, time.x, y);}
        if (nFlag == -1){Glyph.REST_H.showAt(g, H, time.x, y);}
        if (nFlag == 0){Glyph.REST_Q.showAt(g, H, time.x, y);}
        if (nFlag == 1){Glyph.REST_1F.showAt(g, H, time.x, y);}
        if (nFlag == 2){Glyph.REST_2F.showAt(g, H, time.x, y);}
        if (nFlag == 3){Glyph.REST_3F.showAt(g, H, time.x, y);}
        if (nFlag == 4){Glyph.REST_4F.showAt(g, H, time.x, y);}
        int off = UC.gapRestToFirstDot, sp = UC.gapBetweenAugDots;
        for (int i = 0; i < nDot; i++) {g.fillOval(time.x + off + i*sp, y - 3*H/2, H*2/3, H*2/3);}
    }
}
