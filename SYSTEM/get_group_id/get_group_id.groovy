def flowFile = session.get()
if(!flowFile) return
processGroupId = context.procNode?.processGroupIdentifier ?: 'unknown'
flowFile = session.putAttribute(flowFile, 'processGroupId', processGroupId)
session.transfer(flowFile, REL_SUCCESS)
