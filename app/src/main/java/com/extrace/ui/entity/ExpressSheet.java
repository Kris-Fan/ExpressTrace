package com.extrace.ui.entity;

import java.util.Date;


public class ExpressSheet {
    private String id;

    private Integer type;

    private Integer sender;

    private Integer recever;

    private Float weight;

    private Float tranfee;

    private Float packagefee;

    private Float insufee;

    private String accepter;

    private String deliver;
    private String acceptetime;
    private String delivetime;

    private String acc1;

    private String acc2;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getRecever() {
        return recever;
    }

    public void setRecever(Integer recever) {
        this.recever = recever;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getTranfee() {
        return tranfee;
    }

    public void setTranfee(Float tranfee) {
        this.tranfee = tranfee;
    }

    public Float getPackagefee() {
        return packagefee;
    }

    public void setPackagefee(Float packagefee) {
        this.packagefee = packagefee;
    }

    public Float getInsufee() {
        return insufee;
    }

    public void setInsufee(Float insufee) {
        this.insufee = insufee;
    }

    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter == null ? null : accepter.trim();
    }

    public String getDeliver() {
        return deliver;
    }

    public void setDeliver(String deliver) {
        this.deliver = deliver == null ? null : deliver.trim();
    }

    public String getAcceptetime() {
        return acceptetime;
    }

    public void setAcceptetime(String acceptetime) {
        this.acceptetime = acceptetime;
    }

    public String getDelivetime() {
        return delivetime;
    }

    public void setDelivetime(String delivetime) {
        this.delivetime = delivetime;
    }

    public String getAcc1() {
        return acc1;
    }

    public void setAcc1(String acc1) {
        this.acc1 = acc1 == null ? null : acc1.trim();
    }

    public String getAcc2() {
        return acc2;
    }

    public void setAcc2(String acc2) {
        this.acc2 = acc2 == null ? null : acc2.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

	public ExpressSheet(String id, Integer type, Integer sender, Integer recever, Float weight, Float tranfee,
			Float packagefee, Float insufee, String accepter, String deliver, String acceptetime, String delivetime,
			String acc1, String acc2, String status) {
		super();
		this.id = id;
		this.type = type;
		this.sender = sender;
		this.recever = recever;
		this.weight = weight;
		this.tranfee = tranfee;
		this.packagefee = packagefee;
		this.insufee = insufee;
		this.accepter = accepter;
		this.deliver = deliver;
		this.acceptetime = acceptetime;
		this.delivetime = delivetime;
		this.acc1 = acc1;
		this.acc2 = acc2;
		this.status = status;
	}

	public ExpressSheet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ExpressSheet [id=" + id + ", type=" + type + ", sender=" + sender + ", recever=" + recever + ", weight="
				+ weight + ", tranfee=" + tranfee + ", packagefee=" + packagefee + ", insufee=" + insufee
				+ ", accepter=" + accepter + ", deliver=" + deliver + ", acceptetime=" + acceptetime + ", delivetime="
				+ delivetime + ", acc1=" + acc1 + ", acc2=" + acc2 + ", status=" + status + "]";
	}
	
	
    
}