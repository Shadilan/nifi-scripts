import org.apache.poi.ss.usermodel.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.util.*
import java.io.*
import java.text.SimpleDateFormat
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

flowFile = session.get()
if(!flowFile)return

newFlowFile = session.create()
def filename = flowFile.getAttribute('filename')
newFlowFile = session.putAttribute(newFlowFile, "filename",filename)

flowFile.getAttributes().each { key,value ->
  session.putAttribute(newFlowFile, key,value)
}

try{
  Workbook wb = WorkbookFactory.create(session.read(flowFile))

  def sheet = ProcessSheet.evaluateAttributeExpressions(flowFile).value

  def ws = wb.getSheet(sheet)
  int r = ws.getLastRowNum()
  def csv_data_rows = []

  for(def i = SkipRows.value.toInteger() ; i <= r ; i++ ){
          def row = ws.getRow(i)
          def noOfCell = MaxCellNumber.value.toInteger()
          def tmp_data_list = []

          for (def j=0;j < noOfCell;j++){
            Cell cell = row.getCell(j)
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                value = ""
            }
            else if(cell.getCellType() == CellType.NUMERIC){
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy")
                    String formatted = format1.format(date)
                    value = formatted 
                } else {
                    value = (cell.getNumericCellValue())
                }
            }
            else{value = (cell.getStringCellValue()) 
                 value = value.replace("\n","")
            }

            def temp  = '"' + value +'"'
            tmp_data_list.add(temp)

          }
          data_row = tmp_data_list.join(",")
          csv_data_rows.add(data_row)
  }

  session.remove(flowFile)
  newFlowFile = session.write(newFlowFile, {outputStream ->
    outputStream.write(csv_data_rows.join("\n").getBytes(StandardCharsets.UTF_8))
  } as OutputStreamCallback)
  session.transfer(newFlowFile, REL_SUCCESS)
}
catch(ex)
{
 session.remove(newFlowFile)
 log.error("my message" + ex.getMessage())
 session.transfer(flowFile, REL_FAILURE)
}
