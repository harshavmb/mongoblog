package course;

import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ContactDAO {
	
	private final MongoCollection<Document> contactCollection;
	
	public ContactDAO(final MongoDatabase blogDatabase){
		contactCollection = blogDatabase.getCollection("contact");
	}
	
	//contact query added to database
	public boolean addQuery(String name,String email, String message){
		
		Document contact = new Document();
		
		if(name!=null||email!=null||message!=null){
			System.out.println("query added!");
			contact.append("name", name).append("email", email).append("message", message);
		}
		
		try{
			contactCollection.insertOne(contact);
			return true;
		}catch (MongoWriteException e) {
			System.out.println("write exception"+e);
			return false;
		}
		
	}

}
