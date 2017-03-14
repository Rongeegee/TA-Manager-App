/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.workspace;

import djf.ui.AppGUI;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import jtps.jTPS_Transaction;
import tam.TAManagerApp;
import tam.data.TAData;

/**
 *
 * @author Rongan
 */
public class filterOfficeHours_transaction implements jTPS_Transaction {
    TAData data;
    TAWorkspace workspace;
    TAManagerApp app;
    String newStartTime;
    String newEndTime;
    int oldStartHourInt;
    int oldEndHourInt;
    HashMap<String,Label> cellLabel; 
    
    public filterOfficeHours_transaction(TAData data,TAWorkspace workspace,TAManagerApp app, String newStartTime,
        String newEndTime, int oldStartHourInt, int oldEndHourInt){
        this.data = data;
        this.workspace = workspace;
        this.app = app;
        this.newStartTime = newStartTime;
        this.newEndTime = newEndTime;
        this.oldStartHourInt = oldStartHourInt;
        this.oldEndHourInt = oldEndHourInt;
        cellLabel = new HashMap<String,Label>();
        cellLabel.putAll(workspace.getOfficeHoursGridTACellLabels());
    }
    @Override
    public void doTransaction() {
           String startHour;
           String endHour;
           int startHourInt;
           int endHourInt;
           int startRow;
           int endRow;
           startHour = newStartTime.substring(0, newStartTime.indexOf(":"));
           startHourInt = Integer.parseInt(startHour);
           if(newStartTime.equals("12:00am")){
               startRow = 1;
               startHourInt = 0;
           }
           else if(newStartTime.equals("12:00pm")){
               startRow = 25;
               startHourInt = 12;
           }
           else{
           if(newStartTime.contains("pm"))
               startHourInt += 12;
           String startMiliTime = Integer.toString(startHourInt);
           
           
           startMiliTime = startMiliTime + ":" + "00";
           startRow = workspace.getStartRow(startMiliTime);
           }
           
           //get the end row of the time frame
           if(newEndTime.equals("12:00pm")){
               endRow = 25;
               endHourInt = 12;
           }
           else{
           endHour = newEndTime.substring(0, newEndTime.indexOf(":"));
           endHourInt = Integer.parseInt(endHour);
           if (newEndTime.contains("pm"))
               endHourInt += 12;
           String endMiliTime = Integer.toString(endHourInt);
           
           
           endMiliTime = endMiliTime + ":" + "00"; 
           endRow = workspace.getEndRow(endMiliTime);
           }

           workspace.setFilteredHour(startRow, endRow);
           int rowDifference = startRow - 1;
           
           
           data.setTimeFrame(startHourInt,endHourInt);
           
           
           //verify if the action will affect the current hour

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
           
           }
         
         

    @Override
    public void undoTransaction() {       
           data.setTimeFrame(oldStartHourInt,oldEndHourInt);
           
           workspace.resetWorkspace();
           workspace.reloadOfficeHoursGrid(data);
           
           //now add TA from the old pane to the new pane
           //a logical and a bug in the for-loop
           for(String cellKey : cellLabel.keySet()){
               String taName = cellLabel.get(cellKey).getText();
               data.addTAtoCell(cellKey,taName);               
           }  
           
    }
    
    
}
    
    
    

    
    
     

