import org.apache.nifi.processor.ProcessSession

session = (ProcessSession) session
def in_ff = session.get()
if (!in_ff) return

def recordCount
def table = in_ff.'schema.table'
def delimiter = in_ff.'delimiter'
def conn = CTL.db?.getConnection().getWrapped().getInnermostDelegateInternal()
try {
    session.read(in_ff, { inputStream ->
        long rowsInserted = conn.getCopyAPI()
                .copyIn(
                        "COPY ${table} FROM STDIN (FORMAT csv, HEADER, DELIMITER '${delimiter}')",
                        inputStream,
                        8192
                )
        recordCount = rowsInserted.toString()
    })
} catch (Exception e){
    log.error(e)
} finally {
    conn?.close()
}

in_ff.'record.count' = recordCount
session.transfer(in_ff, REL_SUCCESS)