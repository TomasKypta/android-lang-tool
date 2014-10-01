package cz.tomaskypta.tools.langtool;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.xml.sax.SAXException;

public class Tool {

    static class CommandlineArguments {

        @Parameter(names = "-e", description = "Export project dir")
        String exportProject;
        @Parameter(names = "-o", description = "Output file")
        String outputFile;
        @Parameter(names = "--additional-resources", description = "Colon separated list of additional resource files" +
            " to export")
        String additionalResources;
        @Parameter(names = "-i", description = "Import xls file")
        String importFile;
        @Parameter(names = "-m", description = "Mapping file for rewriting resource qualifiers")
        String mappingFile;
        @Parameter(names = "-s", description = "Splitting config file for import")
        String splittingConfigFile;
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        if (args == null || args.length == 0) {
            printHelp();
            return;
        }

        CommandlineArguments parsedArgs = new CommandlineArguments();
        try {
            new JCommander(parsedArgs, args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            printHelp();
            return;
        }

        if (parsedArgs.exportProject != null) {
            // exporting
            ToolExport.run(parsedArgs.exportProject, parsedArgs.outputFile, parsedArgs.additionalResources);
        } else if (parsedArgs.importFile != null) {
            // importing
            if (parsedArgs.splittingConfigFile != null) {
                // splitting
                ToolImportSplitter.run(parsedArgs.importFile, parsedArgs.splittingConfigFile, parsedArgs.mappingFile);
            } else {
                ToolImport.run(parsedArgs.importFile, parsedArgs.mappingFile);
            }
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("commands format:");
        System.out.println("\texport: -e <project dir> [-o <output file>] [--additional-resources <colon separated " +
            "list of additional resources>]");
        System.out.println("\timport: -i <input file> [-s <splitting config>] [-m <mapping file>]");
    }
}
