package course;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;

public class EmailDAO {
	
	private final MongoCollection<Document> emailsCollection;
	
	public EmailDAO(final MongoDatabase blogDatabase){
		emailsCollection = blogDatabase.getCollection("emails");
	}
	
	//email data fetched from emailsCollection
	public Document getEmailData(String emailType){
		Document email;
		
		email = emailsCollection.find(eq("_id", emailType)).first();
		
		if(email==null){
			System.out.println("Template Data is not fetched from database");
		}
		
		return email;
	}

}
