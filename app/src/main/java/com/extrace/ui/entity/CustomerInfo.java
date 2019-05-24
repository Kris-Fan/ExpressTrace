package com.extrace.ui.entity;

public class CustomerInfo {
    private Integer id;

    private String name;

    private String telcode;

    private String department;

    private String regioncode;

    private String address;

    private Integer postcode;

  

	public CustomerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CustomerInfo(Integer id, String name, String telcode, String department, String regioncode, String address,
			Integer postcode) {
		super();
		this.id = id;
		this.name = name;
		this.telcode = telcode;
		this.department = department;
		this.regioncode = regioncode;
		this.address = address;
		this.postcode = postcode;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTelcode() {
        return telcode;
    }

    public void setTelcode(String telcode) {
        this.telcode = telcode == null ? null : telcode.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getRegioncode() {
        return regioncode;
    }

    public void setRegioncode(String regioncode) {
        this.regioncode = regioncode == null ? null : regioncode.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }
}