import groovy.json.JsonSlurper

def flowFile = session.get()
if(!flowFile) return

try {
    def map1 = flowFile.read().withReader("UTF-8"){ new JsonSlurper().parse(it) }
 
    def jsonSlurper = new JsonSlurper()
    def second_json = flowFile.getAttribute("script.etalon_json")
    def map2 = jsonSlurper.parseText(second_json)
    Boolean result = (map1 == map2)

    flowFile = session.putAttribute(flowFile, "script.output", result.toString())

    //Clearing an attribute that is not needed
    flowFile = session.putAttribute(flowFile, "script.etalon_json", "")

    //transfer control to success 
    session.transfer(flowFile, REL_SUCCESS)
} catch (ex) {
    flowFile = session.putAttribute(flowFile, "script.error_message", ex.getMessage())
    flowFile = session.putAttribute(flowFile, "script.error", ex.toString())
    //transfer control to failure
    session.transfer(flowFile, REL_FAILURE)
}
