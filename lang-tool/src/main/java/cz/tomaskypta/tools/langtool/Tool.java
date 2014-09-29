package cz.tomaskypta.tools.langtool;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Tool {

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        if (args == null || args.length == 0) {
            printHelp();
            return;
        }

        // TODO use some library to parse command line options

        if ("-s".equals(args[0])) {
            ToolImportSplitter.run(args[1], args.length > 2 ? args[2] : null);
        } else if ("-i".equals(args[0])) {
            ToolImport.run(args[1]);
        } else if ("-e".equals(args[0])) {
            ToolExport.run(args[1], args.length > 2 ? args[2] : null, args.length > 3 ? args[3] : null);
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("commands format:");
        System.out.println("\texport: -e <project dir> <output file>");
        System.out.println("\timport with splitting: -s <input file> <splitting config>");
        System.out.println("\timport: -i <input file>");
    }
}
