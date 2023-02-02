import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import groovy.json.JsonParserType
import groovy.json.JsonSlurper

import org.apache.commons.io.IOUtils
import org.apache.nifi.processor.io.StreamCallback

import java.nio.charset.StandardCharsets


class DeepJson {

    static def jsonSlurper = new JsonSlurper().setType(JsonParserType.CHARACTER_SOURCE)

    static Object findDeep(Map map, Object key) {
        map.get(key) ?: map.findResult { k, v -> if (v in Map) findDeep(v, key) }
    }

    static Object convertString(String s) {
        try {
            def js = jsonSlurper.parseText(s)
            if (js in Map) {
                convertMap(js)
            } else if (js in ArrayList) {
                convertArray(js)
            } else s
        } catch (ignored) {
            s
        }
    }

    static Map convertMap(Map m) {
        m.each { it.value = convert(it.value) }
    }

    static ArrayList convertArray(ArrayList a) {
        a.collect({convert(it)})
        //a.collect({if (it in Map) convert(it) else it} ) // - replace line above if you want to skip array of string converting
    }

    static Object convert(Object o) {
        if (o in Map) {
            convertMap(o)
        } else if (o in ArrayList) {
            convertArray(o)
        } else if (o in String) {
            convertString(o)
        } else
            o
    }

    static String expandJsonStrings(String s) {
        def object = jsonSlurper.parseText(s)
        new JsonGenerator.Options().disableUnicodeEscaping().build().toJson(convert(object))
    }
}


def flowFile = session.get();
if (flowFile == null) {
    return;
}

flowFile = session.write(
        flowFile,
        { inputStream, outputStream ->
            def content = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            outputStream.write(DeepJson.expandJsonStrings(content).getBytes(StandardCharsets.UTF_8))
        } as StreamCallback
)
session.transfer(flowFile, REL_SUCCESS)