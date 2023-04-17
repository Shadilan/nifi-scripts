import groovy.json.JsonSlurper
import groovy.json.JsonGenerator
import java.nio.charset.StandardCharsets

def flowFile = session.get()
if (flowFile == null) {
    return
}

JsonSlurper jsonSlurper = new JsonSlurper()

if (flowFile.'kafka.topic' == 'CRM_BonusBalances') {
    flowFile = session.write(flowFile,
        { inputStream, outputStream ->
        content = org.apache.commons.io.IOUtils.toString(inputStream, java.nio.charset.StandardCharsets.UTF_8)
        //log.debug(content)
        json = jsonSlurper.parseText(content)
        //log.debug("size ="+json.size().toString())
        for (int i = 0; i < json.size(); i++) {
            //log.debug(json[i]['messageObject'].toString())
            if (json[i]['messageObject']['bonuses']) {
                bonuses = json[i]['messageObject']['bonuses']
            //log.debug(communications.toString())

                if (bonuses.size() != 0) {
                    def strbonuses = new JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
                .toJson(bonuses)

                    //println(strbonuses)
                    //log.debug('Here')
                    json[i]['messageObject']['bonuses'] = strbonuses
                }
        else json[i]['messageObject']['bonuses'] = ''
            }
            json[i]['messageObject'].remove('bonusBalanceState')
            if (json[i]['messageObject']['dueDate'] == '') { json[i]['messageObject']['dueDate'] = null }
            if (json[i]['messageObject']['regDate'] == '') { json[i]['messageObject']['regDate'] = null }
        }
        //println "Result \r\n"
        def resultJson =  new JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
                .toJson(json)

        outputStream.write(resultJson.getBytes(StandardCharsets.UTF_8))
        } as StreamCallback)

    session.transfer(flowFile, REL_SUCCESS)
    return
}

log.debug('CRM_Batch_BonusBalances')

flowFile = session.write(flowFile,
        { inputStream, outputStream ->
    def content = org.apache.commons.io.IOUtils.toString(inputStream, java.nio.charset.StandardCharsets.UTF_8)

    def resultJson = []
    //log.debug(content)
    json = jsonSlurper.parseText(content)
            //log.debug("size ="+json.size().toString())

    for (int i = 0; i < json.size(); i++) {
            //log.debug(json[i]['messageObject'].toString())

        log.debug('MessageObject size: ' + json[i]['MessageObject'].size())

        for (j = 0; j < json[i]['MessageObject'].size(); j++) {
            log.debug(' j =' + j)
            log.debug(json[i]['MessageObject'][j].toString())

            bonuses = json[i]['MessageObject'][j]['bonuses']
            //log.debug(communications.toString())
            if (bonuses.size() > 0) {
                strbonuses = new JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
                .toJson(bonuses)

                log.debug('Convert bonuses to str : ' + strbonuses)
                json[i]['MessageObject'][j]['bonuses'] = strbonuses
            }
            else {
                json[i]['MessageObject'][j]['bonuses'] = ''
            }

            json[i]['MessageObject'][j].remove('bonusBalanceState')
            if (json[i]['MessageObject'][j]['dueDate'] == '') { json[i]['MessageObject'][j]['dueDate'] = null }
            if (json[i]['MessageObject'][j]['regDate'] == '') { json[i]['MessageObject'][j]['regDate'] = null }

            def tmpJson = [
                  metadata: json[i]['Metadata'],
                  messageObject : json[i]['MessageObject'][j]
                ]
            resultJson.add(tmpJson)
        }
    }

    //println "Result \r\n"
    resultJson_str =  new JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
                .toJson(resultJson)

    outputStream.write(resultJson_str.getBytes(StandardCharsets.UTF_8))
        } as StreamCallback)

session.transfer(flowFile, REL_SUCCESS)
