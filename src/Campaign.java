

//import com.sun.security.ntlm.Server;

/*
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
*/
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Domain of the application. (Basically backend)
 *  Model of MVC
 *  Should be independent of the View (Observer) and the Controller.
 */
public class Campaign {

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

	private List<Observer> observers = new LinkedList<Observer>();

	// Log information
	private ArrayList<Impression> impressions; // Impression Log
	private ArrayList<Click> clicks; // Click Log
	private ArrayList<ServerEntry> serverEntries; // Server Log
	
	private static Connection c;
	private static Boolean hasData = false;
	int batchSize = 20;

	/**
	 * Should be called whenever anything in the model changes.
	 * It updates the all the observers, e.g. View
	 */
//	private void triggerUpdate() {
//		for (Observer observer : observers) {
//			observer.observableChanged(this);
//		}
//	}
//
//	public void addObserver(Observer observer) {
//		observers.add(observer);
//	}
	

	public Connection connect() {
		// SQLite connection string
		if(c == null) {
			try {
				String url = "jdbc:sqlite:reset.db";
				c = DriverManager.getConnection(url);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return c;
		} else {
			return c;
		}
	}

	private void  getC() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		//  c=DriverManager.getConnection("jdbc:sqlite:/Users/wangzeyu/Desktop/sqlite/test.db");
		c= DriverManager.getConnection("jdbc:sqlite:test.db");
	//	initialise(clicklogN,implogN,serverlogN);
		//initialiseImpression();
	}
	
		
	public ArrayList<Click> getClickData(){
		
		String sql = "SELECT * FROM clickLog";
		ArrayList<Click> clickList = new ArrayList<>(); 

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while(rs.next()){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");
				LocalDateTime dateTime = LocalDateTime.parse(rs.getString("entry_date"), formatter);
				Click click = new Click(dateTime, rs.getString("id"), rs.getFloat("click_cost"));
				clickList.add(click);
			}

				

		}catch(SQLException e ){
			e.printStackTrace();
		}
		return clickList;
	}


	public ArrayList<Impression> getImpressionData(){

		String sql = "SELECT * FROM impressionLog";
		ArrayList<Impression> impressionList = new ArrayList<>();

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while(rs.next()){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");
				LocalDateTime dateTime = LocalDateTime.parse(rs.getString("entry_date"),formatter);
				Impression imp = new Impression(dateTime, rs.getString("id"), rs.getString("gender"), rs.getString("age"), rs.getString("income"), rs.getString("context"), rs.getFloat("impression_cost"));
				impressionList.add(imp);
			}



		}catch(SQLException e ){
			e.printStackTrace();
		}
		return impressionList;
	}

	
	public ArrayList<ServerEntry> getServerEntry(){

		String sql = "SELECT * FROM serverEntry";
		ArrayList<ServerEntry> entryList = new ArrayList<>();

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while(rs.next()){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");
				LocalDateTime entry_date = LocalDateTime.parse(rs.getString("entry_date"),formatter);
				LocalDateTime exit_date = null;
				if(rs.getString("exit_date") != null){
					exit_date = LocalDateTime.parse(rs.getString("exit_date"), formatter);
				}
				ServerEntry entry = new ServerEntry(entry_date, rs.getString("id"), exit_date, rs.getInt("pagesViewed"), rs.getString("conversion"));
				entryList.add(entry);
			}



		}catch(SQLException e ){
			e.printStackTrace();
		}
		return entryList;
	}
	
	
	public void initialise(String clickName,String impressionName,String serverName){
		if (!hasData){
			hasData = true;
		}

		try{
			if (c == null) {
				getC();
			}

		String sqlClick = "CREATE TABLE IF NOT EXISTS clickLog (\n"
				+ "entry_date TIMESTAMP NOT NULL,\n"
				+ "id TEXT NOT NULL,\n "
				+ "click_cost REAL NOT NULL \n"
				+ ");";

		String sqlImpression = "CREATE TABLE IF NOT EXISTS impressionLog (\n"
				+ "entry_date TIMESTAMP NOT NULL,\n"
				+ "id TEXT NOT NULL,\n "
				+ "gender TEXT NOT NULL,\n"
				+ "age TEXT NOT NULL,\n"
				+ "income TEXT NOT NULL,\n"
				+ "context TEXT NOT NULL, \n"
				+ "impression_cost REAL NOT NULL \n"
				+ ");";

		String sqlServer = "CREATE TABLE IF NOT EXISTS serverLog (\n"
				+ "entry_date TIMESTAMP NOT NULL,\n"
				+ "id TEXT NOT NULL,\n "
				+ "exit_date TIMESTAMP,\n"
				+ "pagesViewed INTEGER NOT NULL,\n"
				+ "conversion TEXT NOT NULL \n"
				+ ");";





		Statement state = c.createStatement();
		state.execute(sqlImpression);
		state.execute(sqlClick);
		state.execute(sqlServer);



			c.setAutoCommit(false);
			PreparedStatement prepClick = c.prepareStatement("INSERT INTO clickLog values(?,?,?);");
			PreparedStatement prepImpression = c.prepareStatement("INSERT INTO impressionLog values(?,?,?,?,?,?,?);");
			PreparedStatement prepServer = c.prepareStatement("INSERT INTO serverLog values(?,?,?,?,?);");


			BufferedReader lineReader = new BufferedReader(new FileReader(clickName));
			String lineText = null;
			int count = 0;
			lineReader.readLine(); //skip header line
			while ((lineText = lineReader.readLine()) != null){
				String[] data = lineText.split(",");
				String date = data[0];
				String id = data[1];
				String clickCost = data[2];

				//Timestamp sqlTimestamp = Timestamp.valueOf(date);
				//prepClick.setTimestamp(1,sqlTimestamp);

				prepClick.setString(1,String.valueOf(Timestamp.valueOf(date)));

				prepClick.setString(2,id);

			//	float clickcosts = Float.parseFloat(clickCost);
			//	prepClick.setFloat(3,clickcosts);
				prepClick.setString(3,String.valueOf(Float.valueOf(clickCost)));

				prepClick.addBatch();

				if (count % batchSize == 0){
					prepClick.executeBatch();
				}

			}

			lineReader.close();

			BufferedReader implineReader = new BufferedReader(new FileReader(impressionName));
			String impLineText = null;
			int impCount = 0;
			implineReader.readLine(); //skip header line
			while ((impLineText = implineReader.readLine()) != null){
			//	System.out.println(serverLineText);
				String[] data = impLineText.split(",");
				String date = data[0];
				String id = data[1];
				String gender = data[2];
				String age = data[3];
				String income = data[4];
				String context = data[5];
				String impressionCost = data[6];

			//	Timestamp sqlTimestamp = Timestamp.valueOf(date);
			//	prepImpression.setTimestamp(1,sqlTimestamp);

				prepImpression.setString(1,String.valueOf(Timestamp.valueOf(date)));

				prepImpression.setString(2,id);
				prepImpression.setString(3,gender);
				prepImpression.setString(4,age);
				prepImpression.setString(5,income);
				prepImpression.setString(6,context);

			//	float impressionCosts = Float.parseFloat(impressionCost);
			//	prepImpression.setFloat(7,impressionCosts);

				prepImpression.setString(7,String.valueOf(Float.valueOf(impressionCost)));

				prepImpression.addBatch();

				if (impCount % batchSize == 0){
					prepImpression.executeBatch();
				}


			}
			implineReader.close();

			BufferedReader serverlineReader = new BufferedReader(new FileReader(serverName));
			String serverLineText = null;
			int serverCount = 0;
			serverlineReader.readLine(); //skip header line
			while ((serverLineText = serverlineReader.readLine()) != null){
				//	System.out.println(serverLineText);
				String[] data = serverLineText.split(",");
				String entryDate = data[0];
				String id = data[1];
				String exitDate = data[2];
				String pagesViewed = data[3];
				String conversion = data[4];

			//	Timestamp sqlTimestamp = Timestamp.valueOf(entryDate);
			//	prepServer.setTimestamp(1,sqlTimestamp);
				prepServer.setString(1,String.valueOf(Timestamp.valueOf(entryDate)));

				prepServer.setString(2,id);

				if(exitDate.equals("n/a")){
				//	Timestamp sqlExitTime = Timestamp.valueOf(exitDate);
				//	prepServer.setTimestamp(3,sqlExitTime);
					prepServer.setNull(3,Types.TIMESTAMP);
				} else {
					prepServer.setString(3,String.valueOf(Timestamp.valueOf(exitDate)));
				}


				int pagesview = Integer.parseInt(pagesViewed);
				prepServer.setInt(4,pagesview);

				prepServer.setString(5,conversion);

				prepServer.addBatch();

				if (serverCount % batchSize == 0){
					prepServer.executeBatch();
				}

			}
			serverlineReader.close();


			prepServer.executeBatch();
			prepImpression.executeBatch();
			prepClick.executeBatch();
			c.commit();
			//   c.close();
		} catch (IOException e){

		} catch (ClassNotFoundException e){

		} catch(SQLException e){

		}

		try {
			c.rollback();
		} catch (SQLException e){

		}

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
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.impressions = impressions;
		//System.out.println(this.impressions.size()); //printout entire log

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
		this.serverEntries = serverEntries;
		//System.out.println(serverEntries); // entire log
	}

/**
	 * These are the SQL calculations when the whole data set is being used 
	 * @return
	 */

	public int calcImpressions(){

		String sql = "SELECT COUNT() FROM impressionLog";

		int totalImpressions = 0;

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while(rs.next())

				totalImpressions = rs.getInt(1);

		}catch(SQLException e ){
			e.printStackTrace();
		}
		this.totalImpressions = totalImpressions;
		return totalImpressions;


	}

	public double calcTotalImpCost() {


		double impressionCost  = 0;

		String sql = "SELECT SUM(i.impression_cost) FROM impressionLog i";
		try (
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				impressionCost = rs.getFloat(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.totalImpCost = impressionCost;
		return impressionCost;
	}


	public int calcClicks(){

		String sql = "SELECT COUNT() FROM clickLog";

		int totalClicks = 0;

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{

			while(rs.next())

				totalClicks = rs.getInt(1);

		}catch(SQLException e ){
			e.printStackTrace();
		}
		this.totalClicks = totalClicks;
		return totalClicks;

	}

	public double calcTotalClickCost(){


		double totalClickCost = 0;
		String sql = "SELECT SUM(c.click_cost) FROM clickLog c";
		try(Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			while(rs.next()){
				totalClickCost = rs.getInt(1);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		this.totalClickCost = totalClickCost;
		return totalClickCost;

	}

	public double calcTotalCost(){
		double clicksum = 0;
		double impsum = 0;

		String sql   = "SELECT SUM(i.impression_cost) AS impCost   FROM impressionLog i";
		String sql1  = "SELECT SUM(c.click_cost)      AS clickCost FROM clickLog      c";


		try(
                Connection conn  = this.connect();
                Statement  stmt  = conn.createStatement();
                ResultSet  rs    = stmt.executeQuery(sql)){

            while(rs.next()){
                impsum = rs.getDouble(1);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        try(
                Connection conn  = this.connect();
                Statement  stmt  = conn.createStatement();
                ResultSet  rs    = stmt.executeQuery(sql1)){

            while(rs.next()){
                clicksum = rs.getDouble(1);
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        this.totalClickCost = clicksum;
        this.totalImpCost   = impsum;
        this.totalCost      = impsum + clicksum;

		return impsum + clicksum;

	}

	public int calcConversions(){

		int totalConversions = 0;

		String sql = "SELECT COUNT() FROM serverLog WHERE conversion = 'Yes' ";

		try(
				Connection conn = this.connect();
				Statement stmt  = conn.createStatement();
				ResultSet rs    = stmt.executeQuery(sql)){

			while(rs.next()){

				totalConversions = rs.getInt(1);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}

		this.totalConversions = totalConversions;
		return totalConversions;


	}



	public double calcConvRate(){

		this.conversionRate = ((double) totalConversions / totalClicks);
		return conversionRate;

	}




	public int calcBounces(){
		int bounces = 0;

		String sql = "SELECT COUNT() FROM serverLog s WHERE pagesViewed = 0";

		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{

			while(rs.next()){

				bounces = rs.getInt(1);

			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		this.bounces = bounces;
		return bounces;
	}

	public double calcBounceRate(){

		this.bounces     = calcBounces();
		this.totalClicks = calcClicks() ;

		return ((double)bounces/totalClicks);
	}



	public int calcUniques(){

		int totalUniques = 0;

		String sql = "SELECT COUNT(DISTINCT id) FROM clickLog";
		try(
				Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{

			while(rs.next()){

				totalUniques = rs.getInt(1);

			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		this.totalUniques = totalUniques;
		return totalUniques;

	}




	public double calcCTR(){

		double CTR =  ((double)totalClicks/ totalImpressions);
		this.CTR   = CTR;
		return CTR;

	}




	public double calcCPA(){

		CPA = totalCost / totalConversions;
		return CPA;

	}

	public double calcCPC(){

		CPC = totalCost / totalClicks;
		return CPA;

	}

	public double calcCPM(){

		CPM =  (totalCost / totalImpressions) * 1000;
		return CPM;

	}

	public void calculateMetrics() {

		calcClicks();
		calcTotalImpCost();
		calcImpressions();
		calcBounces();
		calcUniques();
		calcConversions();

		calcTotalCost();
		calcTotalClickCost();

		calcConvRate();
		calcBounceRate();

		calcCPM();
		calcCPC();
		calcCPA();
		calcCTR();

	}


	/**
	 * These calculations are directly for the filter methods to use when they have made their own data sets and arraylists
	 * @return
	 */


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
		double conversionRate =   totalConversions / totalClicks;
		return  conversionRate;
	}

	public int calcBounces(ArrayList<ServerEntry> serverEntryArray){

		int myBounces = 0;
		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getPagesViewed() <= 1){
				myBounces += 1;
			}
		}
		return myBounces;
	}

	public double calcBounceRate(ArrayList<ServerEntry> serverEntryArray, ArrayList<Click> clickArray){

		int bounces = 0;

		for (ServerEntry serverEntry: serverEntryArray) {
			if (serverEntry.getPagesViewed() <= 1){
				bounces += 1;
			}
		}
		int totalClicks = clickArray.size();
		double bounceRate =  bounces / totalClicks;// change to proper divide function
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
		return  clickArray.size() / impressionArray.size();
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
		double CPM =  (totalImpCost / impressionArray.size()) * 1000;
		return CPM;
	}


	public int getTotalImpressions() {
		return totalImpressions;
	}

	public double getTotalImpressionCost() {
		return totalImpCost;
	}

	public double getTotalClicks() {
		return totalClicks;
	}

	public double getTotalClickCost() {
		return totalClickCost;
	}

	public double getTotalConversions() {
		return totalConversions;
	}

	public double getTotalBounces() {
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
	public String testIsConnected() {
		return ("connected");
	}
	
	public ArrayList filterArray(ArrayList array, Predicate predicate){
		Stream filtered = array.stream().filter(predicate);
		List list = (List) filtered.collect(Collectors.toList());
		ArrayList filt = new ArrayList(list);
		return array;
	}
	
}
