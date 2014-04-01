package hello.world;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MemcacheServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(MemcacheServlet.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/plain");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		UserService userService = UserServiceFactory.getUserService();

		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

		String userEmail = userService.getCurrentUser().getEmail();
		Long count = null;

		if (syncCache.contains(userEmail)) {
			count = (Long) syncCache.get(userEmail);
			resp.getWriter().println("Conteggio visite recuperato da Memcache");
		} else {
			resp.getWriter()
					.println(
							"Conteggio visite recuperato da Datastore perchè non presente in memcache");

			Key visitorKey = KeyFactory.createKey("Visitor", userEmail);

			Entity visitor = null;

			try {
				visitor = datastore.get(visitorKey);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				log.info("Prima visita per l'utente " + userEmail);
				resp.getWriter()
						.println(
								"Non ci sono statistiche per questo utente, visita DatastoreServlet!!");
			}

			if (visitor != null) {
				count = (Long) visitor.getProperty("count");
				syncCache.put(userEmail, count);
				resp.getWriter().println("Conteggio salvato in memcache");
			}
		}

		resp.getWriter().println(userEmail + ": " + count);
	}
}
