package hello.world;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DatastoreServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(DatastoreServlet.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/plain");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		UserService userService = UserServiceFactory.getUserService();

		String userEmail = userService.getCurrentUser().getEmail();
		Key visitorKey = KeyFactory.createKey("Visitor", userEmail);
		Entity visitor = null;

		try {
			visitor = datastore.get(visitorKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			log.info("Prima visita per l'utente " + userEmail);
		}

		if (visitor == null) {
			// prima visita creo da zero l'entity
			visitor = new Entity("Visitor", userEmail);
			visitor.setProperty("count", 0l);
		}

		// aggiorno le proprietà
		Date date = new Date();
		visitor.setProperty("lastSeen", date);
		visitor.setProperty("count", ((long) visitor.getProperty("count")) + 1l);

		datastore.put(visitor);

		// Recupero tutte le entity
		Query query = new Query("Visitor");
		List<Entity> visitors = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		for (Entity entity : visitors) {
			resp.getWriter().println(
					entity.getKey().getName() + ": "
							+ entity.getProperty("count"));
		}
	}
}
