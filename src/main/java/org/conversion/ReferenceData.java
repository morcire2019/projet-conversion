package org.conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceData {
	
	private String numReference;
	
	private String type;
	
	private double price;
	
	private int size;
	
	
    public ReferenceData(String numReference, String type, double price, int size) {
		super();
		this.numReference = numReference;
		this.type = type;
		this.price = price;
		this.size = size;
	}
  
    
	public ReferenceData() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getNumReference() {
		return numReference;
	}


	public void setNumReference(String numReference) {
		this.numReference = numReference;
	}

	public String getType() {
		return type;
	}






	public void setType(String type) {
		this.type = type;
	}






	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	static boolean isDigitsNumbers(String numReference) {
		  if(numReference == null) return false;
		  Pattern pattern = Pattern.compile("^\\d{10}$");
		    Matcher matcher = pattern.matcher(numReference);
		    return matcher.matches();
  }
	
	static boolean isNumeric (String number) {
		Pattern pattern = Pattern.compile("^[+]?\\d+([.]\\d+)?$");  // ("^\\d+(\\.\\d+)?"); // ^[+]?\d+([.]\d+)?$
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}
}
