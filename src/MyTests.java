
import javafx.scene.chart.BarChart;
import org.junit.Test;
import org.testng.Assert;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

public class MyTests {



    //--------- LOADING DATA TESTS -------------------------------------------------------------------------------------
    /**
     *     checking whether after merging the array that the clicks have impression features filled out
     */
    @Test
    public void mergeArrays(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        ArrayList<Click> myclicks = controller.getCampaign().getClicks();
        ArrayList<ServerEntry> myServer = controller.getCampaign().getServerEntries();
        assertNotNull(myclicks.get(0).getContext());
        assertNotNull(myServer.get(0).getContext());
    }

    /**
     *      checking whether the arraylists are not empty following loading the data
     */

    @Test
    public void loadDatasets(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        assertNotNull(controller.getCampaign().getClicks());
        assertNotNull(controller.getCampaign().getImpressions());
        assertNotNull(controller.getCampaign().getServerEntries());
    }

    /**
     *      correct data sets are coming through, integration through the campaign and the controller
     */

    @Test
    public void datasets(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        ArrayList<Click> clicks = controller.getCampaign().getClicks();
        assertEquals(clicks.get(0).getID(), "8895519749317550080");
    }


    // ---------FILTERS TESTING ----------------------------------------------------------------------------------------
    /**
     *      checking the filters are being made correctly
     */
    @Test
    public void filters(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:01:21", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-01 12:04:29", formatter);
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = "Male";
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);


        assertEquals("Male", filter.getGender());
        assertEquals(entryDate, filter.getStartDate());
        assertEquals(exitDate, filter.getEndDate());

    }

    /**
     *      checking the filters are filtering correctly by date on the server log
     */

    @Test
    public void filterServerLog_bydate(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:01:21", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-01 12:04:29", formatter);
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(9, controller.filterServerLog(filter).size());
    }


    /**
     *      checking the filters are filtering correctly by date on the click log
     */
    @Test
    public void filterClickLog_bydate(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:01:21", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-01 12:04:29", formatter);
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(9, controller.filterClickLog(filter).size());
    }

    /**
     *      checking the filters are filtering correctly by date on the impression log
     */
    @Test
    public void filterImpressionLog_bydate(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:01:21", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-01 12:04:29", formatter);
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(114, controller.filterImpressionLog(filter).size());
    }


    /**
     *      Testing filters for age <25
     */

    @Test
    public void filterImpressionLog_byageLess(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(97050, controller.filterImpressionLog(filter).size());
    }

    @Test
    public void filterServerLog_byageLess(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(2985, controller.filterServerLog(filter).size());
    }


    @Test
    public void filterClickLog_byageLess(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(2985, controller.filterClickLog(filter).size());
    }


    /**
     *      Testing filters for age >54
     */


    @Test
    public void filterImpressionLog_byageMore(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(60972, controller.filterImpressionLog(filter).size());
    }

    @Test
    public void filterServerLog_byageMore(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(1906, controller.filterServerLog(filter).size());
    }


    @Test
    public void filterClickLog_byageMore(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(1906, controller.filterClickLog(filter).size());
    }


    /**
     *
     *      Testing filters for multiple age
     *
     */

    @Test
    public void filterImpressionLog_byageMultiple(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(158022, controller.filterImpressionLog(filter).size());
    }

    @Test
    public void filterServerLog_byageMultiple(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(4891, controller.filterServerLog(filter).size());
    }


    @Test
    public void filterClickLog_byageMultiple(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        ageGroup.add(">54");
        ageGroup.add("<25");
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(4891, controller.filterClickLog(filter).size());
    }


    /**
     *
     *      Testing filters by gender
     *
     */


    @Test
    public void filterImpressionLog_byGender(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = "Female";
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(324635, controller.filterImpressionLog(filter).size());
    }

    @Test
    public void filterServerLog_byGender(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = "Male";
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(7988, controller.filterServerLog(filter).size());
    }

    @Test
    public void filterClickLog_byGender(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = "Male";
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(7988, controller.filterClickLog(filter).size());
    }

    @Test
    public void filterImpressionLog_byAllGender(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(486104, controller.filterImpressionLog(filter).size());
    }


/**
 *
 *      Filter by context
 *
 */



    @Test
    public void filterImpressionLog_byAllContext(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        context.add("News");
        context.add("Social Media");
        context.add("Blog");
        context.add("Shopping");
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);
        assertEquals(486104, controller.filterImpressionLog(filter).size());
    }


    @Test
    public void filterImpressionLog_byContext(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        context.add("News");
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(Impression i: controller.getCampaign().getImpressions()){
            if(i.getContext().equals("News")){
                a++;
            }
        }
        assertEquals(a, controller.filterImpressionLog(filter).size());
    }


    @Test
    public void filterServerLog_byContext(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        context.add("News");
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(ServerEntry i: controller.getCampaign().getServerEntries()){
            if(i.getContext().equals("News")){
                a++;
            }
        }
        assertEquals(a, controller.filterServerLog(filter).size());
    }


    @Test
    public void filterClickLog_byContext(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        context.add("News");
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(Click i: controller.getCampaign().getClicks()){
            if(i.getContext().equals("News")){
                a++;
            }
        }
        assertEquals(a, controller.filterClickLog(filter).size());
    }

/**
 *
 *      Filter by income
 *
 */


    @Test
    public void filterImpressionLog_byAllIncome(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        income.add("High");
        income.add("Medium");
        income.add("Low");
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        assertEquals(486104, controller.filterImpressionLog(filter).size());
    }

    @Test
    public void filterImpressionLog_byIncome(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        income.add("High");
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(Impression i: controller.getCampaign().getImpressions()){
            if(i.getIncome().equals("High")){
                a++;
            }
        }
        assertEquals(a, controller.filterImpressionLog(filter).size());
    }


    @Test
    public void filterServerLog_byIncome(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        income.add("High");
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(ServerEntry i: controller.getCampaign().getServerEntries()){
            if(i.getIncome().equals("High")){
                a++;
            }
        }
        assertEquals(a, controller.filterServerLog(filter).size());
    }






    @Test
    public void filterClickLog_byIncome(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryDate = null;
        LocalDateTime exitDate = null;
        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        income.add("High");
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();
        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        int a = 0;
        for(Click i: controller.getCampaign().getClicks()){
            if(i.getIncome().equals("High")){
                a++;
            }
        }
        assertEquals(a, controller.filterClickLog(filter).size());
    }



    //------- TEST GRAPH DATA POINTS ------------------------------------------------------------------------------

    /**
     *
     *      Tests for writing the graphs, checking between boundary dates and times for the graphs
     *
     */

    @Test
    public void datapointsDates(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();


        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:00:00", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-04 00:00:16", formatter);

        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        LineGraph line = new LineGraph(Metric.TOTAL_IMPRESSIONS, TimeInterval.DAY, controller, filter);

        ArrayList<DataPoint> datapoints = line.getDataPoints();

        // expecting three whole days between these two dates
        // inclusive rather than exclusive
        assertEquals(3, datapoints.size());
        assertEquals(33320, datapoints.get(0).getMetric());
        //checking first date is date expected from the dataset
        assertEquals("2015-01-01T12:00:02", datapoints.get(0).getStartTime().toString());
        assertEquals(33978, datapoints.get(1).getMetric());
    }


    @Test
    public void datapointsDatesBoundaryDay(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ArrayList<String> context = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        String gender = null;
        ArrayList<String> ageGroup = new ArrayList<>();


        LocalDateTime entryDate = LocalDateTime.parse("2015-01-01 12:00:02", formatter);
        LocalDateTime exitDate = LocalDateTime.parse("2015-01-04 00:00:16", formatter);

        Filter filter = new Filter(entryDate, exitDate, context, gender, ageGroup, income);

        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        LineGraph line = new LineGraph(Metric.TOTAL_IMPRESSIONS, TimeInterval.DAY, controller, filter);

        ArrayList<DataPoint> datapoints = line.getDataPoints();

        // expecting two whole days between these two dates
        // inclusive of the first date
        assertEquals(3, datapoints.size());
        assertEquals(33320, datapoints.get(0).getMetric());
        //checking first date is date expected from the dataset
        assertEquals("2015-01-01T12:00:02", datapoints.get(0).getStartTime().toString());
        assertEquals(33978, datapoints.get(1).getMetric());
    }

    // testing dates are what are expected when you have increased them using the functions
    @Test
    public void testDates(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDateTime;
        LocalDateTime startDateTime = LocalDateTime.parse("2015-01-01 12:00:02", formatter);
        endDateTime = startDateTime.plusHours(1);

        assertEquals("2015-01-01T13:00:02",endDateTime.toString());

    }


    // ------ TEST DRIVEN DEVELOPMENT ----------------------------------------------------------------------------------




    @Test
    public void saveCampaign(){
            Controller controller = new Controller();
            ArrayList list1 = new ArrayList();
            ArrayList list2 = new ArrayList();
            ArrayList list3 = new ArrayList();
            Filter filter = new Filter(null,null,list1,null, list2,list3 );
            controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
            controller.saveCampaign("test", filter);
            File camp = new File("/Users/danielraad/Desktop/UniCourses/SEG/Code/SEng-Group-Project/AdAuction/Campaign/test");
            assertEquals(true, camp.exists());
    }



    @Test
    public void getCampaign(){
        Controller controller = new Controller();
        controller.loadNewCampaign("/Users/danielraad/Desktop/2_week_campaign_2/server_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/click_log.csv", "/Users/danielraad/Desktop/2_week_campaign_2/impression_log.csv", 1);
        assertTrue(controller.getCampaign() instanceof  Campaign);
        assertNotNull(controller.getCampaign());
    }

    @Test
    public void loadSavedCampaign(){
        Controller controller = new Controller();
        controller.loadCampaign("test");
        Assert.assertNotNull(controller.getCampaign().getClicks());
    }



    // ---CALCULATION TESTING -------------------------------------------------------------------------------------------


    /**
     * Tests for the calculations when an array is being passed through
     */

    @Test
    public void totalCost(){
        Controller myController = new Controller();
        assertEquals(110.0, myController.calcTotalCost(impressions, clicks) );

    }

    @Test
    public void numImpressions(){
        Controller myController = new Controller();
        assertEquals(10, myController.calcImpressions(impressions));

    }

    @Test
    public void impCost(){
        Controller myController = new Controller();
        assertEquals(55.0, myController.calcTotalImpCost(impressions));

    }



    @Test
    public void numClicks(){
        Controller myController = new Controller();
        assertEquals(10, myController.calcClicks(clicks));
    }

    @Test
    public void clickCost(){
        Controller myController = new Controller();
        assertEquals(55.0, myController.calcTotalClickCost(clicks));

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
        assertEquals(1.0, myController.calcCTR(clicks, impressions));
    }


    @Test
    public void calcCPA(){
        Controller myController = new Controller();
        assertEquals(55.0, myController.calcCPA(impressions, clicks, serverEntryArrayList2));
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
        assertEquals(5500.0, myController.calcCPM(impressions));
    }




   // ---PRE DATASETS --------------------------------------------------------------------------------------------------

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
