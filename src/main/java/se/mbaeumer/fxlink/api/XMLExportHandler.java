package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.xmlexport.LinkXMLWriter;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class XMLExportHandler {
	public static void exportData(String filename) throws FileNotFoundException, XMLStreamException, SQLException{
		LinkXMLWriter writer = new LinkXMLWriter(filename);
		writer.writeDataToFile();
	}
}
