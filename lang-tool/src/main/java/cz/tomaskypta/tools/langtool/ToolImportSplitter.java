package cz.tomaskypta.tools.langtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by Tomas Kypta on 19.09.14.
 */
public class ToolImportSplitter {

    private TreeMap<Integer, String> mSplittingMap;
    private HashMap<String, String> mOutputFileNames;
    private File mIntermediateXlsDir;

    public static void run(String input, String config, String mapping) throws IOException,
        ParserConfigurationException, TransformerException {
        if (input == null || "".equals(input)) {
            System.out.println("File name is missed");
            return;
        }
        if (config == null || "".equals(config)) {
            System.out.println("No config, no splitting");
            ToolImport.run(input, mapping, null);
            return;
        }

        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File(input)));
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFWorkbook wbConfig = new HSSFWorkbook(new FileInputStream(new File(config)));
        HSSFSheet sheetConfig = wbConfig.getSheetAt(0);

        ToolImportSplitter tool = new ToolImportSplitter();
        tool.mIntermediateXlsDir = new File("intermediate");
        tool.mIntermediateXlsDir.mkdirs();

        tool.prepareSplittingMap(sheetConfig);
        tool.split(sheet);

        for (String file : tool.mSplittingMap.values()) {
            File outputFile = new File(tool.mIntermediateXlsDir, file);
            System.out.println("Importing file: " + file);
            ToolImport.run(outputFile.getPath(), outputFile.getName().substring(0, outputFile.getName().indexOf('.'))
                , mapping, tool.mOutputFileNames.get(file));
        }
    }

    private void prepareSplittingMap(HSSFSheet sheetConfig) throws IOException, TransformerException {
        mSplittingMap = new TreeMap<Integer, String>();
        mOutputFileNames = new HashMap<String, String>();
        Iterator<Row> it = sheetConfig.rowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            if (row == null || row.getCell(0) == null || row.getCell(1) == null) {
                return;
            }
            String splitName = row.getCell(1).getStringCellValue();
            mSplittingMap.put((int)row.getCell(0).getNumericCellValue(), splitName);
            if (row.getCell(2) != null) {
                mOutputFileNames.put(splitName, row.getCell(2).getStringCellValue());
            }
        }
    }

    private void split(HSSFSheet inSheet) throws IOException, TransformerException {
        Row inTitleRow = inSheet.getRow(0);
        for (Map.Entry<Integer, String> entry : mSplittingMap.entrySet()) {
            System.out.println("Splitting into file: " + entry.getValue());
            File outputFile = new File(mIntermediateXlsDir, entry.getValue());
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(outputFile);

                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet outSheet = wb.createSheet(inSheet.getSheetName());
                copyTitleRow(inTitleRow, outSheet);

                Integer actFileStart = entry.getKey();
                Integer nextFileStart = mSplittingMap.higherKey(entry.getKey());
                if (nextFileStart == null) {
                    nextFileStart = inSheet.getLastRowNum() + 2;
                }

                copyRowRange(inSheet, outSheet, actFileStart, nextFileStart);

                wb.write(fos);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private void copyTitleRow(Row inTitleRow, HSSFSheet outSheet) {
        HSSFRow outTitleRow = outSheet.createRow(0);
        copyRow(inTitleRow, outTitleRow);
    }

    private void copyRowRange(HSSFSheet inSheet, HSSFSheet outSheet, int rowStart, int rowEnd) {
        for (int rowIdx = rowStart, outRowIdx = 1; rowIdx < rowEnd; rowIdx++, outRowIdx++) {
            HSSFRow outRow = outSheet.createRow(outRowIdx);
            HSSFRow inRow = inSheet.getRow(rowIdx-1);
            copyRow(inRow, outRow);
        }
    }

    private void copyRow(Row inTitleRow, HSSFRow outRow) {
        // TODO copy formatting
        Iterator<Cell> it = inTitleRow.cellIterator();
        while (it.hasNext()) {
            Cell srcCell = it.next();
            outRow.createCell(srcCell.getColumnIndex(), srcCell.getCellType());
            outRow.getCell(srcCell.getColumnIndex()).setCellValue(srcCell.getStringCellValue());
        }
    }
}
