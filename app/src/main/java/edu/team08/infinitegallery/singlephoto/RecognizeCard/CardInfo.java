package edu.team08.infinitegallery.singlephoto.RecognizeCard;

public abstract class CardInfo
{
//    private String cardType;
    private String cardNumberID;
    private String name;
    private String dob;
    private String nationality;
    private String dateOfExpired;
    private String country;

    public CardInfo(){
//        cardType="";
        cardNumberID="";
        name="";
        dob="";
        nationality="";
        dateOfExpired="";
        country="";
    }
    public CardInfo(String cardNumberID,String country,String name, String dob,String nationality,String dateOfExpired){
//        this.cardType=type;
        this.cardNumberID=cardNumberID;
        this.name=name;
        this.dob=dob;
        this.nationality=nationality;
        this.dateOfExpired=dateOfExpired;
        this.country=country;
    }
    public abstract String getCardType();
    public String getCardNumberID(){
        return this.cardNumberID;
    }
    public String getName(){
        return this.name;
    }
    public String getDateOfBirth(){
        return this.dob;
    }
    public String getNationality(){
        return this.nationality;
    }
    public String getCountry(){
        return country;
    }

    public abstract String getAddress();
    public abstract String getGender();

    public abstract String getDriverLicenseClass();
    public abstract String getIssuingStateCode();

    public String getDateOfExpired()
    {
        return dateOfExpired;
    }

}
