package com.kerawa.app.utilities;

/**
 * Created by kwemart on 08/05/2015.
 */
public class Contact {
    public String Phone;
    public String _name;

    public Contact() {
    }

    public Contact(String phone, String name) {
        this._name = name;
        this.Phone = phone;
    }

    public String getPhone() {
        return this.Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }
}

