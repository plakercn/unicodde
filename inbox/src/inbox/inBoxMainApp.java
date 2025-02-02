package inbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import unicode.MapKeyComParator;
import unicode.transfer;
import unicode.translateFile;

//收集翻译过的语言文件  LocalStrings_zh_CN_prepared.properties
//到一个统一的文件夹中
//如 F:\dev\OpenOLAT\src\main\java\org\olat\admin\layout\_i18n\LocalStrings_zh_CN_prepared.properties 名称保存为 org~olat~admin~layout~_i18n~LocalStrings_zh_CN_prepared.properties
//使用方法 java -jar inbox.jar   (源代码所在的起始目录 sourcePath) (收集到的文件夹名称 boxPath)  【自多少天前上修改的 beforeMinute]

public class inBoxMainApp {
	static List<File> fileList = new ArrayList<File>()  ;
	public static void main(String[] args) throws FileNotFoundException, IOException { 
		if(args.length<2 ) {
			System.out.println("params:  (源代码所在的起始目录 sourcePath) (收集到的文件夹名称 boxPath)  【自多少天前上修改的 beforeDays(>=0)]");
			return;
		}
		
		String srcPath=args[0];
		String boxPath=args[1];
		
		Integer beforeDays = -1;
		if(args.length==3 ) {
			try {
			beforeDays=Integer.parseInt(args[2]);
			}catch(NumberFormatException  e) {
				beforeDays=0;
				System.out.println("\r\n 天数必须>=0");
				return; 

			} 
		}
		
		String pathSeparator="/";
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
			pathSeparator="\\";
           
		}  
		
		//整理命令行输入的路径串
		 srcPath=srcPath.replace("\\", pathSeparator).replace("/", pathSeparator);
		 if(!srcPath.endsWith(pathSeparator))
			 srcPath=srcPath+pathSeparator;
		 
		String boxFileName="";
		boxPath=boxPath.replace("\\", pathSeparator).replace("/", pathSeparator);
		 if(!boxPath.endsWith(pathSeparator))
			 boxPath=boxPath+pathSeparator;
		 
		 
	    if(!(new File(srcPath)).exists()) {
	        	System.out.println("源代码路径不存在!");
	        	return;
	        }
	        
	    
	    if(!(new File(boxPath)).exists()) {
        	System.out.println("采集库路径不存在!");
        	return;
        }
	    
	    
		 getFileList(srcPath,beforeDays) ;
		 File srcFile=null;
		 
		 //保留从org开始的路径
	//	 String removedRootPath=srcPath.substring(0,srcPath.indexOf("src"+pathSeparator+"main"+pathSeparator+"java"+pathSeparator));
		
	  
		 //循环读到的文件,转换为中文prepared
		 for (int i=0;i< fileList.size();i++){
			    srcFile=(File)fileList.get(i);
			    
			    boxFileName=srcFile.getAbsolutePath().replace(srcPath, "").replace(pathSeparator,"~");
			    System.out.print((i+1)+"...");
			    System.out.print((Calendar.getInstance().getTimeInMillis()-srcFile.lastModified())/(3600000*24)-12);
			    toChinese(srcFile,boxPath+boxFileName);
		 }
		 
 
		
 
	}
	
	//递归读取文件列表

	private static void getFileList(String strPath,int beforeDays) throws IOException {
        File dir = new File(strPath);
        int haveCNPrepareFile=0;
        
        if (dir.isDirectory()) {
        	File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组 
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(),beforeDays); // 获取文件绝对路径
                     
                } else {
                	if (fileName.endsWith("zh_CN_prepare.properties")) { 
                		haveCNPrepareFile=1;
                		 
                	//0.5 时区差
                	if(beforeDays>=0 && (Calendar.getInstance().getTimeInMillis()-files[i].lastModified())>((beforeDays-0.5)*3600000*24)){ //  
                         continue;
                	}
               //     String strFileName = files[i].getAbsolutePath();
                    fileList.add(files[i]);
              //      System.out.println("---" + strFileName+"\n"+(Calendar.getInstance().getTimeInMillis()-files[i].lastModified()));
                	}
                	
                	 
                }  
            }
            //如果当前目录里面没有本地化文件
            if(haveCNPrepareFile==0 && strPath.endsWith("_i18n")) {
            	(new translateFile()).start(strPath+"/LocalStrings_en.properties");
            	File cnFile=new File(strPath+"/LocalStrings_zh_CN_prepare.properties");
            	if(cnFile.exists())
                	fileList.add(cnFile);
            } 

        }
       
    }
	
	//将每个文件转换成中文，放入box目录
	private static void toChinese(File file,String boxFileName) throws FileNotFoundException, IOException {
		
		System.out.println("..."+boxFileName);

		//取得Unicode文原始properties数据
		Properties propsUnicode = new Properties();
		try {
				propsUnicode.load(new FileReader(file.getAbsoluteFile()));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		//
 
		OutputStream out = new FileOutputStream(boxFileName,false);
		Calendar calendar= Calendar.getInstance();
		out.write(("#Guanhaiqing from China "+calendar.getTime()+"\n").getBytes());
		
		Iterator<Entry<Object, Object>> itUnicode = propsUnicode.entrySet().iterator();
 
		while (itUnicode.hasNext()) {
			Entry<Object, Object> entry = itUnicode.next();
			Object key = entry.getKey();
		
			String value = entry.getValue().toString().replace("\n", "\\n");	
	//		if(value.indexOf("\\u")>-1)
	//		   out.write((key+"="+transfer.unicodeToString((value))+"\n").getBytes("UTF-8"));
	//		   else
				   out.write((key+"="+value+"\n").getBytes("UTF-8"));   
		}
		
		out.close();

	}
	
	
	
}
