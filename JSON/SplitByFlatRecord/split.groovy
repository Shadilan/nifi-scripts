import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.ProcessSession
import org.apache.nifi.processor.io.InputStreamCallback
import groovy.json.*


def srcflowFile = session.get();
if (srcflowFile == null) {
    //Exit on zero files
    return;
}

Map<String, File> resultFiles = new HashMap<>();
Map<String, OutputStream> resultOut = new HashMap<>()

/* writeTable Create One Flat Structure
/ prefix = prefix of created fields
/ name = name of table to write
/ m = record to write
/ result = Streams we already created
/ resultF = FlowFiles we already created
/ flowFile = base flow file
/ session = session to operate flowfile
*/
def writeTable(String prefix, String name, Map m, Map result, Map resultF, FlowFile flowFile, ProcessSession session) {
    def table_name = String.format("%s_%s", prefix, name)
    if (!(table_name in result)) {
        def ff = session.create(flowFile)
        def outer = session.write(ff)
        resultF.put(table_name, ff)
        result.put(table_name, outer)
    }
    OutputStream out = result.get(table_name)
    // Writing JsonLineByLine
    out.write(new JsonGenerator.Options().disableUnicodeEscaping().build().toJson(m).getBytes("UTF-8"))
    out.write('\n'.getBytes("UTF-8"))
}

//ConvertName
def translateName(String name) {
    if (name.toLowerCase() in ["token", "default"]) {
        return name + "_fld"
    } else if (name in ["_id"]) {
        return "record" + name
    } else return name
}
/*
/ parseJSON - Parse map object
/ main_id = Id of whole record
/ parent_id = Id of parent level record
/ prefix = prefix to fields
/ name = name of created record
/ flowFile = file to clone
/ session = session to operate flowFiles
*/
def parseJSON(String main_id, String parent_id, String prefix, String name, Map js, Map resultF, Map result, FlowFile flowFile, ProcessSession session) {
    def m = [:]
    if (!parent_id.isBlank()) {
        m.put("parent_id", parent_id)
    }
    String id = parent_id
    if (js.containsKey("_id")) {
        def val = js.get("_id")
        if (val instanceof Map && val.size() == 1) {
            id = val.get(val.keySet()[0])
        } else if (val instanceof String) {
            id = val.toString()
        }
    }
    if (main_id.isBlank()) {
        main_id = id
    } else {
        m.put("main_id", main_id)
    }
    for (String key : js.keySet()) {
        def val = js.get(key)
        if (val instanceof Map) {
            //Parse inner object
            if (val.size() == 1) {
                //If inner object have only one field just flatten it to upper level
                m.put(translateName(key), val.get(val.keySet()[0]))
            } else
            //Recurse call
                parseJSON(main_id, id, prefix, key, val, resultF, result, flowFile, session)
        } else if (val instanceof List) {
            // Parse list object recursively
            parseJSONA(main_id, id, prefix, key, val, resultF, result, flowFile, session)
        } else {
            //Add to record plain fields
            m.put(translateName(key), val)
        }
    }
    //Write plain record to file
    writeTable(prefix, name, m, result, resultF, flowFile, session)
}

/*
/ parseJSONA  - parse list object
/ main_id = Id of whole record
/ parent_id = Id of parent level record
/ prefix = prefix to fields
/ name = name of created record
/ flowFile = file to clone
/ session = session to operate flowFiles
*/
def parseJSONA(String main_id, String id, String prefix, String name, List js, Map resultF, Map result, FlowFile flowFile, ProcessSession session) {
    if (js instanceof List) {
        //Additional check for call in main level
        for (def item in js) {
            //Iterate objects in list
            if (item instanceof Map)
            //Parse map recursively
                parseJSON(main_id, id, prefix, name, item, resultF, result, flowFile, session)
            else if (item instanceof List) {
                //Parse list recursively
                parseJSONA(main_id, id, prefix, name, item, resultF, result, flowFile, session)
            } else {
                //Create record from plain fields
                def m = [:]
                if (!id.isBlank()) {
                    m.put("parent_id", id)
                }
                if (!main_id.isBlank()) {
                    m.put("main_id", main_id)
                }
                m.put(translateName("value"), item)
                writeTable(prefix, name, m, result, resultF, flowFile, session)
            }
        }
    }
}

//Main Cycle
def prefix='TRN'
def table='transaction'
session.read(srcflowFile,
        { it ->
            try {
                def ir = new InputStreamReader(it, 'UTF-8')
                def json = new JsonSlurper().parseText(ir.getText())
                if (json instanceof Map) {
                    parseJSON("", "", prefix, table, json, resultFiles, resultOut, srcflowFile, session)
                } else if (json instanceof List) {
                    parseJSONA("", "", prefix, table, json, resultFiles, resultOut, srcflowFile, session)
                }
            } catch (Exception e) {
                throw e
            }
        } as InputStreamCallback
)

//Sending completed files to Success
for (String key : resultFiles.keySet()) {
    resultOut.get(key).close()
    def ff = session.putAttribute(resultFiles.get(key), "table.name", key)
    session.transfer(ff, REL_SUCCESS)
}
session.remove(srcflowFile)
