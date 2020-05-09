import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private States state = new States();

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
	}

	/**
	 * @param fileName of the serialised Campaign file
	 */
	public void deserializeCampaign(String fileName){
		System.out.println("Deserializing campaign ...");
		try {
			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(file);
			this.campaign = (Campaign)in.readObject();
			in.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException" + " is caught");
		}

		long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		System.out.println("Method took:" + (endTime - startTime) / 1000000);
	}


	/**
	 * @param fileName of the file to be serialised
	 */
	public void serializeCampaign(String fileName){
		System.out.println("Serializing campaign ...");
		long startTime = System.nanoTime();
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(campaign);
			out.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//	public void saveCampaign(String filename){
//		state.save(filename, campaign.getClicks(), campaign.getServerEntries(), campaign.getImpressions());
//	}
//
//	public void loadCampaign(String filename){
//
//		long startTime = System.nanoTime();
//		ArrayList<Object> list = state.loadCampaign(filename);
//
//		ArrayList<Click> clicks = new ArrayList<>();
//		ArrayList<ServerEntry> serverEntries = new ArrayList<>();
//		ArrayList<Impression> impressions = new ArrayList<>();
//
//		for(Object o : list){
//
//			if( o instanceof Click){
//				clicks.add((Click) o);
//			}
//
//			if( o instanceof ServerEntry){
//				serverEntries.add((ServerEntry) o );
//			}
//
//			if( o instanceof Impression){
//				impressions.add((Impression) o);
//			}
//
//		}
//
//		Campaign campaign = new Campaign(serverEntries, clicks, impressions, this);
//		this.campaign = campaign;
//
//		long endTime = System.nanoTime();
//		System.out.println("Method took:" + (endTime - startTime) / 1000000);
//
//	}


//save the log file 
    public void saveCampaign(String campaignName){
		Path path = Paths.get(System.getProperty("user.dir") + "\\" + AD_AUCTION_FOLDER + "\\" + CAMPAIGN_FOLDER + "\\" + campaignName);

		// Create directories if the don't yet exist
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				//fail to create directory
				e.printStackTrace();
			}
		}

		// Copy campaigns

		File serverSource = new File(campaign.serverPath);
		File clickSource = new File(campaign.clickPath);
		File impressionSource = new File(campaign.impressionPath);

		File serverDestination = new File(path + "\\" + SERVER_LOG_NAME);
		File clickDestination = new File(path + "\\" + CLICK_LOG_NAME);
		File impressionDestination = new File(path + "\\" + IMPRESSION_LOG_NAME);

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
				e1.printStackTrace();
			}
		}

		// Create config file
		File file = new File( path + "\\config.txt");
		try{
			String str = Integer.toString(bounceDefinition);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
			writer.write(str);
			writer.close();
		}
		catch (IOException e){
			System.err.println("Cannot create directories - " + e);
			e.printStackTrace();
		}

    }

    public void loadCampaign(String campaignName){
        long startTime = System.nanoTime();
		Path path = Paths.get(System.getProperty("user.dir") + "\\" + AD_AUCTION_FOLDER + "\\" + CAMPAIGN_FOLDER + "\\" + campaignName);
	    try {
			BufferedReader reader = new BufferedReader(new FileReader(path + "\\config.txt"));
			String line = reader.readLine();
			String[] list = line.split(" ");
			System.out.println(list[0]);

	    	loadNewCampaign(path + "\\" + SERVER_LOG_NAME, path + "\\" + CLICK_LOG_NAME, path + "\\" + IMPRESSION_LOG_NAME, Integer.parseInt(list[0]));

	    	System.out.println(gui == null);
        }catch (FileNotFoundException e){
	        e.printStackTrace();
        }catch (IOException e){
	        e.printStackTrace();
        }
	    long endTime = System.nanoTime();
		System.out.println("Method took:" + (endTime - startTime) / 1000000);
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
			startDatePredicate = c -> c.getDateTime().isAfter(filter.getStartDate()); // TODO Discuss if it needs to be inclusive
		}
		else{
			System.out.println("startDate is null");
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<Impression> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getDateTime().isBefore(filter.getEndDate());
		}
		else{
			System.out.println("endDate is null");
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<Impression> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			System.out.println("contexts are empty");
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<Impression> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			System.out.println("gender is null");
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<Impression> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			System.out.println("ageGroup is empty");
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<Impression> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			System.out.println("income is empty");
			incomePredicate = c -> true;
		}

		ArrayList<Impression> filteredImpressions = (ArrayList<Impression>) campaign.getImpressions().stream().filter
				(startDatePredicate.and(endDatePredicate.and(contextsPredicate).and(genderPredicate).and(ageGroupPredicate).and(incomePredicate))).collect(Collectors.toList());

		//for testing
		//long endTime = System.nanoTime();
		//System.out.println("Method took:" + (endTime - startTime) / 1000000);

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
			startDatePredicate = c -> c.getEntryDate().isAfter(filter.getStartDate()); // TODO Discuss if it needs to be inclusive
		}
		else{
			System.out.println("startDate is null");
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<ServerEntry> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getEntryDate().isBefore(filter.getEndDate());
		}
		else{
			System.out.println("endDate is null");
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<ServerEntry> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			System.out.println("contexts are empty");
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<ServerEntry> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			System.out.println("gender is null");
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<ServerEntry> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			System.out.println("ageGroup is empty");
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<ServerEntry> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			System.out.println("income is empty");
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
			startDatePredicate = c -> c.getDateTime().isAfter(filter.getStartDate()); // TODO Discuss if it needs to be inclusive
		}
		else{
			System.out.println("startDate is null");
			startDatePredicate = c -> true;
		}

		// Predicate for endDate
		Predicate<Click> endDatePredicate;
		if(filter.getEndDate() != null){
			endDatePredicate = c -> c.getDateTime().isBefore(filter.getEndDate());
		}
		else{
			System.out.println("endDate is null");
			endDatePredicate = c -> true;
		}

		// Predicates for contexts
		Predicate<Click> contextsPredicate;
		if(!filter.getContexts().isEmpty()){
			contextsPredicate = c -> matchContext(c.getContext(), filter);
		}
		else{
			System.out.println("contexts are empty");
			contextsPredicate = c -> true;
		}

		// Predicate for gender
		Predicate<Click> genderPredicate;
		if(filter.getGender() != null){
			genderPredicate = c -> c.getGender().equals(filter.getGender());
		}
		else{
			System.out.println("gender is null");
			genderPredicate = c -> true;
		}

		// Predicates for ageGroups
		Predicate<Click> ageGroupPredicate;
		if(!filter.getAgeGroups().isEmpty()){
			ageGroupPredicate = c -> matchAgeGroup(c.getAgeGroup(), filter);
		}
		else{
			System.out.println("ageGroup is empty");
			ageGroupPredicate = c -> true;
		}

		// Predicate for incomes
		Predicate<Click> incomePredicate;
		if(!filter.getIncomes().isEmpty()){
			incomePredicate = c -> matchIncome(c.getIncome(), filter);
		}
		else{
			System.out.println("income is empty");
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
		System.out.println(totalConversions);
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
		double CPA = totalCost / totalConversions;
		return CPA;
	}

	public double calcCPC(ArrayList<Click> clickArray){
		double totalClickCost = 0;
		for (Click click: clickArray) {
			totalClickCost += click.getClickCost();
		}
		double CPC = (totalClickCost / clickArray.size());
		return CPC;
	}

	public double calcCPM(ArrayList<Impression> impressionArray){
		double totalImpCost = 0;
		for (Impression imp: impressionArray) {
			totalImpCost += imp.getImpressionCost();
		}
		double CPM =   ((double) totalImpCost / impressionArray.size()) * 1000;
		return CPM;
	}
}