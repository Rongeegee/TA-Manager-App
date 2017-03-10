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

     //ArrayList<String> TAName;
     //ArrayList<String> TAEmail;
     /*
     public AddTA_transaction(TAData data) {
        // KEEP THIS FOR LATER
        TAName = new ArrayList<String>();
        teachingAssistants = data.getTeachingAssistants();
        TAEmail = new ArrayList<String>();
    }
    
     public void addTransaction(TAData data){
         int lastAddedIndex = data.getLastAddIndex();
         TeachingAssistant lastAddedTA = teachingAssistants.get(lastAddedIndex);
         String name = lastAddedTA.getName();
         String email = lastAddedTA.getEmail();
         TAName.add(name);
         TAEmail.add(email);
        
     public void doTransction(){
         
     }
    public void undoTransaction(TAData data){
       String name = TAName.get(TAName.size()-1);
       TAName.remove(TAName.size()-1);
       String email = TAEmail.get(TAEmail.size()-1);
       TAEmail.remove(TAEmail.size() - 1);
       data.addTA(name, email);
       
      
    }*/
    
    public AddTA_transaction(ObservableList<TeachingAssistant> TAs,TeachingAssistant teaching_assitant){
       teachingAssistants = TAs;
       TA = teaching_assitant;
    }

    @Override
    public void doTransaction() {
        if(!teachingAssistants.contains(TA) && TA.isNewAdded())
         teachingAssistants.add(TA);
    }

    @Override
    public void undoTransaction() {
            teachingAssistants.remove(TA);   
    }
 

}
