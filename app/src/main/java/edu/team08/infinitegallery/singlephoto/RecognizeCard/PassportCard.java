package edu.team08.infinitegallery.singlephoto.RecognizeCard;

public class PassportCard extends CardInfo{
    private String issuingStateCode;

    public PassportCard(){
        issuingStateCode="";
    }

    public PassportCard(String cardNumberID,String country,String name, String dob,String nationality,String dateOfExpired,String issuingStateCode)
    {
        super(cardNumberID, country,name, dob, nationality, dateOfExpired);
        this.issuingStateCode=issuingStateCode;
    }
    @Override
    public String getCardType(){
        return "Passport";
    }
    @Override
    public String getAddress(){
        return "";
    }
    @Override
    public String getGender(){
        return "M";
    }
    @Override
    public String getDriverLicenseClass(){
        return "";
    }
    @Override
    public String getIssuingStateCode(){
        return issuingStateCode;
    }

}
