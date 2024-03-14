import org.apache.nifi.processor.ProcessSession

session = (ProcessSession) session
def in_ff = session.get()
if (!in_ff) return

def recordCount
def table = in_ff.'schema.table'
def delimiter = in_ff.'delimiter'
def conn = CTL.db.getConnection()
def readerFactory = RecordReader.csv
try {
    session.read(in_ff, { inputStream ->
        def variables = new HashMap<String, String>(in_ff.attributes)
        def reader = readerFactory.createRecordReader(variables, inputStream, -1L, log)
        def fields = "(" + reader.getSchema().getFieldNames().join(",") + ")"
         long rowsInserted = conn.getWrapped().getInnermostDelegateInternal().getCopyAPI()
                .copyIn(
                        "COPY gidrometcenter.${table}${fields} FROM STDIN (FORMAT csv, DELIMITER '${delimiter}')",
                        inputStream,
                        8192
                )
        recordCount = rowsInserted.toString()
    })
    in_ff.'record.count' = recordCount
    session.transfer(in_ff, REL_SUCCESS)
} catch (Exception e) {
    log.error('Error has occured', e)
    session.transfer(in_ff, REL_FAILURE)
} finally {
    conn.close()
}
