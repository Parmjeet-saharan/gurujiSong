package com.parmjeet.gurujisong;


import java.io.Serializable;
import java.util.Comparator;

public class songList implements Serializable  {
    private String songlastsegmant;
    private String uri;
     private int no_of_time=0;
    public songList(){

   }
    public songList( String songlastsegmant ,String uri){
   this.songlastsegmant=songlastsegmant;
   this.uri=uri;
    }
  public void setSonglastsegmant( String songlastsegmant){
            this.songlastsegmant=songlastsegmant;
  }
  public String getSonglastsegmant(){
       return songlastsegmant;
  }

    public void setUri( String uri){
        this.uri=uri;
    }
    public String getUri(){
        return uri;
    }

    public int getNo_of_time() {
        return no_of_time;
    }

    public void setNo_of_time(int no_of_time) {
        this.no_of_time = no_of_time;
    }


}
