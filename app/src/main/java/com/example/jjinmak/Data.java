package com.example.jjinmak;


import android.widget.Button;
import android.widget.ImageView;

public class Data {

   private String name;
   private String status;
   private String addr;
   private String netRole;
   private int netRoleImage;
   private String ID;


    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNetRole(String netRole) {
        this.netRole = netRole;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setNetRoleImage(int netRoleImage) {
        this.netRoleImage = netRoleImage;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    public int getNetRoleImage() {
        return netRoleImage;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getAddr() {
        return addr;
    }

    public String getStatus() {
        return status;
    }

    public String getNetRole() {
        return netRole;
    }
}

