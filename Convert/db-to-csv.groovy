/* Thanks to Nikolay Akimenko for base script
Added MAX_LINES - Max lines in one OutFile
*/
import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.ProcessSession
import org.apache.nifi.processor.io.InputStreamCallback

import com.linuxense.javadbf.*

static def _(String s) {
    def str1 = __(s)
    def str2 = new String(s.getBytes("ISO-8859-1"), "windows-1251")
    if (str1.length() < str2.length()) { return str1} else { return str2 }
}

static def __(String s) {
    return new String(s.getBytes("ISO-8859-1"), "UTF-8")
}
def MAX_LINES=10000
ProcessSession session=session
def flowFile = session.get()
if(!flowFile) return
List<FlowFile> files=[]
FlowFile currentFile
OutputStream outputStream
try {
    int current=0
    session.read(flowFile, { inputStream ->
        reader = new DBFReader(inputStream)
        //def charset = reader.getCharset()

        int numberOfFields = reader.getFieldCount()
        def fields = new String[numberOfFields]
        String header = ''
        for (int i = 0; i < numberOfFields; i++) {
            fields[i] = reader.getField(i).getName()
            header += _(fields[i])
            if (i < numberOfFields - 1) {header += ','}
        }

        DBFRow row

        files.add(session.create(flowFile))
        currentFile=files.get(files.size()-1)
        outputStream=session.write(currentFile)
        outputStream.write("${header.toString()}".getBytes('UTF-8'))
        while ((row = reader.nextRow()) != null) {
            String csv = '\n'
            for (int i = 0; i < numberOfFields; i++) {
                csv += _(row.getString(fields[i]))
                if (i < numberOfFields - 1) {csv += ','}
            }
            outputStream.write("${csv.toString()}".getBytes('UTF-8'))
            current++
            if (current>MAX_LINES){
                current=0
                outputStream.close()
                files.add(session.create(flowFile))
                currentFile=files.get(files.size()-1)
                outputStream=session.write(currentFile)
                outputStream.write("${header.toString()}".getBytes('UTF-8'))
            }
        }
    } as InputStreamCallback)

    //transfer control to success
    if (outputStream!=null){outputStream.close()}
    if (current==0){
        //Удаляем пустой флоуфайл
        files.remove(files.size()-1)
    }

    session.transfer(files, REL_SUCCESS)
    session.remove(flowFile)
} catch (ex) {
    flowFile = session.putAttribute(flowFile, "script_error_message", ex.getMessage())
    flowFile = session.putAttribute(flowFile, "script_error", ex.toString())
    //transfer control to failure
    session.transfer(flowFile, REL_FAILURE)
    if (outputStream!=null){outputStream.close()}
    session.remove(files)
}
