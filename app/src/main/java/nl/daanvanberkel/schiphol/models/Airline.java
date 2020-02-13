package nl.daanvanberkel.schiphol.models;

public class Airline {
    private String iata;
    private String icao;
    private int nvls;
    private String name;

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public int getNvls() {
        return nvls;
    }

    public void setNvls(int nvls) {
        this.nvls = nvls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
