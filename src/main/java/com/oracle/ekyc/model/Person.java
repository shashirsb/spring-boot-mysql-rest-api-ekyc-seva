package com.oracle.ekyc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.util.Date;

/**
 * Created by shashiramachandra on 20/04/23.
 */
@Entity
@Table(name = "persons")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    private boolean initvalid;

    
    private boolean objstored;

    
    private boolean uidai;

 
    private String imgtoken;


    private String imgURL;

    
    private boolean aadhaar;

 
    private String aadhaarID;


    private String secIDType;

   
    private String secID;

    
    private String phone;

    
    private int age;

    
    private String fname;
    
    
    private String lname;

    
    private boolean gr;

   
    private String msg;

 
    private String schemes;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isInitvalid() {
        return initvalid;
    }

    public void setInitvalid(boolean initvalid) {
        this.initvalid = initvalid;
    }

    @JsonIgnore
    public boolean isObjStored() {
        return objstored;
    }

    public void setObjStored(boolean objstored) {
        this.objstored = objstored;
    }

    public boolean isUidai() {
        return uidai;
    }

    public void setUidai(boolean uidai) {
        this.uidai = uidai;
    }

    public String getImgtoken() {
        return imgtoken;
    }

    public void setImgtoken(String imgtoken) {
        this.imgtoken = imgtoken;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public boolean isAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(boolean aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getAadhaarID() {
        return aadhaarID;
    }

    public void setAadhaarID(String aadhaarID) {
        this.aadhaarID = aadhaarID;
    }

    public String getSecIDType() {
        return secIDType;
    }

    public void setSecIDType(String secIDType) {
        this.secIDType = secIDType;
    }

    public String getSecID() {
        return secID;
    }

    public void setSecID(String secID) {
        this.secID = secID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    @JsonIgnore
    public boolean isGr() {
        return gr;
    }

    public void setGr(boolean gr) {
        this.gr = gr;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSchemes() {
        return schemes;
    }

    public void setSchemes(String schemes) {
        this.schemes = schemes;
    }   

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


 

}
