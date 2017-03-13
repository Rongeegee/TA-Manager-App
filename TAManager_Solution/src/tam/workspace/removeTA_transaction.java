package tam.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import jtps.jTPS_Transaction;
import tam.data.TAData;
import tam.data.TeachingAssistant;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rongan
 */
public class removeTA_transaction implements jTPS_Transaction {
    ObservableList<TeachingAssistant> teachingAssistants;
     TeachingAssistant TA;
     HashMap<String, Label> TALabel;
     TAWorkspace workspace;
     TAData data;
     HashMap<String, Label> backUpLabel;
     HashMap<String, StringProperty> officeHours;
     
     public removeTA_transaction(ObservableList<TeachingAssistant> TAs,TeachingAssistant teaching_assitant,
            TAWorkspace workspace,TAData data,HashMap<String, StringProperty> officeHours){
       teachingAssistants = TAs;
       TA = teaching_assitant;
       this.workspace = workspace;
       this.TALabel = workspace.getOfficeHoursGridTACellLabels();
       this.data = data;
       this.officeHours = officeHours;
    }

    @Override
    public void doTransaction() {
        //if(teachingAssistants.contains(TA))
         teachingAssistants.remove(TA);
         
         String taName = TA.getName();
         backUpLabel = new HashMap<String, Label>();
          for (String  key : TALabel.keySet()) {
                    if (TALabel.get(key).getText().equals(taName)
                    || (TALabel.get(key).getText().contains(taName + "\n"))
                    || (TALabel.get(key).getText().contains("\n" + taName))) {                      
                        backUpLabel.put(key,TALabel.get(key));    
                        data.removeTAFromCell(TALabel.get(key).textProperty(), taName);                   
                }
    }
    }

    @Override
    public void undoTransaction() {
        teachingAssistants.add(TA);
        
        String taName = TA.getName();
        for(String key: backUpLabel.keySet()){
        StringProperty cellProp = officeHours.get(key);
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
}
