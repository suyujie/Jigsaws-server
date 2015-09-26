package server.node.servlet;

import gamecore.servlet.AbstractHttpServlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

@WebServlet("/test")
public class TestServlet extends AbstractHttpServlet {

	private static final long serialVersionUID = 8032990056662355992L;

	public TestServlet() {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OutputStream out = resp.getOutputStream();

		JSONObject jo = new JSONObject();
		jo.put("test", "test");
		out.write(jo.toString().getBytes());

		out.flush();
		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OutputStream out = resp.getOutputStream();
		
		JSONObject jo = new JSONObject();
		jo.put("test", "test");
		out.write(jo.toString().getBytes());
		
		out.flush();
		out.close();
	}
}
