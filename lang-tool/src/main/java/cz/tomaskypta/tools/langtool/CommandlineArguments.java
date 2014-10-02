package cz.tomaskypta.tools.langtool;

import com.beust.jcommander.Parameter;

/**
* Created by Tomáš Kypta on 03.10.14.
*/
public class CommandlineArguments {

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
//        boolean escapeAll;
    @Parameter(names = "--escaping-config", description = "Escaping config for specifing keys to escape when " +
        "importing")
    String escapingConfigFile;
    @Parameter(names = "--unescape-first", description = "For unescaping string when  importing. Happend " +
        "before escaping. Useful when strings are unintionally escaped (e.g. by an error of translation agency).")
    boolean unescapeFirst;
    @Parameter(names = "--ignore-list", description = "List of ignored keys.")
    String ignoreListFile;


    public String getExportProject() {
        return exportProject;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getAdditionalResources() {
        return additionalResources;
    }

    public String getImportFile() {
        return importFile;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public String getSplittingConfigFile() {
        return splittingConfigFile;
    }

    public String getEscapingConfigFile() {
        return escapingConfigFile;
    }

    public boolean isUnescapeFirst() {
        return unescapeFirst;
    }

    public String getIgnoreListFile() {
        return ignoreListFile;
    }
}
