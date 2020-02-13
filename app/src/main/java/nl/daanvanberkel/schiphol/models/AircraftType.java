package nl.daanvanberkel.schiphol.models;

import java.io.Serializable;

public class AircraftType implements Serializable {
    private String iataMain;
    private String iataSub;
    private String longDescription;
    private String shortDescription;

    public String getIataMain() {
        return iataMain;
    }

    public void setIataMain(String iataMain) {
        this.iataMain = iataMain;
    }

    public String getIataSub() {
        return iataSub;
    }

    public void setIataSub(String iataSub) {
        this.iataSub = iataSub;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
