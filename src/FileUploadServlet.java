

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import pojo.HttpClientExample;

@MultipartConfig()
/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println(request.getParameter("nameoffile"));
		
		printNames(request);
		/*
		final Part filePart = request.getPart("file");
		
		if(filePart != null) {
			final String fileName = getFileName(filePart);
			System.out.println("filename " + fileName);
		}
		else
			System.out.println("File part is null");
	    */
		
	//	HttpClientExample httpClientExample = new HttpClientExample();
		//httpClientExample.uploadFile();
	}

	/*
	 private String getFileName(final Part part) {
         final String partHeader = part.getHeader("content-disposition");
         System.out.println("Header = " + partHeader);
       //  LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
         for (String content : part.getHeader("content-disposition").split(";")) {
             if (content.trim().startsWith("filename")) {
                 return content.substring(
                         content.indexOf('=') + 1).trim().replace("\"", "");
             }
         }
         return null;
     }
     */
	
	public void printNames(HttpServletRequest request) throws IllegalStateException, IOException, ServletException{
	    for(Part part : request.getParts()){
	        System.out.println("PN: "+ part.getName());
	        Collection<String> headers = part.getHeaders("content-disposition");
	        if (headers == null)
	            continue;
	        for(String header : headers){
	            System.out.println("CDH: " + header);                  
	        } 
	    }
	}
}
