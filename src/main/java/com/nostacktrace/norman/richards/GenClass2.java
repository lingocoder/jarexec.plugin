package com.nostacktrace.norman.richards;

import java.io.*;
import java.nio.file.*;

import static java.nio.file.Paths.get;


/**
 * <a href="www2.sys-con.com/itsg/virtualcd/java/archives/0707/richards/index.html" target="_blank">Hello World! in 70 bytes</a>
 *
 * @author <a href="mailto:orb@nostacktrace.com?subject=Hello%20World!%20in%2070%20bytes">Norman Richards</a>
 */
public class GenClass2
        extends GenClassBase
{
    private final Path destDir;
    public GenClass2(Path destination){
        this.destDir = this.prepareDestination(destination);
    }

    public GenClass2(){ this(get(DEFAULT_DEST));}

    public void write()
            throws IOException
    {
        DataOutputStream out =
                new DataOutputStream(
                        new FileOutputStream(destDir.resolve("Code.class").toString()));

        out.writeInt(0xCAFEBABE); // magic
        out.writeShort(3);        // minor
        out.writeShort(45);       // major

        out.writeShort(17); // cpool count

        cpoolClassInfo(out, 3);
        cpoolClassInfo(out, 9);
        cpoolUTF8(out, "Code");
        cpoolUTF8(out, "main");
        cpoolUTF8(out, "([Ljava/lang/String;)V");
        cpoolUTF8(out, "java/lang/System");
        cpoolUTF8(out, "out");
        cpoolUTF8(out, "Ljava/io/PrintStream;");
        cpoolUTF8(out, "java/io/PrintStream");
        cpoolUTF8(out, "print");
        cpoolUTF8(out, "(Ljava/lang/String;)V");

        cpoolFieldRef(out, 13, 14);
        cpoolClassInfo(out, 6);
        cpoolNameAndType(out, 7, 8);
        cpoolMethodRef(out, 2, 16);
        cpoolNameAndType(out, 10, 11);

        out.writeShort(0x0021);   // ACC_PUBLIC,
        // ACC_SUPER

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
                -78, 0, 12, // getstatic from cpool
                // 12, a field ref
                42,         // aload_0 load args
                3,          // iconst_0  index 0
                50,         // aaload load from array
                -74, 0, 15, // invoke virtual
                // from method ref in 15
                -79 // return
        };

        // "Code" is cpool 3, also out class name
        out.writeShort(3);
        // total length of following
        out.writeInt(mycode.length + 12);
        // max stack
        out.writeShort(3);
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
        (new GenClass2(get(args[0]))).write();
    }
}
