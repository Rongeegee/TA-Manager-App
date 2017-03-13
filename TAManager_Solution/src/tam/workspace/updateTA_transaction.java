/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.workspace;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import jtps.jTPS_Transaction;
import tam.data.TeachingAssistant;

/**
 *
 * @author Rongan
 */
public class updateTA_transaction implements jTPS_Transaction{
    TeachingAssistant ta;
    Button addButton;
    String oldButtonText;
    String newButtonText;
    String oldName;
    String newName;
    String oldEmail;
    String newEmail;
    TAWorkspace workspace;

    public updateTA_transaction(TeachingAssistant ta,Button addButton,String oldButtonText,String newButtonText,
            String oldName,String newName, String oldEmail,String newEmail, TAWorkspace workspace){
        this.ta = ta;
        this.addButton = addButton;
        this.oldButtonText = oldButtonText;
        this.newButtonText = newButtonText;
        this.oldName = oldName;
        this.newName = newName;
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
        this.workspace = workspace;
    }
    @Override
    public void doTransaction() {
         ta.setName(newName);
         ta.setEmail(newEmail);
         updateCellPane(newName,oldName);
         addButton.setText(newButtonText);
         
    }

    @Override
    public void undoTransaction() {
       ta.setName(oldName);
       ta.setEmail(oldEmail);
       updateCellPane(oldName, newName);
       addButton.setText(oldButtonText);
    }
    
    private void updateCellPane(String newName, String oldName){    
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
    
}
