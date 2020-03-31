
import org.junit.Test;
//import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class MyTests {

    /**
     *     checking whether after merging the array that the clicks have impression features filled out
     */

    @Test
    public void mergeArrays(){
        Controller controller = new Controller();
        Campaign campaign = new Campaign("/Users/danielraad/IdeaProjects/TestCode/server_log.csv", "/Users/danielraad/IdeaProjects/TestCode/click_log.csv", "/Users/danielraad/IdeaProjects/TestCode/impression_log.csv", 1, controller);
        ArrayList<Click> myclicks = campaign.getClicks();
        assertNotNull(myclicks.get(0).getContext());
    }

    /**
     *      checking whether the arraylists are not empty following loading the data
     */

    @Test
    public void loadDatasets(){
        Controller controller = new Controller();
        Campaign campaign = new Campaign("/Users/danielraad/IdeaProjects/TestCode/server_log.csv", "/Users/danielraad/IdeaProjects/TestCode/click_log.csv", "/Users/danielraad/IdeaProjects/TestCode/impression_log.csv", 1, controller);
        assertNotNull(campaign.getClicks());
        assertNotNull(campaign.getImpressions());
        assertNotNull(campaign.getServerEntries());
    }

    @Test
    public void datasets(){
        Controller controller = new Controller();
        Campaign campaign = new Campaign("/Users/danielraad/IdeaProjects/TestCode/server_log.csv", "/Users/danielraad/IdeaProjects/TestCode/click_log.csv", "/Users/danielraad/IdeaProjects/TestCode/impression_log.csv", 1, controller);
        ArrayList<Click> clicks = campaign.getClicks();
        assertEquals(clicks.get(0).getID(), "8895519749317550080");
    }

    @Test
    public void filterServerLog(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/IdeaProjects/TestCode/server_log.csv", "/Users/danielraad/IdeaProjects/TestCode/click_log.csv", "/Users/danielraad/IdeaProjects/TestCode/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:01:21", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-01 12:04:29", formatter);
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(7, controller.filterServerLog(filter).size());
    }

    @Test
    public void filterClickLog(){

    }

    @Test
    public void filterImpressionLog(){

    }




    /**
     * Will test whether the saveGraph method has correctly saved the file
     * potentially change the route of the file so that Controller saves the file
     */

//    @Test
//    public void saveChart(){
//
//        Campaign campaign = new Campaign();
//        LineGraph line = new LineGraph(campaign);
//        String chart = "graph1";
//        File chartImage = new File(chart);
//        line.saveGraph(chart);
//        assertEquals(true, chartImage.exists());
//
//    }



    /**
     * checking whether or not the two arraylists produce the same results
     */
    @Test
    public void datapoint(){

    }


    @Test
    public void granularityTesting(){

    }


    /**
     *  big data base testing the ability to load a million impressions
     *
     */
    @Test
    public void millionImpressions(){

    }

    /**
     * Tests for the calculations when an array is being passed through
     */

    @Test
    public void totalCost(){
        Controller myController = new Controller();
        assertEquals(110, myController.calcTotalCost(impressions, clicks) );

    }

    @Test
    public void numImpressions(){
        Controller myController = new Controller();
        assertEquals(10, myController.calcImpressions(impressions));

    }

    @Test
    public void impCost(){
        Controller myController = new Controller();
        assertEquals(55, myController.calcTotalImpCost(impressions));

    }



    @Test
    public void numClicks(){
        Controller myController = new Controller();
        assertEquals(10, myController.calcClicks(clicks));
    }

    @Test
    public void clickCost(){
        Controller myController = new Controller();
        assertEquals(55, myController.calcTotalClickCost(clicks));

    }

    @Test
    public void calcConversions(){
        Controller myController = new Controller();
        assertEquals(9, myController.calcConversions(serverEntryArrayList9));
        assertEquals(2, myController.calcConversions(serverEntryArrayList2));
    }


    @Test
    public void calcConvRate(){
        Controller myController = new Controller();
        assertEquals(0.9, myController.calcConvRate(serverEntryArrayList9, clicks));
        assertEquals(0.2, myController.calcConvRate(serverEntryArrayList2, clicks));
    }


    @Test
    public void calcBounces(){
        Controller myController = new Controller();
        assertEquals(2, myController.calcBounces(serverEntryArrayList2));
        assertEquals(1, myController.calcBounces(serverEntryArrayList9));
    }

    @Test
    public void calcBounceRate(){
        Controller myController = new Controller();
        assertEquals(0.2, myController.calcBounceRate(serverEntryArrayList2, clicks));
        assertEquals(0.1, myController.calcBounceRate(serverEntryArrayList9, clicks));
    }


    @Test
    public void calcUniques(){
        Controller myController = new Controller();
        assertEquals(9, myController.calcUniques(clicks));
    }


    @Test
    public void calcCTR(){
        Controller myController = new Controller();
        assertEquals(1, myController.calcCTR(clicks, impressions));
    }


    @Test
    public void calcCPA(){
        Controller myController = new Controller();
        assertEquals(55, myController.calcCPA(impressions, clicks, serverEntryArrayList2));
        assertEquals(12.222222222222221, myController.calcCPA(impressions, clicks, serverEntryArrayList9));
    }

    @Test
    public void calcCPC(){
        Controller myController = new Controller();
        assertEquals(5.5, myController.calcCPC(clicks));
    }

    @Test
    public void calcCPM(){
        Controller myController = new Controller();
        assertEquals(5500, myController.calcCPM(impressions));
    }


        ArrayList<ServerEntry> serverEntryArrayList9 = new ArrayList() {{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatDateTime = LocalDateTime.parse("2019-12-12 12:30:30",formatter);
        ServerEntry entry1 = new ServerEntry(formatDateTime, "1", formatDateTime, 1, "Yes");
        ServerEntry entry2 = new ServerEntry(formatDateTime, "1", formatDateTime, 2, "Yes");
        ServerEntry entry3 = new ServerEntry(formatDateTime, "1", formatDateTime, 3, "Yes");
        ServerEntry entry4 = new ServerEntry(formatDateTime, "1", formatDateTime, 4, "Yes");
        ServerEntry entry5 = new ServerEntry(formatDateTime, "1", formatDateTime, 5, "Yes");
        ServerEntry entry6 = new ServerEntry(formatDateTime, "1", formatDateTime, 6, "Yes");
        ServerEntry entry7 = new ServerEntry(formatDateTime, "1", formatDateTime, 7, "Yes");
        ServerEntry entry8 = new ServerEntry(formatDateTime, "1", formatDateTime, 8, "No");
        ServerEntry entry9 = new ServerEntry(formatDateTime, "1", formatDateTime, 9, "Yes");
        ServerEntry entry10 = new ServerEntry(formatDateTime, "1", formatDateTime, 10, "Yes");


        add(entry1);
        add(entry2);
        add(entry3);
        add(entry4);
        add(entry5);
        add(entry6);
        add(entry7);
        add(entry8);
        add(entry9);
        add(entry10);

    }};

    ArrayList<ServerEntry> serverEntryArrayList2 = new ArrayList() {{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatDateTime = LocalDateTime.parse("2019-12-12 12:30:30",formatter);
        ServerEntry entry1 = new ServerEntry(formatDateTime, "1", formatDateTime, 1, "No");
        ServerEntry entry2 = new ServerEntry(formatDateTime, "1", formatDateTime, 1, "No");
        ServerEntry entry3 = new ServerEntry(formatDateTime, "1", formatDateTime, 3, "No");
        ServerEntry entry4 = new ServerEntry(formatDateTime, "1", formatDateTime, 4, "No");
        ServerEntry entry5 = new ServerEntry(formatDateTime, "1", formatDateTime, 5, "Yes");
        ServerEntry entry6 = new ServerEntry(formatDateTime, "1", formatDateTime, 6, "No");
        ServerEntry entry7 = new ServerEntry(formatDateTime, "1", formatDateTime, 7, "Yes");
        ServerEntry entry8 = new ServerEntry(formatDateTime, "1", formatDateTime, 8, "No");
        ServerEntry entry9 = new ServerEntry(formatDateTime, "1", formatDateTime, 9, "No");
        ServerEntry entry10 = new ServerEntry(formatDateTime, "1", formatDateTime, 10, "No");


        add(entry1);
        add(entry2);
        add(entry3);
        add(entry4);
        add(entry5);
        add(entry6);
        add(entry7);
        add(entry8);
        add(entry9);
        add(entry10);

    }};

    ArrayList<Click> clicks = new ArrayList() {{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatDateTime = LocalDateTime.parse("2019-12-12 12:30:30",formatter);
        Click click1 = new Click(formatDateTime, "1", 1);
        Click click2 = new Click(formatDateTime, "2", 2);
        Click click3 = new Click(formatDateTime, "3", 3);
        Click click4 = new Click(formatDateTime, "4", 4);
        Click click5 = new Click(formatDateTime, "5", 5);
        Click click6 = new Click(formatDateTime, "6", 6);
        Click click7 = new Click(formatDateTime, "7", 7);
        Click click8 = new Click(formatDateTime, "8", 8);
        Click click9 = new Click(formatDateTime, "9", 9);
        Click click10 = new Click(formatDateTime, "1", 10);
        add(click1);
        add(click2);
        add(click3);
        add(click4);
        add(click5);
        add(click6);
        add(click7);
        add(click8);
        add(click9);
        add(click10);
    }};


    ArrayList<Impression> impressions = new ArrayList(){{
        //load them with different data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatDateTime = LocalDateTime.parse("2019-12-12 12:30:30",formatter);
        Impression imp1 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float) 1);
        Impression imp2 = new Impression(formatDateTime, "2", "Male", "<25", "High", "Travel", (float)2);
        Impression imp3 = new Impression(formatDateTime, "3", "Male", "<25", "High", "Travel", (float)3);
        Impression imp4 = new Impression(formatDateTime, "4", "Male", "<25", "High", "Travel", (float)4);
        Impression imp5 = new Impression(formatDateTime, "5", "Male", "<25", "High", "Travel", (float)5);
        Impression imp6 = new Impression(formatDateTime, "6", "Male", "<25", "High", "Travel", (float)6);
        Impression imp7 = new Impression(formatDateTime, "7", "Male", "<25", "High", "Travel", (float)7);
        Impression imp8 = new Impression(formatDateTime, "8", "Male", "<25", "High", "Travel", (float)8);
        Impression imp9 = new Impression(formatDateTime, "9", "Male", "<25", "High", "Travel", (float)9);
        Impression imp10 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)10);
        add(imp1);
        add(imp2);
        add(imp3);
        add(imp4);
        add(imp5);
        add(imp6);
        add(imp7);
        add(imp8);
        add(imp9);
        add(imp10);
    }};

    ArrayList<Impression> incorrectImpressionsCosts = new ArrayList(){{
        //load them with different data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatDateTime = LocalDateTime.parse("2019-12-12 12:30:30",formatter);
        Impression imp1 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)1);
        Impression imp2 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)-2);
        Impression imp3 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)-3);
        Impression imp4 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)4);
        Impression imp5 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)5);
        Impression imp6 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)6);
        Impression imp7 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)7);
        Impression imp8 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)8);
        Impression imp9 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)9);
        Impression imp10 = new Impression(formatDateTime, "1", "Male", "<25", "High", "Travel", (float)10);
        add(imp1);
        add(imp2);
        add(imp3);
        add(imp4);
        add(imp5);
        add(imp6);
        add(imp7);
        add(imp8);
        add(imp9);
        add(imp10);
    }};



}
