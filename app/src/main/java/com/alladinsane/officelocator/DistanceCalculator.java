package com.alladinsane.officelocator;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by todd on 1/15/16.
 */
public class DistanceCalculator {

    private static LatLng currentOffice;
    private static LatLng userLocation;

    DistanceCalculator()
    {
       
    }

    public double calculateMyDistanceToMarker()
    {
        double distance = CalculationByDistance(currentOffice.latitude, currentOffice.longitude, userLocation.latitude, userLocation.longitude);
        return distance;
    }
    private double CalculationByDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        int Radius = 6371;//radius of earth in Km
        double lat1 = latitude1;
        double lat2 = latitude2;
        double lon1 = longitude1;
        double lon2 = longitude2;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        Integer kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        Integer meterInDec = Integer.valueOf(newFormat.format(meter));
        System.out.println("Radius Value " + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c;
    }

    public void setCurrentOffice(LatLng loc)
    {
        currentOffice = loc;
    }
    public void setUserLocation(LatLng loc)
    {
        userLocation = loc;
    }
}
