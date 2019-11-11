package org.linuxlsx.java.serializable;

import java.io.Serializable;

/**
 * @date 2018/8/11
 */
public class Father implements Serializable {

    private static final long serialVersionUID = 8533813163908110910L;

    private String father;

    /**
     * 这个doc
     */
    public Father() {
        father = "xxxx";
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    @Override
    public String toString() {
        return "Father{" +
                "father='" + father + '\'' +
                '}';
    }
}
