package music;

import graphicslib.UC;
import reaction.Gesture;
import reaction.Ink;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Sys extends Mass {
    public Staff.List staffs = new Staff.List();
    public Page page = PAGE;
    public int iSys;
    public Sys.Fmt fmt;
    public Time.List times;
    public Stem.List stems = new Stem.List();

    public Sys(int iSys, Sys.Fmt sysFmt) {
        super("BACK");
        this.iSys = iSys;
        this.fmt = sysFmt;
        this.times = new Time.List(this);
        for (int i = 0; i < sysFmt.size(); i++){
            addStaff(new Staff(i, sysFmt.get(i)));
        }
        addReaction(new Reaction("E-E") {    // Beam stems
            @Override
            public int bid(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();

                System.out.println("Before fastReject");    // Debugging

                if (stems.fastReject(y1, y2)){return UC.noBid;}
                Stem.List temp = stems.allIntersecters(x1,y1,x2,y2); // possible overlap: find intersecting stems

                System.out.println("Intersected" + temp.size());    // More debugging

                if(temp.size() < 2){return UC.noBid;} // crossing a single stem is a Stem reaction, not a Sys reaction

                Beam b = temp.get(0).beam; //check if all crossed stems are owned by the same beam (including null!)
                for(Stem s : temp){if(s.beam != b){return UC.noBid;}} // different owners is reject

                if(b == null && temp.size() != 2){return UC.noBid;} // only new Beam if exactly 2
                if(b == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)){return UC.noBid;} // only new if both are zero nFlag
                return 50; // this is either a creates new Beam or flags a set of beams
            }

            @Override
            public void act(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                Stem.List temp = stems.allIntersecters(x1, y1, x2, y2);
                Beam b = temp.get(0).beam;
                if (b == null){
                    new Beam(temp.get(0), temp.get(1));
                }else{
                    for (Stem s : temp){s.incFlag();}
                }
            }
        });
    }
    public Time getTime(int x) {return times.getTime(x);}
    public int yTop(){return page.sysTop(iSys);}
    public int yBot(){return staffs.get(staffs.size() - 1).yBot();}
    public void addStaff(Staff s){
        staffs.add(s);
        s.sys = this;
    }

    @Override
    public void show(Graphics g) {
        int y = yTop(), x = PAGE.margins.left;
        g.drawLine(x, y, x, y + fmt.height());
    }

    //--------------------------------Fmt--------------------------------------------
    public static class Fmt extends ArrayList<Staff.Fmt> {
        public int maxH = 0;
        public ArrayList<Integer> staffOffset = new ArrayList<>();

        public int height() {int last = size() - 1; return staffOffset.get(last) + get(last).height();}
        public void showAt(Graphics g, int y){
            for (int i = 0; i < size(); i++){
                get(i).showAt(g, y + staffOffset.get(i));
            }
        }

        public void addStaffFmt(Staff.Fmt sf, int yOff) {
            add(sf);
            staffOffset.add(yOff);
            if(maxH < sf.H){
                maxH = sf.H;
            }
        }
    }
    //--------------------------------List--------------------------------------------
    public static class List extends ArrayList<Sys>{

    }
}
