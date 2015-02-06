/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcdcgen;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ariful
 */
public class SummeryRow {
 
    private final SimpleStringProperty _prop;
    private final SimpleStringProperty _sa;
    private final SimpleStringProperty _gd;
    private final SimpleStringProperty _hc;
    private final SimpleStringProperty _lahc;
    private final SimpleStringProperty _ps;
    
    public SummeryRow(  String prop,
                        String sa,
                        String gd,
                        String hc,
                        String lahc,
                        String ps){
        
        this._prop  = new SimpleStringProperty(prop);
        this._sa    = new SimpleStringProperty(sa);
        this._gd    = new SimpleStringProperty(gd);
        this._hc    = new SimpleStringProperty(hc);
        this._lahc  = new SimpleStringProperty(lahc);
        this._ps    = new SimpleStringProperty(ps);
                
    }//end
    
    public void setProperty(String value){
        this._prop.set(value);
    }
    public String getProperty(){
        return this._prop.get();
    }
    
    public void setSa(String value){
        this._sa.set(value);
    }
    public String getSa(){
        return this._sa.get();
    }
    
    
    
    public void setPs(String value){
        this._ps.set(value);
    }
    public String getPs(){
        return this._ps.get();
    }
    
    
    
    public void setGd(String value){
        this._gd.set(value);
    }
    public String getGd(){
        return this._gd.get();
    }
    
    
    public void setHc(String value){
        this._hc.set(value);
    }
    public String getHc(){
        return this._hc.get();
    }
    
    
    public void setLahc(String value){
        this._lahc.set(value);
    }
    public String getLahc(){
        return this._lahc.get();
    }
    
}//end class