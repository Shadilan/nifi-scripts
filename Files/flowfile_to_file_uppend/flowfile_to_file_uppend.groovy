import groovy.json.*

def flowFile = session.get()
if(!flowFile) return

try {
  session.read(flowFile, { inputStream ->

    def file_path = flowFile.getAttribute("file_path");
    def file_name = flowFile.getAttribute("file_name");

    def filePath = new File(file_path);
    if( !filePath.exists() ) {
        filePath.mkdir()
    }

    new File( file_path, file_name ).withWriterAppend { w ->
        w << inputStream << "\n"
    }


  } as InputStreamCallback)

  //transfer control to success 
  session.transfer(flowFile, REL_SUCCESS)

} catch (ex) {
    flowFile = session.putAttribute(flowFile, "script.error_message", ex.getMessage())
    flowFile = session.putAttribute(flowFile, "script.error", ex.toString())
    //transfer control to failure
    session.transfer(flowFile, REL_FAILURE)
}
