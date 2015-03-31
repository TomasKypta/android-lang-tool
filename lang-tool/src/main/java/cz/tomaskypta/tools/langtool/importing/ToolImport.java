package cz.tomaskypta.tools.langtool.importing;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cz.tomaskypta.tools.langtool.util.EscapingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ToolImport {

    private DocumentBuilder builder;
    private File outResDir;
    private PrintStream out;
    private HashMap<String, String> mMapping;
    private ImportConfig mConfig;

    public ToolImport(PrintStream out) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        builder = dbf.newDocumentBuilder();
        this.out = out == null ? System.out : out;
    }

    public static void run(ImportConfig config) throws IOException, ParserConfigurationException, TransformerException {
        if (config == null) {
            System.err.println("Cannot import, missing config");
            return;
        }

        if (StringUtils.isEmpty(config.inputFile)) {
            System.err.println("Cannot import, missing input file name");
            return;
        }

        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File(config.inputFile)));
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFSheet sheetMapping = null;
        if (!StringUtils.isEmpty(config.mappingFile)) {
            HSSFWorkbook wbMapping = new HSSFWorkbook(new FileInputStream(new File(config.mappingFile)));
            sheetMapping = wbMapping.getSheetAt(0);
        }

        String outputDirName = config.outputDirName;
        if (StringUtils.isEmpty(outputDirName)) {
            outputDirName = sheet.getSheetName();
        }

        if (config.outputFileName == null) {
            config.outputFileName = "strings.xml";
        }

        ToolImport tool = new ToolImport(null);
        tool.mConfig = config;
        tool.outResDir = new File("out/" + outputDirName + "/res");
        tool.outResDir.mkdirs();
        tool.prepareMapping(sheetMapping);
        tool.parse(sheet);
    }

    public static void run(PrintStream out, String projectDir, String input) throws IOException, ParserConfigurationException, TransformerException {
        ToolImport tool = new ToolImport(out);
        if (input == null || "".equals(input)) {
            tool.out.println("File name is missed");
            return;
        }

        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File(input)));
        HSSFSheet sheet = wb.getSheetAt(0);


        tool.outResDir = new File(projectDir, "/res");
        //tool.outResDir.mkdirs();
        tool.parse(sheet);
    }

    private void prepareMapping(HSSFSheet sheetMapping) {
        if (sheetMapping == null) {
            return;
        }
        mMapping = new HashMap<String, String>();
        Iterator<Row> it = sheetMapping.rowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            mMapping.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
        }
    }

    private void parse(HSSFSheet sheet) throws IOException, TransformerException {
        Row row = sheet.getRow(0);
        Iterator<Cell> cells = row.cellIterator();
        cells.next();// ignore key
        int i = 1;
        while (cells.hasNext()) {
            String lang = cells.next().toString();
            if (mMapping != null && mMapping.containsKey(lang)) {
                lang = mMapping.get(lang);
            }
            generateLang(sheet, lang, i);
            i++;
        }
    }

    private void generateLang(HSSFSheet sheet, String lang, int column) throws IOException,
        TransformerException {

        Document dom = builder.newDocument();
        Element root = dom.createElement("resources");
        dom.appendChild(root);

        Iterator<Row> iterator = sheet.rowIterator();
        iterator.next();//ignore first row;
        Element pluralsNode = null;
        Element stringArrayNode = null;
        String plurarName = null;
        String arrayName = null;

        while (iterator.hasNext()) {
            HSSFRow row = (HSSFRow)iterator.next();
            Cell cell = row.getCell(0);// android key
            if (cell == null) {
                continue;
            }
            String key = cell.toString();
            if (key == null || "".equals(key)) {
                root.appendChild(dom.createTextNode(""));
                continue;
            }
            if (key.startsWith("/**")) {
                root.appendChild(dom.createComment(key.substring(3, key.length() - 3)));
                continue;
            }

            if (key.startsWith("//")) {
                root.appendChild(dom.createComment(key.substring(2)));
                continue;
            }

            if (mConfig.isIgnoredKey(key)) {
                root.appendChild(dom.createTextNode(""));
                continue;
            }

            int plurarIndex = key.indexOf("#");
            int arrayIndex = key.indexOf("[");

            if (plurarIndex >= 0) {
                // plurals
                Cell valueCell = row.getCell(column);
                String value = "";
                if (valueCell != null) {
                    value = valueCell.toString();// value
                }
                String plurarNameNew = key.substring(0, plurarIndex);
                String quantity = key.substring(plurarIndex + 1);
                if (!plurarNameNew.equals(plurarName)) {
                    plurarName = plurarNameNew;
                    pluralsNode = dom.createElement("plurals");
                    pluralsNode.setAttribute("name", plurarName);
                }
                value = prepareOutputValue(lang, key, value);
                addContent(dom, pluralsNode, value, "item", null, quantity);

                root.appendChild(pluralsNode);
            } else if (arrayIndex >= 0) {
                // string-array
                Cell valueCell = row.getCell(column);
                String value = "";
                if (valueCell != null) {
                    value = valueCell.toString();// value
                }
                String arrayNameNew = key.substring(0, arrayIndex);
                // we don't really need the index
//                String tmp = key.substring(arrayIndex+1);
//                int index = Integer.parseInt(tmp.substring(0, tmp.indexOf("]")));

                // it's not bullet-proof, but for the time being good enough
                if (!arrayNameNew.equals(arrayName)) {
                    arrayName = arrayNameNew;
                    stringArrayNode = dom.createElement("string-array");
                    stringArrayNode.setAttribute("name", arrayName);
                }

                value = prepareOutputValue(lang, key, value);
                addContent(dom, stringArrayNode, value, "item", null, null);

                root.appendChild(stringArrayNode);
            } else {
                //string
                Cell valueCell = row.getCell(column);
                if (valueCell == null) {
                    addEmptyKeyValue(dom, root, key);
                    continue;
                }
                String value = valueCell.toString();// value

                if (value.isEmpty()) {
                    addEmptyKeyValue(dom, root, key);
                } else {

                    value = prepareOutputValue(lang, key, value);

                    addContent(dom, root, value, "string", key, null);
                }
            }

        }

        save(dom, lang);
    }

    private void addContent(Document dom, Element root, String value, String nodeName, String key, String quantity) {
        if (!mConfig.isMixedContent(key)) {
            addContentAsString(dom, root, value, nodeName, key, quantity);
            return;
        }

        try {
            DocumentBuilder db = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
            // TODO improve
            // currently ignoring errors - there were irrelevant messages about '&'
            db.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {

                }

                @Override
                public void error(SAXParseException exception) throws SAXException {

                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {

                }
            });
            Element content = db
                .parse(new ByteArrayInputStream(("<" + nodeName + ">" + value + "</" + nodeName + ">").getBytes()))
                .getDocumentElement();
            if (key != null) {
                content.setAttribute("name", key);
            }
            if (quantity != null) {
                content.setAttribute("quantity", quantity);
            }
            Node tmp = dom.importNode(content, true);
            root.appendChild(tmp);
        } catch (Exception e) {
            addContentAsString(dom, root, value, nodeName, key, quantity);
        }
    }

    private void addContentAsString(Document dom, Element root, String value, String nodeName, String key, String quantity) {
        Element node = dom.createElement(nodeName);
        ContentType type = ContentType.detect(key);

        if (key != null) {
            int index = key.indexOf("!");
            if (index != -1) {
                key = key.substring(0, index);
            }
            node.setAttribute("name", key);
        }
        if (quantity != null) {
            node.setAttribute("quantity", quantity);
        }

        if (type == ContentType.CDATA) {
            node.appendChild(dom.createCDATASection(value));
        } else {
            node.setTextContent(value);
        }
        root.appendChild(node);
    }

    private String prepareOutputValue(String lang, String key, String value) {
        ImportConfig.Transformation tranformation = mConfig.getKeyTransformation(key);
        if (tranformation != null) {
            value = tranformation.apply(value, lang);
        }
        if (mConfig.unescapeFirst) {
            value = EscapingUtils.unescapeQuotes(value);
        }
        if (mConfig.isEscapedKey(key)) {
            value = EscapingUtils.escapeWithQuotes(value);
        } else {
            value = EscapingUtils.escapeWithBackslash(value);
        }
        return value;
    }

    private static void addEmptyKeyValue(Document dom, Element root, String key) {
        root.appendChild(dom.createComment(String.format(" TODO: string name=\"%s\" ", key)));
    }

    private void save(Document doc, String lang) throws TransformerException {
        File dir;
        if ("default".equals(lang) || lang == null || "".equals(lang)) {
            dir = new File(outResDir, "values");
        } else {
            dir = new File(outResDir, "values-" + lang);
        }
        dir.mkdir();

        //DOMUtils.prettyPrint(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(dir, mConfig.outputFileName));

        transformer.transform(source, result);
    }
}
