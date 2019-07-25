package get.jsoup.mavenjsoup;

import java.awt.image.TileObserver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
			Document doc = Jsoup.connect("http://dantri.com.vn").get();
			String title = doc.text();
			long time = System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter(new FileWriter(time+".txt"));
			bw.write(title);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
