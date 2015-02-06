/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcdcgen;

//Import MCDC
import ahb_fx1.mcdc.Mcdc;
import ahb_fx1.mcdc.PSTG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import javafx.animation.FadeTransition;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

//import javafx.scene.image.ImageView;
import javafx.scene.image.*;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author ariful
 */
public class AHB_Fx1 extends Application {
    private static String OS = System.getProperty("os.name").toLowerCase();
    
    private FadeTransition fadeIn = new FadeTransition(Duration.millis(3000));
    
    
    private TableView   tblResult;
    private TableView   tblSummery;
    private TextField   txtIterator;
    private TextField   txtExitLimit;
    
//   FOR SA
    private TextField   txtInitialTemp;
    private TextField   txtCoolingRate;
    
//    For HC
    private TextField   txtHcIterator;
    
//    FOR GD    
    private TextField   txtGdIterator;
    private TextField   txtGdFinalLevel;
    
//    For Lahc
    private TextField   txtLahcIterator;
    private TextField   txtLahcMemorySize;
    
//    PS
    private TextField   txtPsIterator;
    private TextField   txtPsSize;
    
    private Mcdc        mcdc;
    private PSTG        pstg;
    
    private ArrayList   saTime, gdTime, hcTime, lahcTime, psTime;
    private ArrayList   saPairs, gdPairs, hcPairs, lahcPairs, psResult;
    
    private TextField   txtExpression ;
    private TextField   txtPairwiseExpression;
    private Button      btnExportResult;
    
    private Label       lblStatus;
     
    
//    Data
    private final ObservableList<ResultRow> data = FXCollections.observableArrayList();
    private final ObservableList<SummeryRow> sumData = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) {
                        
        
        BorderPane  layout = new BorderPane();
        layout.setTop(this.setTop());
        layout.setLeft(this.setAlgorithmControl());
        layout.setRight(this.setRight());
        
        layout.setCenter(this.setResultTable());
//        layout.setCenter(this.setResultTabs());
        
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        Scene scene = new Scene(layout, bounds.getWidth(), bounds.getHeight());
        
        primaryStage.setTitle("MCDC Pro - Automated Test Case Generator for MCDC & Pairwise Testing");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }//end start function

    private static boolean isWindows() {
 
		return (OS.indexOf("win") >= 0);
 
	}
 
    private static boolean isMac() {

            return (OS.indexOf("mac") >= 0);

    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            
            launch(args);                
            
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
        
    }
    
    private VBox setRight(){
        VBox right = new VBox();
    
        right.setPadding(new Insets(15, 12, 15, 12));
        right.setSpacing(10);
//        right.setStyle("-fx-background-color:#262626;");
        
        lblStatus = new Label();
        
        lblStatus.setText("Idle");
        lblStatus.setStyle("-fx-font-weight:bold;");
        lblStatus.setPadding(new Insets(15, 12, 15,12));
        
        fadeIn.setNode(lblStatus);

        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        
        
//        Export result
        btnExportResult = new Button("Export Result");
        btnExportResult.setDisable(true);
        btnExportResult.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e)  {
             try {
                writeExcel();
                
              }
              catch (Exception ex) {
                ex.printStackTrace();
              }//catch
            }//end void
        });
        
           
            
        right.getChildren().addAll(lblStatus,btnExportResult);
        
        return right;
    }
    
    private HBox setTop(){
        HBox hbox = new HBox();
        
        try{                    
        
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: gray;");
        
        
        Label title = new    Label();
        title.setText("Insert Boolean Expression");        
        title.setTextFill(Color.web("White"));
        title.setStyle("-fx-font-size:1.2em;");
        
        txtExpression = new TextField();
        txtExpression.setText("(a&&b||c)");
        txtExpression.setMinWidth(350);
        txtExpression.setStyle("-fx-font-size:1.3em; -fx-font-weight:bold;");
        
        
        Label ptitle = new    Label();
        ptitle.setText("Pairwise Expression");        
        ptitle.setTextFill(Color.web("White"));
        ptitle.setStyle("-fx-font-size:1.2em;");
        
        txtPairwiseExpression = new TextField();
        txtPairwiseExpression.setText("-i 2,2,2 -t 2");
        txtPairwiseExpression.setMinWidth(350);
        txtPairwiseExpression.setStyle("-fx-font-size:1.3em; -fx-font-weight:bold;");
        
        Button btnSearch = new Button();
        btnSearch.setText("Search");
        
//        Search Click Event
               
        btnSearch.setOnAction((ActionEvent e) -> {
                            
            ArrayList <String> result      = new ArrayList<String>();
            
            lblStatus.setText("In Progress....");
            tblResult.getItems().clear();            
            tblSummery.getItems().clear();      
            
            txtExpression.setDisable(true);
            txtIterator.setDisable(true);                        
                                    
            
            result = this.runLoop(txtExpression.getText(), Integer.parseInt(txtIterator.getText())
                                , Integer.parseInt(txtExitLimit.getText()));                        
                        
            this.addToSummery();
            
            lblStatus.setText("Process Complete");
            fadeIn.playFromStart();
            
            txtExpression.setDisable(false);
            txtIterator.setDisable(false);
            btnExportResult.setDisable(false);
            
        });
                
        
        
            hbox.getChildren().addAll(title,txtExpression, ptitle, txtPairwiseExpression, btnSearch);
        
            return hbox;    
        
        }catch(Exception e){
            System.out.println("setTop.Error: "+e.getMessage());
            return hbox;
        }
        
    }//end function
    
    
    
    
    private Double calculateAverageTime(ArrayList<Double> lists){
        
//        System.out.println(lists);
        
        Double avgTime = new Double(0.0);
        
         if(!lists.isEmpty()) {
            for (Double list : lists) {
                avgTime += list;
//                System.out.println("time: "+list+ " avg: "+avgTime);
            }
            
          }else{
             
         }
                      
        return avgTime.doubleValue() / lists.size();
        
    }//end function
    
       
    private VBox setAlgorithmControl()
    {
        VBox vbox = new VBox();
        Label vboxtitle      = new Label();
        Insets inset = new Insets(15,12,15,12);
        vbox.setPadding(inset);
//        vbox.maxWidth(200);
//        Title
        vboxtitle.setText("Algorithm Configuration");
        vboxtitle.setPadding(new Insets(5, 0, 10, 0));
        vboxtitle.setStyle("-fx-font-weight:bold");
//        vboxtitle.setPadding(new Insets(15, 12, 15,12));
                        
//        Global Configuration
        TitledPane tpGlobal = new TitledPane();
        tpGlobal.setText("Global Configuration");
                
        GridPane grGlobal = new GridPane();
        grGlobal.setVgap(4);
//        grGlobal.setPadding(new Insets(5,5,5,5));
        
        Label lblIterator = new Label("Iteration Time");
//        lblIterator.setPadding(inset);
        
//        Iteration Number
        txtIterator = new TextField();
        txtIterator.setText("1");
        txtIterator.setMaxWidth(50);
        
        txtExitLimit    = new TextField();
        txtExitLimit.setText("100000");
                
        grGlobal.add(lblIterator, 0, 0);
        grGlobal.add(txtIterator, 1, 0);
        
        grGlobal.add(new Label("MCDC Exit Limit"), 0, 1);
        grGlobal.add(txtExitLimit, 1, 1);
        
        tpGlobal.setContent(grGlobal);
        
//      Simulated Annealing  
        TitledPane tpSa     = new TitledPane();
        tpSa.setText("Simulated Annealing");
        GridPane gpSa       = new GridPane();
        gpSa.setVgap(5);
        
                
        txtInitialTemp = new TextField();
        txtCoolingRate = new TextField();
        
        txtInitialTemp.setText("10000.00");
        txtCoolingRate.setText("0.01");
        
        gpSa.add(new Label("Initial Temperature"), 0, 0);
        gpSa.add(txtInitialTemp, 1, 0);
        
        gpSa.add(new Label("Cooling Rate"), 0, 1);
        gpSa.add(txtCoolingRate, 1, 1);
        
        tpSa.setContent(gpSa);
        
                
//        Hill Climbing
        TitledPane tpHc     = new TitledPane();
        tpHc.setText("Hill Climbing");
        GridPane gpHc       = new GridPane();
        gpHc.setVgap(5);
        
        txtHcIterator       = new TextField();
        txtHcIterator.setText("10");
        
        gpHc.add(new Label("Iteration"), 0, 0);
        gpHc.add(txtHcIterator, 1, 0);
        
        
        tpHc.setContent(gpHc);
        
//        Great Deluge
        TitledPane tpGd     = new TitledPane();
        tpGd.setText("Great Deluge");
        GridPane    gpGd    = new GridPane();
        gpGd.setVgap(5);
        
        gpGd.add(new Label("Iteration"), 0, 0);
        txtGdIterator       = new TextField();
        txtGdIterator.setText("10");
        gpGd.add(txtGdIterator, 1, 0);
        
        gpGd.add(new Label("Final Level"), 0, 1);
        txtGdFinalLevel     = new TextField();
        txtGdFinalLevel.setText("0.0");
        gpGd.add(txtGdFinalLevel, 1, 1);
        
        
        tpGd.setContent(gpGd);
        
        
//        Late acceptance Hill Climbing
        TitledPane tpLahc       = new TitledPane();
        tpLahc.setText("Late Acceptance Hill Climbing");
        GridPane    gpLahc      = new GridPane();
        
        gpLahc.setVgap(5);
        
        gpLahc.add(new Label("Iteration"), 0, 0);
        txtLahcIterator         = new   TextField();
        txtLahcIterator.setText("100");
        gpLahc.add(txtLahcIterator, 1, 0);
        
        gpLahc.add(new Label("Memory Size"), 0, 1);
        txtLahcMemorySize         = new   TextField();
        txtLahcMemorySize.setText("500");
        gpLahc.add(txtLahcMemorySize, 1, 1);
        
        tpLahc.setContent(gpLahc);
        
        
//        Partical Swamp
        TitledPane tpPs       = new TitledPane();
        tpPs.setText("Particle Swarm Optimization");
        GridPane    gpPs        = new GridPane();
        
        gpPs.setVgap(5);
        
        gpPs.add(new Label("Max Iteration"), 0, 0);
        txtPsIterator         = new   TextField();
        txtPsIterator.setText("200");
        gpPs.add(txtPsIterator, 1, 0);
        
        gpPs.add(new Label("Particle Size"), 0, 1);
        txtPsSize         = new   TextField();
        txtPsSize.setText("100");
        gpPs.add(txtPsSize, 1, 1);
        
        tpPs.setContent(gpPs);
        
        
        
        Accordion accord    = new Accordion();        
        accord.getPanes().addAll(tpGlobal, tpSa, tpHc, tpGd, tpLahc, tpPs);
//        accord.setExpandedPane(tpResults);
        
        
//        Developed by
        GridPane gpCredit = new GridPane();
        gpCredit.setVgap(5);
        gpCredit.setPadding(inset);
        gpCredit.setStyle("-fx-float:bottom;");
        
        Label lDevBy= new Label();
        lDevBy.setPadding(new Insets(5, 0, 10, 0));
        lDevBy.setStyle("-fx-font-weight:bold;font-size:1.15em;");
        lDevBy.setText("Developed By");
        
        gpCredit.add(lDevBy, 0, 0);
        
        Label lAuthors = new Label();
        lAuthors.setText("Dr. Kamal Z Zamli, Ariful Haque");
        lAuthors.setStyle("-fx-font-weight:bold");
        gpCredit.add(lAuthors, 0, 1);
        
        Label lAffiliation = new Label();
        lAffiliation.setWrapText(true);
        lAffiliation.setText("Faculty of Computer Systems \n& Software Engineering\nUniversiti Malaysia Pahang\nJln Tun Razak, Gambang\nMalaysia");        
        gpCredit.add(lAffiliation, 0, 2);
        
        
//        Image image = new Image(getClass().getResourceAsStream("labels.jpg"));
//        ImageView imgView = new ImageView(image);
        ImageView iv = new ImageView(getClass().getResource("logo-ump.png").toExternalForm());
        iv.maxWidth(200);
        
        gpCredit.add(iv, 0, 3);



        vbox.getChildren().addAll(vboxtitle, accord,gpCredit);
                
//        vbox.getChildren().addAll(vboxtitle, lblIterator, txtIterator);
        return vbox; 
    }//end function 
    
    private VBox setResultTabs(){
        
        VBox    center = new VBox();
        center.setPadding(new Insets(0, 10, 10, 10));
        center.setStyle("-fx-background-color:gray;");
        
        TitledPane tpResults    = new TitledPane();                
        TitledPane tpLogs       = new TitledPane();
        Accordion  accord       = new Accordion();
        
        tpResults.setText("Results");
        tpResults.setContent(new Label("Result table here"));
        
        tpLogs.setText("Log");
        tpLogs.setContent(new Label("Log table here"));
        
    
        accord.getPanes().addAll(tpResults, tpLogs);
        accord.setExpandedPane(tpResults);
        
        center.getChildren().addAll(accord);
        
        return center;
        
    }//end function
    
    
    private VBox setResultTable(){
        
        VBox    center = new VBox();
        center.setPadding(new Insets(0, 10, 10, 10));
        center.setStyle("-fx-background-color:gray;");
        
                
        tblResult = new   TableView();
        tblResult.setPadding(new Insets(0, 5, 20, 5));
        
        TableColumn tcSn        = new TableColumn("Sn");
        tcSn.setMinWidth(20);
        tcSn.setCellValueFactory(
                new PropertyValueFactory<ResultRow, Integer>("sn"));
        
        TableColumn tcSeq       = new TableColumn("Sequence");
        tcSeq.setCellValueFactory(
                new PropertyValueFactory<ResultRow, Integer>("seq"));
        
        TableColumn tcAlgorithm = new TableColumn("Algorithm");
        tcAlgorithm.setCellValueFactory(
                new PropertyValueFactory<ResultRow, String>("algorithm"));
        
        TableColumn tcMcdcPairs = new TableColumn("Total Test Cases");
        tcMcdcPairs.setMinWidth(100);
        tcMcdcPairs.setCellValueFactory(
                new PropertyValueFactory<ResultRow, Integer>("pairs"));
        
        TableColumn tcTestCase = new TableColumn("Test Case");
        tcTestCase.setMinWidth(150);
        tcTestCase.setCellValueFactory(
                new PropertyValueFactory<ResultRow, String>("testcase"));
        
        TableColumn tcTime      = new TableColumn("Total Time");
        tcTime.setCellValueFactory(
                new PropertyValueFactory<ResultRow, Double>("time"));
        
        
        tblResult.setItems(data);
        tblResult.getColumns().addAll(tcSn, tcSeq, tcAlgorithm, tcMcdcPairs, tcTestCase, tcTime);
        tblResult.setMaxHeight(600);
        
        Label lblSummery = new Label("Result Summery");
        lblSummery.setStyle("-fx-font-size:1.5em; -fx-font-weight:bold;-fx-color:white;");
        lblSummery.setTextFill(Color.web("Yellow"));
        lblSummery.setPadding(new Insets(10, 0, 0, 0));
        
//        Result Summery Table
        tblSummery    = new TableView();
        tblSummery.setPadding(new Insets(15, 10, 10, 10));
        tblSummery.setStyle("-fx-border-width: 10 5 5 5; -fx-border-insets: 0;");
        tblSummery.setMaxHeight(200);
        
        
        TableColumn tcProp        = new TableColumn("Property");        
        tcProp.setMinWidth(150);
        tcProp.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("property"));
        
        TableColumn tcSa        = new TableColumn("Sa");        
        tcSa.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("sa"));
       
        TableColumn tcGd        = new TableColumn("GD");        
        tcGd.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("gd"));
        
        TableColumn tcHc        = new TableColumn("HC");        
        tcHc.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("hc"));
        
        TableColumn tcLahc        = new TableColumn("LAHC");        
        tcLahc.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("lahc"));
        
        TableColumn tcPs        = new TableColumn("PS");        
        tcPs.setCellValueFactory(
                new PropertyValueFactory<SummeryRow, String>("ps"));
        
        tblSummery.setItems(sumData);
        tblSummery.getColumns().addAll(tcProp ,tcSa, tcGd, tcHc, tcLahc, tcPs);
        
        System.out.println(sumData);
//        TitledPane tpResults    = new TitledPane();                
        TitledPane tpLogs       = new TitledPane();
        TitledPane tpLogSummery = new TitledPane();
        Accordion  accord       = new Accordion();
        
//        tpResults.setText("Results");
//        tpResults.setContent(new Label("Result table here"));
        
        tpLogs.setText("Log");
        tpLogs.setContent(tblResult);
        
        
        tpLogSummery.setText("Log Summary");
        tpLogSummery.setContent(tblSummery);
        
    
        accord.getPanes().addAll(tpLogs, tpLogSummery);
        accord.setExpandedPane(tpLogs);
       
        
        center.getChildren().addAll(accord);                                
//        center.getChildren().addAll(tblResult, lblSummery, tblSummery);
                
        return center;
        
    }//end function
    
    
    private ArrayList runLoop(String argExpression, Integer loopLimit, Integer mcdcExitLimit)
    {
         ArrayList <Object> finalResult      = new ArrayList<Object>();
                         
        saTime                              = new ArrayList();
        saPairs                             = new ArrayList();

        gdTime                              = new ArrayList();
        gdPairs                             = new ArrayList();

        hcTime                              = new ArrayList();
        hcPairs                             = new ArrayList();

        lahcTime                            = new ArrayList();
        lahcPairs                           = new ArrayList();
        
        psTime                              = new ArrayList();
        psResult                            = new ArrayList();

        mcdc = new Mcdc();      
        mcdc.initialize(argExpression);
        mcdc.setMcdcExitLimit(mcdcExitLimit);
        
//        pstg = new PSTG();
//        pstg.setMaxIteration(Integer.parseInt(txtPsIterator.getText()));
//        pstg.setParticleSize(Integer.parseInt(txtPsSize.getText()));
        
        for (int i=0; i<loopLimit ;i++)
        {

            System.out.println("SN: "+i);

//            SIMULATED ANNALYING
            ArrayList saList = new ArrayList();

            saList =this.runSA(mcdc, i);
            this.addToTable(saList);


//            GREAT DELUDGE
            ArrayList gdList = new ArrayList();               
            gdList =this.runGD(mcdc, i);
            this.addToTable(gdList);


//            HILL CLIMBING
            ArrayList hcList = new ArrayList();
            hcList =this.runHC(mcdc, i);
            this.addToTable(hcList);


//          LAHC
            ArrayList lahcList = new ArrayList();
            lahcList =this.runLAHC(mcdc, i);
            this.addToTable(lahcList);
            
//            PS
            ArrayList psList = new ArrayList();
            psList =this.runPs( i);
            this.addToTable(psList);

        }//end for


        
//        For loop Here           
       
        return finalResult;
   
    }//end function runLoop
    
    
    
    private void addToSummery(){
   
                
        sumData.add(new SummeryRow("Max Time",  Collections.max(saTime).toString() ,
                                                Collections.max(gdTime).toString(),
                                                Collections.max(hcTime).toString(),
                                                Collections.max(lahcTime).toString(),
                                                Collections.max(psTime).toString()));
        
        sumData.add(new SummeryRow("Min Time",  Collections.min(saTime).toString() ,
                                                Collections.min(gdTime).toString(),
                                                Collections.min(hcTime).toString(),
                                                Collections.min(lahcTime).toString(),
                                                Collections.min(psTime).toString()));
        
        sumData.add(new SummeryRow("Avg Time",  this.calculateAverageTime(saTime).toString() ,
                                                this.calculateAverageTime(gdTime).toString() ,
                                                this.calculateAverageTime(hcTime).toString() ,
                                                this.calculateAverageTime(lahcTime).toString(), 
                                                this.calculateAverageTime(psTime).toString()));
    
        sumData.add(new SummeryRow("Highest Paris",  Collections.max(saPairs).toString() ,
                                                Collections.max(gdPairs).toString(),
                                                Collections.max(hcPairs).toString(),
                                                Collections.max(lahcPairs).toString(),
                                                Collections.max(psResult).toString()));
        
        sumData.add(new SummeryRow("Lowest Paris",  Collections.min(saPairs).toString() ,
                                                Collections.min(gdPairs).toString(),
                                                Collections.min(hcPairs).toString(),
                                                Collections.min(lahcPairs).toString(),
                                                Collections.max(psResult).toString()));
                
        
    }//end function
    
    private String prepareTestCase(ArrayList tc)
    {
        String str = tc.toString();
        
        str = str.replace(":", "");
        str = str.replace("[", "");
        str = str.replace("]","");
        str = str.replace("0", "F");
        str = str.replace("1", "T");
        str = str.replace(",", "\n");
//        System.out.println(str);
        
        return str;
    }
    
//    private String preparePsTestCase(ArrayList tc)
//    {
//        String str = tc.toString();
//        
//        str = str.replace("[", "");
//        str = str.replace("]","");
//        str = str.replace("0", "F");
//        str = str.replace("1", "T");        
//        str = str.replace(",", "\n");
////        System.out.println(str);
//        
//        return str;
//    }
    
    private String prepareTestCaseForPrint(String tc){
        
        String str = tc.toString();
        str = str.replace("\n", "; ");
        return str;
    }
    
    private void addToTable(ArrayList list){
    
        try{
//        System.out.println(list);        
        String[] row = list.toString().split(",");
              
//        System.out.println(row[1]+", algo: "+row[2]+", pairs: "+row[3]+" tc: "+row[4] +", time: "+row[5]);        
        
        data.add( new ResultRow(Integer.parseInt(row[0].substring(1).trim()),
                                Integer.parseInt(row[1].trim()),
                                row[2],
                                Integer.parseInt(row[3].trim()),
                                row[4],
                                Double.parseDouble(row[5].substring(0,row[5].length()-1))));   
        
//        tblResult.setItems(data);
                       
        
//        System.out.println("ROW2 Case: "+row[2].toString().toLowerCase().trim());
        
        
        switch(row[2].toString().toLowerCase().trim()){
            case "sa":
                    this.saTime.add(Double.parseDouble(row[5].substring(0,row[5].length()-1)));                    
                    this.saPairs.add(row[3]);
                break;
            case "gd":
                    this.gdTime.add(Double.parseDouble(row[5].substring(0,row[5].length()-1)));
                    this.gdPairs.add(row[3]);
                break;
            case "hc":
                    this.hcTime.add(Double.parseDouble(row[5].substring(0,row[5].length()-1)));
                    this.hcPairs.add(row[3]);
                break;
            case "lahc":
                    this.lahcTime.add(Double.parseDouble(row[5].substring(0,row[5].length()-1)));
                    this.lahcPairs.add(row[3]);
                break;    
            case "ps":
                    this.psTime.add(Double.parseDouble(row[5].substring(0,row[5].length()-1)));
                    this.psResult.add(row[3]);
                break;    
        }        
            
        }catch(Exception e){
            System.out.println("error: "+e.getMessage());
        }
        
                
    }//end function
    
    private ArrayList runSA(Mcdc mcdc, Integer i){               
                
            ArrayList ar        = new ArrayList();
            ArrayList result    = new ArrayList();
            
            long startTime = System.nanoTime();
            
            result  = mcdc.searchSA(Double.parseDouble(txtInitialTemp.getText()), 
                                    Double.parseDouble(txtCoolingRate.getText()));
            
//            System.out.print("SA case: "+ this.prepareTestCase(result));
            
            Integer mcdcSize = result.size();
            
            long endTime = System.nanoTime();
            
//            2. Calculate Time            
            double seconds = (double)(endTime - startTime) / 1000000000.0;               
            NumberFormat formatter = new DecimalFormat("#0.00");
//            System.out.println("Execution time is " + formatter.format((seconds)) + " seconds");
            
            
//            3. Prepare row result array [sn, seq, algorithm, mcdcsize, time];           
                ar.add(i);
                ar.add(1);
                ar.add("SA");
                ar.add(mcdcSize);
                ar.add(this.prepareTestCase(result));
                ar.add(formatter.format((seconds)));
                
                
            return ar;
        
    }//END FUNCTION
    
        
    private ArrayList runLAHC(Mcdc mcdc, Integer i){               
                
            ArrayList ar = new ArrayList();
            ArrayList result    = new ArrayList();
            
            long startTime = System.nanoTime();
            result      = mcdc.searchLAHC( Integer.parseInt(txtLahcIterator.getText()),
                                            Integer.parseInt(txtLahcMemorySize.getText()) );
            
            Integer mcdcSize = result.size();
            
            long endTime = System.nanoTime();
            
            double seconds = (double)(endTime - startTime) / 1000000000.0;               
            NumberFormat formatter = new DecimalFormat("#0.00");
//            System.out.println("Execution time is " + formatter.format((seconds)) + " seconds");
            
            
//            3. Prepare row result array [sn, seq, algorithm, mcdcsize, time];           
                ar.add(i);
                ar.add(4);
                ar.add("LAHC");
                ar.add(mcdcSize);
                ar.add(this.prepareTestCase(result));
                ar.add(formatter.format((seconds)));
                
                
            return ar;
        
    }//END FUNCTION
    
    private ArrayList runGD(Mcdc mcdc, Integer i){               
        
            ArrayList ar = new ArrayList();
            ArrayList result    = new ArrayList();
            
            long startTime = System.nanoTime();
            
            result = mcdc.searchGD(Integer.parseInt(txtGdIterator.getText()),
                                    Double.parseDouble(txtGdFinalLevel.getText()));
            
            Integer mcdcSize = result.size();

            long endTime = System.nanoTime();
            
            double seconds = (double)(endTime - startTime) / 1000000000.0;               
            NumberFormat formatter = new DecimalFormat("#0.00");
//            System.out.println("Execution time is " + formatter.format((seconds)) + " seconds");
            
            
                ar.add(i);
                ar.add(2);
                ar.add("GD");
                ar.add(mcdcSize);
                ar.add(this.prepareTestCase(result));
                ar.add(formatter.format((seconds)));
                
                
            return ar;
        
    }//END FUNCTION
    
    
    private ArrayList runPs(Integer i){               
        
            pstg = new PSTG();
            pstg.setMaxIteration(Integer.parseInt(txtPsIterator.getText()));
            pstg.setParticleSize(Integer.parseInt(txtPsSize.getText()));
        
            ArrayList ar = new ArrayList();
            ArrayList result    = new ArrayList();
            
            long startTime = System.nanoTime();
            
            
            String str = txtPairwiseExpression.getText();
            
            System.out.println("PS Expression: "+str);
            String args[] = str.split(" ");
            
            result = pstg.searchPS(args);
            
            Integer psSize = result.size();

            long endTime = System.nanoTime();
            
            double seconds = (double)(endTime - startTime) / 1000000000.0;               
            NumberFormat formatter = new DecimalFormat("#0.00");
//            System.out.println("Execution time is " + formatter.format((seconds)) + " seconds");            
            
                ar.add(i);
                ar.add(5);
                ar.add("PS");
                ar.add(psSize);
                ar.add(this.prepareTestCase(result));
                ar.add(formatter.format((seconds)));
                
                
            return ar;
        
    }//END FUNCTION
    
    
     private void writeExcel() throws Exception {
        
        Writer writer = null;
        try {            
            
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmm").format(Calendar.getInstance().getTime());
    //        File file = new File("C:\\Person.csv.");
            
            if (isWindows()) {
//                System.out.println("This is Windows");
                new File(System.getProperty("user.dir")+"\\TestResult\\").mkdirs();
                
                File file_win = new File(System.getProperty("user.dir")+"\\TestResult\\"+"mcdcpro_"+timeStamp+".csv");
                writer = new BufferedWriter(new FileWriter(file_win));
            } else if (isMac()) {
//                System.out.println("This is Mac");
             
                new File(System.getProperty("user.dir")+"/TestResult/").mkdirs();
                
                File file_mac = new File(System.getProperty("user.dir")+"/TestResult/"+"mcdcpro_"+timeStamp+".csv");
                writer = new BufferedWriter(new FileWriter(file_mac));
            }else{
                new File(System.getProperty("user.dir")+"/TestResult/").mkdirs();
                
                File file_mac = new File(System.getProperty("user.dir")+"/TestResult/"+"mcdcpro_"+timeStamp+".csv");
                writer = new BufferedWriter(new FileWriter(file_mac));
            }
            
            

//            Write expression;
            String exp = "Expression,"+txtExpression.getText() +"\n\n";
            writer.write(exp);
            
            String columns = "sn,sequence,algorithm,pairs,testcase,time\n";
            writer.write(columns);
            for (ResultRow result : data) {

                String text = result.getSn() + "," + result.getSeq() + "," + result.getAlgorithm() 
                        + "," + result.getPairs()+ "," 
                        + this.prepareTestCaseForPrint( result.getTestcase())+ "," 
                        + result.getTime() + "\n";
                writer.write(text);

            }//end for
            
            writer.write("\n");
            
            String summeryCol = "property, sa, gd, hc, lahc, ps\n";
            writer.write(summeryCol);
            
            for (SummeryRow sum : sumData) {

                String text = sum.getProperty() +"," + sum.getSa() + "," + sum.getGd() 
                        +"," + sum.getHc() + ","+ sum.getLahc() + ","+ sum.getPs() + "\n";
                writer.write(text);

            }//end for
            
            writer.write("\n\nGenerated by,MCDC Pro\n");
            
            writer.write("Date,"+new SimpleDateFormat("MMM dd yyyy - HH.mm").format(Calendar.getInstance().getTime())+"\n");
            
            lblStatus.setText("Export Complete!");
            

        } catch (Exception ex) {
            ex.printStackTrace();
        }//end catch
        finally {

            writer.flush();
             writer.close();
        }//end finally
        
    }//end function writeExcel();
    
    
    private ArrayList runHC(Mcdc mcdc, Integer i){               
        
        ArrayList ar = new ArrayList();
        ArrayList result    = new ArrayList();
        
        long startTime = System.nanoTime();
        
        result = mcdc.searchHC(Integer.parseInt(txtHcIterator.getText()));
        
        Integer mcdcSize = result.size();

        long endTime = System.nanoTime();
            
    
        double seconds = (double)(endTime - startTime) / 1000000000.0;               
        NumberFormat formatter = new DecimalFormat("#0.00");
            

        ar.add(i);
        ar.add(3);
        ar.add("HC");
        ar.add(mcdcSize);
        ar.add(this.prepareTestCase(result));
        ar.add(formatter.format((seconds)));
                
                
        return ar;
        
    }//END FUNCTION
       
}//end class
