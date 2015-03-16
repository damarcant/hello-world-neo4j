package damarcant.neo4j.main;

import java.io.File;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 * hello-world-neo4j
 * 
 * @author damarcant	
 *
 */
public class MyNeo4j {
	
	private static MyNeo4j instance = null;
	private GraphDatabaseService graphDb;

	protected MyNeo4j() {
		 
	}

	public static MyNeo4j getInstance() {
		if (instance == null) {
			instance = new MyNeo4j();
		}
		return instance;
	}
	
	/**
	 * Crea una nueva base de datos si no existe 
	 * o se conecta si ya existe
	 * 
	 * @param pathToDB
	 * @param indexProperty
	 */
	public void createOrConnectDB(String pathToDB, String indexProperty){
		
		File dbFile = new File(pathToDB);
		
		if (dbFile.exists()){
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(pathToDB);
		}else{
			
			if(indexProperty!=null){
				//creamos una nueva base de datos indexada
			    graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(pathToDB).
			    		setConfig( GraphDatabaseSettings.node_keys_indexable, indexProperty ). 
			        	    setConfig( GraphDatabaseSettings.node_auto_indexing, "true" ). //indice automático en nodos y relaciones con propiedades de tipo "name"
			        	    newGraphDatabase(); 
			}else{
				//base de datos sin índices
				graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(pathToDB).
		        	    newGraphDatabase(); 
			}
			
		}  
		registerShutdown(graphDb); 
	}
	
	/**
	 * Borra los ficheros generados en la creación de la base de datos
	 * 
	 * @param pathToDB
	 */
	public void destroyDB(String pathToDB){
		File dbFile = new File(pathToDB);
		deleteFileOrDirectory(dbFile); 
	} 
	 
	public void disconnectDB(){ 
		graphDb.shutdown();
	}
	
	public void queryAndPrintDB(String query){
		
        ExecutionEngine engine = new ExecutionEngine(graphDb); 
        ExecutionResult result = engine.execute(query);
        System.out.println(result.dumpToString());         
	}
	
	
	public void deleteAllNodesAndProperties() {
		
		ExecutionEngine engine = new ExecutionEngine(graphDb);
		String query = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";

		ExecutionResult result = engine.execute(query);
		System.out.println(result.dumpToString());
	}
	
	/**
	 * Crea un nodo del tipo label, con una propiedad de nombre propertyName
	 * y con valor propertyValue
	 * 
	 * @param label
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	@SuppressWarnings("finally")
	public Node createNode(Label label, String propertyName, String propertyValue) {
	 
		Transaction tx = graphDb.beginTx();
		Node myNode = null;
		try{			
			myNode = graphDb.createNode(); 
			myNode.addLabel(label);
			myNode.setProperty(propertyName, propertyValue);			
			tx.success(); 	
		}catch(Exception ex){
			tx.failure(); 
		}finally{
			tx.close();
			return myNode; 
		}		
	} 
	 
	/**
	 * Crea relacion rel entre p1 y p2
	 * 
	 * @param p1
	 * @param p2
	 * @param rel
	 */
	@SuppressWarnings("finally")
	public Relationship createRelationship(Node p1, Node p2, RelationshipType rel){
		Transaction tx = graphDb.beginTx();
		Relationship _rel = null;
		try{		
			_rel = p1.createRelationshipTo( p2, rel );  
			tx.success(); 	
		}catch(Exception ex){
			tx.failure(); 
		}finally{
			tx.close();
			return _rel; 
		}		 
	}

		
	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	private void deleteFileOrDirectory( File file ){
        if ( file.exists() )
        {
            if ( file.isDirectory() )
            {
                for ( File child : file.listFiles() )
                {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
	
	/**
	 * Cierra la conexión con la base de datos si termina la ejecución del programa de forma intencionada
	 * 
	 * @param graphDb
	 */
	private void registerShutdown( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
	

}
