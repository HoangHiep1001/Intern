package result.data.sparkjava;
import static spark.Spark.*;
/**
 * Hello world!
 *
 */
public class App 
{
	public static String id;
    public static void main( String[] args )
    {
    	port(8080);
    	//stop();
    	get("spark/getData", (request, response)->{
    		 id = request.queryParams("n");
    		  System.out.println(id);
    		  return Integer.parseInt(id)*Integer.parseInt(id);
    		});
    	System.out.println(id);

    }
}
