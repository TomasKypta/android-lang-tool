package cz.tomaskypta.tools.langtool;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import cz.tomaskypta.tools.langtool.exporting.ToolExport;
import cz.tomaskypta.tools.langtool.importing.ImportConfig;
import cz.tomaskypta.tools.langtool.importing.ToolImport;
import cz.tomaskypta.tools.langtool.importing.splitting.SplittingConfig;
import cz.tomaskypta.tools.langtool.importing.splitting.ToolImportSplitter;
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
//        @Parameter(names = "--escapeAll", description = "Escape strings")
//        boolean escape;
        @Parameter(names = "--escaping-config", description = "Escaping config for specifing keys to escape when " +
            "importing")
        String escapingConfigFile;
        @Parameter(names = "--unescape-first", description = "For unescaping string when  importing. Happend " +
            "before escaping. Useful when strings are unintionally escaped (e.g. by an error of translation agency).")
        boolean unescapeFirst;
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
            System.err.println(e.getMessage());
            printHelp();
            return;
        }

        if (parsedArgs.exportProject != null) {
            // exporting
            ToolExport.run(parsedArgs.exportProject, parsedArgs.outputFile, parsedArgs.additionalResources);
        } else if (parsedArgs.importFile != null) {
            SplittingConfig config = new SplittingConfig();
//                config.escapeAll = parsedArgs.escape;
            config.inputFile = parsedArgs.importFile;
            config.mappingFile = parsedArgs.mappingFile;
            config.outputDirName = null;
            config.outputFileName = null;
            config.splittingConfigFile = parsedArgs.splittingConfigFile;
            config.setEscapingConfig(parsedArgs.escapingConfigFile);
            config.unescapeFirst = parsedArgs.unescapeFirst;

            // importing
            if (parsedArgs.splittingConfigFile != null) {
                // splitting
                ToolImportSplitter.run(config);
            } else {
                ToolImport.run(config);
            }
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("commands format:");
        System.out.println("\texport: -e <project dir> [-o <output file>] [--additional-resources <colon separated " +
            "list of additional resources>]");
        System.out.println("\timport: -i <input file> [-s <splitting config>] [-m <mapping file>] [--escaping-config " +
            "<escaping config file>] [--unescape-before-escaping]");
    }
}
