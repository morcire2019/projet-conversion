/**
 * 
 */
package org.conversion;

/**
 * @author KALLO MOHAMED
 *
 */
public enum Color {
	
	/** rouge */
	ROUGE("R"),
	/** vert */
	VERT("G"),
	/** blue */
	BLUE("B");
	
	private String code;
	
	Color(String code) {
		this.code = code;	
	}
	
	   // Static method return Color by code.
	   public static Color getColorByCode(String code) {
	       for (Color Color : Color.values()) {
	           if (Color.code.equals(code)) {
	               return Color;
	           }
	       }
	       return null;
	   }
	 
	   public String getCode() {
	       return code;
	   }
	 
	   public void setCode(String code) {
	       this.code = code;
	   }
	 

}
