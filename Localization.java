/**
 * Localization.java
 * @author jmd
 * 
 * Main class for localization. Herp derp.
 */
 
 import javax.imageio.ImageIO;
 import java.awt.image.ColorConvertOp;
 import java.awt.color.ColorSpace;
 import java.io.File;
 import java.io.IOException;
 import java.awt.image.BufferedImage;
 import java.util.Arrays;               // for testing
 
 // Player imports
import javaclient3.*;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.*;
 
 public class Localization {
    
    /**
     * Loads in the map from an external file. Does some number fudging,
     * because the expected file is a PNG and the RGB value grabbed from it
     * for white is -1 (representing free space) and the rest (some other
     * negative number) is black (representing obstacles).
     *
     * @param filename  the name of the file containing the map
     * @return  a 2D integer array [x][y] containing the grayscale rgb value
     *          at (x,y) where the origin is the top-left-most pixel
     */
    public static int[][] getMap(String filename) {
        int[][] map;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        map = new int[img.getWidth()][img.getHeight()];
        
        int pixel;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                pixel = img.getRGB(x,y);
                map[x][y] = (pixel == -1) ? 255 : 0;    // some number fudging
            }
        }
        
        return map;
    }
    
    
    public static void main(String[] args) {
        int[][] map = getMap(args[0]);
        System.out.println(Arrays.deepToString(map));
        
        // copypasta initalization stuff from other jawns
        PlayerClient pc;
        if (args.length == 1)
            pc = new PlayerClient("localhost",6665);
        else
            pc = new PlayerClient(args[1],Integer.valueOf(args[2]));
    
        Position2DInterface pos = pc.requestInterfacePosition2D(0,PlayerConstants.PLAYER_OPEN_MODE);
    
        RangerInterface ranger = pc.requestInterfaceRanger(0,PlayerConstants.PLAYER_OPEN_MODE);
        
        /* So as not to potentially overload the ranger interface, we should
         * probably pass the localizer to the wanderer and have the wanderer
         * update the localizer as it receives info from the ranger and pos.
         *
         * Or we would just let them independently abuse the ranger and pos.
         * Whatever.
         */
        // Localizer loc = new Localizer();
        // Wanderer w = new Wanderer(pc,pos,ranger,loc);
        
        // loc.start();
        // w.start();
        
        // ! All the following commented stuff should be in the wander Thread !
        
//         while (true) {
//     
//             double turnrate = 0, fwd = 0;
//             double omega = 20*Math.PI/180; 
//     
//             pc.readAll();
//     
//             if (!ranger.isDataReady())
//             continue;
//     
//             double[] ranges = ranger.getData().getRanges();
//     
//             // yay cut and paste from C++!
//             // rightmost is sensor 0, 30 degrees behind us.
//             // directly right is 30/(360/1024) = 85
//             // directly left is 210/(360/1024) = 597
//             // do simple collision avoidance
//             double rightval = (ranges[85]+ranges[90])/2.0;
//             double leftval = (ranges[592]+ranges[597])/2.0;
//             double frontval = (ranges[340]+ranges[345])/2.0;
//             
//             System.out.printf("(%7f,%7f,%7f) left: %7f fwd: %7f right: %7f\n",
//                       pos.getX(),pos.getY(),pos.getYaw(),
//                       leftval,frontval,rightval);
//             
//             if (frontval < 0.5) {
//             fwd = 0;
//             if (leftval < rightval)
//                 turnrate = -1*omega;
//             else
//                 turnrate = omega;
//             } else {
//             fwd = 0.25;
//             if (leftval < 1.0)
//                 turnrate = -1*omega;
//             else if (rightval < 1.0)
//                 turnrate = omega;
//             }
//             
//             pos.setSpeed(fwd,turnrate);
//         }
    }
 }