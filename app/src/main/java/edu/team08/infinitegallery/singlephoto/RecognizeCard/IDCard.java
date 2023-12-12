package edu.team08.infinitegallery.singlephoto.RecognizeCard;

public class IDCard extends CardInfo {
    private String address;
    private String gender;
    public IDCard()
    {
        super();
        this.address="";
        this.gender="";
    }
    public IDCard(String cardNumberID,String country,String name, String dob,String nationality,String dateOfExpired,String address,String gender){
        super(cardNumberID, country,name, dob, nationality, dateOfExpired);
        this.address=address;
        this.gender=gender;
    }
    @Override
    public String getCardType()
    {
        return "Id Card";
    }
    @Override
    public String getAddress(){
        return address;
    }
    @Override
    public String getGender(){
        return gender;
    }
    @Override
    public String getDriverLicenseClass(){
        return "";
    }
    @Override
    public String getIssuingStateCode(){
        return "";
    }

}
