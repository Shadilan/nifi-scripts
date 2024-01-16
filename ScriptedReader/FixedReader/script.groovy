import org.apache.nifi.serialization.record.MapRecord
import org.apache.nifi.serialization.record.RecordField
import org.apache.nifi.serialization.RecordReader
import org.apache.nifi.serialization.SimpleRecordSchema
import org.apache.nifi.controller.AbstractControllerService
import org.apache.nifi.logging.ComponentLog
import org.apache.nifi.serialization.MalformedRecordException
import org.apache.nifi.serialization.record.Record
import org.apache.nifi.serialization.RecordReaderFactory
import org.apache.nifi.serialization.record.RecordSchema
import org.apache.nifi.serialization.record.RecordFieldType

class FixedReader implements RecordReader {

    private final BufferedReader bufferedReader

    public FixedReader(InputStream input) {
        bufferedReader = new BufferedReader(new InputStreamReader(input))
    }

    public Record nextRecord(final boolean coerceTypes, final boolean dropUnknownFields) throws IOException, MalformedRecordException {
        final String line = bufferedReader.readLine()
        if (line == null) {
            return null
        }

        List<String> kvps = line.tokenize(' ')
        Map<String, Object> result = [:]
        List<RecordField> recordFields = List.of(new RecordField("EmployeeID",RecordFieldType.STRING.getDataType()),
                new RecordField("EmployeeName",RecordFieldType.STRING.getDataType()),
                new RecordField("EmployeeSalary",RecordFieldType.STRING.getDataType()))
        result.put("EmployeeID",line.substring(0,5))
        result.put("EmployeeName",line.substring(5,35))
        result.put("EmployeeSalary",line.substring(35,42)+'.'+line.substring(42,44))
        SimpleRecordSchema schema = new SimpleRecordSchema(recordFields)
        return new MapRecord(schema, result)
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close()
    }

    @Override
    public RecordSchema getSchema() {
        return schema
    }

}

class FixedReaderFactory extends AbstractControllerService implements RecordReaderFactory {

    public FixedReaderFactory() {
    }

    public RecordReader createRecordReader(final Map<String, String> variables, final InputStream inputStream, final long inputLength, final ComponentLog componentLog) throws IOException {
        return new FixedReader(inputStream)
    }

}

reader = new FixedReaderFactory()
