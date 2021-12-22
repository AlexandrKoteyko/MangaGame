package com.example.javatry;

import static android.widget.Toast.LENGTH_SHORT;

import android.widget.Toast;

public class User {
    private static User currentUser = null;
    private String name; //
    private int bank; //
    private byte canbye;
    private final byte[] activesArray = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; // status 0=not own 1=owned 2=laid
    private final int[] activesPrice = {60,70,80,100,110,120,150,160,165,180,185,187,230,220,210,195,197,200}; // price
    private final int[] activesRent = {15, 18,20, 30, 33, 35, 40, 45, 48, 50, 53, 55, 73, 70, 67, 60, 63, 65}; // rent
    private final String[] activename={"NTT Docomo","KDDI","Softbank Mobile","Єнряку-Дзи","Гінкаку-Дзи", "Тадайдзи","Fuji Television",
    "Tochigi TV","Acari TV","Mitsubishi","Mazda","Honda","Sony","Canon","Panasonic","Studio Pierrot","Ganiax","Bones"}; //names
    private boolean isLogged = false;

    private User() {
        this.bank = 500;
        this.canbye=0;
    }

    public static User getInstance() {
        if (currentUser == null) {
            return (currentUser = new User());
        }

        return currentUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }
    
    public void addToBank(int value) {
        this.bank += value;
    }
    public byte isOwner(int value) {            //return status
        value-=16;
        byte[] arr=getActivesArray();
        return arr[value];
        
    }
    public String rateText(int value)
    {
        String tex="";
        if (this.isOwner(value)==0)
            tex="Було списано"+this.rentfind(value)+"$";
        else if (this.isOwner(value)==1)
            tex="Було нараховано"+this.rentfind(value)+"$";
        return tex;
    }
    public String endgame()
    {
        String tex="";
        int sum=0;
        if (this.bank<0)
        {
            for (int i=0; i<18;i+=1)
            {
                if (activesArray[i]==1)
                    sum+=Math.round(activesPrice[i]*0.9);
            }
            if (this.bank+sum>=0)
            {
                tex="Ваш баланс від'ємний. Терміново продавайте активи";
                this.canbye=1;

            }
            else
            {
                this.canbye=3;
                tex="Ваша гра закінчена";
            }
            return tex;
        }
        return null;

    }
    public void rateprice(int value)
    {
        byte active= (byte) (value-16);
        byte actives =isOwner(value);
        byte[] arr=getActivesArray();
        if (actives ==0)                               // if zero you must paid
        {
            this.bank-=activesRent[active];

        }
        else if (actives ==1)                         //if one paid to you
        {
            this.bank+=activesRent[active];

        }
    }

    public void cnangeOnw(int value)            //if you want bye/sell
    {
        byte active= (byte) (value-16);
        byte actives =isOwner(value);
        byte[] arr=getActivesArray();
        if (actives ==0)                               // buying
        {
            this.bank-=activesPrice[active];
            this.activesArray[active]=1;
        }
        else if (actives ==1)                         //selling
        {
            this.bank+=Math.round(activesPrice[active]*0.9);
            this.activesArray[active]=2;
        }
        else if (actives ==2)                            //rebuying
        {
            this.bank-=Math.round(activesPrice[active]*1.1);
            this.activesArray[active]=1;
        }
    }
    public String namefind(int value)           //return name for index
    {
     return this.activename[value-16];
    }
    public int pricefind(int value)           //return price for index
    {
        return this.activesPrice[value-16];
    }
    public int rentfind(int value)           //return rent for index
    {
        return this.activesRent[value-16];
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public byte[] getActivesArray() {
        return activesArray;
    }

    public byte getCanbye() {
        return canbye;
    }
}
