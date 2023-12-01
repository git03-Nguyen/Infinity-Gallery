package edu.team08.infinitegallery.optionsearch;

import java.io.File;

public class PhotoInfo {

        private File file;
        private double latitude;
        private double longitude;

        private String address;

        public PhotoInfo(File file, double latitude, double longitude,String address) {
            this.file = file;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address=address;
        }

        public File getFile() {
            return file;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getAddress()
        {
            return address;
        }

        public void setAddress(String address)
        {
            this.address=address;
        }
        public double getLongitude() {
            return longitude;
        }
    }

