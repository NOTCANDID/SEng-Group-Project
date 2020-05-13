import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Translates user actions into operations on the campaign.
 */
public class Controller {

	private Campaign campaign;
	private GUI gui;
	private int bounceDefinition = 1;

	public static final String AD_AUCTION_FOLDER = "AdAuction";
	public static final String CAMPAIGN_FOLDER = "Campaign";
	public static final String IMAGE_FOLDER = "Images";
	public static final String CLICK_LOG_NAME = "click_log.csv";
	public static final String SERVER_LOG_NAME = "server_log.csv";
	public static final String IMPRESSION_LOG_NAME = "impression_log.csv";

	public void setGUI(GUI gui){

		this.gui = gui;
	}

	public Campaign getCampaign(){
		return this.campaign;
	}

	/**
	 * Create new campaign by loading log Files
	 * @param bounceDefinition - how bounces are registered for this campaign
	 */
	public void loadNewCampaign(String serverFilePath, String clickFilePath, String impressionFilePath, int bounceDefinition){
		this.bounceDefinition = bounceDefinition;
		campaign = new Campaign(serverFilePath, clickFilePath, impressionFilePath, this);
		campaign.setName("Untitled Campaign");
	}

	/**
	 * 	Create old campaign with a name
	 */

	public void loadOldCampaign(String serverFilePath, String clickFilePath, String impressionFilePath, int bounceDefinition, String name){
		this.bounceDefinition = bounceDefinition;
		campaign = new Campaign(serverFilePath, clickFilePath, impressionFilePath, this);
		campaign.setName(name);
	}



//save the log file 
    public void saveCampaign(String campaignName, Filter filter){
		Path path = Paths.get(System.getProperty("user.dir") + File.separator + AD_AUCTION_FOLDER + File.separator + CAMPAIGN_FOLDER + File.separator + campaignName);

		// Create directories if the don't yet exist
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				//fail to create directory
				GUI.displayError(e.getMessage());
			}
		}

		// Copy campaigns

		File serverSource = new File(campaign.serverPath);
		File clickSource = new File(campaign.clickPath);
		File impressionSource = new File(campaign.impressionPath);

		File serverDestination = new File(path + File.separator + SERVER_LOG_NAME);
		File clickDestination = new File(path + File.separator + CLICK_LOG_NAME);
		File impressionDestination = new File(path + File.separator + IMPRESSION_LOG_NAME);

		HashMap<File, File> sourcesAndDestinations = new HashMap<>();
		sourcesAndDestinations.put(serverSource, serverDestination);
		sourcesAndDestinations.put(clickSource,clickDestination);
		sourcesAndDestinations.put(impressionSource, impressionDestination);

		for(Map.Entry<File, File> sourceAndDestination : sourcesAndDestinations.entrySet()){
			try{
				FileInputStream fis = new FileInputStream(sourceAndDestination.getKey());
				FileOutputStream fos = new FileOutputStream(sourceAndDestination.getValue());

				byte[] buffer = new byte[1024];
				int length;

				while ((length = fis.read(buffer)) > 0) {

					fos.write(buffer, 0, length);
				}
			} catch (IOException e1) {
				GUI.displayError(e1.getMessage());
			}
		}

		// Create config file
		File file = new File( path + File.separator + "config.txt");
		try{
			String str = Integer.toString(bounceDefinition);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
			writer.write(str);
			if(filter != null) {

				String dateFrom = "";
				String dateTo = "";
				if (filter.getStartDate() != null) {
					dateFrom = filter.getStartDate().toString();
				}

				if (filter.getEndDate() != null) {
					dateTo = filter.getEndDate().toString();
				}

				String context = "Context:";
				for (String c : filter.getContexts()) {
					context += c + ",";
				}
				String gender = "Gender:" + filter.getGender();
				String ageGroup = "Age:";
				for (String age : filter.getAgeGroups()) {
					ageGroup += age + ",";
				}
				String income = "Income:";
				for (String inc : filter.getIncomes()) {
					income += inc + ",";
				}


				writer.newLine();
				writer.write(dateFrom);
				writer.newLine();
				writer.write(dateTo);
				writer.newLine();
				writer.write(context);
				writer.newLine();
				writer.write(gender);
				writer.newLine();
				writer.write(ageGroup);
				writer.newLine();
				writer.write(income);

			}
			writer.close();
		}
		catch (IOException e){
			GUI.displayError(e.getMessage());
		}

    }

    public Filter loadCampaign(String campaignName){
		Path path = Paths.get(System.getProperty("user.dir") + File.separator + AD_AUCTION_FOLDER + File.separator + CAMPAIGN_FOLDER + File.separator + campaignName);
	    try {
			BufferedReader reader = new BufferedReader(new FileReader(path + File.separator + "config.txt"));
			String line = reader.readLine();
			String[] list = line.split(" ");
	    	loadOldCampaign(path + File.separator + SERVER_LOG_NAME, path + File.separator + CLICK_LOG_NAME, path + File.separator + IMPRESSION_LOG_NAME, Integer.parseInt(list[0]), campaignName);


	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			ArrayList<String> filterIncomes = new ArrayList();
			ArrayList<String> filterAge = new ArrayList();
			ArrayList<String> filterContext = new ArrayList();
			String gender = null;
			Filter filter;
			String dateFrom;
			if((dateFrom = reader.readLine()) != null) {

				String[] from = dateFrom.split("T");
				String date1 = from[0];
				LocalDateTime startTime = LocalDate.parse(date1, formatter).atStartOfDay();

				String dateTo = reader.readLine();
				String[] to = dateTo.split("T");
				String dateTo1 = to[0];
				LocalDateTime endTime = LocalDate.parse(dateTo1, formatter).atTime(23, 59, 59);

				String nextLine;


				while ((nextLine = reader.readLine()) != null) {
					String[] strFilter = nextLine.split(":");
					switch (strFilter[0]) {
						case "Income":
							filterIncomes = new ArrayList<>();
							String[] incomes = strFilter[1].split(",");
							for (String inc : incomes) {
								filterIncomes.add(inc);
							}
							break;
						case "Age":
							filterAge = new ArrayList<>();
							String[] ages = strFilter[1].split(",");
							for (String a : ages) {
								filterAge.add(a);
							}
							break;
						case "Gender":
							gender = strFilter[1];
							break;
						case "Context":
							filterContext = new ArrayList<>();
							String[] contexts = strFilter[1].split(",");
							for (String c : contexts) {
								filterContext.add(c);
							}
							break;
					}
				}
				filter = new Filter(startTime, endTime, filterContext, gender, filterAge, filterIncomes);
			} else {
				LocalDateTime startTime = LocalDate.parse("2015-01-01", formatter).atStartOfDay();
				LocalDateTime endTime = LocalDate.parse("2015-01-14",formatter).atTime(23, 59, 59);
				filter = new Filter(startTime, endTime, filterContext, gender, filterAge, filterIncomes);

			}
			return filter;
        }catch (FileNotFoundException e){
			GUI.displayError(e.getMessage());
        }catch (IOException e){
			GUI.displayError(e.getMessage());
        }

	    return null;
    }

	public double getBounceRate(){
		return campaign.getBounceRate();
	}

	public int getTotalImpressions(){
		return campaign.getTotalImpressions();
	}

	public int getTotalClicks(){
		return campaign.getTotalClicks();
	}

	public int getTotalUnique(){
		return campaign.getTotalUnique();
	}

	public int getTotalBounces(){
		return campaign.getTotalBounces();
	}

	public int getTotalConversions(){
		return campaign.getTotalConversions();
	}

	public double getCTR(){
		return campaign.getCTR();
	}

	public double getCPA(){
		return campaign.getCPA();
	}

	public double getCPC(){
		return campaign.getCPC();
	}

	public double getCPM(){
		return campaign.getCPM();
	}

	public double getTotalCost(){
		return campaign.getTotalCost();
	}

	public double getConversionRate(){
		return campaign.getConversionRate();
	}

	/**
	 * @param metric - values shown on y axis
	 * @param timeInterval - interval over which each data point is computed
	 * @param filter - list of filters
	 */
	public LineGraph createLineGraph(Metric metric, TimeInterval timeInterval,  Filter filter){
		return new LineGraph(metric, timeInterval, this, filter);
	}

	/**
	 * @param noBars - number of bars/classes in the histogram
	 * @param accuracy - number of decimal places the boundaries of bars are rounded to
	 * @param filter - list of filters
	 */
	public Histogram createHistogram(int noBars, int accuracy, Filter filter){
		return new Histogram(this, noBars, accuracy, filter);
	}

	public BarGraph createBarChar(Metric metric, BarChartType barChartType, Filter filter){
		return new BarGraph(metric, barChartType, filter, this);
	}

	//--FILTERS---------------------------------------------------------------------------------------------------------

	public ArrayList<Impression> filterImpressionLog(Filter filter){
		//for testing
		//long startTime = System.nanoTime();

		// Predicate for 0 costs
		//TODO discuss if its needed
		//Predicate<Click> clickCostPredicate = c -> c.getClickCost() > 0;

		// Predicate for startDate
		Predicate<Impression> startDatePredicate;
		if(filter.getStartDate() != null){
			startDatePredicate = c -> c.getDateTime().isAfter(filter.getStartDate()) || c.getDateTime().isEqual(filter.getStartDate()) ; // TODO Discuss if it needs to be inclusive
		}
		else{
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<Impression> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getDateTime().isBefore(filter.getEndDate()) || c.getDateTime().isEqual(filter.getEndDate());
		}
		else{
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<Impression> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<Impression> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<Impression> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<Impression> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			incomePredicate = c -> true;
		}

		ArrayList<Impression> filteredImpressions = (ArrayList<Impression>) campaign.getImpressions().stream().filter
				(startDatePredicate.and(endDatePredicate.and(contextsPredicate).and(genderPredicate).and(ageGroupPredicate).and(incomePredicate))).collect(Collectors.toList());

		return filteredImpressions;
	}

	public ArrayList<ServerEntry> filterServerLog(Filter filter){
		//for testing
		//long startTime = System.nanoTime();

		// Predicate for 0 costs
		//TODO discuss if its needed
		//Predicate<Click> clickCostPredicate = c -> c.getClickCost() > 0;

		// Predicate for startDate
		Predicate<ServerEntry> startDatePredicate;
		if(filter.getStartDate() != null){
			startDatePredicate = c -> c.getEntryDate().isAfter(filter.getStartDate()) || c.getEntryDate().isEqual(filter.getStartDate()); // TODO Discuss if it needs to be inclusive
		}
		else{
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<ServerEntry> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getEntryDate().isBefore(filter.getEndDate()) || c.getEntryDate().isEqual(filter.getEndDate());
		}
		else{
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<ServerEntry> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<ServerEntry> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<ServerEntry> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<ServerEntry> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			incomePredicate = c -> true;
		}

		ArrayList<ServerEntry> filteredServerEntries = (ArrayList<ServerEntry>) campaign.getServerEntries().stream().filter
				(startDatePredicate.and(endDatePredicate.and(contextsPredicate).and(genderPredicate).and(ageGroupPredicate).and(incomePredicate))).collect(Collectors.toList());

		//for testing
		//long endTime = System.nanoTime();
		//System.out.println("Method took:" + (endTime - startTime) / 1000000);

		return filteredServerEntries;
	}

	public ArrayList<Click> filterClickLog(Filter filter){
		//for testing
		//long startTime = System.nanoTime();

		// Predicate for 0 costs
		//TODO discuss if its needed
		//Predicate<Click> clickCostPredicate = c -> c.getClickCost() > 0;

		// Predicate for startDate
		Predicate<Click> startDatePredicate;
		if(filter.getStartDate() != null){
			startDatePredicate = c -> c.getDateTime().isAfter(filter.getStartDate()) || c.getDateTime().isEqual(filter.getStartDate()); // TODO Discuss if it needs to be inclusive
		}
		else{
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<Click> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getDateTime().isBefore(filter.getEndDate()) || c.getDateTime().isEqual(filter.getEndDate());
		}
		else{
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<Click> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<Click> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<Click> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<Click> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			incomePredicate = c -> true;
		}

		ArrayList<Click> filteredClicks = (ArrayList<Click>) campaign.getClicks().stream().filter
				(startDatePredicate.and(endDatePredicate.and(contextsPredicate).and(genderPredicate).and(ageGroupPredicate).and(incomePredicate))).collect(Collectors.toList());

		//for testing
		//long endTime = System.nanoTime();
		//System.out.println("Method took:" + (endTime - startTime) / 1000000);

		return filteredClicks;
	}

	/**
	 * Checks if there exists a context in a list of contexts (filters) that matches given context
	 * @param context - given context
	 * @return - true if there is a match
	 */
	private boolean matchContext(String context, Filter filter){
		for(String c : filter.getContexts()){
			if(c.equals(context))
				return true;
		}
		return false;
	}

	/**
	 * Checks if there exists an ageGroup in a list of ageGroups that matches given ageGroup
	 * @param ageGroup - given ageGroup
	 * @return - true if there is a match
	 */
	private boolean matchAgeGroup(String ageGroup, Filter filter){
		for(String a : filter.getAgeGroups()){
			if(a.equals(ageGroup))
				return true;
		}
		return false;
	}

	/**
	 * Checks if there exists an income in a list of incomes that matches given income
	 * @param income - given income
	 * @return - true if there is a match
	 */
	private boolean matchIncome(String income, Filter filter){
		for(String i : filter.getIncomes()){
			if(i.equals(income))
				return true;
		}
		return false;
	}

	//--Calculations---------------------------------------------------------------------------------------------------------
	public int calcImpressions(ArrayList<Impression> impressionArray){
		int mytotalImpressions = impressionArray.size();
		return  mytotalImpressions;
	}

	public double calcTotalImpCost(ArrayList<Impression> impressionArray){
		double mytotalImpCost = 0;
		for (Impression imp: impressionArray) {
			mytotalImpCost += imp.getImpressionCost();
		}
		return (double) mytotalImpCost;
	}

	public int calcClicks(ArrayList<Click> clickArray){
		int mytotalClicks = clickArray.size();
		return mytotalClicks;
	}


	public double calcTotalClickCost(ArrayList<Click> clickArray){
		double mytotalClickCost = 0;
		for (Click click: clickArray) {
			mytotalClickCost += click.getClickCost();
		}
		return  mytotalClickCost;
	}


	public double calcTotalCost(ArrayList<Impression> impressionArray, ArrayList<Click> clickArray){
		double totalClickCost = 0;
		double totalImpCost = 0;
		for (Impression imp: impressionArray) {
			totalImpCost += imp.getImpressionCost();
		}
		for (Click click: clickArray) {
			totalClickCost += click.getClickCost();
		}


		return totalClickCost + totalImpCost;
	}

	public int calcConversions(ArrayList<ServerEntry> serverEntryArray){

		int mytotalConversions = 0;
		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getConversion().equals("Yes")){
				mytotalConversions += 1;
			}
		}
		return mytotalConversions;
	}

	public double calcConvRate(ArrayList<ServerEntry> serverEntryArray, ArrayList<Click> clickArray){

		int totalConversions = 0;
		int totalClicks = 0;
		totalClicks = clickArray.size();
		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getConversion().equals("Yes")) {
				totalConversions += 1;
			}
		}

		if(totalClicks <= 0 || totalConversions <= 0){
			return 0;
		}

		double conversionRate =   (double) totalConversions / totalClicks;
		return  conversionRate;
	}

	public int calcBounces(ArrayList<ServerEntry> serverEntryArray){

		int myBounces = 0;
		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getPagesViewed() <= bounceDefinition){
				myBounces += 1;
			}
		}

		return myBounces;
	}

	public double calcBounceRate(ArrayList<ServerEntry> serverEntryArray, ArrayList<Click> clickArray){
		int bounces = 0;

		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getPagesViewed() <= bounceDefinition){
				bounces += 1;
			}
		}
		int totalClicks = clickArray.size();
		if(totalClicks <= 0 || bounces <= 0){
			return 0;
		}
		double bounceRate = (double) bounces / totalClicks;
		return bounceRate;
	}

	public int calcUniques(ArrayList<Click> clickArray){
		HashSet<String> uniquesHashset = new HashSet<String>();
		for (Click click: clickArray) {
			uniquesHashset.add(click.getID());
		}
		int totalUniques = uniquesHashset.size();
		return totalUniques;
	}

	public double calcCTR(ArrayList<Click> clickArray, ArrayList<Impression> impressionArray){

		if(clickArray.size() <= 0 || impressionArray.size() <= 0){
			return 0;
		}

		double CTR =  ((double) clickArray.size() / impressionArray.size());
		return  CTR;
	}

	public double calcCPA(ArrayList<Impression> impressionArray, ArrayList<Click> clickArray, ArrayList<ServerEntry> serverEntryArray){
		double totalImpCost = 0;
		double totalClickCost = 0;
		int totalConversions = 0;
		double totalCost = 0;
		for (Impression imp: impressionArray) {
			totalImpCost += imp.getImpressionCost();
		}

		for (Click click: clickArray) {
			totalClickCost += click.getClickCost();
		}

		totalCost = totalClickCost + totalImpCost;

		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getConversion().equals("Yes")){
				totalConversions += 1;
			}
		}

		if(totalCost <= 0 || totalConversions <= 0){
			return 0;
		}

		double CPA = totalCost / totalConversions;
		return CPA;
	}

	public double calcCPC(ArrayList<Click> clickArray){
		double totalClickCost = 0;
		for (Click click: clickArray) {
			totalClickCost += click.getClickCost();
		}

		if(totalClickCost <= 0 || clickArray.size() <= 0){
			return 0;
		}

		double CPC = (totalClickCost / clickArray.size());
		return CPC;
	}

	public double calcCPM(ArrayList<Impression> impressionArray){
		double totalImpCost = 0;
		for (Impression imp: impressionArray) {
			totalImpCost += imp.getImpressionCost();
		}

		if(totalImpCost <= 0 || impressionArray.size() <= 0){
			return 0;
		}

		double CPM =   ((double) totalImpCost / impressionArray.size()) * 1000;
		return CPM;
	}

	public boolean isCampaignNameFree(String name) {

		Path path = Paths.get(System.getProperty("user.dir") + File.separator + AD_AUCTION_FOLDER + File.separator + CAMPAIGN_FOLDER + File.separator);

		// Create directories if the don't yet exist
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				//fail to create directory
				GUI.displayError(e.getMessage());
			}
		}

		File[] files = new File(System.getProperty("user.dir") + File.separator + Controller.AD_AUCTION_FOLDER + File.separator + Controller.CAMPAIGN_FOLDER).listFiles();
		ArrayList<String> campaigns = new ArrayList<String>();
		for (File file : files) {
			if (file.isDirectory()) {
				campaigns.add(file.getName());
			}
		}
		for(String campaignName : campaigns){
			if(campaignName.equals(name)){
				return false;
			}
		}
		return true;
	}

	public LocalDate getCampaignStartDate() {
		LocalDateTime earliestTime = LocalDateTime.MAX;
		for(Click click : campaign.getClicks()){
			if(click.getDateTime().isBefore(earliestTime)){
				earliestTime = click.getDateTime();
			}
		}
		for(Impression impression : campaign.getImpressions()){
			if(impression.getDateTime().isBefore(earliestTime)){
				earliestTime = impression.getDateTime();
			}
		}

		for(ServerEntry se : campaign.getServerEntries()){
			if(se.getEntryDate().isBefore(earliestTime)){
				earliestTime = se.getEntryDate();
			}
		}

		return earliestTime.toLocalDate();
	}

	public LocalDate getCampaignEndDate() {
		LocalDateTime latestTime = LocalDateTime.MIN;
		for(Click click : campaign.getClicks()){
			if(click.getDateTime().isAfter(latestTime)){
				latestTime = click.getDateTime();
			}
		}
		for(Impression impression : campaign.getImpressions()){
			if(impression.getDateTime().isAfter(latestTime)){
				latestTime = impression.getDateTime();
			}
		}

		for(ServerEntry se : campaign.getServerEntries()){
			if(se.getEntryDate().isAfter(latestTime)){
				latestTime = se.getEntryDate();
			}
		}

		return latestTime.toLocalDate();
	}
}