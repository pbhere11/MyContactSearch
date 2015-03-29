package com.pritesh.MyContactSearch;

public class Contacts {
    private byte[] icon;
    private String name;

    public Contacts(String name,byte[] icon) {
        this.icon = icon;
        this.name = name;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
