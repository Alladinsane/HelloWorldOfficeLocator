package com.alladinsane.officelocator;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

public class OfficeLocation implements Parcelable{

	String name, address, address2, city, state, zipcode, phone, fax, image;
	double latitude, longitude, distance = -1;
	DistanceCalculator distanceCalculator = new DistanceCalculator();

	OfficeLocation()
	{

	}
	OfficeLocation(String name, String address, String address2, String city, String state,
				   String zipcode, String phone, String fax, double latitude, double longitude,
				   String image)
			{
				this.name = name;
				this.address = address;
				this.address2 = address2;
				this.city = city;
				this.state = state;
				this.zipcode = zipcode;
				this.phone = phone;
				this.fax = fax;
				this.latitude = latitude;
				this.longitude = longitude;
				this.image = image;
			}

	public OfficeLocation(Parcel in){
		String[] data= new String[12];

		in.readStringArray(data);
		this.name = data[0];
		this.address = data[1];
		this.address2 = data[2];
		this.city = data[3];
		this.state = data[4];
		this.zipcode = data[5];
		this.phone = data[6];
		this.fax = data[7];
		this.latitude = Double.parseDouble(data[8]);
		this.longitude = Double.parseDouble(data[9]);
		this.image = data[10];
		this.distance = Double.parseDouble(data[11]);
	}
	public void setDistance(double distance)
	{
		this.distance = distance;
	}
    public double getDistance()
    {
        return distance;
    }
    public String toString()
    {
        String dist;
        if(distance<0)
            dist = " ";
        else {
            DecimalFormat form = new DecimalFormat("0.00");
            dist = form.format(distance);
            dist = dist + " miles away";
        }
        return (name + "\n" + address + "\n" + address2 + "\n" + city + ", " + state + " " + zipcode + "\n" + dist);
    }
    public String toStringWithoutName()
    {
        String dist;
        if(distance<0)
            dist = " ";
        else {
            DecimalFormat form = new DecimalFormat("0.00");
            dist = form.format(distance);
            dist = dist + " miles away";
        }
        return (address + "\n" + address2 + "\n" + city + ", " + state + " " + zipcode + "\n" + dist);
    }
	@Override
	public int describeContents() {
// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
// TODO Auto-generated method stub

		dest.writeStringArray(new String[]{this.name, this.address, this.address2, this.city, this.state, this.zipcode,
				this.phone, this.fax, String.valueOf(this.latitude), String.valueOf(this.longitude), this.image, String.valueOf(this.distance)});
	}

	public static final Creator<OfficeLocation> CREATOR= new Creator<OfficeLocation>() {

		@Override
		public OfficeLocation createFromParcel(Parcel source) {
// TODO Auto-generated method stub
			return new OfficeLocation(source);  //using parcelable constructor
		}

		@Override
		public OfficeLocation[] newArray(int size) {
// TODO Auto-generated method stub
			return new OfficeLocation[size];
		}
	};

}

