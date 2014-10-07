package cz.tomaskypta.tools.langtool.importing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import cz.tomaskypta.tools.langtool.CommandlineArguments;
import cz.tomaskypta.tools.langtool.CommonConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by Tomáš Kypta on 02.10.14.
 */
public class ImportConfig extends CommonConfig {

    static class Transformation {
        String regex;
        String transformation;

        Transformation(String regex, String transformation) {
            this.regex = regex;
            this.transformation = transformation;
        }

        String apply(String str) {
            if (str == null) {
                return null;
            }
            return str.replaceFirst(regex, transformation);
        }
    }

    public boolean escapeAll;
    public String inputFile;
    public String mappingFile;
    public String outputDirName;
    public String outputFileName;
    public String escapingConfigFile;
    private Set<String> escapedSet;
    public boolean unescapeFirst;
    public String extraTransformations;
    private Map<String, Transformation> transformationsMap;


    public ImportConfig() {
        super();
        this.escapedSet = new HashSet<String>();
        transformationsMap = new HashMap<String, Transformation>();
    }

    public ImportConfig(ImportConfig other) {
        super(other);
        this.escapeAll = other.escapeAll;
        this.inputFile = other.inputFile;
        this.mappingFile = other.mappingFile;
        this.outputDirName = other.outputDirName;
        this.outputFileName = other.outputFileName;
        this.escapedSet = new HashSet<String>(other.escapedSet);
        this.unescapeFirst = other.unescapeFirst;
        this.transformationsMap = new HashMap<String, Transformation>(other.transformationsMap);
        this.extraTransformations = other.extraTransformations;
    }

    public ImportConfig(CommandlineArguments args) {
        super(args);
        this.inputFile = args.getImportFile();
        this.mappingFile = args.getMappingFile();
        // TODO
        this.outputDirName = null;
        this.outputFileName = null;
        this.setEscapingConfig(args.getEscapingConfigFile());
        this.unescapeFirst = args.isUnescapeFirst();
        this.setTransformations(args.getExtraTransformations());
    }

    public Boolean isEscapedKey(String key) {
        return escapedSet.contains(key);
    }

    public void setEscapingConfig(String escapingConfigFile) {
        this.escapedSet = new HashSet<String>();
        this.escapingConfigFile = escapingConfigFile;
        if (StringUtils.isEmpty(escapingConfigFile)) {
            return;
        }

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

    public Transformation getKeyTransformation(String key) {
        return transformationsMap.get(key);
    }

    public void setTransformations(String extraImportTransformations) {
        this.transformationsMap = new HashMap<String, Transformation>();
        this.extraTransformations = extraImportTransformations;
        if (StringUtils.isEmpty(extraImportTransformations)) {
            return;
        }

        try {
            HSSFWorkbook wbEscaping = new HSSFWorkbook(new FileInputStream(new File(this.extraTransformations)));
            HSSFSheet sheetEscaping = wbEscaping.getSheetAt(0);
            Iterator<Row> it = sheetEscaping.rowIterator();
            while (it.hasNext()) {
                Row row = it.next();
                if (row == null || row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null) {
                    return;
                }
                Transformation transformation = new Transformation(row.getCell(1).getStringCellValue(),
                    row.getCell(2).getStringCellValue());
                transformationsMap.put(row.getCell(0).getStringCellValue(), transformation);
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }
}
