package com.extrace.ui.entity;

public class UserInfo {
    private Integer uid;

    private String pwd;

    private String name;

    private Integer urull;

    private String telcode;

    private Integer status;

    private String dptid;

    private String receivepackageid;

    private String delivepackageid;

    private String transpackageid;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getUrull() {
        return urull;
    }

    public void setUrull(Integer urull) {
        this.urull = urull;
    }

    public String getTelcode() {
        return telcode;
    }

    public void setTelcode(String telcode) {
        this.telcode = telcode == null ? null : telcode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDptid() {
        return dptid;
    }

    public void setDptid(String dptid) {
        this.dptid = dptid == null ? null : dptid.trim();
    }

    public String getReceivepackageid() {
        return receivepackageid;
    }

    public void setReceivepackageid(String receivepackageid) {
        this.receivepackageid = receivepackageid == null ? null : receivepackageid.trim();
    }

    public String getDelivepackageid() {
        return delivepackageid;
    }

    public void setDelivepackageid(String delivepackageid) {
        this.delivepackageid = delivepackageid == null ? null : delivepackageid.trim();
    }

    public String getTranspackageid() {
        return transpackageid;
    }

    public void setTranspackageid(String transpackageid) {
        this.transpackageid = transpackageid == null ? null : transpackageid.trim();
    }
}
