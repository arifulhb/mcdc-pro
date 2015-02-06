/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcdcgen;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ariful
 */
public class ResultRow {
 
    private final SimpleIntegerProperty _sn;
    private final SimpleIntegerProperty _seq;
    private final SimpleStringProperty  _algorithm;
    private final SimpleIntegerProperty _pairs;
    private final SimpleStringProperty  _tescase;
    private final SimpleDoubleProperty  _time;
    
    
    public ResultRow(Integer sn,
                        Integer seq,
                        String  algorithm,
                        Integer pairs,
                        String  testcase,
                        Double  time){
        
        this._sn        = new SimpleIntegerProperty(sn); 
        this._seq       = new SimpleIntegerProperty(seq); 
        this._algorithm = new SimpleStringProperty(algorithm);
        this._pairs     = new SimpleIntegerProperty(pairs);
        this._tescase   = new SimpleStringProperty(testcase);
        this._time      = new SimpleDoubleProperty(time);
                
    }//end conts

//    SN
    public void setSn(Integer value){
        this._sn.set(value);
    }
    public Integer getSn(){
        return this._sn.get();
    }
    
    
//    Seq
    public void setSeq(Integer value){
        this._seq.set(value);
    }
    public Integer getSeq(){
        return this._seq.get();
    }
    
//    Algo   
    public void setAlgorithm(String value){
        this._algorithm.set(value);
    }
    public String getAlgorithm(){
        return this._algorithm.get();
    }
    
//    Test Case
    public void setTestcase(String value){
        
        this._tescase.set(value);
    }
    public String getTestcase(){        
        return this._tescase.get();
    }
    
//    Pairs
    public void setPairs(Integer value){
        this._pairs.set(value);
    }
    public Integer getPairs(){
        return this._pairs.get();
    }    
    
//    Time
    public void setTime(Double value){
        this._time.set(value);
    }
    public Double getTime(){
        return this._time.get();
    }    
    
}//end class