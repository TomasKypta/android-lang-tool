android-lang-tool
=================

Tool for exporting and importing Android string resources for translation.

It exports Android string resources to Excel and imports them back to the project after translation.
The tool scans Android project and exports strings, by default from strings.xml. Additional resources can be specified.
All the resources are concatenated in a single Excel file.

Xml comments are supported 
Missing traslations have red background in the xls file.

To build the application execute: `mvn package`
To run the application execute: `java -jar langtools-VERSION-jar-with-dependencies.jar`

Tool has 3 modes:
* exporting to xls
* importing from xls
* importing from xls with splitting xls into multiple separate xls files
 
## Exporting
`
params: -e <project dir> <output file> <list of additional resources>
`

**project dir** - path to the Android project 
**output file** - name of the generated Excel file
**list of additional resources** - list of additional resources separated by ':'

## Importing

`
params: -i <input file>
`

**input file** - name of the Excel file for importing into the project

## Importing with splitting

`
params: -s <input file> <splitting config file>
`

**input file** - name of the Excel file for importing into the project
**splitting config file** - Excel file containing splitting info

### Format of splitting configuration file

* The first column contains row index of the beginning of a subfile.
* The second column contains name of the output subfile. 