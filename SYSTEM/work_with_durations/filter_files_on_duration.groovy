import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.FlowFileFilter
import org.apache.nifi.processor.ProcessSession

ProcessSession session = session

long currentTime = new Date().getTime()
List<FlowFile> files = session.get(new FlowFileFilter() {
    @Override
    FlowFileFilter.FlowFileFilterResult filter(FlowFile flowFile) {
      //Accept Files that queued for more than 1 minute, but only those which process start not later than hour ago
        if (flowFile.lastQueueDate < currentTime - 60000 && flowFile.getEntryDate() > currentTime - 3600000)
            return FlowFileFilter.FlowFileFilterResult.ACCEPT_AND_CONTINUE
        else return FlowFileFilter.FlowFileFilterResult.REJECT_AND_CONTINUE
    }
})
if (files.size() == 0) return
//transfer files to Success
session.transfer(files, REL_SUCCESS)

files = session.get(new FlowFileFilter() {
    @Override
    FlowFileFilter.FlowFileFilterResult filter(FlowFile flowFile) {
      //Accept Files that start processed more than hour ago
        if (flowFile.getEntryDate() <= currentTime - 3600000) return FlowFileFilter.FlowFileFilterResult.ACCEPT_AND_CONTINUE
        else return FlowFileFilter.FlowFileFilterResult.REJECT_AND_CONTINUE
    }
})
if (files.size()==0) return
// Send such old files to Failure
session.transfer(files.REL_FAILURE)
