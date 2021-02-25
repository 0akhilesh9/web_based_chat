package chatagent.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utility {
	static InputStream input;
	static public Properties prop;

	public void utilityDesc() {
		try {
			prop = new Properties();
			input = new FileInputStream(this.getClass().getClassLoader().getResource("/config.properties").getPath());
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
