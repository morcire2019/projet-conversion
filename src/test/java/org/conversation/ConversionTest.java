package org.conversation;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

import org.conversion.Conversion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;



@DisplayName("Test junit5 de la classe Conversion")
public class ConversionTest {
	
	   
		@BeforeAll
		static void initAll() {
			System.out.println("BeforeAll");
		}
		
	 @Test
	  void testExpectedExceptionWhenInvalidFileOuputFormat() {
		 
		 String OutPutFileFormat = "TXT"; // Correct format (XML/JSON)
		 String inputFilePath = "chemin du fichier d'entrée";
		 String outputFilePath = "chemin du fichier de sortie";

	    Assertions.assertThrows(IllegalArgumentException.class, () -> {
	    	Conversion.convertFormatTxtToJsonOrXml(inputFilePath, OutPutFileFormat, outputFilePath)
	      ;
	    });
	  }
	 
	 @Test
	 void should_return_FileNotFoundException_when_inputFile_not_found() {
		 String OutPutFileFormat = "JSON"; // Correct format (XML/JSON)
		 String inputFilePath = "chemin du fichier d'entrée";
		 String outputFilePath = "chemin du fichier de sortie";
		 
		 Assertions.assertThrows(NoSuchFileException.class, () -> {
		    	Conversion.convertFormatTxtToJsonOrXml(inputFilePath, OutPutFileFormat, outputFilePath)
		      ;
		    });
	 }


}
