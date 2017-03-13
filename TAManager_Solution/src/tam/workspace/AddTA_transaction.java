package tam.workspace;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import jtps.jTPS_Transaction;
import tam.TAManagerApp;
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
public class AddTA_transaction implements jTPS_Transaction{
     ObservableList<TeachingAssistant> teachingAssistants;
     TeachingAssistant TA;

  
    
    public AddTA_transaction(ObservableList<TeachingAssistant> TAs,TeachingAssistant teaching_assitant){
       teachingAssistants = TAs;
       TA = teaching_assitant;
    }

    @Override
    public void doTransaction() {
        if(!teachingAssistants.contains(TA))
         teachingAssistants.add(TA);
    }

    @Override
    public void undoTransaction() {
            teachingAssistants.remove(TA);   
    }
 

}
