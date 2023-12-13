package edu.team08.infinitegallery.singlephoto.recognition;

public class DriverLicenseCard extends CardInfo{
    private String driveLicenseClass;
    private String address;
    public DriverLicenseCard(){
        driveLicenseClass="";
    }

    public DriverLicenseCard(String cardNumberID,String country,String name, String dob,String nationality,String dateOfExpired,String driveLicenseClass,String address)
    {
        super(cardNumberID, country,name, dob, nationality,dateOfExpired);
        this.driveLicenseClass=driveLicenseClass;
        this.address=address;
    }
    @Override
    public String getCardType(){
        return "Driver Licence";
    }

    @Override
    public String getAddress(){
        return address;
    }
    @Override
    public String getGender(){
        return "";
    }
    @Override
    public String getDriverLicenseClass(){
        return driveLicenseClass;
    }
    @Override
    public String getIssuingStateCode(){
        return "";
    }

}
