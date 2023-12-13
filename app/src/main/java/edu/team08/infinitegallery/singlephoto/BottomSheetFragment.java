package edu.team08.infinitegallery.singlephoto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.singlephoto.recognition.CardInfo;
import edu.team08.infinitegallery.singlephoto.recognition.DriverLicenseCard;
import edu.team08.infinitegallery.singlephoto.recognition.IDCard;
import edu.team08.infinitegallery.singlephoto.recognition.PassportCard;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private String tagType;
    private String country;
    private String fullName;
    private String nationality;
    private String dob;
    private String gender;
    private String documentID;
    private String dateOfExpired;

    private String address;
    private String driverLicenseClass;
    private String issuingStateCode;

    public BottomSheetFragment(){}
    public static BottomSheetFragment newInstance(CardInfo card)
    {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();

        args.putString("TAG_TYPE", card.getCardType());
        args.putString("CARD_NUMBER_ID",card.getCardNumberID());
        args.putString("DATE_EXPIRED",card.getDateOfExpired());
        args.putString("COUNTRY", card.getCountry());
        args.putString("FULL_NAME", card.getName());
        args.putString("NATIONALITY", card.getNationality());
        args.putString("DOB", card.getDateOfBirth());

        if (card instanceof IDCard) {

            args.putString("GENDER", card.getGender());
            args.putString("ADDRESS",card.getAddress());

        }
        else if (card instanceof PassportCard)
        {

            args.putString("STATE_CODE",card.getIssuingStateCode());
            args.putString("GENDER", card.getGender());
        }
        else if (card instanceof DriverLicenseCard)
        {
            args.putString("DRIVER_CLASS",card.getDriverLicenseClass());
            args.putString("ADDRESS",card.getAddress());
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tagType = getArguments().getString("TAG_TYPE");
            documentID=getArguments().getString("CARD_NUMBER_ID");
            dateOfExpired=getArguments().getString("DATE_EXPIRED");
            country = getArguments().getString("COUNTRY");
            fullName = getArguments().getString("FULL_NAME");
            nationality = getArguments().getString("NATIONALITY");
            dob = getArguments().getString("DOB");
            gender = getArguments().getString("GENDER");

            if (getArguments().containsKey("ADDRESS")) {
                address = getArguments().getString("ADDRESS");
                // Do something with address
            }
            if (getArguments().containsKey("GENDER")) {
                gender = getArguments().getString("GENDER");

            }
            if (getArguments().containsKey("STATE_CODE")) {
                issuingStateCode = getArguments().getString("STATE_CODE");
            }
            if (getArguments().containsKey("DRIVER_CLASS")) {
                driverLicenseClass= getArguments().getString("DRIVER_CLASS");
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        // Set the data to the TextViews
        TextView tagTypeTextView = view.findViewById(R.id.tagTypeTextView);
        TextView countryTextView = view.findViewById(R.id.countryTextView);
        TextView cardIdTextView=view.findViewById(R.id.cardNumberIdTextView);
        TextView dateExiredTextView=view.findViewById(R.id.dateExpiredTextView);
        TextView fullNameTextView = view.findViewById(R.id.fullNameTextView);
        TextView nationalityTextView = view.findViewById(R.id.nationalityTextView);
        TextView dobTextView = view.findViewById(R.id.dobTextView);
        TextView genderTextView = view.findViewById(R.id.genderTextView);
        TextView addressTextView=view.findViewById(R.id.addressTextView);
        TextView stateCodeTextView=view.findViewById(R.id.stateCodeTextView);
        TextView driverClassTextView=view.findViewById(R.id.classDriverLicenceTextView);


        tagTypeTextView.setText(getString(R.string.tag_type) + tagType);
        countryTextView.setText(getString(R.string.country) + country);
        cardIdTextView.setText(getString(R.string.card_id) + documentID);

        if ("AI".equals(driverLicenseClass) && "undefined".equals(dateOfExpired)) {
            dateOfExpired = getString(R.string.expiration_date_no_limit);
            dateExiredTextView.setText(getString(R.string.expiration_date) + dateOfExpired);
        } else if (!"undefined".equals(dateOfExpired)) {
            dateExiredTextView.setText(getString(R.string.expiration_date) + dateOfExpired);
        }

        fullNameTextView.setText(getString(R.string.full_name) + fullName);
        nationalityTextView.setText(getString(R.string.nationality) + nationality);
        dobTextView.setText(getString(R.string.dob) + dob);

        // Check if documentID exists
        if (!"undefined".equals(gender) && !"".equals(gender)) {
            genderTextView.setText(getString(R.string.gender) + gender);
        }

        // Check if address exists (only for IDCard)
        if (!"undefined".equals(address)) {
            addressTextView.setText(getString(R.string.address) + address);
        }

        if (!"undefined".equals(issuingStateCode) && "Passport".equals(tagType)) {
            stateCodeTextView.setText(getString(R.string.issuing_state_code) + issuingStateCode);
        }

        if (!"undefined".equals(driverLicenseClass) && "Driver Licence".equals(tagType)) {
            driverClassTextView.setText(getString(R.string.driver_class) + driverLicenseClass);
        }


        return view;
    }
}