package com.github.emailtohl.building.common.jpa.relationEntities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Relation2 implements Serializable {
	private static final long serialVersionUID = -3114234776366877130L;
	private short id;
	private String me = "Relation2";
	private Relation1 relation1;
	
	@Id
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public String getMe() {
		return me;
	}
	public void setMe(String me) {
		this.me = me;
	}
	public Relation1 getRelation1() {
		return relation1;
	}
	public void setRelation1(Relation1 relation1) {
		this.relation1 = relation1;
	}

}