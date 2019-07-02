package unicode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

public class createUnionFile {
	public void start(String srcFileName) throws FileNotFoundException, IOException {
		
		String  unicodeFileName="";
		String[] seg=srcFileName.split("\\.");
		
		unicodeFileName =seg[0]+"_unicode"+".";
		if(seg.length>1)
			unicodeFileName=unicodeFileName+seg[1];

		
		Properties propsCN = new Properties();
		propsCN.load(new FileReader(srcFileName));
	 
			//取得德文原始properties数据
			Properties propsDE = new Properties();
			try {
				propsDE.load(new FileReader(srcFileName));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Iterator<Entry<Object, Object>> itDE = propsDE.entrySet().iterator();
			  
			System.out.println("start to unicode......");
			Integer i=0;
			Map<String,String> map=new TreeMap<String,String>();
			// 构造MAP
			while (itDE.hasNext()) {
				Entry<Object, Object> entry = itDE.next();
				Object key = entry.getKey();
			
 				String value = entry.getValue().toString();
  
				if((i%50)==0)
					   System.out.print(i.toString()+" ");
				   
			    map.put(key.toString(), transfer.string2Unicode(value)); 
				   i++;
			}
			
			if(map==null || map.isEmpty()) {
                  System.out.println("no new Data");
                  return;
			}
			
			
			 System.out.println("\r\n开始排序......");
			Map<String,String> sortMap=new TreeMap<String,String>(new MapKeyComParator());
					sortMap.putAll(map);
			
			OutputStream out = new FileOutputStream(unicodeFileName,false);
			
			Calendar calendar= Calendar.getInstance();
			out.write(("#Guanhaiqing from China "+calendar.getTime()+"\n").getBytes());
				
			for (Map.Entry<String,String> entry : sortMap.entrySet()){
				out.write((entry.getKey()+"="+entry.getValue()+"\n").getBytes());
			}
			 
			out.close();
			System.out.println("Total:"+i.toString()+"\nfinished!");  
	       // propsUn.store(out, "by guanhq");
			
		}

}
