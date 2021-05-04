/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sw614f21.p6project.DataStructures;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class EndpointSequence {
    public int ID;
    public ArrayList <Endpoint> Sequence;
    
    public EndpointSequence (int ID, ArrayList<Endpoint> sequence){
        this.ID = ID;
        this.Sequence = sequence;
    }
}
