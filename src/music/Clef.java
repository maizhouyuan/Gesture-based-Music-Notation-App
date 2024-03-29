package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

/*
 Design for Clef class implementation
 Maizhou - draft, don't rely on it

We're implementing 2 types of Clefs, with gesture "SW-SE" for Treble Clef & "SE-SW" for Bass Clef
 -  Clefs have to align with Time ? No! independent of time
 -  Treble Clef: starts on the second line, C major across the line below the staff
 -  Bass Clef: starts on the fourth line, C major at the second line

 -  Clefs are determining the location of heads
 -  Thus has to adjust the heads with Head class

What parameters I need?
 -  staff object
 - positional x and y of the clef
 - line number
 - Reactions for gestures
 - Graphics g for show routine
 - adjustments for head - based on Clef positioning ?

To-do list
 -  extends Mass
 -  has to access Staff class and Head class
 -  2 new Reactions for two clefs
 -  show routine required
 -  Training for two gestures are also required

 */
public class Clef extends Mass {
    public Staff staff;
    public int x, line=4;
    public Glyph glyph;
    public Clef(Staff staff, int x, Glyph glyph){
        super("NOTE");
        this.staff = staff;
        this.x = x;
        this.glyph = glyph;
        //int H = staff.H();
        //  int top = staff.yTop() - H;
        // line = 3;

    }
    public void show(Graphics g){
        int H = staff.H();;
        glyph.showAt(g, H, x, staff.yTop()+line*H);
    }

}
