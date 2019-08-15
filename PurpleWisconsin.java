import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * The MapPanel class extends JPanel and produces a map of Wisconsin
 * and the voting results by each county in a given election year.
 *
 * @author Joseph Prostko
 * @version November 2018
 */
public class MapPanel extends JPanel {

    // The instance variable readMap holds a MapData object which holds the data
    // necessary to produce a voting map of Wisconsin.
    private MapData readMap;

    private HashMap<String, ElectionResults> results;

    /**
     * MapPanel() is the default constructor for MapPanel.
     */
    MapPanel() {
        // The readMap instance variable is instantiated as a new MapData() object.
        readMap = new MapData();

        //The WI.txt file name is inserted into the readMapData() method.
        readMapData("WI.txt");

        results = new HashMap<>();

        readElectionResults("WIGovernor2018.txt");

        setLayout(new BorderLayout());

    }


    /**
     * The public void readMapData() method reads the data from a file into
     * the program and creates a list of Wisconsin counties.
     *
     * @param filename which is the name of the file being scanned.
     */
    public void readMapData(String filename) {

        // The readMap name is set equal to "Wisconsin Voting Results."
        readMap.name = "Wisconsin Voting Results";

        // A new Scanner named file is declared.
        Scanner file = null;

        // try catch brackets surround the instantiation of file.
        try {
            // file is instantiated as a new scanner looking at a new File from the filename document.
            file = new Scanner(new File(filename));
        }
        // The catch statement catches and handles a FileNotFoundException.
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // The method continues to read in the data from the file until it runs out of lines to read in.
        while (file.hasNext()) {

            // The minLongitude of readMap is set equal to the first double of the file.
            readMap.minLongitude = file.nextDouble();

            // The minLatitude of readMap is set equal to the second double of the file.
            readMap.minLatitude = file.nextDouble();

            // The maxLongitude of readMap is set equal to the third double of the file.
            readMap.maxLongitude = file.nextDouble();

            // The maxLatitude of readMap is set equal to the fourth double of the file.
            readMap.maxLatitude = file.nextDouble();

            // totalCounties holds the value of how many counties in Wisconsin are specified in the file.
            int totalCounties = file.nextInt();

            // The subRegionList of readMap is instantiated as a new ArrayList<>().
            readMap.subRegionList = new ArrayList<>();

            // A for loop reads in the value for each individual SubRegion and adds it to the points ArrayList
            for (int a = 0; a < totalCounties; a++) {

                // The SubRegion temp is instantiated as a new SubRegion() and will hold the value of each subregion
                // as its data is being read in.
                SubRegion temp = new SubRegion();

                // temp.points is instantiated as a new ArrayList<>().
                temp.points = new ArrayList<>();

                file.nextLine();

                file.nextLine();

                // temp.name is instantiated as the file's county name and WI state name separated by a comma.
                temp.name = file.nextLine();

                file.nextLine();

                // temp.PointCount is set to the next integer in the file.
                temp.PointCount = file.nextInt();

                // An inner loop reads in the values of each point being added to the current temp SubRegion.
                for (int b = 0; b < temp.PointCount; b++) {

                    // A temporary Point.Double mid is instantiated as a new Point.Double().
                    Point.Double mid = new Point.Double();

                    // The values of mid are set as the next two doubles in the document.
                    mid.setLocation(file.nextDouble(), file.nextDouble());

                    // mid is added to temp's points ArrayList.
                    temp.points.add(mid);

                }

                // temp is added to readMap's subRegionList.
                readMap.subRegionList.add(temp);
            }
        }
    }

    /**
     * The paint method in this program is used to produce a colored
     * map of Wisconsin with the electoral votes displayed as a color
     * for each county.
     * @param g- the output of a colored electoral map of Wisconsin.
     */
    @Override
    public void paint(Graphics g) {
        // The double mX sets the value of the border of the longitude of the Wisconsin map.
        double mX = (this.getWidth() - 21) / (readMap.maxLongitude - readMap.minLongitude);

        // The double bX sets the value of the border of the longitude of the Wisconsin map.
        double bX = -mX * readMap.minLongitude + 10;

        // The double mY sets the value of the border of the latitude of the Wisconsin map.
        double mY = (this.getHeight() - 21) / (readMap.minLatitude - readMap.maxLatitude);

        // The double bY sets the value of the border of the latitude of the Wisconsin map.
        double bY = -mY * readMap.maxLatitude + 10;

        // The for loop below reads in the values of each item in the subRegionList of readMap.
        for (int a = 0; a < readMap.subRegionList.size(); a++) {

            // The poly Polygon is the temporary polygon instantiated for each subregion.
            Polygon poly = new Polygon();

            // The temp SubRegion holds the value of the current SubRegion in the readMap.subRegionList.
            SubRegion temp = readMap.subRegionList.get(a);

            // The mid ElectionResults returns the data of the results from the results list of ElectionResults.
            ElectionResults mid = results.get(temp.name);

            // The for loop below reads in the value of each point stored in the current temp subRegion.
            for (int b = 0; b < temp.PointCount; b++) {

                // The pixelX double is set using the boundaries of latitude as set above.
                double pixelX = mX * temp.points.get(b).x + bX;

                // The pixelY double is set using the boundaries of longitude as set above.
                double pixelY = mY * temp.points.get(b).y + bY;

                // The point is added to the poly polygon.
                poly.addPoint((int) pixelX, (int) pixelY);

            }

            // int totalVotes holds the total number of votes in the temp subRegion.
            int totalVotes = mid.demVotes + mid.repVotes + mid.other;

            // int redHue holds the value of red in the new color RGB scale using the number of Republican votes.
            int redHue = (int)(((double)mid.repVotes / totalVotes) * 255);

            // int blueHue holds the value of blue in the new color RGB scale using the number of Democratic votes.
            int blueHue = (int)(((double)mid.demVotes / totalVotes) * 255);

            // int greenHue holds the value of green in the new color RGB scale using the number of other votes.
            int greenHue = (int)(((double)mid.other / totalVotes) * 255);

            // stateColor is the new color obtained from the RGB values above.
            Color stateColor = new Color(redHue, greenHue, blueHue);

            // the color of the graphics is set to stateColor.
            g.setColor(stateColor);

            // the polygon is filled with stateColor.
            g.fillPolygon(poly);

            // the color is set back to black.
            g.setColor(Color.BLACK);

            // the current subRegion's boundaries are drawn.
            g.drawPolygon(poly);
        }
    }

    /**
     * public void readElectionResults() reads in the values of the current election results
     * from a text file.
     * @param filename the name of the file containing the election results of the specified year.
     */
    public void readElectionResults(String filename) {

        // A new Scanner named file is declared.
        Scanner file = null;

        // try catch brackets surround the instantiation of file.
        try {
            // file is instantiated as a new scanner looking at a new File from the filename document.
            file = new Scanner(new File(filename));
        }
        // The catch statement catches and handles a FileNotFoundException.
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // The file's delimiter is set as a comma.
        file.useDelimiter(",");

        // The first line of the file is skipped.
        file.nextLine();

        // The while loop continuously reads in data from the file until it runs out.
        while (file.hasNext()) {

            // The temp ElectionResults holds the values of the current election results for each SubRegion.
            ElectionResults temp = new ElectionResults();

            // temp's name is set as the first String.
            temp.name = file.next();

            // temp's number of Republican votes is set as the next int.
            temp.repVotes = file.nextInt();

            // temp's number of Democratic votes is set as the next int.
            temp.demVotes = file.nextInt();

            // temp's number of other votes is set as the next int.
            temp.other = file.nextInt();

            // temp is put in the results HashMap<>().
            results.put(temp.name, temp);

            // The scanner moves on to the next line.
            file.nextLine();
        }
    }

    /**
     * MapData is an object which holds the data to produce one map of Wisconsin's election.
     */
    private class MapData {

        // The String name holds the value of the map's name.
        private String name;

        // The double minLongitude holds the value of the map's minimum longitude.
        private double minLongitude;

        // The double minLatitude holds the value of the map's minimum latitude.
        private double minLatitude;

        // The double maxLongitude holds the value of the map's maximum longitude.
        private double maxLongitude;

        // The double maxLatitude holds the value of the map's maximum latitude.
        private double maxLatitude;

        // The ArrayList<SubRegion> subRegionList holds each SubRegion of the map.
        private ArrayList<SubRegion> subRegionList;
    }

    /**
     * SubRegion is an object which holds the boundaries and shape of each county of Wisconsin.
     */
    private class SubRegion {

        // The int PointCount holds the total number of points for each Wisconsin conuty.
        private int PointCount;

        // The String name holds the name of each SubRegion.
        private String name;

        // The ArrayList<Point.Double> points holds each point of the current SubRegion.
        private ArrayList<Point.Double> points;
    }

    /**
     * ElectionResults holds the data for each SubRegion election results.
     */
    private class ElectionResults {

        // The String name holds the value of the current SubRegion's name.
        private String name;

        // The int repVotes holds the total number of Republican votes for that county.
        private int repVotes;

        // The int demVotes holds the total number of Democratic votes for that county.
        private int demVotes;

        // The int other holds the total number of other votes for that county.
        private int other;
    }

    /**
     * The main method uses all object and methods in the MapPanel class to produce a Wisconsin election map.
     * @param args all functions used in the program.
     */
    public static void main(String[] args) {

        // MapPanel panel is instantiated as a new MapPanel().
        MapPanel panel = new MapPanel();

        // JFrame mapFrame is instantiated as a new JFrame().
        JFrame mapFrame = new JFrame();

        // panel is added to mapFrame.
        mapFrame.add(panel);

        // mapFrame is set as visible.
        mapFrame.setVisible(true);

        // mapFrame is set as a 500 x 500 size frame.
        mapFrame.setSize(500, 500);

        // mapFrame is set to terminate the program upon closing the frame.
        mapFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
