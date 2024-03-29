package music;

import graphicslib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Page extends Mass {
    public static Margins margins = new Margins();
    public Sys.Fmt sysFmt;
    public int sysGap;
    public Sys.List sysList = new Sys.List();
    public Page(Sys.Fmt sysFmt){
        super("BACK");
        this.sysFmt = sysFmt;
        addReaction(new Reaction("E-W") {    // Adding new staff to existing system
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM();
                if (y <= PAGE.margins.top + sysFmt.height() + 30){
                    return UC.noBid;
                }
                return 50;
            }

            @Override
            public void act(Gesture g) {
                int y = g.vs.yM();
                PAGE.addNewStaff(y - PAGE.margins.top);
            }
        });

        addReaction(new Reaction("E-E") {    // Adding new system
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM();
                int yBot = PAGE.sysTop(sysList.size());
                if (y < yBot) {
                    return UC.noBid;
                }
                return 50;
            }

            @Override
            public void act(Gesture g) {
                int y = g.vs.yM();
                if (PAGE.sysList.size() == 1){
                    PAGE.sysGap = y - PAGE.sysTop(1);
                }
                PAGE.addNewSys();
            }
        });
    }

    @Override
    public void show(Graphics g) {
        for (int i = 0; i < sysList.size(); i++){
            sysFmt.showAt(g, sysTop(i));
        }
    }

    public int sysTop(int iSys) {
        return margins.top + iSys * (sysFmt.height() + sysGap);
    }

    public void addNewSys(){
        sysList.add(new Sys(sysList.size(), sysFmt));
    }

    public void addNewStaff(int yOff){
        Staff.Fmt sf = new Staff.Fmt();
        int n = sysFmt.size();
        sysFmt.addStaffFmt(sf, yOff);
        for (int i = 0; i < sysList.size(); i++){
            sysList.get(i).addStaff(new Staff(n, sf));
        }
    }

    //--------------------------------Margins--------------------------------------------
    public static class Margins{
        private static int MM = 50;
        public int top = MM, left = MM, bot = UC.mainWindowHeight - MM, right = UC.mainWindowWidth - MM;
    }
}
