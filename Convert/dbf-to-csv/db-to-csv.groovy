/* Thanks to  Nikolay Akimov https://github.com/vomikan
based on https://github.com/albfernandez/javadbf#reading-a-dbf-file
Added MAX_LINES - Max lines in one OutFile
*/
import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.ProcessSession
import org.apache.nifi.processor.io.InputStreamCallback

import com.linuxense.javadbf.*

SEP = ","
MAX_LINES = 10000

def escapeCSV(String s) {
    str1 = new String(s.getBytes("ISO-8859-1"), "UTF-8")
    str2 = new String(s.getBytes("ISO-8859-1"), "windows-1251")
    if (str1.length() < str2.length()) { s = str1 } else { s = str2 }
    if (s.contains(SEP)) { 
      s = /"/ + s.replaceAll(/"/, /""/) + /"/
    } 
    return s
}

ProcessSession session=session
def flowFile = session.get()
if(!flowFile) return
List<FlowFile> files=[]
FlowFile currentFile
OutputStream outputStream
try {
    int current = 0
    session.read(flowFile, { inputStream ->
        reader = new DBFReader(inputStream)

        int numberOfFields = reader.getFieldCount()
        fields = new String[numberOfFields]
        String header = ''
        for (int i = 0; i < numberOfFields; i++) {
            fields[i] = reader.getField(i).getName()
            header += escapeCSV(fields[i])
            if (i < numberOfFields - 1) { header += SEP }
        }

        DBFRow row
        while ((row = reader.nextRow()) != null) {
            if (current == 0) {
                files.add(session.create(flowFile))
                currentFile = files.get(files.size() - 1)
                outputStream = session.write(currentFile)
                outputStream.write("${header.toString()}".getBytes('UTF-8'))
            }
            String csv = '\n'
            for (int i = 0; i < numberOfFields; i++) {
                csv += escapeCSV(row.getString(fields[i]))
                if (i < numberOfFields - 1) { csv += SEP }
            }
            outputStream.write("${csv.toString()}".getBytes('UTF-8'))
            current++
            if (current >= MAX_LINES) {
                current = 0
                outputStream.close()
            }
        }
    } as InputStreamCallback)

    //transfer control to success
    if (outputStream != null){ outputStream.close() }

    session.transfer(files, REL_SUCCESS)
    session.remove(flowFile)
} catch (ex) {
    flowFile = session.putAttribute(flowFile, "script.error_message", ex.getMessage())
    flowFile = session.putAttribute(flowFile, "script.error", ex.toString())
    //transfer control to failure
    session.transfer(flowFile, REL_FAILURE)
    if (outputStream != null){outputStream.close()}
    session.remove(files)
}
