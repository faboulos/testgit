package com.kerawa.app.utilities;

/**
 * Created by root on 08/02/16.
 */
public class CardDetails {


    private String title ;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String details ;

  public  CardDetails(String det,String titl)
  {
      details = det ;
      title = titl ;
  }
  public CardDetails()
  {

  }
}
