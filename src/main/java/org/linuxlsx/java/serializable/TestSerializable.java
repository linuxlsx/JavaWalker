package org.linuxlsx.java.serializable;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;

/**
 * @author rongruo.lsx
 * @date 2018/8/11
 */
public class TestSerializable {

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
//
////        Father father = new Father();
////        Son son = new Son();
////
////        File file = new File("/Users/linuxlsx/workspace/github/JavaWalker/son.out");
////        if(!file.exists()){
////            file.createNewFile();
////        }
////
////        File file2 = new File("/Users/linuxlsx/workspace/github/JavaWalker/hession.out");
////        if(!file2.exists()){
////            file2.createNewFile();
////        }
////
////        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
////        out.writeObject(son);
////        out.close();
////
////        HessianOutput hessianOutput = new HessianOutput(new FileOutputStream(file2));
////        hessianOutput.writeObject(son);
////        hessianOutput.close();
//
//        File file = new File("/Users/linuxlsx/workspace/github/JavaWalker/son.out");
//        File file2 = new File("/Users/linuxlsx/workspace/github/JavaWalker/hession.out");
//
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
//        Father father = (Father) in.readObject();
//
//        HessianInput hessianInput = new HessianInput(new FileInputStream(file2));
//        Father fatherr = (Father) hessianInput.readObject();
//
//
//        System.out.println(father);
//
//
//    }

    public static void main(String[] args) {
        byte[] bytes = {112, 114, 111, 99, 101, 115, 115, 32,101,114,114,111,114};

        System.out.println(new String(bytes));

    }
}
