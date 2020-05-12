import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *  Contains data about single campaign
 *  Handles all data manipulation
 */
public class Campaign implements Serializable {
	private static final long serialVersionUID = 4518483514452733851L;

	// Log information
	private ArrayList<Impression> impressions; // Impression Log
	private ArrayList<Click> clicks; // Click Log
	private ArrayList<ServerEntry> serverEntries; // Server Log
	private HashMap<String, Impression> impressionSet;

	// Campaign's metrics
	private int totalImpressions;
	private double totalImpCost;
	private int totalClicks;
	private double totalClickCost;
	private double totalCost;
	private int totalConversions;
	private double conversionRate;
	private int bounces;
	private double bounceRate;
	private int totalUniques;
	private double CTR; // click-through-rate
	private double CPA; // cost-per-acquisition
	private double CPC; // cost-per-click
	private double CPM; // cost-per-thousand impressions

	private String campaignName;

	private Controller controller;
	public String serverPath;
	public String clickPath;
	public String impressionPath;


	public Campaign(){

	}
	//--LOADING---------------------------------------------------------------------------------------------------------
	/**
	 * Loads all the data from CSV files into arrayLists and calculates the metrics
	 */
	public Campaign(String serverFilePath, String clickFilePath, String impressionFilePath, Controller controller){
		this.controller = controller;

		this.serverPath = serverFilePath;
		this.clickPath = clickFilePath;
		this.impressionPath = impressionFilePath;

		loadImpressionLog(impressionFilePath);
		loadSeverlog(serverFilePath);
		loadClickLog(clickFilePath);
		calculateMetrics(clicks, impressions, serverEntries);
	}


	public void setName(String name){
		this.campaignName = name;
	}

	public String getName(){
		return campaignName;
	}

	public void loadClickLog (String clickFileName){
		ArrayList<Click> clicks = new ArrayList<Click>();
		String clickLog = clickFileName;
		File clickLogFile = new File(clickLog);
		String clickLine = "";
		try {
			Scanner inputStream = new Scanner(clickLogFile);
			//To remove the first clickLine (headings)
			inputStream.nextLine();

			while (inputStream.hasNext()){
				clickLine = inputStream.nextLine();
				//seperating colums based on comma
				String[] clickValues = clickLine.split(",");

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime formatDateTime = LocalDateTime.parse(clickValues[0],formatter);

				String id = clickValues[1];

				Float clickCost = Float.parseFloat(clickValues[2]);

				clicks.add(new Click(formatDateTime,id,clickCost));
			}
			inputStream.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		long startTime = System.nanoTime();
		for(Click c : clicks){

			String ageGroup = impressionSet.get(c.getID()).getAgeGroup();
			String context = impressionSet.get(c.getID()).getContext();
			String income = impressionSet.get(c.getID()).getIncome();
			String gender = impressionSet.get(c.getID()).getGender();
			float impCost = impressionSet.get(c.getID()).getImpressionCost();

			c.setAgeGroup(ageGroup);
			c.setContext(context);
			c.setGender(gender);
			c.setIncome(income);
			c.setImpressionCost(impCost);

		}
		this.clicks = clicks;
	}

	public void loadImpressionLog (String impressionFileName){
		ArrayList<Impression> impressions = new ArrayList<Impression>();
		String impressionLog = impressionFileName;
		File impressionLogFile = new File(impressionLog);
		String impressionLine = "";

		//Catch block to check if the impressionLogFile is there
		try {
			Scanner inputStream = new Scanner(impressionLogFile);
			//To remove the first impressionLine (headings)
			inputStream.nextLine();

			while (inputStream.hasNext()) {
				impressionLine = inputStream.nextLine();

				//seperating columns based on comma
				String[] impressionValues = impressionLine.split(",");

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

				LocalDateTime dateTime = LocalDateTime.parse(impressionValues[0],formatter);
				String id = impressionValues[1];
				String gender = impressionValues[2];
				String ageGroup = impressionValues[3];
				String income = impressionValues[4];
				String context = impressionValues[5];
				Float impressionCost = Float.parseFloat(impressionValues[6]);

				impressions.add(new Impression(dateTime,id,gender,ageGroup,income,context,impressionCost));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		this.impressions = impressions;
		impressionSet = new HashMap<>();
		for(Impression i: impressions){
			impressionSet.put(i.getID(), i);
		}
	}

	public void loadSeverlog (String serverFileName){
		ArrayList<ServerEntry> serverEntries = new ArrayList<ServerEntry>();
		File serverLogFile = new File(serverFileName);
		String serverLine = "";

		//Catch block to check if the serverLogFile is there
		try {
			Scanner inputStream = new Scanner(serverLogFile);
			//To remove the first serverLine (headings)
			inputStream.nextLine();

			while (inputStream.hasNext()) {
				try{
					serverLine = inputStream.nextLine();
					//seperating columns based on comma
					String[] serverValues = serverLine.split(",");

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime entryDate = LocalDateTime.parse(serverValues[0], formatter);

					String id = serverValues[1];

					LocalDateTime exitDate;
					if(serverValues[2].equals("n/a")){
						exitDate = null;
					}
					else exitDate = LocalDateTime.parse(serverValues[2],formatter);

					int pagesViewed = Integer.parseInt(serverValues[3]);

					String conversion = serverValues[4];


					serverEntries.add(new ServerEntry(entryDate,id,exitDate,pagesViewed,conversion));
				} catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
			inputStream.close();
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for(ServerEntry s : serverEntries){
			String ageGroup = impressionSet.get(s.getID()).getAgeGroup();
			String context = impressionSet.get(s.getID()).getContext();
			String income = impressionSet.get(s.getID()).getIncome();
			String gender = impressionSet.get(s.getID()).getGender();
			float impCost = impressionSet.get(s.getID()).getImpressionCost();

			s.setAgeGroup(ageGroup);
			s.setContext(context);
			s.setGender(gender);
			s.setIncome(income);
			s.setImpressionCost(impCost);
		}


		this.serverEntries = serverEntries;

	}

	//--CALCULATIONS----------------------------------------------------------------------------------------------------
	/**
	 * Calculates and updates all metrics given lists of campaign's data
	 */
	public void calculateMetrics(ArrayList<Click> clickList, ArrayList<Impression> impList, ArrayList<ServerEntry> serverEntries) {
		totalImpressions = controller.calcImpressions(impList);
		totalImpCost = controller.calcTotalImpCost(impList);
		totalClicks = controller.calcClicks(clickList);
		totalClickCost = controller.calcTotalClickCost(clickList);
		totalCost = controller.calcTotalCost(impList, clickList);
		totalConversions = controller.calcConversions(serverEntries);
		conversionRate = controller.calcConvRate(serverEntries, clickList);
		bounces = controller.calcBounces(serverEntries);
		bounceRate = controller.calcBounceRate(serverEntries, clickList);
		totalUniques = controller.calcUniques(clickList);
		CTR = controller.calcCTR(clickList, impList);
		CPA = controller.calcCPA(impList, clickList, serverEntries);
		CPC = controller.calcCPC(clickList);
		CPM = controller.calcCPM(impList);
	}






	//--GETTERS---------------------------------------------------------------------------------------------------------

	public int getTotalImpressions() {
		return totalImpressions;
	}

	public double getTotalImpressionCost() {
		return totalImpCost;
	}

	public int getTotalClicks() {
		return totalClicks;
	}

	public double getTotalClickCost() {
		return totalClickCost;
	}

	public int getTotalConversions() {
		return totalConversions;
	}

	public int getTotalBounces() {
		return bounces;
	}

	public double getBounceRate() {
		return bounceRate;
	}

	public int getTotalUnique() {
		return totalUniques;
	}

	public double getCTR() {
		return CTR;
	}

	public double getCPA() {
		return CPA;
	}

	public double getCPC() {
		return CPC;
	}

	public double getCPM() {
		return CPM;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public ArrayList<Impression> getImpressions() {
		return impressions;
	}

	public ArrayList<ServerEntry> getServerEntries() {
		return serverEntries;
	}

	public ArrayList<Click> getClicks(){
		return clicks;
	}

	public double getTotalCost() {
		return totalCost;
	}

	//--SETTERS---------------------------------------------------------------------------------------------------------
}
