package com.myapps.upesse.upes_spefest.ui.activity;

public class SearchUser {
    private String uname;
    private String udp;

    public SearchUser(String uname, String udp) {
        this.uname = uname;
        this.udp = udp;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUdp() {
        return udp;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }
}
