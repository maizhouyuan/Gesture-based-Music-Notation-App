package graphicslib;

import java.awt.*;

public class UC {    // Universal Constants, avoid magic numbers
    public static final int mainWindowWidth = 1000;
    public static final int mainWindowHeight = 700;
    public static int inkBufferMax = 600;
    public static final int normSampleSize = 25;
    public static final int normCoordMax = 1000;
    public static Color inkColor = Color.BLACK;
    public static final int noMatchDist = 500000;
    public static int dotThreshold = 5;
    public static String ShapeDbFileName = "shapeDB.DAT";
    public static int noBid = 1000;
    public static int barToMarginSnap = 40;
    public static String FontName = "Sinfonia";
    public static int snapTime = 60;
    public static int gapRestToFirstDot = 28;
    public static int gapBetweenAugDots = 10;
}
