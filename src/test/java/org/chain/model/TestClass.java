package org.chain.model;

public class TestClass
{
    private int num;
    private String string;

    public TestClass(int num, String string){
        setNum(num);
        setString(string);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
