package tam.workspace;

import djf.controller.AppFileController;
import djf.ui.AppGUI;
import static tam.TAManagerProp.*;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoCancelDialogSingleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import tam.file.TimeSlot;
import tam.style.TAStyle;
import static tam.style.TAStyle.CLASS_HIGHLIGHTED_GRID_CELL;
import static tam.style.TAStyle.CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN;
import static tam.style.TAStyle.CLASS_OFFICE_HOURS_GRID_TA_CELL_PANE;
import tam.workspace.TAWorkspace;
import jtps.jTPS;
import jtps.jTPS_Transaction;

/**
 * This class provides responses to all workspace interactions, meaning
 * interactions with the application controls not including the file
 * toolbar.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class TAController {
    // THE APP PROVIDES ACCESS TO OTHER COMPONENTS AS NEEDED
    TAManagerApp app;
   
    boolean cont;
   
    /**
     * Constructor, note that the app must already be constructed.
     */
    public TAController(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
    }
    
    /**
     * This helper method should be called every time an edit happens.
     */    
    private void markWorkAsEdited() {
        // MARK WORK AS EDITED
        AppGUI gui = app.getGUI();
        gui.getFileController().markAsEdited(gui);
    }
    
    /**
     * This method responds to when the user requests to add
     * a new TA via the UI. Note that it must first do some
     * validation to make sure a unique name and email address
     * has been provided.
     */
    public void handleAddTA() {
        // WE'LL NEED THE WORKSPACE TO RETRIEVE THE USER INPUT VALUES
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        
        // WE'LL NEED TO ASK THE DATA SOME QUESTIONS TOO
        TAData data = (TAData)app.getDataComponent();
        EmailValidator checkEmail = new EmailValidator();
        
        // WE'LL NEED THIS IN CASE WE NEED TO DISPLAY ANY ERROR MESSAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        if(workspace.addButton.getText().equals("Add TA"))
        {
        // DID THE USER NEGLECT TO PROVIDE A TA NAME?
        if (name.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));            
        }
        // DID THE USER NEGLECT TO PROVIDE A TA EMAIL?
        else if (email.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_EMAIL_TITLE), props.getProperty(MISSING_TA_EMAIL_MESSAGE));                        
        }
        // DOES A TA ALREADY HAVE THE SAME NAME OR EMAIL?
        else if (data.containsTA(name, email)) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));                                    
        }
        
        else if(!checkEmail.validate(email)){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("EMAIL NOT IN CORRECT FORMAT", "PLEASE ENTER THE EMAIL IN THE CORRECT FORMAT");
        }
        // EVERYTHING IS FINE, ADD A NEW TA
        else {
            
            {
            // ADD THE NEW TA TO THE DATA
            data.addTA(name, email);
            //TeachingAssistant tas = new TeachingAssistant(name, email);
            //data.enterTA(tas);
            // CLEAR THE TEXT FIELDS
            nameTextField.setText("");
            emailTextField.setText("");
            
            // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
            nameTextField.requestFocus();
            
            // WE'VE CHANGED STUFF
            markWorkAsEdited();
        
           
            }
        }
        }
            else if (workspace.addButton.getText().equals("Update TA")){
                
             // GET THE TABLE
            TableView taTable = workspace.getTATable();
            if (name.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));            
        }
        // DID THE USER NEGLECT TO PROVIDE A TA EMAIL?
            else if (email.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_EMAIL_TITLE), props.getProperty(MISSING_TA_EMAIL_MESSAGE));                        
        }
            else if(!checkEmail.validate(email)){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("EMAIL NOT IN CORRECT FORMAT", "PLEASE ENTER THE EMAIL IN THE CORRECT FORMAT");
        }
        
            else{
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {       
                 TeachingAssistant ta = (TeachingAssistant)selectedItem;
                 String oldName = ta.getName();
                 ta.setName(name);
                 String newName = ta.getName();
                 String oldEmail = ta.getEmail();
                 ta.setEmail(email);
                 updateCellPane(newName,oldName);
                 nameTextField.setText("");
                 emailTextField.setText("");
                 workspace.addButton.setText("Add TA");
                jTPS_Transaction updateTA_transaction = new updateTA_transaction(ta,workspace.addButton,
                "Update TA", "Add TA",oldName,newName,oldEmail,email,workspace);
                data.getJTPS().addTransaction(updateTA_transaction);
                 markWorkAsEdited();
            }
            }
            }
            
        }
    
    
    public void filter(){
        int startRow;
        int endRow;
        int startHourInt;
        int endHourInt;
                
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TAData data = (TAData)app.getDataComponent();
        if(workspace.startTime.getSelectionModel().getSelectedItem() != null &&
                workspace.endTime.getSelectionModel().getSelectedItem() != null){
        String startTime = workspace.startTime.getSelectionModel().getSelectedItem().toString(); 
        String endTime = workspace.endTime.getSelectionModel().getSelectedItem().toString();
         if(workspace.TAHours.indexOf(startTime) < workspace.TAHours.indexOf(endTime)){
           
           //get the current start Hour and end Hour
           int oldStartHourInt = data.getStartHour();
           int oldEndHourInt = data.getEndHour();
           
           // get the starting row of the time frame  
           String startHour = startTime.substring(0, startTime.indexOf(":"));
           startHourInt = Integer.parseInt(startHour);
           if(startTime.equals("12:00am")){
               startRow = 1;
               startHourInt = 0;
           }
           else if(startTime.equals("12:00pm")){
               startRow = 25;
               startHourInt = 12;
           }
           else{
           if(startTime.contains("pm"))
               startHourInt += 12;
           String startMiliTime = Integer.toString(startHourInt);
           
           
           startMiliTime = startMiliTime + ":" + "00";
           startRow = workspace.getStartRow(startMiliTime);
           }
           
           //get the end row of the time frame
           if(endTime.equals("12:00pm")){
               endRow = 25;
               endHourInt = 12;
           }
           else{
           String endHour = endTime.substring(0, endTime.indexOf(":"));
           endHourInt = Integer.parseInt(endHour);
           if (endTime.contains("pm"))
               endHourInt += 12;
           String endMiliTime = Integer.toString(endHourInt);
           
           
           endMiliTime = endMiliTime + ":" + "00"; 
           endRow = workspace.getEndRow(endMiliTime);
           }
   
           workspace.setFilteredHour(startRow, endRow);
           int rowDifference = startRow - 1;
           
           
           data.setTimeFrame(startHourInt,endHourInt);
           
           
           //verify if the action will affect the current hour
           if(workspace.checkOfficeHourIsAfftected(startRow, endRow))
                confirmation();

           
           if(cont == true || !workspace.checkOfficeHourIsAfftected(startRow, endRow)){
           workspace.resetWorkspace();
           workspace.reloadOfficeHoursGrid(data);
           
           //now add TA from the old pane to the new pane
           //a logical and a bug in the for-loop
           for(String cellKey : workspace.getFilteredHour().keySet()){
               String column = cellKey.substring(0,cellKey.indexOf("_"));
               String row = cellKey.substring(cellKey.indexOf("_") + 1, cellKey.length());
               int cellRow = Integer.parseInt(row);
               int newCellRow = cellRow - rowDifference;
               String newRow =  Integer.toString(newCellRow);
               String newCellKey = column + "_" + newRow;
               String cellText = workspace.getFilteredHour().get(cellKey).getText();
               data.addTAtoCell(newCellKey,cellText);               
           }    
           workspace.resetOfficeHoursGridTACellLabels();
         
           jTPS_Transaction filterTA_transaction = new filterOfficeHours_transaction(data,workspace,app,startTime,
           endTime,oldStartHourInt,oldEndHourInt);
           data.getJTPS().addTransaction(filterTA_transaction);
           markWorkAsEdited();
           }
         }
        
         else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Start Time must be earlier than end time.");
            alert.showAndWait();
         }
        }
    }

    
    
     private void confirmation(){
       //AppYesNoCancelDialogSingleton.getSingleton();
       Alert alert = new Alert(AlertType.CONFIRMATION);
       alert.setTitle(null);
       alert.setHeaderText(null);
       alert.setContentText("Office hour will be affected. Are you sure to continue?");

       Optional<ButtonType> result = alert.showAndWait();
       if (result.get() == ButtonType.OK){
            cont = true;
        } else {
           cont = false;
        }
     }
     
     
     
    
    public void clearWorkspace(){
        TAData data = (TAData)app.getDataComponent();
        data.getTeachingAssistants().clear();         
    }
    
    public void clearOfficeHours(){
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        for(String key : workspace.officeHoursGridTACellLabels.keySet()){
            Label label = workspace.officeHoursGridTACellLabels.get(key);
            if(!label.getText().isEmpty()){
                label.setText("");
            }
        }
         
    }
    
    public void updateCellPane(String newName, String oldName){
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        for (Label label : workspace.officeHoursGridTACellLabels.values()){
            if(label.getText().contains(oldName)){
            if (label.getText().equals(oldName)) {
            label.setText(newName);
        }
        // IS IT THE FIRST TA IN A CELL WITH MULTIPLE TA'S?
        else if (label.getText().indexOf(oldName) == 0) {
            int startIndex = label.getText().indexOf("\n") + 1;
            String remain = label.getText().substring(startIndex);
            label.setText(newName+"\n"+remain);
        }
        // IS IT IN THE MIDDLE OF A LIST OF TAs
        else if (label.getText().indexOf(oldName) < label.getText().indexOf("\n", label.getText().indexOf(oldName))) {
            int startIndex = label.getText().indexOf("\n" + oldName);
            int endIndex = startIndex + oldName.length() + 1;
            String labelText = label.getText().substring(0, startIndex) + "\n"+ newName + label.getText().substring(endIndex);
            label.setText(labelText);
        }
            // IT MUST BE THE LAST TA
        else {
            int startIndex = label.getText().indexOf("\n" + oldName);
            String labelText = label.getText().substring(0, startIndex) +"\n" + newName;
            label.setText(labelText);
        }
            }
                
        }
    
    }
    
    
    public void updateButton(){
            TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
            TableView taTable = workspace.getTATable();
            
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                workspace.addButton.setText("Update TA");
            }
    }
    
    public void updateTextField(){
            TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
            TableView taTable = workspace.getTATable();
            
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                   TeachingAssistant ta = (TeachingAssistant)selectedItem;
                   String taName = ta.getName();
                   String taEmail = ta.getEmail();
                   workspace.nameTextField.setText(taName);
                   workspace.emailTextField.setText(taEmail);
            }
    }
    /**
     * This function provides a response for when the user presses a
     * keyboard key. Note that we're only responding to Delete, to remove
     * a TA.
     * 
     * @param code The keyboard code pressed.
     */
    public void handleKeyPress(KeyCode code) {
        // DID THE USER PRESS THE DELETE KEY?
        if (code == KeyCode.DELETE) {
            // GET THE TABLE
            TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
            TableView taTable = workspace.getTATable();
            
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // GET THE TA AND REMOVE IT
                TeachingAssistant ta = (TeachingAssistant)selectedItem;
                String taName = ta.getName();
                TAData data = (TAData)app.getDataComponent();
                data.removeTA(taName);
              
                
                // AND BE SURE TO REMOVE ALL THE TA'S OFFICE HOURS
                HashMap<String, Label> labels = workspace.getOfficeHoursGridTACellLabels();
                for (Label label : labels.values()) {
                    if (label.getText().equals(taName)
                    || (label.getText().contains(taName + "\n"))
                    || (label.getText().contains("\n" + taName))) {
                        data.removeTAFromCell(label.textProperty(), taName);
                    }
                }
  
                // WE'VE CHANGED STUFF
                markWorkAsEdited();
            }
        }
    }


    /**
     * This function provides a response for when the user clicks
     * on the office hours grid to add or remove a TA to a time slot.
     * 
     * @param pane The pane that was toggled.
     */
    public void handleCellToggle(Pane pane) {
        // GET THE TABLE
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        
        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // GET THE TA
            TeachingAssistant ta = (TeachingAssistant)selectedItem;
            String taName = ta.getName();
            TAData data = (TAData)app.getDataComponent();
            String cellKey = pane.getId();
            
            // AND TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
            data.toggleTAOfficeHours(cellKey, taName);
            
            // WE'VE CHANGED STUFF
            markWorkAsEdited();
        }
    }
    
    void handleGridCellMouseExited(Pane pane) {
        String cellKey = pane.getId();
        TAData data = (TAData)app.getDataComponent();
        int column = Integer.parseInt(cellKey.substring(0, cellKey.indexOf("_")));
        int row = Integer.parseInt(cellKey.substring(cellKey.indexOf("_") + 1));
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();

        Pane mousedOverPane = workspace.getTACellPane(data.getCellKey(column, row));
        mousedOverPane.getStyleClass().clear();
        mousedOverPane.getStyleClass().add(CLASS_OFFICE_HOURS_GRID_TA_CELL_PANE);

        // THE MOUSED OVER COLUMN HEADER
        Pane headerPane = workspace.getOfficeHoursGridDayHeaderPanes().get(data.getCellKey(column, 0));
        headerPane.getStyleClass().remove(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);

        // THE MOUSED OVER ROW HEADERS
        headerPane = workspace.getOfficeHoursGridTimeCellPanes().get(data.getCellKey(0, row));
        headerPane.getStyleClass().remove(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        headerPane = workspace.getOfficeHoursGridTimeCellPanes().get(data.getCellKey(1, row));
        headerPane.getStyleClass().remove(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        
        // AND NOW UPDATE ALL THE CELLS IN THE SAME ROW TO THE LEFT
        for (int i = 2; i < column; i++) {
            cellKey = data.getCellKey(i, row);
            Pane cell = workspace.getTACellPane(cellKey);
            cell.getStyleClass().remove(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
            cell.getStyleClass().add(CLASS_OFFICE_HOURS_GRID_TA_CELL_PANE);
        }

        // AND THE CELLS IN THE SAME COLUMN ABOVE
        for (int i = 1; i < row; i++) {
            cellKey = data.getCellKey(column, i);
            Pane cell = workspace.getTACellPane(cellKey);
            cell.getStyleClass().remove(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
            cell.getStyleClass().add(CLASS_OFFICE_HOURS_GRID_TA_CELL_PANE);
        }
    }

    void handleGridCellMouseEntered(Pane pane) {
        String cellKey = pane.getId();
        TAData data = (TAData)app.getDataComponent();
        int column = Integer.parseInt(cellKey.substring(0, cellKey.indexOf("_")));
        int row = Integer.parseInt(cellKey.substring(cellKey.indexOf("_") + 1));
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        
        // THE MOUSED OVER PANE
        Pane mousedOverPane = workspace.getTACellPane(data.getCellKey(column, row));
        mousedOverPane.getStyleClass().clear();
        mousedOverPane.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_CELL);
        
        // THE MOUSED OVER COLUMN HEADER
        Pane headerPane = workspace.getOfficeHoursGridDayHeaderPanes().get(data.getCellKey(column, 0));
        headerPane.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        
        // THE MOUSED OVER ROW HEADERS
        headerPane = workspace.getOfficeHoursGridTimeCellPanes().get(data.getCellKey(0, row));
        headerPane.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        headerPane = workspace.getOfficeHoursGridTimeCellPanes().get(data.getCellKey(1, row));
        headerPane.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        
        // AND NOW UPDATE ALL THE CELLS IN THE SAME ROW TO THE LEFT
        for (int i = 2; i < column; i++) {
            cellKey = data.getCellKey(i, row);
            Pane cell = workspace.getTACellPane(cellKey);
            cell.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        }

        // AND THE CELLS IN THE SAME COLUMN ABOVE
        for (int i = 1; i < row; i++) {
            cellKey = data.getCellKey(column, i);
            Pane cell = workspace.getTACellPane(cellKey);
            cell.getStyleClass().add(CLASS_HIGHLIGHTED_GRID_ROW_OR_COLUMN);
        }
    }
}