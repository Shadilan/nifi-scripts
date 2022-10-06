import org.apache.nifi.flowfile.FlowFile

import java.time.*
import java.time.format.*

FlowFile flowFile = session.get()
if (!flowFile) return

String ts_raw = flowFile.ts_raw

if (ts_raw == null) {
    REL_FAILURE << flowFile
    return
}

String ts = LocalDateTime.parse(ts_raw, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))

flowFile = session.putAttribute(flowFile, "ts", ts)
REL_SUCCESS << flowFile
