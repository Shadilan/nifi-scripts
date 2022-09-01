def ff = session.get()
if(!ff)return
ff = session.write(ff, {rawIn, rawOut->
    // ## transform streams into reader and writer
    rawIn.withReader("UTF-8"){reader->
        rawOut.withWriter("UTF-16LE"){writer->
            //transfer reader UTF-8 to writer UTF-16LE
            writer.write("\ufeff");
            writer << reader
        }
    }
} as StreamCallback)
session.transfer(ff, REL_SUCCESS)
