package com.myspring.core;

public class Person {
    private int pid;
    private String pname;

    @Override
    public String toString() {
        return "Person{" +
                "pid=" + pid +
                ", pname='" + pname + '\'' +
                '}';
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public Person(int pid, String pname) {
        this.pid = pid;
        this.pname = pname;
    }

    public Person() {
    }
}
