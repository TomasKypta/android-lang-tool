package cz.tomaskypta.tools.langtool.importing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cz.tomaskypta.tools.langtool.CommandlineArguments;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by Tomáš Kypta on 02.10.14.
 */
public class ImportConfig {

    public boolean escapeAll;
    public String inputFile;
    public String mappingFile;
    public String outputDirName;
    public String outputFileName;
    public String escapingConfigFile;
    private Set<String> escapedSet;
    public boolean unescapeFirst;
    public String ignoreListFile;
    private Set<String> ignoredSet;

    public ImportConfig() {
        this.escapedSet = new HashSet<String>();
        this.ignoredSet = new HashSet<String>();
    }

    public ImportConfig(ImportConfig other) {
        this.escapeAll = other.escapeAll;
        this.inputFile = other.inputFile;
        this.mappingFile = other.mappingFile;
        this.outputDirName = other.outputDirName;
        this.outputFileName = other.outputFileName;
        this.escapedSet = new HashSet<String>(other.escapedSet);
        this.unescapeFirst = other.unescapeFirst;
        this.ignoreListFile = other.ignoreListFile;
        this.ignoredSet = new HashSet<String>(other.ignoredSet);
    }

    public ImportConfig(CommandlineArguments args) {
        this.inputFile = args.getImportFile();
        this.mappingFile = args.getMappingFile();
        // TODO
        this.outputDirName = null;
        this.outputFileName = null;
        this.setEscapingConfig(args.getEscapingConfigFile());
        this.unescapeFirst = args.isUnescapeFirst();
        this.setIgnoredList(args.getIgnoreListFile());
    }

    public Boolean isEscapedKey(String key) {
        return escapedSet.contains(key);
    }

    public void setEscapingConfig(String escapingConfigFile) {
        this.escapingConfigFile = escapingConfigFile;
        if (escapingConfigFile == null) {
            return;
        }
        escapedSet.clear();

        try {
            HSSFWorkbook wbEscaping = new HSSFWorkbook(new FileInputStream(new File(this.escapingConfigFile)));
            HSSFSheet sheetEscaping = wbEscaping.getSheetAt(0);
            Iterator<Row> it = sheetEscaping.rowIterator();
            while (it.hasNext()) {
                Row row = it.next();
                if (row == null || row.getCell(0) == null) {
                    return;
                }
                escapedSet.add(row.getCell(0).getStringCellValue());
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    public Boolean isIgnoredKey(String key) {
        return ignoredSet.contains(key);
    }

    public void setIgnoredList(String ignoredListFile) {
        this.ignoreListFile = ignoredListFile;
        if (ignoredListFile == null) {
            return;
        }
        ignoredSet.clear();

        try {
            HSSFWorkbook wbEscaping = new HSSFWorkbook(new FileInputStream(new File(this.ignoreListFile)));
            HSSFSheet sheetEscaping = wbEscaping.getSheetAt(0);
            Iterator<Row> it = sheetEscaping.rowIterator();
            while (it.hasNext()) {
                Row row = it.next();
                if (row == null || row.getCell(0) == null) {
                    return;
                }
                ignoredSet.add(row.getCell(0).getStringCellValue());
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }
}
