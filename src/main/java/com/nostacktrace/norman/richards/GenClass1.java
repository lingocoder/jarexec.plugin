package com.nostacktrace.norman.richards;

import java.io.*;
import java.nio.file.*;

import static java.nio.file.Paths.get;

/**
 * <a href="www2.sys-con.com/itsg/virtualcd/java/archives/0707/richards/index.html" target="_blank">Hello World! in 70 bytes</a>
 *
 * @author <a href="mailto:orb@nostacktrace.com?subject=Hello%20World!%20in%2070%20bytes">Norman Richards</a>
 */
public class GenClass1
    extends GenClassBase

{
    private final Path destDir;
    public GenClass1(Path destination){
        this.destDir = this.prepareDestination(destination);
    }

    public GenClass1(){ this(get(DEFAULT_DEST));}

    public void write()
            throws IOException
    {
        DataOutputStream out =
                new DataOutputStream(
                        new FileOutputStream(destDir.resolve("Code.class").toString()));

        out.writeInt(0xCAFEBABE); // magic
        out.writeShort(3);        // minor
        out.writeShort(45);       // major
        
        out.writeShort(19); // cpool count
                            // 1 + cpool len

        cpoolClassInfo(out, 3);
        cpoolClassInfo(out, 10);

        cpoolUTF8(out, "Code");
        cpoolUTF8(out, "main");
        cpoolUTF8(out, "([Ljava/lang/String;)V");
        cpoolUTF8(out, "Hello World!");
        cpoolUTF8(out, "java/lang/System");
        cpoolUTF8(out, "out");
        cpoolUTF8(out, "Ljava/io/PrintStream;");
        cpoolUTF8(out, "java/io/PrintStream");
        cpoolUTF8(out, "print");
        cpoolUTF8(out, "(Ljava/lang/String;)V");
        cpoolFieldRef(out, 14, 15);
        cpoolClassInfo(out, 7); 
        cpoolNameAndType(out, 8, 9);
        cpoolMethodRef(out, 2, 17);
        cpoolNameAndType(out, 11, 12);
        cpoolString(out, 6);


        out.writeShort(0x0021);   // ACC_PUBLIC,
                                  //  ACC_SUPER

        out.writeShort(1); // this class name
        out.writeShort(2); // super class name

        out.writeShort(0); // no interfaces
        out.writeShort(0); // no fields

        out.writeShort(1); // one method

        // main
        out.writeShort(0x0009); // ACC_PUBLIC, 
                                // ACC_STATIC
        out.writeShort(4);      // name is main
        out.writeShort(5);      // main signature
        out.writeShort(1);      // attr count

        // main class Code attribute
        byte[] mycode = {
            -78, 0, 13,   // getstatic from 
                          // cpool 13 field ref
            18, 18,       // load constant String
                          // from cpool 18
            -74, 0, 16,   // invoke virtual from 
                          // method ref 16
            -79           // return
        }; 

        // "Code" is cpool 2, also out class name
        out.writeShort(3);
        // total length of following
        out.writeInt(mycode.length + 12);
        // max stack
        out.writeShort(2);
        // max locals
        out.writeShort(1);
        // code length;
        out.writeInt(mycode.length);
        // code bytes
        out.write(mycode);
        // exception table size
        out.writeShort(0);
        // sub attr table size
        out.writeShort(0);

        // class attribute count
        out.writeShort(0); 

        out.close();
    }

    public static void main(String[] args) 
        throws Exception
    {
        (new GenClass1(get(args[0]))).write();
    }
}
