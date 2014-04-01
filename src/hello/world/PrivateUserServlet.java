package hello.world;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class PrivateUserServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();

		String thisURL = req.getRequestURI();

		resp.setContentType("text/html");

		resp.getWriter().println(
				"<p>Hello, " + req.getUserPrincipal().getName()
						+ "!  You can <a href=\""
						+ userService.createLogoutURL(thisURL)
						+ "\">sign out</a>.</p>");
	}
}
