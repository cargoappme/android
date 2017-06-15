package me.cargoapp.cargo;

/**
 * Created by Mathieu on 14/06/2017.
 */

public class Destination {
    private int id;
    private String adress;
    private double lon;
    private double lat;

    public Destination(){}

    public Destination(String adress, double lon, double lat){
        this.adress = adress;
        this.lon = lon;
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String toString(){
        return "ID : "+id+"\nAdress : "+adress+"\nLon : "+lon+"\nLat : "+lat;
    }
}
