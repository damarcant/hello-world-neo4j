package damarcant.neo4j.main;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;


/**
 * hello-world-neo4j
 * 
 * @author damarcant	
 *
 */
public class Main {

	private static final String DB_PATH = "target/hello-world-db";
   
  
    //========= RELACIONES Y ETIQUETAS ===================================
    

	public enum MyRelationshipTypes implements RelationshipType
    {
        KNOWS, LIKES
    }

    public enum MyLabels implements Label{
    	PERSON, ARTIST
    }
    
	public static void main(String[] args) {		

		//creamos DB con índice en la propiedad "name" de los nodos
		MyNeo4j.getInstance().createOrConnectDB(DB_PATH, "name");
		//borramos los nodos y propiedades que pueda haber en la base de datos
		MyNeo4j.getInstance().deleteAllNodesAndProperties();
		
		//creamos los nodos
		Node david = MyNeo4j.getInstance().createNode(MyLabels.PERSON, "name", "David");
		System.out.println("Node David created with id: "+	david.getId()); 
		Node juan = MyNeo4j.getInstance().createNode(MyLabels.PERSON, "name", "Juan");
		Node pedro = MyNeo4j.getInstance().createNode(MyLabels.PERSON, "name", "Pedro");
		Node laura = MyNeo4j.getInstance().createNode(MyLabels.PERSON, "name", "Laura");
				
		//creamos las relaciones 
		MyNeo4j.getInstance().createRelationship(david, juan, MyRelationshipTypes.KNOWS);
		MyNeo4j.getInstance().createRelationship(david, pedro, MyRelationshipTypes.KNOWS); 
		MyNeo4j.getInstance().createRelationship(juan, laura, MyRelationshipTypes.KNOWS);
		 
		//planteamos dos consultas
		String query_friendsOfDavid = "MATCH (p:PERSON { name: \"David\" })-[:KNOWS]->(person) RETURN person.name as amigos_de_David"; 
		String query_friendsOfFriendsOfDavid = "MATCH (p:PERSON { name: \"David\" })-[:KNOWS]->(p1)-[:KNOWS]->(p2) RETURN p2.name as amigos_de_amigos_de_David";  
		 
		MyNeo4j.getInstance().queryAndPrintDB(query_friendsOfDavid); 
		 
		MyNeo4j.getInstance().queryAndPrintDB(query_friendsOfFriendsOfDavid); 

		MyNeo4j.getInstance().disconnectDB(); 

	} 
}
