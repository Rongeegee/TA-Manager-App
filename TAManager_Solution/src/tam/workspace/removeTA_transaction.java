package tam.workspace;

import javafx.collections.ObservableList;
import jtps.jTPS_Transaction;
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
     
     public removeTA_transaction(ObservableList<TeachingAssistant> TAs,TeachingAssistant teaching_assitant){
       teachingAssistants = TAs;
       TA = teaching_assitant;
    }

    @Override
    public void doTransaction() {
        //if(teachingAssistants.contains(TA))
         teachingAssistants.remove(TA);
    }

    @Override
    public void undoTransaction() {
        teachingAssistants.add(TA);
    }
}
