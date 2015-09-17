/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package course;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class encapsulates the controllers for the blog web application. It
 * delegates all interaction with MongoDB to four Data Access Objects (DAOs).
 * <p/>
 * It is also the entry point into the web application.
 */
public class BlogController {

	private final Configuration cfg;
	private final BlogPostDAO blogPostDAO;
	private final UserDAO userDAO;
	private final SessionDAO sessionDAO;
	private final ContactDAO contactDAO;
	private final EmailDAO emailDAO;
	private static Session session;

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			new BlogController("mongodb://localhost");
		} else {
			new BlogController(args[0]);
		}

		BlogController.EmailInit.initEmail();
		
	}

	public BlogController(String mongoURIString) throws IOException {
		final MongoClient mongoClient = new MongoClient(new MongoClientURI(
				mongoURIString));
		final MongoDatabase blogDatabase = mongoClient.getDatabase("blog");

		blogPostDAO = new BlogPostDAO(blogDatabase);
		userDAO = new UserDAO(blogDatabase);
		sessionDAO = new SessionDAO(blogDatabase);
		contactDAO = new ContactDAO(blogDatabase);
		emailDAO = new EmailDAO(blogDatabase);

		cfg = createFreemarkerConfiguration();
		setPort(8082);
		initializeRoutes();

	}

	
	//Email configuration is initialized here..
	static class EmailInit {

		public static Session initEmail() {
			
			Properties props = new Properties();

			try {
				//mail configuration settings are loaded here
				props.load(new FileInputStream(new File("settings.properties")));
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//give your gmail credentials here
			 session = Session.getDefaultInstance(props,
					new Authenticator() {

						protected PasswordAuthentication getPasswordAuthentication() {

							return new PasswordAuthentication(
									"xxxxx@gmail.com", "********");

						}

					});
			System.out.println("Email setup initialized!");

			return session;
		}
	}

	abstract class FreemarkerBasedRoute extends Route {

		final Template template;

		/**
		 * Constructor
		 * 
		 * @param path
		 *            The route path which is used for matching. (e.g. /hello,
		 *            users/:name)
		 */
		protected FreemarkerBasedRoute(final String path,
				final String templateName) throws IOException {
			super(path);
			template = cfg.getTemplate(templateName);
		}

		@Override
		public Object handle(Request request, Response response) {
			StringWriter writer = new StringWriter();
			try {
				doHandle(request, response, writer);
			} catch (Exception e) {
				e.printStackTrace();
				response.redirect("/internal_error");
			}
			return writer;
		}

		protected abstract void doHandle(final Request request,
				final Response response, final Writer writer)
				throws IOException, TemplateException;

	}

	private void initializeRoutes() throws IOException {

		Spark.staticFileLocation("https://drive.google.com/folderview?id=0B89YLZTwQ7ZCZ0dmSm4zRG9FT2c/");

		// this is the blog home page
		get(new FreemarkerBasedRoute("/", "blog_template.ftl") {
			@Override
			public void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				String username = sessionDAO
						.findUserNameBySessionId(getSessionCookie(request));

				List<Document> posts = blogPostDAO.findByDateDescending(10);
				SimpleHash root = new SimpleHash();

				root.put("myposts", posts);
				if (username != null) {
					root.put("username", username);
				}

				template.process(root, writer);
			}
		});

		// used to display actual blog post detail page
		get(new FreemarkerBasedRoute("/post/:permalink", "entry_template.ftl") {

			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				String permalink = request.params(":permalink");

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				System.out.println("/post: get " + permalink);

				Document post = blogPostDAO.findByPermalink(permalink);
				if (post == null) {
					response.redirect("/post_not_found");
				} else {
					// empty comment to hold new comment in form at bottom of
					// blog entry detail page
					SimpleHash newComment = new SimpleHash();
					newComment.put("name", "");
					newComment.put("email", "");
					newComment.put("body", "");

					SimpleHash root = new SimpleHash();

					root.put("post", post);
					root.put("comment", newComment);
					root.put("username", username);

					template.process(root, writer);
				}
			}
		});

		// handle the signup post
		post(new FreemarkerBasedRoute("/signup", "signup.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				String email = request.queryParams("email");
				String username = request.queryParams("username");
				String password = request.queryParams("password");
				String verify = request.queryParams("verify");

				HashMap<String, String> root = new HashMap<String, String>();
				root.put("username", StringEscapeUtils.escapeHtml4(username));
				root.put("email", StringEscapeUtils.escapeHtml4(email));

				if (validateSignup(username, password, verify, email, root)) {
					// good user
					System.out.println("Signup: Creating user with: "
							+ username + " " + password);
					
					if (!userDAO.addUser(username, password, email)) {
						// duplicate user
						root.put("username_error",
								"Username already in use, Please choose another");
						template.process(root, writer);
					} else {
						// good user, let's start a session
						String sessionID = sessionDAO.startSession(username);
						System.out.println("Session ID is" + sessionID);

						response.raw().addCookie(
								new Cookie("session", sessionID));
						response.redirect("/welcome");
						
						regConfEmail(email);
					}
				} else {
					// bad signup
					System.out.println("User Registration did not validate");
					template.process(root, writer);
				}
			}
		});

		// present signup form for blog
		get(new FreemarkerBasedRoute("/signup", "signup.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				SimpleHash root = new SimpleHash();

				// initialize values for the form.
				root.put("username", "");
				root.put("password", "");
				root.put("email", "");
				root.put("password_error", "");
				root.put("username_error", "");
				root.put("email_error", "");
				root.put("verify_error", "");

				template.process(root, writer);
			}
		});

		get(new FreemarkerBasedRoute("/welcome", "welcome.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				if (username == null) {
					System.out
							.println("welcome() can't identify the user, redirecting to signup");
					response.redirect("/signup");

				} else {
					SimpleHash root = new SimpleHash();

					root.put("username", username);

					template.process(root, writer);
				}
			}
		});

		// will present the form used to process new blog posts
		get(new FreemarkerBasedRoute("/newpost", "newpost_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				// get cookie
				String username = sessionDAO
						.findUserNameBySessionId(getSessionCookie(request));

				if (username == null) {
					// looks like a bad request. user is not logged in
					response.redirect("/login");
				} else {
					SimpleHash root = new SimpleHash();
					root.put("username", username);

					template.process(root, writer);
				}
			}
		});

		// handle the new post submission
		post(new FreemarkerBasedRoute("/newpost", "newpost_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				/*
				 * String title =
				 * StringEscapeUtils.escapeHtml4(request.queryParams
				 * ("subject")); String post =
				 * StringEscapeUtils.escapeHtml4(request.queryParams("body"));
				 * String tags =
				 * StringEscapeUtils.escapeHtml4(request.queryParams("tags"));
				 */

				String title = request.queryParams("subject");
				String post = request.queryParams("body");
				String tags = request.queryParams("tags");

				String username = sessionDAO
						.findUserNameBySessionId(getSessionCookie(request));

				if (username == null) {
					response.redirect("/login"); // only logged in users can
													// post to blog
				} else if (title.equals("") || post.equals("")) {
					// redisplay page with errors
					HashMap<String, String> root = new HashMap<String, String>();
					root.put("errors",
							"post must contain a title and blog entry.");
					root.put("subject", title);
					root.put("username", username);
					root.put("tags", tags);
					root.put("body", post);
					template.process(root, writer);
				} else {
					// extract tags
					ArrayList<String> tagsArray = extractTags(tags);

					// substitute some <p> for the paragraph breaks
					post = post.replaceAll("\\r?\\n", "<p>");

					String permalink = blogPostDAO.addPost(title, post,
							tagsArray, username);

					// now redirect to the blog permalink
					response.redirect("/post/" + permalink);
				}
			}
		});

		get(new FreemarkerBasedRoute("/welcome", "welcome.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				if (username == null) {
					System.out
							.println("welcome() can't identify the user, redirecting to signup");
					response.redirect("/signup");

				} else {
					SimpleHash root = new SimpleHash();

					root.put("username", username);

					template.process(root, writer);
				}
			}
		});

		// checks the user session and shows the right hand nav href link
		// accordingly. Suppose, if user logs in, it shows his username and
		// logout at the end.
		// In case of anonymous user, login will be shown in absence of logout
		// href at the end
		get(new FreemarkerBasedRoute("/", "lib/toprighthandnav.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				SimpleHash root = new SimpleHash();

				root.put("username", username);

				template.process(root, writer);
			}
		});

		// process a new comment
		post(new FreemarkerBasedRoute("/newcomment", "entry_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				String name = StringEscapeUtils.escapeHtml4(request
						.queryParams("commentName"));
				String email = StringEscapeUtils.escapeHtml4(request
						.queryParams("commentEmail"));
				String body = StringEscapeUtils.escapeHtml4(request
						.queryParams("commentBody"));
				String permalink = request.queryParams("permalink");

				Document post = blogPostDAO.findByPermalink(permalink);
				if (post == null) {
					response.redirect("/post_not_found");
				}
				// check that comment is good
				else if (name.equals("") || body.equals("")) {
					// bounce this back to the user for correction
					SimpleHash root = new SimpleHash();
					SimpleHash comment = new SimpleHash();

					comment.put("name", name);
					comment.put("email", email);
					comment.put("body", body);
					root.put("comment", comment);
					root.put("post", post);
					root.put("errors",
							"Post must contain your name and an actual comment");

					template.process(root, writer);
				} else {
					blogPostDAO.addPostComment(name, email, body, permalink);
					response.redirect("/post/" + permalink);
				}
			}
		});

		// present the login page
		get(new FreemarkerBasedRoute("/login", "login.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				SimpleHash root = new SimpleHash();

				root.put("username", "");
				root.put("login_error", "");

				template.process(root, writer);
			}
		});

		// process output coming from login form. On success redirect folks to
		// the welcome page
		// on failure, just return an error and let them try again.
		post(new FreemarkerBasedRoute("/login", "login.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String username = request.queryParams("username");
				String password = request.queryParams("password");

				System.out.println("Login: User " + username + " logged in! ");

				Document user = userDAO.validateLogin(username, password);

				if (user != null) {

					// valid user, let's log them in
					String sessionID = sessionDAO.startSession(user.get("_id")
							.toString());

					if (sessionID == null) {
						response.redirect("/internal_error");
					} else {
						// set the cookie for the user's browser
						response.raw().addCookie(
								new Cookie("session", sessionID));

						response.redirect("/welcome");
					}
				} else {
					SimpleHash root = new SimpleHash();

					root.put("username",
							StringEscapeUtils.escapeHtml4(username));
					root.put("password", "");
					root.put("login_error", "Invalid Login");
					template.process(root, writer);
				}
			}
		});

		// tells the user that the URL is dead
		get(new FreemarkerBasedRoute("/post_not_found", "post_not_found.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				SimpleHash root = new SimpleHash();
				template.process(root, writer);
			}
		});

		// allows the user to logout of the blog
		get(new FreemarkerBasedRoute("/logout", "signup.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String sessionID = getSessionCookie(request);

				if (sessionID == null) {
					// no session to end
					response.redirect("/login");
				} else {
					// deletes from session table
					sessionDAO.endSession(sessionID);

					// this should delete the cookie
					Cookie c = getSessionCookieActual(request);
					c.setMaxAge(0);

					response.raw().addCookie(c);

					response.redirect("/login");
				}
			}
		});

		// process a new comment
		post(new FreemarkerBasedRoute("/contact", "contact_template.ftl") {

			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				System.out.println("Inside contact route");
				String name = StringEscapeUtils.escapeHtml4(request
						.queryParams("contactName"));
				String email = StringEscapeUtils.escapeHtml4(request
						.queryParams("contactEmail"));
				String message = StringEscapeUtils.escapeHtml4(request
						.queryParams("contactMessage"));

				System.out.println("name " + name + " email " + email
						+ " message " + message);

				if (name.equals("") || message.equals("") || email.equals("")) {
					// bounce this back to the user for correction
					SimpleHash query = new SimpleHash();
					SimpleHash root = new SimpleHash();

					query.put("name", name);
					query.put("email", email);
					query.put("message", message);
					root.put("contact_error",
							"Contact Query must contain your name,email and message!");
					template.process(root, writer);
				} else {
					contactDAO.addQuery(name, email, message);
					response.redirect("/contact");
				}
			}
		});

		// present contact form for blog
		get(new FreemarkerBasedRoute("/contact", "contact_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				SimpleHash root = new SimpleHash();

				// initialize values for the form.
				root.put("name", "");
				root.put("email", "");
				root.put("message", "");
				root.put("contact_error", "");
				root.put("username", username);
				template.process(root, writer);
			}
		});

		// used to process internal errors
		get(new FreemarkerBasedRoute("/internal_error", "error_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				SimpleHash root = new SimpleHash();

				root.put("error", "System has encountered an error.");
				template.process(root, writer);
			}
		});

		// used to process about page
		get(new FreemarkerBasedRoute("/about", "about_template.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {

				String cookie = getSessionCookie(request);
				String username = sessionDAO.findUserNameBySessionId(cookie);

				SimpleHash root = new SimpleHash();
				root.put("username", username);
				System.out.println("Inside about page");
				template.process(root, writer);
			}
		});

	}

	// helper function to get session cookie as string
	private String getSessionCookie(final Request request) {
		if (request.raw().getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.raw().getCookies()) {
			if (cookie.getName().equals("session")) {
				return cookie.getValue();
			}
		}
		return null;
	}

	// helper function to get session cookie as string
	private Cookie getSessionCookieActual(final Request request) {
		if (request.raw().getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.raw().getCookies()) {
			if (cookie.getName().equals("session")) {
				return cookie;
			}
		}
		return null;
	}

	// tags the tags string and put it into an array
	private ArrayList<String> extractTags(String tags) {

		// probably more efficent ways to do this.
		//
		// whitespace = re.compile('\s')

		tags = tags.replaceAll("\\s", "");
		String tagArray[] = tags.split(",");

		// let's clean it up, removing the empty string and removing dups
		ArrayList<String> cleaned = new ArrayList<String>();
		for (String tag : tagArray) {
			if (!tag.equals("") && !cleaned.contains(tag)) {
				cleaned.add(tag);
			}
		}

		return cleaned;
	}

	// validates that the registration form has been filled out right and
	// username confirms
	public boolean validateSignup(String username, String password,
			String verify, String email, HashMap<String, String> errors) {
		String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
		String PASS_RE = "^.{3,20}$";
		String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

		errors.put("username_error", "");
		errors.put("password_error", "");
		errors.put("verify_error", "");
		errors.put("email_error", "");

		if (!username.matches(USER_RE)) {
			errors.put("username_error",
					"invalid username. try just letters and numbers");
			return false;
		}

		if (!password.matches(PASS_RE)) {
			errors.put("password_error", "invalid password.");
			return false;
		}

		if (!password.equals(verify)) {
			errors.put("verify_error", "password must match");
			return false;
		}

		if (!email.equals("")) {
			if (!email.matches(EMAIL_RE)) {
				errors.put("email_error", "Invalid Email Address");
				return false;
			}
		}

		return true;
	}
	
	private Configuration createFreemarkerConfiguration() {
		Configuration retVal = new Configuration();
		retVal.setClassForTemplateLoading(BlogController.class, "/freemarker");
		return retVal;
	}
	
	private void regConfEmail(String email){
		
		try{
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailDAO.getEmailData("registerconf").getString("from")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			
			message.setSubject(emailDAO.getEmailData("registerconf").getString("subject"));

			BodyPart body = new MimeBodyPart();
			
			//freemarker stuff
			//Configuration cfg = new Configuration();
			Template template = cfg.getTemplate("register_email_template.ftl");
			Map<String,String> rootMap = new HashMap<String, String>();
			rootMap.put("to", email);
			rootMap.put("body", emailDAO.getEmailData("registerconf").getString("body"));
			rootMap.put("link", emailDAO.getEmailData("registerconf").getString("link"));
			rootMap.put("security", emailDAO.getEmailData("registerconf").getString("security"));
			rootMap.put("sign", emailDAO.getEmailData("registerconf").getString("sign"));
			Writer out = new StringWriter();
			try {
				template.process(rootMap, out);
			} catch (TemplateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			body.setContent(out.toString(),"text/html");
			//body.setText(out.toString());
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);

			body = new MimeBodyPart();
			
			//message.setContent(multipart, "text/html");
			message.setContent(multipart);
			
			Transport.send(message);
			
			System.out.println("Mail sent!");
			
			
		}catch (AddressException e1) {
			e1.printStackTrace();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
