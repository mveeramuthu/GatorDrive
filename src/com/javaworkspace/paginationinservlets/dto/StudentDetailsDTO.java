/**
 * StudentDetailsDTO.java
 */
package com.javaworkspace.paginationinservlets.dto;

import java.io.Serializable;

/**
 * @author www.javaworkspace.com
 * 
 */
public class StudentDetailsDTO implements Serializable {

	private String studentName;
	private String fatherName;
	private int age;
	private String country;

	public StudentDetailsDTO(String studentName, String fatherName, int age,
			String country) {
		this.studentName = studentName;
		this.fatherName = fatherName;
		this.age = age;
		this.country = country;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}