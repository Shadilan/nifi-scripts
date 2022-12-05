import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.FlowFileFilter

//Filter for accept all files
class AllFiles implements FlowFileFilter{
    @Override
    FlowFileFilterResult filter(FlowFile flowFile) {
        return FlowFileFilterResult.ACCEPT_AND_CONTINUE
    }
}


List<FlowFile> inFlowFiles = session.get(new AllFiles())

//If have no files return
if (inFlowFiles.isEmpty()) return

//Transfer all to successd
session.transfer(inFlowFiles,REL_SUCCESS)
