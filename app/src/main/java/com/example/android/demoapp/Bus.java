package com.example.android.demoapp;

/**
 * Store information about a bus.
 */
public class Bus {
	private String busNumber;
	private String busDirection;
	private String busStop;
	private String busStopId;
	private String busTiming;
	private String agency;


	public Bus(String busNumber, String busDirection, String busStop, String busTiming, String agency,String busStopId) {
		super();
		this.busNumber = busNumber;
		this.busDirection = busDirection;
		this.busStop = busStop;
		this.busTiming = busTiming;
		this.agency=agency;
		this.busStopId=busStopId;
	}

	public String getBusNumber() {
		return busNumber;
	}
	public String getBusDirection() {
		return busDirection;
	}
	public String getBusStop() {return busStop;}
	public String getBusTiming() {
		return busTiming;
	}
	public String getBusStopId(){return busStopId;}
	public String getAgency(){return agency;}
}
