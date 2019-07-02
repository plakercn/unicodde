package unicode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

 
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


 
public class translateFile {
public void start(String fileName) throws FileNotFoundException, IOException {
 
      
        String  deFileName =fileName ;
        String  cnOldFileName="";

        String srcLanguage ="de";
		if(deFileName.indexOf("_en.")>0)
			srcLanguage="en";
		
		//原德文文件和已经完成的unicode中文文件应该在同一个目录下
		cnOldFileName=deFileName.replace("_"+srcLanguage+".","_zh_CN.");
		String[] seg=cnOldFileName.split("\\.");
		// 翻译后的预处理文件
		String cnNewFileName =seg[0]+"_prepare"+".";
		if(seg.length>1)
			cnNewFileName=cnNewFileName+seg[1];
				
		//取得中文原始properties数据
		Boolean oldExisited=(new File(cnOldFileName)).exists();
		
		//如果原中文已经存在，把原中文信息迁移过来
		if(!oldExisited) {
			File file= new File(cnOldFileName);
			file.createNewFile();
		}
		
		  Files.copy((new File(cnOldFileName).toPath()),(new File(cnNewFileName).toPath()),StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("copy old data:"+cnOldFileName+"===>"+cnNewFileName);
		    
			Properties propsCN = new Properties();
			propsCN.load(new FileReader(cnNewFileName));
				
			 
			//取得德文原始properties数据
			Properties propsDE = new Properties();
			try {
				propsDE.load(new FileReader(deFileName));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
			Iterator<Entry<Object, Object>> itDE = propsDE.entrySet().iterator();
			OutputStream out = new FileOutputStream(cnNewFileName,true);  
			System.out.println("start translate......");
			Integer i=0;
			while (itDE.hasNext()) {
				Entry<Object, Object> entry = itDE.next();
				Object key = entry.getKey();
				//如果德文文件中增加了新的项目，插入到中文文件重
				if(propsCN.getProperty(key.toString())==null) {
				   String value = entry.getValue().toString();
				   
				   //不转换$开头的
				   if(value.trim().length()>0 && !value.trim().substring(0,1).equals("$")) {
					       //判断有无html标签，如果有，把原文放入
						   String noHtmlValue=transfer.getTextFromHtml(value);
						   if(!noHtmlValue.equals("")) {  //全部是HTML，则不翻译
							   if(!noHtmlValue.equals(value)) {
						            out.write("\r\n".getBytes("UTF-8"));
								    out.write('#'); 
						            out.write((key+"="+value).getBytes("UTF-8"));  
							   }
			  	               
							   value=transfer.translate(value.toString(),"zh",srcLanguage).replace("{ ", "{").replace(" }", "}").replace("：", ":");   //百度翻译
							  
								value=value.replace("”","").replace("“","").replace("（",")").replace("）",")").replace("\n", "\\n").replace("\r","\\r");
				          }
				   }
				   out.write(("\r\n"+key.toString()+"="+value).getBytes("UTF-8"));
				   if((i%20)==0)
					   System.out.print(i.toString()+" ");
				 //  propsCN.setProperty(key.toString(), value); 
				   i++;
				}
			}
			System.out.print("\n\nTotal:"+i.toString()+"\n\nfinish  translation!");
	       // propsCN.store(out, null);
	        out.close();
			
		}

}
	        