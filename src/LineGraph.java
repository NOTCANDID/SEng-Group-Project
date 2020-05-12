import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contains all data and methods needed to display the line chart.
 */
public class LineGraph {

    private Controller controller;

    private Metric metric;
    private TimeInterval timeInterval;
    private ArrayList dataPoints;

    private Filter filter;

    public LineGraph(Metric metric, TimeInterval timeInterval, Controller controller, Filter filter){
        this.metric = metric;
        this.timeInterval = timeInterval;
        this.controller = controller;
        this.filter = filter;
        calculateDataPoints();
    }

    public void saveGraph(String filename){

    }

    //--CALCULATIONS----------------------------------------------------------------------------------------------------

    /**
     * Use campaign's reference to get Campaign's data and calculation methods.
     * Apply filters to campaign's data
     * Calculates data points to be displayed on the chart.
     * dataPoints' Pair contains value of the metric computed over some time and that time period
     * (e.g. <30 bounces, 9am - 10am on 29/02/2020>)
     * TriggerUpdate at the end so the View can get the dataPoints.
     */
    private void calculateDataPoints(){
        switch (metric){
            case TOTAL_IMPRESSIONS:
                dataPoints = calculateTotalImpressions();
                break;
            case TOTAL_IMPRESSION_COST:
                dataPoints = calculateImpressionCosts();
                break;
            case TOTAL_CLICKS:
                dataPoints = calculateTotalClicks();
                break;
            case TOTAL_CLICK_COST:
                dataPoints = calculateClickCosts();
                break;
            case TOTAL_COST:
                dataPoints = calculateTotalCosts();
                break;
            case TOTAL_CONVERSIONS:
                dataPoints = calculateTotalConversions();
                break;
            case CONVERSION_RATE:
                dataPoints = calculateConversionRates();
                break;
            case BOUNCES:
                dataPoints = calculateBounces();
                break;
            case BOUNCE_RATE:
                dataPoints = calculateBounceRates();
                break;
            case TOTAL_UNIQUES:
                dataPoints = calculateTotalUniques();
                break;
            case CLICK_THROUGH_RATE:
                dataPoints = calculateCTRs();
                break;
            case COST_PER_AQUISITION:
                dataPoints = calculateCPAs();
                break;
            case COST_PER_CLICK:
                dataPoints = calculateCPCs();
                break;
            case COST_PER_1000_IMPRESSIONS:
                dataPoints = calculateCPMs();
               // CLICK_THROUGH_RATE,
                //        COST_PER_AQUISITION,
                //        COST_PER_CLICK,
                //        COST_PER_1000_IMPRESSIONS
                // impression
                break;
        }

    }

    private ArrayList<DataPoint<Integer, LocalDateTime>> calculateTotalImpressions(){
        ArrayList<DataPoint<Integer, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Integer, LocalDateTime>>();
        ArrayList<Impression> impressionLog = controller.filterImpressionLog(filter);
        Collections.sort(impressionLog);
        if(impressionLog.size() == 0){
            return dataPoints;
        }
        LocalDateTime startDateTime = impressionLog.get(0).getDateTime();

        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Impression> impressions = new ArrayList<Impression>();
        while(impressionLog.size() > i){
            Impression impression = impressionLog.get(i);
            if(impression.getDateTime().isBefore(endDateTime)){
                impressions.add(impression);
            }
            else{
                dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcImpressions(impressions), startDateTime));

                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                impressions = new ArrayList<Impression>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcImpressions(impressions), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateImpressionCosts() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<Impression> impressionLog = controller.filterImpressionLog(filter);
        Collections.sort(impressionLog);

        if(impressionLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = impressionLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Impression> impressions = new ArrayList<Impression>();
        while(impressionLog.size() > i){
            Impression impression = impressionLog.get(i);
            if(impression.getDateTime().isBefore(endDateTime)){
                impressions.add(impression);
            }
            else{
                dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcTotalImpCost(impressions), startDateTime));

                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                impressions = new ArrayList<Impression>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcTotalImpCost(impressions), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Integer, LocalDateTime>> calculateTotalClicks() {
        ArrayList<DataPoint<Integer, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Integer, LocalDateTime>>();
        ArrayList<Click> clickLog = controller.filterClickLog(filter);
        clickLog.sort(getClickComparator());

        if(clickLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = clickLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Click> clicks = new ArrayList<Click>();
        while(clickLog.size() > i){
            Click click = clickLog.get(i);
            if(click.getDateTime().isBefore(endDateTime)){
                clicks.add(click);
            }
            else{
                dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcClicks(clicks), startDateTime));
                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                clicks = new ArrayList<Click>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcClicks(clicks), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateClickCosts() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<Click> clickLog = controller.filterClickLog(filter);
        clickLog.sort(getClickComparator());

        if(clickLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = clickLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Click> clicks = new ArrayList<Click>();
        while(clickLog.size() > i){
            Click click = clickLog.get(i);
            if(click.getDateTime().isBefore(endDateTime)){
                clicks.add(click);
            }
            else{
                dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcTotalClickCost(clicks), startDateTime));
                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                clicks = new ArrayList<Click>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcTotalClickCost(clicks), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateTotalCosts() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<DataPoint<Double, LocalDateTime>> clickCosts = calculateClickCosts();
        ArrayList<DataPoint<Double, LocalDateTime>> impressionCosts = calculateImpressionCosts();

        for(int i = 0; i < clickCosts.size(); i++){
            dataPoints.add(new DataPoint<Double, LocalDateTime>((clickCosts.get(i).getMetric() + impressionCosts.get(i).getMetric()), clickCosts.get(i).getStartTime()));
        }
        return dataPoints;
    }

    private ArrayList<DataPoint<Integer, LocalDateTime>> calculateTotalConversions() {
        ArrayList<DataPoint<Integer, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Integer, LocalDateTime>>();
        ArrayList<ServerEntry> serverEntriesLog = controller.filterServerLog(filter);
        Collections.sort(serverEntriesLog);

        if(serverEntriesLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = serverEntriesLog.get(0).getEntryDate();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<ServerEntry> serverEntries = new ArrayList<ServerEntry>();
        while(serverEntriesLog.size() > i){
            ServerEntry serverEntry = serverEntriesLog.get(i);
            if(serverEntry.getEntryDate().isBefore(endDateTime)){
                serverEntries.add(serverEntry);
            }
            else{
                dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcConversions(serverEntries), startDateTime));

                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                serverEntries = new ArrayList<ServerEntry>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcConversions(serverEntries), startDateTime));
        return dataPoints;
    }

    private  ArrayList<DataPoint<Double, LocalDateTime>> calculateConversionRates() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<DataPoint<Integer, LocalDateTime>> totalConversions = calculateTotalConversions();
        ArrayList<DataPoint<Integer, LocalDateTime>> totalClicks = calculateTotalClicks();

        for(int i = 0; i < totalConversions.size(); i++){
            dataPoints.add(new DataPoint<Double, LocalDateTime>(((double)totalConversions.get(i).getMetric() / totalClicks.get(i).getMetric()), totalConversions.get(i).getStartTime()));
        }

        return dataPoints;
    }

    private ArrayList<DataPoint<Integer, LocalDateTime>> calculateBounces() {
        ArrayList<DataPoint<Integer, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Integer, LocalDateTime>>();
        ArrayList<ServerEntry> serverEntriesLog = controller.filterServerLog(filter);

        if(serverEntriesLog.size() == 0){
            return dataPoints;
        }

        Collections.sort(serverEntriesLog);
        LocalDateTime startDateTime = serverEntriesLog.get(0).getEntryDate(); //TODO handle index out of bound exception

        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<ServerEntry> serverEntries = new ArrayList<ServerEntry>();
        while(serverEntriesLog.size() > i){
            ServerEntry serverEntry = serverEntriesLog.get(i);
            if(serverEntry.getEntryDate().isBefore(endDateTime)){
                serverEntries.add(serverEntry);
            }
            else{
                dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcBounces(serverEntries), startDateTime));

                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                serverEntries = new ArrayList<ServerEntry>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcBounces(serverEntries), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateBounceRates() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<DataPoint<Integer, LocalDateTime>> bounces = calculateBounces();
        ArrayList<DataPoint<Integer, LocalDateTime>> impressionCosts = calculateTotalImpressions();

        for(int i = 0; i < bounces.size(); i++){
            dataPoints.add(new DataPoint<Double, LocalDateTime>(((double)bounces.get(i).getMetric() / impressionCosts.get(i).getMetric()), bounces.get(i).getStartTime()));
        }
        return dataPoints;
    }

    private ArrayList<DataPoint<Integer, LocalDateTime>> calculateTotalUniques() {
        ArrayList<DataPoint<Integer, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Integer, LocalDateTime>>();
        ArrayList<Click> clickLog = controller.filterClickLog(filter);
        clickLog.sort(getClickComparator());

        if(clickLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = clickLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Click> clicks = new ArrayList<Click>();
        while(clickLog.size() > i){
            Click click = clickLog.get(i);
            if(click.getDateTime().isBefore(endDateTime)){
                clicks.add(click);
            }
            else{
                dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcUniques(clicks), startDateTime));
                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                clicks = new ArrayList<Click>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Integer, LocalDateTime>(controller.calcUniques(clicks), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateCTRs() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<DataPoint<Integer, LocalDateTime>> clicks = calculateTotalClicks();
        ArrayList<DataPoint<Integer, LocalDateTime>> impressions = calculateTotalImpressions();

        for(int i = 0; i < clicks.size(); i++){ //TODO add protection when arraylists have different sizes
            dataPoints.add(new DataPoint<Double, LocalDateTime>(((double)clicks.get(i).getMetric() / impressions.get(i).getMetric()), clicks.get(i).getStartTime()));
        }
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateCPAs() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<DataPoint<Double, LocalDateTime>> totalCosts = calculateTotalCosts();
        ArrayList<DataPoint<Integer, LocalDateTime>> totalConversions = calculateTotalConversions();

        for(int i = 0; i < totalCosts.size(); i++){
            dataPoints.add(new DataPoint<Double, LocalDateTime>(((double)totalCosts.get(i).getMetric() / totalConversions.get(i).getMetric()), totalCosts.get(i).getStartTime()));
        }

        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateCPCs() {
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<Click> clickLog = controller.filterClickLog(filter);
        clickLog.sort(getClickComparator());

        if(clickLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = clickLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Click> clicks = new ArrayList<Click>();
        while(clickLog.size() > i){
            Click click = clickLog.get(i);
            if(click.getDateTime().isBefore(endDateTime)){
                clicks.add(click);
            }
            else{
                dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcCPC(clicks), startDateTime));
                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                clicks = new ArrayList<Click>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcCPC(clicks), startDateTime));
        return dataPoints;
    }

    private ArrayList<DataPoint<Double, LocalDateTime>> calculateCPMs(){
        ArrayList<DataPoint<Double, LocalDateTime>> dataPoints = new ArrayList<DataPoint<Double, LocalDateTime>>();
        ArrayList<Impression> impressionLog = controller.filterImpressionLog(filter);
        Collections.sort(impressionLog);

        if(impressionLog.size() == 0){
            return dataPoints;
        }

        LocalDateTime startDateTime = impressionLog.get(0).getDateTime();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        int i = 0;
        ArrayList<Impression> impressions = new ArrayList<>();
        while(impressionLog.size() > i){
            Impression impression = impressionLog.get(i);
            if (impression.getDateTime().isBefore(endDateTime)){
                impressions.add(impression);
            }
            else {
                dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcCPM(impressions), startDateTime));
                startDateTime = endDateTime;
                endDateTime = getEndDateTime(startDateTime);
                impressions = new ArrayList<Impression>();
                i--;
            }
            i++;
        }
        dataPoints.add(new DataPoint<Double, LocalDateTime>(controller.calcCPM(impressions), startDateTime));
        return dataPoints;
    }

    /**
     * Increments the startDate of the interval by the amount corresponding to timeInterval
     * @param startDateTime starting dateTime of the interval
     * @return ending dateTime of the interval
     */
    private LocalDateTime getEndDateTime(LocalDateTime startDateTime){
        LocalDateTime endDateTime;
        switch(timeInterval){
            case HOUR:
                endDateTime = startDateTime.plusHours(1);
                break;
            case DAY:
                endDateTime = startDateTime.plusDays(1);
                break;
            case WEEK:
                endDateTime = startDateTime.plusWeeks(1);
                break;
            case MONTH:
                endDateTime = startDateTime.plusMonths(1);
                break;
            default:
                endDateTime = startDateTime; //TODO handle this case differently
                 break;
        }
        return endDateTime;
    }

    public ArrayList<DataPoint> getDataPoints(){ return dataPoints; }


    private Comparator<Click> getClickComparator(){
        Comparator<Click> dateComparator = new Comparator<Click>(){

            @Override
            public int compare(Click c1, Click c2) {
                return c1.getDateTime().compareTo(c2.getDateTime());
            }
        };
        return dateComparator;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}