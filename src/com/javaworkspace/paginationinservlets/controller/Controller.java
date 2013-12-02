/*
 * Controller.java
 */
package com.javaworkspace.paginationinservlets.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javaworkspace.paginationinservlets.dto.StudentDetailsDTO;

/**
 * @author www.javaworkspace.com
 * 
 */
public class Controller extends HttpServlet {
	static final long serialVersionUID = 1L;
	int offset;
	int length;
	List list;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		int maxEntriesPerPage = 3;
		int page = 1;

		String pageNumberValue = request.getParameter("pageNumber");

		if (pageNumberValue != null) {
			try {
				page = Integer.parseInt(pageNumberValue);
				System.out.println("Page Number:" + page);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		int offset = maxEntriesPerPage * (page - 1);
		TestList(offset, maxEntriesPerPage);

		HttpSession httpSession = request.getSession();
		httpSession.setAttribute("pages", getPages());
		httpSession.setAttribute("studentDetails", getListByOffsetAndLength());

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("Home2.jsp");
		dispatcher.forward(request, response);
	}

	/*
	 * Hard-coded sample data. Normally this would come from a real data source
	 * such as a database
	 */
	public void fillList() {
		list = new ArrayList();
		list.add(new StudentDetailsDTO("Mark", "John", 25, "India"));
		list.add(new StudentDetailsDTO("Harry", "Scott", 35, "England"));
		list.add(new StudentDetailsDTO("Nathan", "Ridley", 12, "America"));
		list.add(new StudentDetailsDTO("Tom", "Hanks", 55, "France"));
		list.add(new StudentDetailsDTO("Roger", "Chris", 65, "Germany"));
		list.add(new StudentDetailsDTO("Antony", "Jason", 45, "Denmark"));
		list.add(new StudentDetailsDTO("Che", "Guevara", 45, "Argentina"));
	}

	/**
	 * @param offset
	 * @param length
	 */
	public void TestList(int offset, int length) {
		this.offset = offset;
		this.length = length;
		fillList();
	}

	/**
	 * @return List
	 */
	public ArrayList getListByOffsetAndLength() {

		ArrayList arrayList = new ArrayList();
		int to = this.offset + this.length;
		if (this.offset > list.size())
			this.offset = list.size();
		if (to > list.size())
			to = list.size();
		for (int i = this.offset; i < to; i++) {
			arrayList.add(list.get(i));
		}
		return arrayList;
	}

	/**
	 * @return List with page numbers
	 */
	public List getPages() {
		List pageNumbers = new ArrayList();
		int pages = list.size() / this.length;
		if (list.size() % this.length != 0) {
			pages = pages + 1;
		}

		for (int i = 1; i <= pages; i++) {
			pageNumbers.add(new Integer(i));
		}
		return pageNumbers;
	}
}