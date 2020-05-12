import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;

public class BarGraph {
    private ArrayList<Bar> bars;
    private Metric metric;
    private BarChartType barChartType;
    private Filter filter;

    private Controller controller;

    public BarGraph(Metric metric, BarChartType barChartType, Filter filter, Controller controller){
        this.metric = metric;
        this.barChartType = barChartType;
        this.filter = filter;
        this.controller = controller;
        calculateBars();
    }

    /**
     * Calculate bars to be displayed on chart
     */
    private void calculateBars(){
        ArrayList<Impression> impressions = controller.filterImpressionLog(filter);
        ArrayList<Click> clicks = controller.filterClickLog(filter);
        ArrayList<ServerEntry> serverEntries = controller.filterServerLog(filter);
        switch (barChartType) { //TODO pass empty arraylists if they are not needed for this metric (for performance)
            case DAY_OF_WEEK:
                bars = calculatePerDayOfWeek(impressions, clicks, serverEntries);
                break;
            case TIME_OF_DAY:
                bars = calculatePerTimeOfDay(impressions, clicks, serverEntries);
                break;
        }
    }

    private ArrayList<Bar> calculatePerTimeOfDay(ArrayList<Impression> impressions, ArrayList<Click> clicks, ArrayList<ServerEntry> serverEntries) {
        // Partition Impressions by hours
        HashMap<Integer, ArrayList<Impression>> partitionedImpressions = new HashMap<>();
        for(int hour = 0; hour < 24; hour++){
            partitionedImpressions.put(hour, new ArrayList<Impression>());
        }
        for(Impression impression : impressions){
            partitionedImpressions.get(impression.getDateTime().getHour()).add(impression);
        }

        // Partition Clicks by hours
        HashMap<Integer, ArrayList<Click>> partitionedClicks = new HashMap<>();
        for(int hour = 0; hour < 24; hour++){
            partitionedClicks.put(hour, new ArrayList<Click>());
        }
        for(Click click : clicks){
            partitionedClicks.get(click.getDateTime().getHour()).add(click);
        }

        // Partition ServerEntries by hours
        HashMap<Integer, ArrayList<ServerEntry>> partitionedServerEntries = new HashMap<>();
        for(int hour = 0; hour < 24; hour++){
            partitionedServerEntries.put(hour, new ArrayList<ServerEntry>());
        }
        for(ServerEntry serverEntry : serverEntries){
            partitionedServerEntries.get(serverEntry.getEntryDate().getHour()).add(serverEntry);
        }

        // Calculate Bars based on Metric
        ArrayList<Bar> bars = new ArrayList<Bar>();
        switch (metric) {
            case TOTAL_IMPRESSIONS:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(partitionedImpressions.get(hour).size(), "" + hour));
                }
                break;
            case TOTAL_IMPRESSION_COST:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcTotalImpCost(partitionedImpressions.get(hour)), "" + hour));
                }
                break;
            case TOTAL_CLICKS:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(partitionedClicks.get(hour).size(), "" + hour));
                }
                break;
            case TOTAL_CLICK_COST:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcTotalClickCost(partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case TOTAL_COST:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcTotalCost(partitionedImpressions.get(hour), partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case TOTAL_CONVERSIONS:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcConversions(partitionedServerEntries.get(hour)), "" + hour));
                }
                break;
            case CONVERSION_RATE:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcConvRate(partitionedServerEntries.get(hour), partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case BOUNCES:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcBounces(partitionedServerEntries.get(hour)), "" + hour));
                }
                break;
            case BOUNCE_RATE:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcBounceRate(partitionedServerEntries.get(hour), partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case TOTAL_UNIQUES:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcUniques(partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case CLICK_THROUGH_RATE:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcCTR(partitionedClicks.get(hour), partitionedImpressions.get(hour)), "" + hour));
                }
                break;
            case COST_PER_AQUISITION:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcCPA(partitionedImpressions.get(hour), partitionedClicks.get(hour), partitionedServerEntries.get(hour)), "" + hour));
                }
                break;
            case COST_PER_CLICK:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcCPC(partitionedClicks.get(hour)), "" + hour));
                }
                break;
            case COST_PER_1000_IMPRESSIONS:
                for(int hour = 0; hour < 24; hour++){
                    bars.add(new Bar<>(controller.calcCPM(partitionedImpressions.get(hour)), "" + hour));
                }
                break;
        }

        return bars;
    }

    private ArrayList<Bar> calculatePerDayOfWeek(ArrayList<Impression> impressions, ArrayList<Click> clicks, ArrayList<ServerEntry> serverEntries) {
        // Partition Impressions by days of the week
        HashMap<DayOfWeek, ArrayList<Impression>> partitionedImpressions = new HashMap<>();
        for(DayOfWeek day : DayOfWeek.values()){
            partitionedImpressions.put(day, new ArrayList<Impression>());
        }
        for(Impression impression : impressions){
            partitionedImpressions.get(impression.getDateTime().getDayOfWeek()).add(impression);
        }

        // Partition Clicks by days of the week
        HashMap<DayOfWeek, ArrayList<Click>> partitionedClicks = new HashMap<>();
        for(DayOfWeek day : DayOfWeek.values()){
            partitionedClicks.put(day, new ArrayList<Click>());
        }
        for(Click click : clicks){
            partitionedClicks.get(click.getDateTime().getDayOfWeek()).add(click);
        }

        // Partition ServerEntries by days of the week
        HashMap<DayOfWeek, ArrayList<ServerEntry>> partitionedServerEntries = new HashMap<>();
        for(DayOfWeek day : DayOfWeek.values()){
            partitionedServerEntries.put(day, new ArrayList<ServerEntry>());
        }
        for(ServerEntry serverEntry : serverEntries){
            partitionedServerEntries.get(serverEntry.getEntryDate().getDayOfWeek()).add(serverEntry);
        }

        // Calculate Bars based on Metric
        ArrayList<Bar> bars = new ArrayList<Bar>();
        switch (metric) {
            case TOTAL_IMPRESSIONS:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(partitionedImpressions.get(day).size(), day.toString()));
                }
                break;
            case TOTAL_IMPRESSION_COST:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcTotalImpCost(partitionedImpressions.get(day)), day.toString()));
                }
                break;
            case TOTAL_CLICKS:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(partitionedClicks.get(day).size(), day.toString()));
                }
                break;
            case TOTAL_CLICK_COST:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcTotalClickCost(partitionedClicks.get(day)), day.toString()));
                }
                break;
            case TOTAL_COST:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcTotalCost(partitionedImpressions.get(day), partitionedClicks.get(day)), day.toString()));
                }
                break;
            case TOTAL_CONVERSIONS:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcConversions(partitionedServerEntries.get(day)), day.toString()));
                }
                break;
            case CONVERSION_RATE:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcConvRate(partitionedServerEntries.get(day), partitionedClicks.get(day)), day.toString()));
                }
                break;
            case BOUNCES:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcBounces(partitionedServerEntries.get(day)), day.toString()));
                }
                break;
            case BOUNCE_RATE:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcBounceRate(partitionedServerEntries.get(day), partitionedClicks.get(day)), day.toString()));
                }
                break;
            case TOTAL_UNIQUES:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcUniques(partitionedClicks.get(day)), day.toString()));
                }
                break;
            case CLICK_THROUGH_RATE:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcCTR(partitionedClicks.get(day), partitionedImpressions.get(day)), day.toString()));
                }
                break;
            case COST_PER_AQUISITION:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcCPA(partitionedImpressions.get(day), partitionedClicks.get(day), partitionedServerEntries.get(day)), day.toString()));
                }
                break;
            case COST_PER_CLICK:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcCPC(partitionedClicks.get(day)), day.toString()));
                }
                break;
            case COST_PER_1000_IMPRESSIONS:
                for(DayOfWeek day : DayOfWeek.values()){
                    bars.add(new Bar<>(controller.calcCPM(partitionedImpressions.get(day)), day.toString()));
                }
                break;
        }

        return bars;
    }

    public ArrayList<Bar> getBars() {
        return bars;
    }
}