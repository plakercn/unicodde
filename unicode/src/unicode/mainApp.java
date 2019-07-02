package unicode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

  

public class mainApp {
	public static void main(String[] args) throws FileNotFoundException, IOException { 
	
		if(args.length<=1 || !(args[0].equals("-p") || args[0].equals("-u"))) {
			System.out.println("params: verb propertyFileName\r\nverb:\r\n\t-p=Prepare localStrings_zh_CN_prepared.properties file.\r\n\t-u=create Uincode LocalStrings_zh_CN.properties\r\npropertyFileName:\r\n\tsource file name,extend '.properties' is default ");
			return;
		}
		
		String srcFileName="";
		if(args[0].equals("-p"))
			srcFileName=System.getProperty("user.dir")+"\\"+"LocalStrings_de.properties";
		else
			srcFileName=System.getProperty("user.dir")+"\\"+"LocalStrings_zh_CN.properties";
		 
		if(args.length>1) {
			srcFileName=args[1].replace("\"", "");	
		}
		
	 
        if(!(new File(srcFileName)).exists()) {
        	System.out.println(srcFileName+" not exisited!");
        	return;
        }
		
        System.out.println("open file:"+srcFileName);
        
		//"F:\\dev\\OpenOLAT\\src\\main\\java\\org\\olat\\repository\\_i18n\\LocalStrings_de.properties";
        if(args[0].equals("-p"))
	       (new translateFile()).start(srcFileName);
        else
        	(new createUnionFile()).start(srcFileName);
	}
	
	
	
}
