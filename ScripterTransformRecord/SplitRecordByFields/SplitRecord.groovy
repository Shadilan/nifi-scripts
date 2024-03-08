results = []

fields = record.getRawFieldNames();
schema = record.getSchema();
fields.each{key -> {
    log.debug("Got key: ${key}")
    nMap = new java.util.HashMap<String, Object>()
    log.debug("create map")
    nMap.put(key,record.getValue(key))
    log.debug("Put to map value")
    newrecord = new org.apache.nifi.serialization.record.MapRecord(schema, nMap)
    log.debug("Сreate new record with field ${key}")
    results.add(newrecord)
}}

results