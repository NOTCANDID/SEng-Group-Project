import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import org.controlsfx.control.CheckComboBox;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class GUI extends Application {

    private static Controller controller;
    private static File impressionFile;
    private static File clicksFile;
    private static File serverFile;
    private final String[] metrics = {"Number of Impressions", "Number of Clicks", "Number of Uniques", "Number of " +
			"Bounces", "Number of Conversions", "Total Cost", "Click Through Rate", "Cost per Acquisition", "Cost per " +
			"Click", "Cost per thousand Impressions", "Bounce Rate"};
    private ComboBox<String> fileOption;
    private ComboBox<String> settingOption;
    private final TimeInterval[] granularityOptions = {TimeInterval.HOUR, TimeInterval.DAY, TimeInterval.WEEK,
			TimeInterval.MONTH};
    private final BarChartType[] barChartType = {BarChartType.DAY_OF_WEEK, BarChartType.TIME_OF_DAY};
    private final String[] genders = {"","Male", "Female"};
    private final String[] ageGroups = {"<25", "25-34", "35-44", "45-54", ">54"};
    private final String[] incomeGroups = {"Low", "Medium", "High"};
    private final String[] contextGroups = {"News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel"};
    final String cssDefault = "-fx-border-color: black;\n"
            + "-fx-border-insets: 50.0 10.0 0.0 10.0;\n"
            + "-fx-border-width: 3;\n"
            + "-fx-padding: 10;\n"
            + "-fx-border-style: segments(10,15,15,15) solid;\n";

    // Metrics
    private TextField bounceRateField;
    private TextField noImpressionsField;
    private TextField noClicksField;
    private TextField noUniquesField;
    private TextField noBouncesField;
    private TextField noConversionsField;
    private TextField ctrField;
    private TextField cpaField;
    private TextField cpcField;
    private TextField cpmField;
    private TextField totalCostField;
    private TextField conversionRateField;

    private NumberFormat format = NumberFormat.getNumberInstance(Locale.UK);
	
	private Background back = new Background(new BackgroundFill(Color.web("c8e3f0"),CornerRadii.EMPTY,Insets.EMPTY));

	Filter currentFilter = null;

	public void setFilter(Filter filter){
	    currentFilter = filter;
    }

    public GUI() {
    }

    public GUI(Controller con) {
        this.controller = con;
    }


    public void start(){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        fileChooserWindow();

    }


    private void mainWindow() throws Exception {
        Group root = new Group();
        Stage primaryStage = new Stage();

        StackPane layering = new StackPane();
        Canvas canvas = new Canvas();

        BorderPane mainWindow = new BorderPane();
        BorderPane toolBar = new BorderPane();
        VBox chartOptions = new VBox(20);
        HBox mainArea = new HBox(10);
        HBox options = new HBox();
	    
	//Slider slider = new Slider(6,20,10);

        // Centre: metrics and filters
        VBox filtersAndMetrics = new VBox(20);
        TilePane impressionFilterOptions = impressionFilters();
        BorderPane filters = new BorderPane();
        Button filterButton = new Button("Apply Filters");


        if(currentFilter != null) {
            ObservableList<Node> filterNodes = null;
            filterNodes = impressionFilterOptions.getChildren();

            int a = 0;
            int b = 0;

            for (Node n : filterNodes) {
                if (n instanceof VBox) {
                    for (Node m : ((VBox) n).getChildren()) {
                        if (m instanceof ComboBox) {
                            ((ComboBox) m).setValue(currentFilter.getGender());
                        } else if (m instanceof DatePicker) {
                            try {
                                if (a == 0) {
                                    ((DatePicker) m).setValue(currentFilter.getStartDate().toLocalDate());
                                    a++;
                                } else {
                                    ((DatePicker) m).setValue(currentFilter.getEndDate().toLocalDate());
                                }
                            } catch (NullPointerException e) {
                                GUI.displayError(e.getMessage());
                            }
                        } else if (m instanceof CheckComboBox) {

                            if (b == 0) {
                                for (Object o : currentFilter.getContexts()) {
                                    ((CheckComboBox) m).getCheckModel().check(o);
                                }
                                b++;
                            } else if (b == 1) {
                                for (Object o : currentFilter.getAgeGroups()) {
                                    ((CheckComboBox) m).getCheckModel().check(o);
                                }
                                b++;
                            } else {
                                for (Object o : currentFilter.getIncomes()) {
                                    ((CheckComboBox) m).getCheckModel().check(o);
                                }
                                b++;
                            }
                        }
                    }
                }
            }
        }


        filters.setAlignment(filterButton, Pos.BOTTOM_LEFT);
       // filterButton.setAlignment(Pos.BOTTOM_RIGHT);
        filters.setLeft(impressionFilterOptions);
        filters.setRight(filterButton);
        filtersAndMetrics.getChildren().addAll(getMetricsWindow(), new Separator(), filters);
        filtersAndMetrics.setMargin(filters, new Insets(0, 10, 0, 10));
        filterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                

                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                setFilter(filter);
                ArrayList<Click> clicks = controller.filterClickLog(filter);
                ArrayList<Impression> impressions = controller.filterImpressionLog(filter);
                ArrayList<ServerEntry> serverEntries = controller.filterServerLog(filter);

                bounceRateField.setText(format.format(controller.calcBounceRate(serverEntries, clicks)));
                noImpressionsField.setText(format.format(controller.calcImpressions(impressions)));
                noClicksField.setText(format.format(controller.calcClicks(clicks)));
                noUniquesField.setText(format.format(controller.calcUniques(clicks)));
                noBouncesField.setText(format.format(controller.calcBounces(serverEntries)));
                noConversionsField.setText(format.format(controller.calcConversions(serverEntries)));
                ctrField.setText(format.format(controller.calcCTR(clicks, impressions)));
                cpaField.setText(format.format(controller.calcCPA(impressions, clicks, serverEntries)));
                cpcField.setText(format.format(controller.calcCPC(clicks)));
                cpmField.setText(format.format(controller.calcCPM(impressions)));
                totalCostField.setText(format.format(controller.calcTotalCost(impressions, clicks)));
                conversionRateField.setText(format.format(controller.calcConvRate(serverEntries, clicks)));
            }
        });

        String[] fileOptionText = {"Load...", "Save", "Save as..."};
        String[] settingsOptionText = {"Text...", "Colour..."};

        fileOption = new ComboBox<String>(FXCollections.observableArrayList(fileOptionText));
        fileOption.setValue("File");

     //   settingOption = new ComboBox<String>(FXCollections.observableArrayList(settingsOptionText));
     //   settingOption.setValue("Settings");
        
        Menu menu = new Menu("File");


        MenuItem m1 = new MenuItem("Load...");
            m1.setOnAction(event -> {
                File[] files = new File(System.getProperty("user.dir") + File.separator + Controller.AD_AUCTION_FOLDER + File.separator + Controller.CAMPAIGN_FOLDER).listFiles();
                ArrayList<String> campaigns = new ArrayList<String>();
                for (File file : files) {
                    if (file.isDirectory()) {
                        campaigns.add(file.getName());
                    }
                }
                if(campaigns.size() == 0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Load Campaign");
                    alert.getDialogPane().getStylesheets().add("/GUI.css");
                    alert.setHeaderText(null);
                    alert.setContentText("There are no campaigns to load.");

                    alert.showAndWait();
                }
                else{
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(campaigns.get(0), campaigns);
                    dialog.getDialogPane().getStylesheets().add("/GUI.css");
                    dialog.setTitle("AdAuction");
                    dialog.setHeaderText("Load Campaign");
                    dialog.setContentText("Select campaign to be loaded:");

                    Optional<String> result = dialog.showAndWait();
                    String campaignName;
                    if (result.isPresent()){
                        currentFilter = controller.loadCampaign(result.get());
                        primaryStage.setTitle("Ad Auction Dashboard - " + controller.getCampaign().getName());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("/GUI.css");
                        try {
                            primaryStage.close();
                            mainWindow();
                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                        alert.setTitle("Load Campaign");
                        alert.setHeaderText(null);
                        alert.setContentText(result.get() + " has been loaded.");
                        alert.showAndWait();
                    }
                }
            });
        MenuItem m2 = new MenuItem("Save");
        m2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String campaignName = "Campaign " + System.nanoTime();
                controller.saveCampaign(campaignName, currentFilter);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("/GUI.css");
                alert.setTitle("Load Campaign");
                alert.setHeaderText(null);
                alert.setContentText("Campaign saved as \"" + campaignName + "\".");
                alert.showAndWait();
                
            }
        });
        MenuItem m3 = new MenuItem("Save as...");
            m3.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.getDialogPane().getStylesheets().add("/GUI.css");
                    dialog.setTitle("AdAuction");
                    dialog.setHeaderText("Save campaign");
                    dialog.setContentText("Please enter the name of the campaign:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("/GUI.css");
                        alert.setHeaderText(null);
                        alert.setTitle("Save Campaign");
                        if(result.get().equals("")){
                            alert.setContentText("Campaign saving failed. You need to provide the name of the campaign. Please try again.");

                        }
                        else if(!controller.isCampaignNameFree(result.get())){
                            alert.setContentText("Campaign saving failed. The name is already used by another campaign. Please try again with a different name.");
                        } else{
                            controller.saveCampaign(result.get(), currentFilter);

                            alert.setContentText("Campaign saved as \"" + result.get() + "\".");
                        }
                        alert.showAndWait();

                    }
                }
            });

        MenuItem m4 = new MenuItem("Help?");
            m4.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        File pdf = new File(this.getClass().getResource("Ad Auction User Manual.pdf").toURI());
                        Desktop.getDesktop().open(pdf);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (URISyntaxException e){
                        e.printStackTrace();
                    }
                }
            });
        menu.getItems().addAll(m1, m2, m3, m4);
        MenuBar mb = new MenuBar();
        mb.getMenus().add(menu);
     //   VBox topBox = new VBox();
     //   topBox.getChildren().addAll(mb, slider);

     //   mainWindow.setTop(mb);
	ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Paint fill = colorPicker.getValue();
                BackgroundFill backgroundFill = new BackgroundFill(fill, CornerRadii.EMPTY,Insets.EMPTY);
                Background background = new Background(backgroundFill);
                back = background;
                // add back on top
                mainWindow.setBackground(background);
                //   histogramWindow().setBackground(background);
                mb.setBackground(background);
            }
        });



        options.getChildren().addAll(mb,colorPicker);
        
       // toolBar.getChildren().add(options);
        Button lineGraphButton = new Button();
        Button histogramButton = new Button();
        Button barChartButton = new Button();

        histogramButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                histogramWindow().setBackground(back);

            }
        });

        lineGraphButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                lineWindow().setBackground(back);
            }
        });

        barChartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                barWindow().setBackground(back);
            }
        });



        Image lineGraphimage = new Image(this.getClass().getResourceAsStream("/lineGraphIcon.png"), 100, 100, true, true);
        Image histogramimage = new Image(this.getClass().getResourceAsStream("/barChartIcon.png"), 100, 100, true, true);
        Image barChartImage = new Image(this.getClass().getResourceAsStream("/dataIcon.png"), 100, 100, true, true);

        histogramButton.setGraphic(new ImageView(histogramimage));
        barChartButton.setGraphic(new ImageView(barChartImage));
        lineGraphButton.setGraphic(new ImageView(lineGraphimage));

        VBox chart1 = new VBox();
            Label lineLabel = new Label("Create Line Graph");
            chart1.getChildren().addAll(lineGraphButton, lineLabel);

            chart1.setAlignment(Pos.CENTER);
        VBox chart2 = new VBox();
            Label histoLabel = new Label("Create Histogram");
            chart2.getChildren().addAll(histogramButton, histoLabel);
            chart2.setAlignment(Pos.CENTER);
        VBox chart3 = new VBox();
            Label barLabel = new Label("Create Bar Chart");
            chart3.getChildren().addAll(barChartButton, barLabel);
            chart3.setAlignment(Pos.CENTER);


        chartOptions.getChildren().addAll(chart1, chart2, chart3);
        BorderPane.setMargin(filtersAndMetrics, new Insets(60, 100, 10, 50));//top was 150
        BorderPane.setMargin(chartOptions, new Insets(50, 25, 10, 50));


       Slider slider = new Slider();

        slider.setMax(18.5);
        slider.setMin(10);
        //  slider.setValue(20);
        //   slider.setMaxWidth(200);


        Label fontSize = new Label("Font size ");

        Label fontLabel = new Label();
        fontLabel.setText(String.valueOf(slider.getValue()));
        HBox fontHbox = new HBox();
        slider.setMax(15);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                fontLabel.setText(String.format("%.0f",t1));
                //     impressionFilterOptions.setStyle(String.valueOf(Font.font(slider.getValue())));

                //  bounceLabel.setFont(Font.font(slider.getValue()));


                chartOptions.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));
                filtersAndMetrics.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));
                //  filters.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));
                //  impressionFilterOptions.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));
                options.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));

                //  mainWindow.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt;", slider.getValue()));

            }
        });

        fontHbox.getChildren().addAll(fontSize,fontLabel,slider);
        fontHbox.setMargin(slider, new Insets(25, 20,0, 0));
        fontHbox.setMargin(fontSize, new Insets(25, 20,0, 0));
        fontHbox.setMargin(fontLabel, new Insets(25, 20,0, 0));

        //mainWindow.setStyle("-fx-background-color: #c8e3f0;");
	    
	 //   HBox h = new HBox(mainWindow,slider);
	 //   h.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt; -fx-background-color: #c8e3f0;", slider.valueProperty()));
	    layering.getChildren().addAll(canvas, mainWindow);
	    slider.setMaxWidth(100);
	    toolBar.setRight(fontHbox);
	    toolBar.setLeft(options);
	    mainWindow.setTop(toolBar);
        mainWindow.setLeft(chartOptions);
        mainWindow.setCenter(filtersAndMetrics);
        mainWindow.setStyle("-fx-background-color: #c8e3f0;");
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());

        root.getChildren().add(layering);
	    root.setAutoSizeChildren(true);
        Scene scene = new Scene(root, 1700, 700);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("/GUI.css");
        if(controller.getCampaign().getName() != null){
            primaryStage.setTitle("Ad Auction Dashboard - " + controller.getCampaign().getName());
        } else {
            primaryStage.setTitle("Ad Auction Dashboard");
        }

        primaryStage.show();

        //controller.createBarChar(Metric.TOTAL_IMPRESSIONS, BarChartType.DAY_OF_WEEK,
        //              new Filter(null, null, new ArrayList<>(), null, new ArrayList<String>(), new ArrayList<String>()));
    }

    private VBox getMetricsWindow() {

        HBox metricLayout = new HBox(10);
        VBox metricLabels1 = new VBox(18);
        VBox metricLabels2 = new VBox(18);
        VBox metricBoxes1 = new VBox(10);
        VBox metricBoxes2 = new VBox(10);
        VBox windowLayout = new VBox(10);
        HBox granularityLayout = new HBox(10);
//        HBox files = new HBox(10);
        
//        Label serverLabel = new Label(serverFile.getName());
//        Label impressionLabel = new Label(impressionFile.getName());
//        Label clickLabel = new Label(clicksFile.getName());

        Label bounceRateLabel = new Label("Bounce Rate");
        Label noImpressionsLabel = new Label("No. of Impressions");
        Label noClicksLabel = new Label("No. of Clicks");
        Label noUniquesLabel = new Label("No. of Uniques");
        Label noBouncesLabel = new Label("No. of Bounces");
        Label noConversionsLabel = new Label("No. of Conversions");
        Label ctrLabel = new Label("Click Through Rate");
        Label cpaLabel = new Label("Cost Per Aquisition");
        Label cpcLabel = new Label("Cost per Conversion");
        Label cpmLabel = new Label("Cost per 1000 impressions");
        Label totalCostLabel = new Label("Total Cost");
        Label conversionRateLabel = new Label("Conversion Rate");

        bounceRateField = new TextField();
        noImpressionsField = new TextField();
        noClicksField = new TextField();
        noUniquesField = new TextField();
        noBouncesField = new TextField();
        noConversionsField = new TextField();
        ctrField = new TextField();
        cpaField = new TextField();
        cpcField = new TextField();
        cpmField = new TextField();
        totalCostField = new TextField();
        conversionRateField = new TextField();

        bounceRateField.setEditable(false);
        noImpressionsField.setEditable(false);
        noClicksField.setEditable(false);
        noUniquesField.setEditable(false);
        noBouncesField.setEditable(false);
        bounceRateField.setEditable(false);
        noConversionsField.setEditable(false);
        ctrField.setEditable(false);
        cpaField.setEditable(false);
        cpcField.setEditable(false);
        cpmField.setEditable(false);
        totalCostField.setEditable(false);
        conversionRateField.setEditable(false);


        if(currentFilter != null){
            ArrayList<Click> clicks = controller.filterClickLog(currentFilter);
            ArrayList<Impression> impressions = controller.filterImpressionLog(currentFilter);
            ArrayList<ServerEntry> serverEntries = controller.filterServerLog(currentFilter);

            bounceRateField.setText(format.format(controller.calcBounceRate(serverEntries, clicks)));
            noImpressionsField.setText(format.format(controller.calcImpressions(impressions)));
            noClicksField.setText(format.format(controller.calcClicks(clicks)));
            noUniquesField.setText(format.format(controller.calcUniques(clicks)));
            noBouncesField.setText(format.format(controller.calcBounces(serverEntries)));
            noConversionsField.setText(format.format(controller.calcConversions(serverEntries)));
            ctrField.setText(format.format(controller.calcCTR(clicks, impressions)));
            cpaField.setText(format.format(controller.calcCPA(impressions, clicks, serverEntries)));
            cpcField.setText(format.format(controller.calcCPC(clicks)));
            cpmField.setText(format.format(controller.calcCPM(impressions)));
            totalCostField.setText(format.format(controller.calcTotalCost(impressions, clicks)));
            conversionRateField.setText(format.format(controller.calcConvRate(serverEntries, clicks)));
        } else {


            bounceRateField.setText("" + format.format(controller.getBounceRate()));
            noImpressionsField.setText("" + format.format(controller.getTotalImpressions()));
            noClicksField.setText("" + format.format(controller.getTotalClicks()));
            noUniquesField.setText("" + format.format(controller.getTotalUnique()));
            noBouncesField.setText("" + format.format(controller.getTotalBounces()));
            noConversionsField.setText("" + format.format(controller.getTotalConversions()));
            ctrField.setText("" + format.format(controller.getCTR()));
            cpaField.setText("" + format.format(controller.getCPA()));
            cpcField.setText("" + format.format(controller.getCPC()));
            cpmField.setText("" + format.format(controller.getCPM()));
            totalCostField.setText("" + format.format(controller.getTotalCost()));
            conversionRateField.setText("" + format.format(controller.getConversionRate()));

        }


        TilePane impressionFilterOptions = impressionFilters();

//        Label granularityLabel = new Label("Time Interval");
//        ComboBox<TimeInterval> granularityField =
//				new ComboBox<TimeInterval>(FXCollections.observableArrayList(granularityOptions));
//        granularityField.setValue(TimeInterval.WEEK);


//        granularityLayout.getChildren().addAll(granularityLabel, granularityField);
        metricLabels1.getChildren().addAll(bounceRateLabel, noImpressionsLabel, noClicksLabel, noUniquesLabel,
										   noBouncesLabel, noConversionsLabel);
        metricBoxes1.getChildren().addAll(bounceRateField, noImpressionsField, noClicksField, noUniquesField,
										  noBouncesField, noConversionsField);
        metricLabels2.getChildren().addAll(ctrLabel, cpaLabel, cpcLabel, cpmLabel, totalCostLabel, conversionRateLabel);
        metricBoxes2.getChildren().addAll(ctrField, cpaField, cpcField, cpmField, totalCostField, conversionRateField);

//        files.getChildren().addAll(clickLabel, impressionLabel, serverLabel);

        metricLayout.getChildren().addAll(metricLabels1, metricBoxes1, metricLabels2, metricBoxes2);
        windowLayout.getChildren().addAll(granularityLayout, metricLayout);
//        granularityLayout.setMargin(granularityLabel, new Insets(3, 0, 0, 0));
        //windowLayout.setMargin(granularityLayout, new Insets(10, 10, 5, 10));
        
//        granularityLayout.setMargin(granularityLabel, new Insets(0,0,0,10));
        metricLayout.setMargin(metricLabels2, new Insets(5, 0, 0, 30));
        metricLayout.setMargin(metricLabels1, new Insets(5, 0, 0, 10));


        return windowLayout;
    }

    private VBox histogramWindow() {
        Stage newWindow = new Stage();
        VBox filterPane = new VBox(10);

        TilePane impressionFilterOptions = impressionFilters();

        filterPane.getChildren().add(impressionFilterOptions);
        
        VBox windowLayout = new VBox(10);

        Button createChart = new Button("Create");
        
        createChart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

            	 ArrayList<String> filters = new ArrayList<String>();
                 ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                 ObservableList<Node> filterNodes = null;
                 filterNodes = impressionFilterOptions.getChildren();
                 

                 for (Node n : filterNodes) {
                     if (n instanceof VBox) {
                         for (Node m : ((VBox) n).getChildren()) {
                             if (m instanceof TextField) {
                                 filters.add(((TextField) m).getText());
                             } else if (m instanceof ComboBox) {
                                 filters.add((String) ((ComboBox) m).getValue());
                             } else if(m instanceof DatePicker) {
                                 try{
                                     filters.add(((DatePicker) m).getValue().toString());
                                 }catch(NullPointerException e){
                                     filters.add("");
                                 }
                             } else if(m instanceof CheckComboBox) {
                             	
                             	ArrayList<String> temp2 = new ArrayList<String>();
                             	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                             	for (Object t : temp) {
                             		temp2.add(t.toString());
                             	}
                             	filterArrays.add(temp2);
                             	
                             }
                             
                         }
                     }
                 }

                 LocalDateTime startDate = null;
                 LocalDateTime endDate = null;
                 ArrayList<String> context = new ArrayList<String>();
                 ArrayList<String> ageGroups = new ArrayList<String>();
                 ArrayList<String> incomes = new ArrayList<String>();
                 String gender = new String();
                 gender = null;
                 //Same situation as above, this time looking for each filter
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                 if (!filters.get(0).equals("") && filters.get(0) != null) {
                     startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                 }
                 if (!filters.get(1).equals("") && filters.get(1) != null) {
                     endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                 }

                 if (filters.get(2) != null && !filters.get(2).equals(""))
                     gender = filters.get(2);
                 
                 context =  filterArrays.get(0);
                 ageGroups = filterArrays.get(1);
                 incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                createHistogram(filter).setBackground(back);
                newWindow.close();


            }

        });




        windowLayout.getChildren().addAll(filterPane, createChart);
        windowLayout.setMargin(filterPane, new Insets(20, 10, 0, 20));
        windowLayout.setMargin(createChart, new Insets(0, 10, 0, 20));
        windowLayout.setStyle("-fx-background-color: #c8e3f0;");
        Scene scene = new Scene(windowLayout, 425, 425);

        newWindow.setScene(scene);
        scene.getStylesheets().add("/GUI.css");
        newWindow.setTitle("Create Histogram - " + controller.getCampaign().getName());
        newWindow.show();

	return windowLayout;
//
//        newWindow.setTitle("Create Histogram");
//        newWindow.show();
    }



    private TilePane impressionFilters() {
        TilePane filters = new TilePane(10, 5);

        filters.setPrefColumns(3);
        filters.setPrefRows(2);
        
        VBox ageBox = new VBox(5);
        VBox entryBox = new VBox(5);
        VBox exitBox = new VBox(5);
        VBox contextBox = new VBox(5);
        VBox genderBox = new VBox(5);
        VBox incomeBox = new VBox(5);
        
        Label ageLabel = new Label("Age Group");
        Label entryLabel = new Label("Date From");//TODO add earliest date in campaign to the name
        Label exitLabel = new Label("Date Until");
        Label contextLabel = new Label("Context");
        Label genderLabel = new Label("Gender");
        Label incomeLabel = new Label("Income");


        DatePicker entryDate = new DatePicker(controller.getCampaignStartDate());
        DatePicker exitDate = new DatePicker(controller.getCampaignEndDate());
//        entryDate.setPromptText("Date From");
//        exitDate.setPromptText("Date Until");
        CheckComboBox<String> age = new CheckComboBox<String>(FXCollections.observableArrayList(ageGroups));
        CheckComboBox<String> context = new CheckComboBox<String>(FXCollections.observableArrayList(contextGroups));
        ComboBox<String> gender = new ComboBox<String>(FXCollections.observableArrayList(genders));
        CheckComboBox<String> income = new CheckComboBox<String>(FXCollections.observableArrayList(incomeGroups));

        age.setPrefWidth(100);
        context.setPrefWidth(100);
        gender.setPrefWidth(100);
        income.setPrefWidth(100);
        
//        age.setPromptText("Age Group");
//        context.setPromptText("Context");
//        gender.setPromptText("Gender");
//        income.setPromptText("Income");

        entryBox.getChildren().addAll(entryLabel, entryDate);
        exitBox.getChildren().addAll(exitLabel, exitDate);
        contextBox.getChildren().addAll(contextLabel, context);
        genderBox.getChildren().addAll(genderLabel, gender);
        ageBox.getChildren().addAll(ageLabel, age);
        incomeBox.getChildren().addAll(incomeLabel, income);
        
        filters.getChildren().addAll(entryBox, exitBox, contextBox, genderBox, ageBox, incomeBox);

        return filters;
    }

    private VBox createHistogram(Filter filters) {
        Histogram histogram = controller.createHistogram(5, 2, filters);
        ArrayList<HistogramBar> histogramBars = histogram.getHistogramBars();

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> chart = new BarChart<String, Number>(xAxis, yAxis);

        xAxis.setLabel("Cost Range");
        yAxis.setLabel("Frequency");
        chart.setTitle("Total Cost Histogram");
        XYChart.Series series1 = new XYChart.Series();
        chart.setBarGap(0);
        chart.setCategoryGap(0);

        for (HistogramBar b : histogramBars) {
            series1.getData().add(new XYChart.Data(b.getLowerBound() + " - " + b.getUpperBound(), b.getFrequency()));
        }
        

        chart.getData().addAll(series1);

        TilePane impressionFilterOptions = impressionFilters();

        VBox filterPane = new VBox(10);
        filterPane.getChildren().addAll(impressionFilterOptions);
        HBox metricsGranularity = new HBox(10);

        ObservableList<Node> filterNodes = null;
        filterNodes = impressionFilterOptions.getChildren();
        
        int a = 0;
        int b = 0;

        for (Node n : filterNodes) {
            if (n instanceof VBox) {
                for (Node m : ((VBox) n).getChildren()) {
                     if (m instanceof ComboBox) {
                        ((ComboBox) m).setValue(filters.getGender());
                    } else if(m instanceof DatePicker) {
                        try{
                        	if (a == 0) {
                        		((DatePicker) m).setValue(filters.getStartDate().toLocalDate());
                        		a++;
                        	}
                        	else {
                        		((DatePicker) m).setValue(filters.getEndDate().toLocalDate());
                        	}
                        }catch(NullPointerException e){
                            GUI.displayError(e.getMessage());
                        }
                    } else if(m instanceof CheckComboBox) {
                    	
                    	if (b == 0) {
                    		for (Object o : filters.getContexts()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else if (b == 1) {
                    		for (Object o : filters.getAgeGroups()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else {
                    		for (Object o : filters.getIncomes()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}               	
                    }               
                }
            }
        }
        
        Button filterHistogram = new Button("Filter");

        filterHistogram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                

                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                Histogram histogram = controller.createHistogram(5, 2, filter);
                ArrayList<HistogramBar> bars = histogram.getHistogramBars();//you can then just grab the data from it and use
        		// it in the graph
                XYChart.Series series2 = new XYChart.Series();
                for (HistogramBar b : bars) {
                	series2.getData().add(new XYChart.Data(b.getLowerBound() + " - " + b.getUpperBound(), b.getFrequency()));
                }
                chart.getData().clear();
                chart.getData().add(series2);
                chart.setTitle("Total Cost Histogram");
            }
        });
        
        HBox metricsAndCreate = new HBox(25);
        metricsAndCreate.getChildren().addAll(filterHistogram);
        
        HBox filterOptions = new HBox(10);
        
        Stage window = new Stage();
        Label chartLabel = new Label("Histogram for cost variation");

        VBox filterPrintSave = new VBox(10);

        filterHistogram.setMinWidth(50);
       

        Button printButton = new Button("Print");
        printButton.setOnAction(event -> {
            printChart(chart, window);
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event ->{
            saveAsPng(window);
        });

        printButton.setMinWidth(50);
        filterPrintSave.getChildren().addAll(filterHistogram, printButton, saveButton);
        metricsAndCreate.getChildren().addAll(metricsGranularity, filterPrintSave);
        filterOptions.getChildren().addAll(filterPane, metricsAndCreate);
        filterOptions.setMargin(filterPane, new Insets(0, 20, 0, 0));
        
        VBox vbox = new VBox(15);
        vbox.getChildren().addAll(chart,filterOptions);
        vbox.setStyle("-fx-background-color: #c8e3f0;");
        
        
        Scene scene = new Scene(vbox, 800, 700);
        scene.getStylesheets().add("/GUI.css");
        window.setTitle("Historgram - " + controller.getCampaign().getName());
        window.setScene(scene);
        window.show();

	return vbox;
    }

    private VBox lineWindow() {
        Stage window = new Stage();

        TilePane impressionFilterOptions = impressionFilters();

        HBox impressionMetricsOptions = addImpHbox(Metric.BOUNCES);
        VBox filterPane = new VBox(10);
        Label granLabel = new Label("Granularity: ");
        filterPane.getChildren().addAll(impressionFilterOptions, impressionMetricsOptions);

        ComboBox<TimeInterval> granularity =
				new ComboBox<TimeInterval>(FXCollections.observableArrayList(granularityOptions));
        granularity.setValue(TimeInterval.DAY);

        VBox windowLayout = new VBox(10);
        HBox metricsGranularity = new HBox(10);

        Button createLineGraph = new Button("Create");

        createLineGraph.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                ArrayList<Metric> metrics = new ArrayList<Metric>();

                ObservableList<Node> metricsNodes = null;
                metricsNodes = impressionMetricsOptions.getChildren();


                for (Node n : metricsNodes) {
                    if (n instanceof ChoiceBox) {
                        metrics.add((Metric) ((ChoiceBox) n).getValue());
                    }
                }


                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                

                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);
                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                createLineChart(metrics.get(0), filter, granularity.getValue()).setBackground(back);
                window.close();

            }
        });

        metricsGranularity.getChildren().addAll(granLabel, granularity);
        windowLayout.getChildren().addAll(filterPane, metricsGranularity, createLineGraph);
        windowLayout.setStyle("-fx-background-color: #c8e3f0;");
        windowLayout.setMargin(filterPane, new Insets(20, 10, 0, 20));
        windowLayout.setMargin(metricsGranularity, new Insets(0, 10, 0, 20));
        windowLayout.setMargin(createLineGraph, new Insets(0, 10, 0, 20));
        
        Scene scene = new Scene(windowLayout, 425, 425);
        window.setScene(scene);
        scene.getStylesheets().add("/GUI.css");
        window.setTitle("Create LineGraph - " + controller.getCampaign().getName());
        window.show();

	return windowLayout;

    }
    
    private VBox barWindow() {
        Stage window = new Stage();

        TilePane impressionFilterOptions = impressionFilters();

        HBox impressionMetricsOptions = addImpHbox(Metric.BOUNCES);
        VBox filterPane = new VBox(10);
        Label barTypeLabel = new Label("Type: ");
        filterPane.getChildren().addAll(impressionFilterOptions, impressionMetricsOptions);

        ComboBox<BarChartType> type =
				new ComboBox<BarChartType>(FXCollections.observableArrayList(barChartType));
        type.setValue(BarChartType.DAY_OF_WEEK);

        VBox windowLayout = new VBox(10);
        HBox metricsType = new HBox(10);

        Button filterBarChart = new Button("Filter");

        filterBarChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                ArrayList<Metric> metrics = new ArrayList<Metric>();

                ObservableList<Node> metricsNodes = null;
                metricsNodes = impressionMetricsOptions.getChildren();


                for (Node n : metricsNodes) {
                    if (n instanceof ChoiceBox) {
                        metrics.add((Metric) ((ChoiceBox) n).getValue());
                    }
                }


                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                

                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                createBarChart(metrics.get(0), filter, type.getValue()).setBackground(back);
                window.close();

            }
        });

        metricsType.getChildren().addAll(barTypeLabel, type);
        windowLayout.getChildren().addAll(filterPane, metricsType, filterBarChart);
        windowLayout.setMargin(filterPane, new Insets(20, 10, 0, 20));
        windowLayout.setMargin(metricsType, new Insets(0, 10, 0, 20));
        windowLayout.setMargin(filterBarChart, new Insets(0, 10, 0, 20));
        
        windowLayout.setStyle("-fx-background-color: #c8e3f0;");

        Scene scene = new Scene(windowLayout, 425, 425);
        window.setScene(scene);
        scene.getStylesheets().add("/GUI.css");
        window.setTitle("Create Bar Chart - " + controller.getCampaign().getName());
        window.show();

	return windowLayout;

    }
    
    private VBox createBarChart(Metric metric, Filter filters, BarChartType type) {
        Stage stage = new Stage();
        stage.setTitle("Bar Chart - " + controller.getCampaign().getName());
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("" + type);


        final BarChart<String, Number> barChart =
                new BarChart<String, Number>(xAxis, yAxis);

        barChart.setTitle(metric + " bar chart");

        XYChart.Series series = new XYChart.Series();
        series.setName(metric + " per " + type);
        

        BarGraph barGraph = new BarGraph(metric, type, filters, controller); //this is where the
		// filters, metric and granularity will be passed
        ArrayList<Bar> dataBars = barGraph.getBars();//you can then just grab the data from it and use
		// it in the graph

        for (Bar b : dataBars) {
        	series.getData().add(new XYChart.Data(b.getCategory(), b.getMetric()));
        }

        TilePane impressionFilterOptions = impressionFilters();

        HBox impressionMetricsOptions = addImpHbox(metric);
        VBox filterPane = new VBox(10);
        Label granLabel = new Label("Type: ");
        filterPane.getChildren().addAll(impressionFilterOptions, impressionMetricsOptions);


        ComboBox<BarChartType> barType =
				new ComboBox<BarChartType>(FXCollections.observableArrayList(barChartType));
        barType.setValue(type);

        VBox windowLayout = new VBox(10);
        HBox metricsGranularity = new HBox(10);

        ObservableList<Node> filterNodes = null;
        filterNodes = impressionFilterOptions.getChildren();
        
        int a = 0;
        int b = 0;

        for (Node n : filterNodes) {
            if (n instanceof VBox) {
                for (Node m : ((VBox) n).getChildren()) {
                     if (m instanceof ComboBox) {
                        ((ComboBox) m).setValue(filters.getGender());
                    } else if(m instanceof DatePicker) {
                        try{
                        	if (a == 0) {
                        		((DatePicker) m).setValue(filters.getStartDate().toLocalDate());
                        		a++;
                        	}
                        	else {
                        		((DatePicker) m).setValue(filters.getEndDate().toLocalDate());
                        	}
                        }catch(NullPointerException e){
                            GUI.displayError(e.getMessage());
                        }
                    } else if(m instanceof CheckComboBox) {
                    	
                    	if (b == 0) {
                    		for (Object o : filters.getContexts()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else if (b == 1) {
                    		for (Object o : filters.getAgeGroups()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else {
                    		for (Object o : filters.getIncomes()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}               	
                    }               
                }
            }
        }
        
        Button filterBarChart = new Button("Filter");

        filterBarChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                ArrayList<Metric> metrics = new ArrayList<Metric>();

                ObservableList<Node> metricsNodes = null;
                metricsNodes = impressionMetricsOptions.getChildren();


                for (Node n : metricsNodes) {
                    if (n instanceof ChoiceBox) {
                        metrics.add((Metric) ((ChoiceBox) n).getValue());
                    }
                }


                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                
                

                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                BarGraph barGraph = new BarGraph(metrics.get(0), barType.getValue(), filter, controller); //this is where the
        		// filters, metric and granularity will be passed
                ArrayList<Bar> dataBars = barGraph.getBars();//you can then just grab the data from it and use
        		// it in the graph
                XYChart.Series series2 = new XYChart.Series();
                series2.setName(metrics.get(0) + " per " + barType.getValue());
                for (Bar b : dataBars) {
                    series2.getData().add(new XYChart.Data(b.getCategory(), b.getMetric()));
                }
                xAxis.setLabel("" + barType.getValue());
                barChart.getData().clear();
                barChart.getData().add(series2);
                barChart.setTitle(metrics.get(0) + " Bar chart");
            }
        });

        barChart.setMinHeight(600);
        
        VBox filterPrintSave = new VBox(10);
        metricsGranularity.getChildren().addAll(granLabel, barType);
        HBox metricsAndCreate = new HBox(25);
       filterBarChart.setMinWidth(50);
       

        Button printButton = new Button("Print");
        Button saveButton = new Button("Save");

        printButton.setOnAction(event -> {
            printChart(barChart, stage);
        });

        saveButton.setOnAction(event -> {
            saveAsPng(stage);
        });
        
        printButton.setMinWidth(50);
        filterPrintSave.getChildren().addAll(filterBarChart, printButton, saveButton);
        metricsAndCreate.getChildren().addAll(metricsGranularity, filterPrintSave);
        VBox mainWindow = new VBox(20);
        HBox filterOptions = new HBox(10);
        filterOptions.getChildren().addAll(filterPane, metricsAndCreate);
        mainWindow.getChildren().addAll(barChart, filterOptions);
        mainWindow.setStyle("-fx-background-color: #c8e3f0;");
        mainWindow.setMargin(filterOptions, new Insets(0, 20, 0, 10));
        Scene scene = new Scene(mainWindow, 900, 800);
        //lineChart.getData().add(series);
        barChart.getData().addAll(series);
        scene.getStylesheets().add("/GUI.css");
        stage.setScene(scene);
        stage.show();
	
	return mainWindow;

    }


    public void saveAsPng(Stage stage) {
        String timeStamp = new SimpleDateFormat("HHmmss_yyyyMMdd").format(Calendar.getInstance().getTime());
        WritableImage image = stage.getScene().snapshot(null);


        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().getStylesheets().add("/GUI.css");
        dialog.setTitle("AdAuction");
        dialog.setHeaderText("Save Chart");
        dialog.setContentText("Please enter the name of the chart:");
        File file = null;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add("/GUI.css");
            alert.setHeaderText(null);
            alert.setTitle("Save Chart");
            if (result.get().equals("")) {
                alert.setContentText("Chart saving failed. You need to provide the name of the chart. Please try again.");
            } else {
                alert.setContentText("Campaign saved as \"" + result.get() + "\".");
                file = new File(result.get() + timeStamp + ".png");

            }

            alert.showAndWait();


            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                GUI.displayError(e.getMessage());
            }
        }
    }


    //Metric
    public HBox addImpHbox(Metric metric) {
        ChoiceBox<Metric> impressionMetricChoices = new ChoiceBox<Metric>();
        for(Metric metrics : Metric.values()){
            impressionMetricChoices.getItems().add(metrics);
        }
        impressionMetricChoices.setValue(metric);
        Label impressionMetricLabel = new Label("Metric: ");
        HBox impMetricBox = new HBox(impressionMetricLabel, impressionMetricChoices);

        final String cssDefault = "-fx-border-insets: 10.0 10.0 0.0 10.0;\n"
                + "-fx-border-width: 3;\n"
                + "-fx-padding: 10;\n";


        return impMetricBox;
    }



    private VBox createLineChart(Metric metric, Filter filters, TimeInterval interval) {
        Stage stage = new Stage();
        stage.setTitle("Line Chart - " + controller.getCampaign().getName());
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");


        LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);

        lineChart.setTitle(metric + " line chart");
        lineChart.setAnimated(false);
        
        XYChart.Series series = new XYChart.Series();
        series.setName(metric + " per " + interval);


        LineGraph lineGraph = new LineGraph(metric, interval, controller, filters); //this is where the
        // filters, metric and granularity will be passed
        ArrayList<DataPoint> dataPoints = lineGraph.getDataPoints();//you can then just grab the data from it and use
        // it in the graph


        for (DataPoint dp : dataPoints) {
            series.getData().add(new XYChart.Data(dp.getStartTime().toString(), dp.getMetric()));
        }


        lineChart.setTitle(metric + " line chart");

        TilePane impressionFilterOptions = impressionFilters();

        HBox impressionMetricsOptions = addImpHbox(metric);
        VBox filterPane = new VBox(10);
        Label granLabel = new Label("Granularity: ");
        filterPane.getChildren().addAll(impressionFilterOptions, impressionMetricsOptions);

        ComboBox<TimeInterval> granularity =
                new ComboBox<TimeInterval>(FXCollections.observableArrayList(granularityOptions));

        ObservableList<Node> filterNodes = null;
        filterNodes = impressionFilterOptions.getChildren();
        
        int a = 0;
        int b = 0;

        for (Node n : filterNodes) {
            if (n instanceof VBox) {
                for (Node m : ((VBox) n).getChildren()) {
                     if (m instanceof ComboBox) {
                        ((ComboBox) m).setValue(filters.getGender());
                    } else if(m instanceof DatePicker) {
                        try{
                        	if (a == 0) {
                        		((DatePicker) m).setValue(filters.getStartDate().toLocalDate());
                        		a++;
                        	}
                        	else {
                        		((DatePicker) m).setValue(filters.getEndDate().toLocalDate());
                        	}
                        }catch(NullPointerException e){
                            GUI.displayError(e.getMessage());
                        }
                    } else if(m instanceof CheckComboBox) {
                    	
                    	if (b == 0) {
                    		for (Object o : filters.getContexts()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else if (b == 1) {
                    		for (Object o : filters.getAgeGroups()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    	else {
                    		for (Object o : filters.getIncomes()) {
                    			((CheckComboBox) m).getCheckModel().check(o);
                    		}
                    		b++;
                    	}
                    }               
                }
            }
        }
        
        

        granularity.setValue(interval);

        VBox windowLayout = new VBox(10);
        HBox metricsGranularity = new HBox(10);

        Button filterLineGraph = new Button("Filter");
        final VBox mainWindow = new VBox(20);

        filterLineGraph.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                ArrayList<Metric> metrics = new ArrayList<Metric>();

                ObservableList<Node> metricsNodes = null;
                metricsNodes = impressionMetricsOptions.getChildren();

                ObservableList<Node> createNodes = metricsGranularity.getChildren();

                for (Node n : metricsNodes) {
                    if (n instanceof ChoiceBox) {
                        metrics.add((Metric) ((ChoiceBox) n).getValue());
                    }
                }


                ArrayList<String> filters = new ArrayList<String>();
                ArrayList<ArrayList<String>> filterArrays = new ArrayList<ArrayList<String>>();
                ObservableList<Node> filterNodes = null;
                filterNodes = impressionFilterOptions.getChildren();
                

                
                for (Node n : filterNodes) {
                    if (n instanceof VBox) {
                        for (Node m : ((VBox) n).getChildren()) {
                            if (m instanceof TextField) {
                                filters.add(((TextField) m).getText());
                            } else if (m instanceof ComboBox) {
                                filters.add((String) ((ComboBox) m).getValue());
                            } else if(m instanceof DatePicker) {
                                try{
                                    filters.add(((DatePicker) m).getValue().toString());
                                }catch(NullPointerException e){
                                    filters.add("");
                                }
                            } else if(m instanceof CheckComboBox) {
                            	
                            	ArrayList<String> temp2 = new ArrayList<String>();
                            	List<Object> temp = ((CheckComboBox) m).getCheckModel().getCheckedItems();
                            	for (Object t : temp) {
                            		temp2.add(t.toString());
                            	}
                            	filterArrays.add(temp2);
                            	
                            }
                            
                        }
                    }
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                ArrayList<String> context = new ArrayList<String>();
                ArrayList<String> ageGroups = new ArrayList<String>();
                ArrayList<String> incomes = new ArrayList<String>();
                String gender = new String();
                gender = null;
                //Same situation as above, this time looking for each filter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (!filters.get(0).equals("") && filters.get(0) != null) {
                    startDate = LocalDate.parse(filters.get(0), formatter).atStartOfDay();
                }
                if (!filters.get(1).equals("") && filters.get(1) != null) {
                    endDate = LocalDate.parse(filters.get(1), formatter).atTime(23, 59, 59);
                }

                if (filters.get(2) != null && !filters.get(2).equals(""))
                    gender = filters.get(2);
                
                context =  filterArrays.get(0);
                ageGroups = filterArrays.get(1);
                incomes = filterArrays.get(2);

                Filter filter = new Filter(startDate, endDate, context, gender, ageGroups, incomes);
                LineGraph lineGraph = new LineGraph(metrics.get(0), granularity.getValue(), controller, filter); //this is where the
                // filters, metric and granularity will be passed
                ArrayList<DataPoint> dataPoints = lineGraph.getDataPoints();//you can then just grab the data from it and use
                // it in the graph
                XYChart.Series series2 = new XYChart.Series();
                series2.setName(metrics.get(0) + " per " + granularity.getValue());
                for (DataPoint dp : dataPoints) {
                    series2.getData().add(new XYChart.Data(dp.getStartTime().toString(), dp.getMetric()));
                }
                lineChart.getData().clear();
                //lineChart.getData().add(series2);
                new ZoomManager<>(mainWindow, lineChart, series2);
                lineChart.setTitle(metrics.get(0) + " line chart");
            }
        });

        lineChart.setMinHeight(600);

        metricsGranularity.getChildren().addAll(granLabel, granularity);
        HBox metricsAndCreate = new HBox(25);
        

        VBox filterPrintSave = new VBox(10);
        filterLineGraph.setMinWidth(50);
       

        Button printButton = new Button("Print");
        printButton.setOnAction(event -> {
            printChart(lineChart, stage);
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            saveAsPng(stage);
        });
        
        printButton.setMinWidth(50);
        filterPrintSave.getChildren().addAll(filterLineGraph, printButton, saveButton);
        metricsAndCreate.getChildren().addAll(metricsGranularity, filterPrintSave);
        
        HBox filterOptions = new HBox(10);
        filterOptions.getChildren().addAll(filterPane, metricsAndCreate);
        ScrollPane pane = new ScrollPane();
        pane.setContent(lineChart);


        mainWindow.getChildren().addAll(lineChart, filterOptions);
        mainWindow.setMargin(filterOptions, new Insets(0, 20, 0, 10));
        mainWindow.setStyle("-fx-background-color: #c8e3f0;");
        Scene scene = new Scene(mainWindow, 900, 800);


        new ZoomManager<>(mainWindow, lineChart, series);
        scene.getStylesheets().add("/GUI.css");
        stage.setScene(scene);
        stage.show();
	    
	return mainWindow;

//        printChart(lineChart, stage);
    }
    private void fileChooserWindow() {

    	Label bounceLabel = new Label("How many pages define a bounce: ");
    	TextField bounceField = new TextField("1");
    	HBox bounceBox = new HBox(5);
    	
    	bounceBox.getChildren().addAll(bounceLabel, bounceField);
    	
    	Button loadButton = new Button("Load Previous...");

    	Button deleteButton = new Button("Delete Previous...");
    	VBox loadDeleteBox = new VBox(10);
        VBox fileChooserButtons = new VBox(10);
       
        BorderPane fileChooserLayout = new BorderPane();

        HBox impressionVBox = new HBox(50);
        HBox clicksVBox = new HBox(50);
        HBox serverVBox = new HBox(50);
        
        Stage newWindow = new Stage();
        newWindow.setTitle("Load files");

        Button loadImpressions = new Button("Load Impression file");
        Button loadClicks = new Button("Load Clicks file");
        Button loadServer = new Button("Load Server file");
        Button continueButton = new Button("Continue");

        Label serverFileLabel = new Label();
        Label clickFileLabel = new Label();
        Label impressionFileLabel = new Label();

        impressionVBox.getChildren().addAll(loadImpressions, impressionFileLabel);
        clicksVBox.getChildren().addAll(loadClicks, clickFileLabel);
        serverVBox.getChildren().addAll(loadServer, serverFileLabel);
        
        loadButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
                File[] files = new File(System.getProperty("user.dir") + File.separator + Controller.AD_AUCTION_FOLDER + File.separator + Controller.CAMPAIGN_FOLDER).listFiles();
                ArrayList<String> campaigns = new ArrayList<String>();
                if(files != null){
                    for (File file : files) {
                        if (file.isDirectory()) {
                            campaigns.add(file.getName());
                        }
                    }

                    if(campaigns.size() == 0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Load Campaign");
                        alert.getDialogPane().getStylesheets().add("/GUI.css");
                        alert.setHeaderText(null);
                        alert.setContentText("There are no campaigns to load.");

                        alert.showAndWait();
                    }
                    else{
                        ChoiceDialog<String> dialog = new ChoiceDialog<>(campaigns.get(0), campaigns);
                        dialog.setTitle("AdAuction");
                        dialog.setHeaderText("Load Campaign");
                        dialog.setContentText("Select campaign to be loaded:");
                        dialog.getDialogPane().getStylesheets().add("/GUI.css");


                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            currentFilter = controller.loadCampaign(result.get());
                            try {
                                mainWindow();
                            } catch (Exception e) {
                                GUI.displayError(e.getMessage());
                            }
                            newWindow.close();
                        }
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Load Campaign");
                    alert.getDialogPane().getStylesheets().add("/GUI.css");
                    alert.setHeaderText(null);
                    alert.setContentText("There are no campaigns to load.");

                    alert.showAndWait();
                }
			}
		});



        deleteButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
//			    String userDirectory = System.getProperty("user.dir");
                File[] files = new File(System.getProperty("user.dir") + File.separator + Controller.AD_AUCTION_FOLDER + File.separator + Controller.CAMPAIGN_FOLDER).listFiles();
                ArrayList<String> campaigns = new ArrayList<String>();
                if(files != null){
                    for (File file : files) {
                        if (file.isDirectory()) {
                            campaigns.add(file.getName());
                        }
                    }

                    if(campaigns.size() == 0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delete Campaign");
                        alert.getDialogPane().getStylesheets().add("/GUI.css");
                        alert.setHeaderText(null);
                        alert.setContentText("There are no campaigns to show.");

                        alert.showAndWait();
                    }
                    else{
                        ChoiceDialog<String> dialog = new ChoiceDialog<>(campaigns.get(0), campaigns);
                        dialog.setTitle("AdAuction");
                        dialog.setHeaderText("Delete Campaign");
                        dialog.setContentText("Select campaign to be deleted:");
                        dialog.getDialogPane().getStylesheets().add("/GUI.css");


//                        CheckComboBox<String> result = new CheckComboBox<>(FXCollections.observableArrayList());

                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            File currentFile = new File(System.getProperty("user.dir") + File.separator + Controller.AD_AUCTION_FOLDER + File.separator + Controller.CAMPAIGN_FOLDER + File.separator +  result.get());
                            try {
                                deleteDirectory(currentFile);
                            } catch (Exception e) {
                                GUI.displayError(e.getMessage());
                            }
                        }

                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete Campaign");
                    alert.getDialogPane().getStylesheets().add("/GUI.css");
                    alert.setHeaderText(null);
                    alert.setContentText("There are no campaigns to show.");

                    alert.showAndWait();
                }
            }
        });
        
        loadImpressions.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Impressions File");
                File tempFile = fileChooser.showOpenDialog(newWindow);
                if (tempFile != null) {
	                try {
						Scanner input = new Scanner(tempFile);
						if(input.nextLine().split(",").length == 7) {
							impressionFile = tempFile;
							impressionFileLabel.setText(impressionFile.getName());
						}
						else {
							displayError("File contains the wrong number of columns, check it's the right one?");
						}
						input.close();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
                        GUI.displayError(e.getMessage());
					}
                }
            }
        });

        loadClicks.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
            	FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Clicks File");
                File tempFile = fileChooser.showOpenDialog(newWindow);
                if (tempFile != null) {
	                try {
						Scanner input = new Scanner(tempFile);
						if(input.nextLine().split(",").length == 3) {
							clicksFile = tempFile;
							clickFileLabel.setText(clicksFile.getName());
						}
						else {
							displayError("File contains the wrong number of columns, check it's the right one?");
						}
						input.close();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
                        GUI.displayError(e.getMessage());
					}
                }
            }
        });

        loadServer.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
            	FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Clicks File");
                File tempFile = fileChooser.showOpenDialog(newWindow);
                if (tempFile != null) {
	                try {
						Scanner input = new Scanner(tempFile);
						if(input.nextLine().split(",").length == 5) {
							serverFile = tempFile;
							serverFileLabel.setText(serverFile.getName());
						}
						else {
							displayError("File contains the wrong number of columns");
						}
						input.close();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
                        GUI.displayError(e.getMessage());
					}
                }
            }

        });

        continueButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (serverFile != null && clicksFile != null && impressionFile != null) {
                	
                	try {
                		Integer bounceRate = Integer.parseInt(bounceField.getText());

	                    //campaign.setBounceDefinition(Integer.parseInt(bounceDefiner.getText()));
	                    
	                    controller.loadNewCampaign(serverFile.getAbsolutePath(), clicksFile.getAbsolutePath(), impressionFile.getAbsolutePath(), bounceRate);
	                    // TODO change 1 to use a bounceDefinition specified by the user
	                    try {
	                        mainWindow();
	                    } catch (Exception e) {
                            GUI.displayError(e.getMessage());
	                    }
	                    newWindow.close();
                	}
                	catch (NumberFormatException e) {
                		displayError("Bounce value must be an integer");
                	}
                }
                else {
                	displayError("Not enough files selected");
                }
            }
        });
	    Slider slider = new Slider();
        //  fileChooserLayout.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt; -fx-background-color: #c8e3f0;", slider.valueProperty()));
        slider.setMax(28);
        slider.setMin(10);
        slider.setValue(14);
        slider.setMaxWidth(150);


        Label fontSize = new Label("Font size ");
        Label fontLabel = new Label();
        fontLabel.setText(String.valueOf(slider.getValue()));
        HBox fontHbox = new HBox();
        //  fontLabel.setMinSize(10,10);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                fontLabel.setText(String.format("%.0f",t1));

                bounceLabel.setFont(Font.font(slider.getValue()));
                bounceField.setFont(Font.font(slider.getValue()));
                loadButton.setFont(Font.font(slider.getValue()));
                deleteButton.setFont(Font.font(slider.getValue()));
                loadImpressions.setFont(Font.font(slider.getValue()));
                loadClicks.setFont(Font.font(slider.getValue()));
                loadServer.setFont(Font.font(slider.getValue()));
                continueButton.setFont(Font.font(slider.getValue()));
                serverFileLabel.setFont(Font.font(slider.getValue()));
                clickFileLabel.setFont(Font.font(slider.getValue()));
                impressionFileLabel.setFont(Font.font(slider.getValue()));
                //  fileChooserLayout.styleProperty().bind(Bindings.format("-fx-font-size: %.1fpt; -fx-background-color: #c8e3f0;", slider.getValue()));

            }
        });

        fontHbox.getChildren().addAll(fontSize,fontLabel,slider);
	    

        loadServer.setMinWidth(125);
        loadClicks.setMinWidth(125);
        continueButton.setMinWidth(125);

        
        loadDeleteBox.getChildren().addAll(loadButton,deleteButton);


        Separator sep = new Separator();
        sep.setMaxWidth(250);
        fileChooserButtons.getChildren().addAll(loadButton, deleteButton, sep,  clicksVBox, impressionVBox, serverVBox, bounceBox, continueButton);
        fileChooserButtons.setMargin(loadButton, new Insets(5, 10, 0, 20));
        fileChooserButtons.setMargin(deleteButton, new Insets(5,  10, 0, 20));
        fileChooserButtons.setMargin(clicksVBox, new Insets(5, 10, 0, 20));
        fileChooserButtons.setMargin(impressionVBox, new Insets(10, 10, 0, 20));
        fileChooserButtons.setMargin(serverVBox, new Insets(10, 10, 0, 20));
        fileChooserButtons.setMargin(continueButton, new Insets(30, 10, 10, 20));
        fileChooserButtons.setMargin(bounceBox, new Insets(10, 10, 10, 20));

       // fileChooserLayout.getChildren().addAll(fileChooserButtons,slider);
	    fileChooserLayout.setLeft(fileChooserButtons);
        fileChooserLayout.setRight(fontHbox);
        fileChooserLayout.setStyle("-fx-background-color: #c8e3f0;");


        Scene scene = new Scene(fileChooserLayout, 1050, 500);
        fileChooserLayout.getStylesheets().add("/GUI.css");
        newWindow.setScene(scene);
        newWindow.show();
    }

    /**
     *
     * @param node - element to be printed
     * @param stage - stage containing the chart
     */
    public void printChart(Node node, Stage stage){
        //Get the Default Printer
        Printer defaultPrinter = Printer.getDefaultPrinter();
        Scale scale = null;
        if (defaultPrinter != null)
        {


            // Create a printer job for the default printer
            PrinterJob job = PrinterJob.createPrinterJob();
            JobSettings jobSettings = job.getJobSettings();
            jobSettings.setPageLayout(defaultPrinter.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM));
            jobSettings.setPrintColor(PrintColor.MONOCHROME);


            if (job.showPageSetupDialog(stage))
            {
                // Show the printer job status

                scale = new Scale(jobSettings.getPageLayout().getPrintableWidth() / node.getBoundsInParent().getWidth(), jobSettings.getPageLayout().getPrintableHeight()/node.getBoundsInParent().getHeight());
                node.getTransforms().add(scale);
                // Print the node
                boolean printed = job.printPage(node);

                if (printed)
                {
                    // End the printer job
                    job.endJob();

                }
                else
                {
                    // Write Error Message
                    System.err.println("Printing failed.");
                }
            }
            else
            {
                // Write Error Message
                System.err.println("Could not create a printer job.");
            }

        }
        else
        {
            System.err.println("No printers installed.");
        }
        if(scale != null){
            node.getTransforms().remove(scale);
        }
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    
    public static void displayError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.getDialogPane().getStylesheets().add("/GUI.css");
        alert.showAndWait();
    }
}