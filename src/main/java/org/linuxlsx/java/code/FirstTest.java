package org.linuxlsx.java.code;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.FileInputStream;

/**
 * @author rongruo.lsx
 * @date 2018-12-17
 */
public class FirstTest {

    public static void main(String[] args) throws Exception {

        FileInputStream in = new FileInputStream("/Users/linuxlsx/workspace/github/JavaWalker/src/main/java/org/linuxlsx/java/serializable/Father.java");

        CompilationUnit cu = JavaParser.parse(in);


        System.out.println(cu.toString());

        for (Node node : cu.getChildNodes()) {

            if(node instanceof ClassOrInterfaceDeclaration){

                ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration) node;
                Javadoc javadoc = declaration.getJavadoc().get();
                JavadocComment javadocComment = declaration.getJavadocComment().get();

                declaration.setJavadocComment(javadoc.addBlockTag(new JavadocBlockTag(JavadocBlockTag.Type.AUTHOR, "linuxlsx")).toComment());

                System.out.println("hello");
            }

        }

        System.out.println(cu.toString());

        new Visitor().visit(cu, cu);
    }

    public static class Visitor extends VoidVisitorAdapter{

        @Override
        public void visit(JavadocComment n, Object arg) {

            super.visit(n, arg);
        }


    }
}
