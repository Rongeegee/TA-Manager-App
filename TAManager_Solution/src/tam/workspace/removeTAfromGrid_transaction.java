/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.workspace;

import java.util.HashMap;
import javafx.beans.property.StringProperty;
import jtps.jTPS_Transaction;

/**
 *
 * @author Rongan
 */
public class removeTAfromGrid_transaction implements jTPS_Transaction{
    HashMap<String, StringProperty> officeHours;
    String cellKey;
    String taName;
    
    public removeTAfromGrid_transaction(String cellKey, String taName, HashMap<String, StringProperty> officeHours){
        this.cellKey = cellKey;
        this.taName = taName;
        this.officeHours = officeHours;
    }
    
    @Override
    public void doTransaction() {
       StringProperty cellProp = officeHours.get(cellKey);
       String cellText = cellProp.getValue();

        // IF IT ALREADY HAS THE TA, REMOVE IT
        if (cellText.contains(taName)) {
            removeTAFromCell(cellProp, taName);
        } 
    }
    
    private void removeTAFromCell(StringProperty cellProp, String taName) {
        // GET THE CELL TEXT
        String cellText = cellProp.getValue();
        // IS IT THE ONLY TA IN THE CELL?
        if (cellText.equals(taName)) {
            cellProp.setValue("");
        }
        // IS IT THE FIRST TA IN A CELL WITH MULTIPLE TA'S?
        else if (cellText.indexOf(taName) == 0) {
            int startIndex = cellText.indexOf("\n") + 1;
            cellText = cellText.substring(startIndex);
            cellProp.setValue(cellText);
        }
        // IS IT IN THE MIDDLE OF A LIST OF TAs
        else if (cellText.indexOf(taName) < cellText.indexOf("\n", cellText.indexOf(taName))) {
            int startIndex = cellText.indexOf("\n" + taName);
            int endIndex = startIndex + taName.length() + 1;
            cellText = cellText.substring(0, startIndex) + cellText.substring(endIndex);
            cellProp.setValue(cellText);
        }
        // IT MUST BE THE LAST TA
        else {
            int startIndex = cellText.indexOf("\n" + taName);
            cellText = cellText.substring(0, startIndex);
            cellProp.setValue(cellText);
        }
    }

    @Override
    public void undoTransaction() {
        StringProperty cellProp = officeHours.get(cellKey);
        String cellText = cellProp.getValue();
        
        if(!cellText.contains(taName)){
            if (cellText.length() == 0) {
            cellProp.setValue(taName);
        } else {
            cellProp.setValue(cellText + "\n" + taName);
            }
        }
    }
    
}
