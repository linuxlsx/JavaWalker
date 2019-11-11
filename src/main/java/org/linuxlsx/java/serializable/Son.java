package org.linuxlsx.java.serializable;

/**
 * @author rongruo.lsx
 * @date 2018/8/11
 */
public class Son extends Father{

    private String son;

    public Son() {
        son = "son";
    }

    @Override
    public String toString() {
        return "Son{" +
                "son='" + son + '\'' +
                '}';
    }

    public String getSon() {
        return son;
    }

    public void setSon(String son) {
        this.son = son;
    }
}
