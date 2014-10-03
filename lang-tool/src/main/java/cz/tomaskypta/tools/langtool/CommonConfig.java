package cz.tomaskypta.tools.langtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by tomas on 04.10.14.
 */
public class CommonConfig {

    public String ignoreListFile;
    private Set<String> ignoredSet;

    public CommonConfig() {
        this.ignoredSet = new HashSet<String>();
    }

    public CommonConfig(CommonConfig other) {
        this.ignoreListFile = other.ignoreListFile;
        this.ignoredSet = new HashSet<String>(other.ignoredSet);
    }

    public CommonConfig(CommandlineArguments args) {
        this();
        this.setIgnoredList(args.getIgnoreListFile());
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
