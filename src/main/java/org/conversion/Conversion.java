package org.conversion;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.conversion.ReferenceData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Conversion { 

	/**
	 * @param inputFilePath
	 * @param outputFormat
	 * @param outputFilePath
	 * @return
	 * @throws IOException 
	 */
	public static void convertFormatTxtToJsonOrXml(String inputFilePath, String outputFormat, String outputFilePath) throws IOException {

		// Test if output file format is valid( XML/JSON);
		if(!Format.JSON.name().equalsIgnoreCase(outputFormat) && !Format.XML.name().equalsIgnoreCase(outputFormat)) {
			throw new IllegalArgumentException(Constants.UNKNOWN_OUTPUT_FORMAT);
		}

		// List for correct expected data
		final ArrayList<ReferenceData> list = new ArrayList<ReferenceData>();

		// List of errors
		final ArrayList<Error> errors = new ArrayList<Error>();

		File file = new File(inputFilePath);

		if(!file.exists()) {
			throw new FileNotFoundException("The input file not found : "+inputFilePath);
		}else{

			// Get all data in input file  as List of String
			List<String> lines = readTextFileByLines(inputFilePath);

			if(lines != null && lines.size() > 0) {
				for(int i = 0; i<lines.size(); i++){

					String[] line = lines.get(i).split(";");

					boolean isDigit = ReferenceData.isDigitsNumbers(line[0]);
					Color color = Color.getColorByCode(line[1]);
					boolean isPriceNumeric = ReferenceData.isNumeric(line[2]);
					boolean isSizeNumeric = ReferenceData.isNumeric(line[3]);


					if(isDigit && color !=null && isPriceNumeric && isSizeNumeric) {

						ReferenceData ref = new ReferenceData();
						ref.setNumReference(line[0]);
						ref.setSize(Integer.valueOf(line[3]));
						ref.setPrice(Double.valueOf(line[2]));
						ref.setType(color.getCode());

						// ajouter les données valides dans la liste 
						list.add(ref);
					} else {

						// create Error object
						Error error = new Error();

						error.setValue(lines.get(i));
						error.setLine(++i);

						if(!isDigit) {
							error.setMessage(Constants.ERROR_NUM_REFERENCE);
						} else if (color == null){
							error.setMessage(Constants.ERROR_TYPE); 
						} else if(!isPriceNumeric) {
							error.setMessage(Constants.ERROR_PRICE);
						}else if(!isSizeNumeric) {
							error.setMessage(Constants.ERROR_SIZE);
						}
						// ajouter les données erronées dans la liste errors
						errors.add(error);
					}

				} 
			}

			System.out.println(file.getName());

			if(Format.JSON.name().equalsIgnoreCase(outputFormat)) {

				JSONObject object = getJsonObject(list, errors,file.getName());

				//Create and save ouput file
				createSaveOuputFile(object, outputFilePath);

			} else if(Format.XML.name().equalsIgnoreCase(outputFormat)) {
				writeDataIntoXmlFile(list, errors, file.getName(),outputFilePath);
			}	            
		}


	}

	public static List<String> readTextFileByLines(String fileName) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(fileName));
		return lines;
	}


	// Convertir les données sous format JSON
	static JSONObject getJsonObject(final List<ReferenceData> references, final List<Error> errors, final String fileName) {

		JSONObject objet = new JSONObject();

		try {
			objet.put("inputFile", fileName);
			objet.put("references", new JSONArray(references));
			objet.put("errors", new JSONArray(errors));
		} catch(JSONException e) {
			System.err.println("Erreur lors de l'insertion du tableau.");
			System.err.println(e);
			System.exit(-1);
		}
		return objet;

	}




	/**
	 * Cette méthode permet de créer le format xml
	 * @param references       List< ReferenceData>
	 * @param errors           List<Error>
	 * @param inputFileName    String
	 * @param ouputFileName	   String
	 */
	static void writeDataIntoXmlFile(final List<ReferenceData> refs, final List<Error> errors, String inputFileName, String ouputFileName) {

		try {
			DocumentBuilderFactory dbFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			// root element
			Element rootElement = doc.createElement("report");
			doc.appendChild(rootElement);

			// inputFile element
			Element inputFile = doc.createElement("inputFile");
			rootElement.appendChild(inputFile);
			inputFile.appendChild(doc.createTextNode(inputFileName));


			// references element
			Element references = doc.createElement("references");
			rootElement.appendChild(references);

			// errors element
			Element erreurs= doc.createElement("errors");
			rootElement.appendChild(erreurs);



			// Les données de références valides
			if(refs != null && refs.size() >0) {

				for(ReferenceData refData : refs) {
					Element reference = doc.createElement("reference");

					Attr attrType = doc.createAttribute("color");
					attrType.setValue(refData.getType());

					Attr attrPrice = doc.createAttribute("price");
					attrPrice.setValue(String.valueOf(refData.getPrice()));

					Attr attrSize = doc.createAttribute("size");
					attrSize.setValue(String.valueOf(refData.getSize()));

					Attr attrNumRef = doc.createAttribute("numReference");
					attrNumRef.setValue(String.valueOf(refData.getSize()));

					reference.setAttributeNode(attrType);
					reference.setAttributeNode(attrPrice);
					reference.setAttributeNode(attrSize);
					reference.setAttributeNode(attrNumRef);

					references.appendChild(reference);
				};

			}

			// Les données de références invalides
			if(errors != null && errors.size() >0) {

				for(Error err : errors) {
					Element error = doc.createElement("error");

					Attr attrLine = doc.createAttribute("line");
					attrLine.setValue(String.valueOf(err.getLine()));

					Attr attrMessage = doc.createAttribute("message");
					attrMessage.setValue(err.getMessage());

					error.setAttributeNode(attrLine);
					error.setAttributeNode(attrMessage);
					error.appendChild(doc.createTextNode(err.getValue()));

					erreurs.appendChild(error);
				};

			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(ouputFileName));
			transformer.transform(source, result);

			// Output to console for testing
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException  
	 * 
	 * */
	static void createSaveOuputFile(final JSONObject obj, String ouputFileName) throws IOException {

		// Création du fichier de sortie
		FileWriter fs = null;
		try {
			fs = new FileWriter(ouputFileName);
		} catch(IOException e) {
			System.err.println("Erreur lors de l'ouverture du fichier '" + ouputFileName+ "'.");
			System.err.println(e);
			System.exit(-1);
		}

		// Sauvegarde dans le fichier
		try {
			fs.write(obj.toString());
		} catch(IOException e) {
			System.err.println("Erreur lors de l'écriture dans le fichier '" + ouputFileName+ "'.");
			System.err.println(e);
			System.exit(-1);
			throw e;
		}finally {

			// Fermeture du fichier
			try {
				fs.flush();
				fs.close();
				System.out.println("JSONObject :" +obj);
			} catch (IOException e) {
				System.err.println("Erreur lors de la fermeture du fichier '" + ouputFileName+"'.");
				System.err.println(e);
				System.exit(-1);
				throw e;
			}

		}
	}


	public static void main(String[] args) throws IOException {

		String xmlFile = "C:\\Users\\KALLO MOHAMED\\Documents\\Downloads\\errors.xml";
		String inputFileName = "C:\\Users\\KALLO MOHAMED\\Documents\\Downloads\\data_text.txt";
		String format = "XML";

		convertFormatTxtToJsonOrXml(inputFileName, format, xmlFile);
	}  
}  