package com.nostacktrace.norman.richards;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Paths.get;

/**
 * <a href="www2.sys-con.com/itsg/virtualcd/java/archives/0707/richards/index.html" target="_blank">Hello World! in 70 bytes</a>
 *
 * @author <a href="mailto:orb@nostacktrace.com?subject=Hello%20World!%20in%2070%20bytes">Norman Richards</a>
 */
public class GenClassBase {

    static final String DEFAULT_DEST = System.getProperty("user.dir");
    final String ERR_MSG = "The folder you specified as the destination: 's%' does not exist. Will use the current working directory: '" + DEFAULT_DEST + "' as an alternative destination.";

    Path prepareDestination(final Path destination){
        Path classes = destination;
        try{ boolean exists = Files.exists(destination) ? true : Files.createFile(destination).toFile().exists();
        } catch(IOException ioe){
            System.err.println(String.format(ERR_MSG, destination.toString()));
            classes = get(DEFAULT_DEST);
        } finally { /*this.destDir = classes;*/}
        return classes;
    }

    void cpoolUTF8(DataOutputStream out,
                   String text)
            throws IOException
    {
        byte[] mybytes = text.getBytes();
        out.writeByte(1);  // UTF8 info
        out.writeShort(mybytes.length);
        out.write(mybytes);
    }


    void cpoolNameAndType(DataOutputStream out,
                          int name, int type)
            throws IOException
    {
        out.writeByte(12);
        out.writeShort(name);
        out.writeShort(type);
    }


    void cpoolString(DataOutputStream out,
                     int utf8index)
            throws IOException
    {
        out.writeByte(8);
        out.writeShort(utf8index);
    }

    void cpoolClassInfo(DataOutputStream out,
                        int nameindex)
            throws IOException
    {
        out.writeByte(7);
        out.writeShort(nameindex);
    }

    void cpoolFieldRef(DataOutputStream out,
                       int classindex,
                       int nameandtypeindex)
            throws IOException
    {
        out.writeByte(9);
        out.writeShort(classindex);
        out.writeShort(nameandtypeindex);
    }

    void cpoolMethodRef(DataOutputStream out,
                        int classindex,
                        int nameandtypeindex)
            throws IOException
    {
        out.writeByte(10);
        out.writeShort(classindex);
        out.writeShort(nameandtypeindex);
    }
}
