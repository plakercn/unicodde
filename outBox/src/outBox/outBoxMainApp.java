package outBox;

 
	import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.io.FileReader;
	import java.io.IOException;
	import java.io.OutputStream;
import java.net.MalformedURLException;
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

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.Map.Entry;

	import unicode.MapKeyComParator;
	import unicode.transfer;

	//将采集器中的中文文件转换成unicode文件 并上传或分发到对应的部署目录中s
	 	//使用方法 java -jar outbox.jar [-ftp]  (待分发源目录 boxpath) (分发到的的文件夹起始目录（本地或服务器) destPath)  【自多少天前上修改的 beforeMinute]
     // -ftp  F:\dev\box  /home/guan/openolat/classes
	public class outBoxMainApp {
		static List<File> fileList = new ArrayList<File>()  ;
		static int isFtp =0;
		public static void main(String[] args) throws FileNotFoundException, IOException {
			if(args.length<2 || (args.length>2 && !args[0].toLowerCase().equals("-ftp"))) {
				System.out.println("params: [-ftp]  (采集器目录 destPath) (分发到的的文件夹起始目录（本地或服务器) destPath)  [自多少天前上修改的 beforeMinute]");
				return;
			}
		
		String boxPath=args[0];
		String destPath=args[1];
		if(args[0].toLowerCase().equals("-ftp")) {
			isFtp=1;
			boxPath=args[1];
		    destPath=args[2];
		}


		Integer beforeDays = -1;
		if(args.length==4 ) {
			try {
			beforeDays=Integer.parseInt(args[3]);
			}catch(NumberFormatException  e) {
				beforeDays=0;
				System.out.println("\r\n 天数必须>=0");
				return; 

			}
		}
		
		int destLinux=0;
		String pathSeparator="/";
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
			pathSeparator="\\";
           
		}  else {
			destLinux=1;
		}
			
		
		if(!destPath.endsWith("/"))
				destPath=destPath+"/";
		//整理命令行输入的路径串
		 boxPath=boxPath.replace("\\", pathSeparator).replace("/", pathSeparator);
		 if(!boxPath.endsWith(pathSeparator))
			 boxPath=boxPath+pathSeparator;
		 
		String boxFileName="";
		 
		 
	    if(!(new File(boxPath)).exists()) {
	        	System.out.println("源代码路径不存在!");
	        	return;
	        }
	        



		 //保留从org开始的路径
		 File dir = new File(boxPath+"unicode");
		 if(!dir.exists()) {
			 dir.mkdir();
		 }

		 dir = new File(boxPath);
		 
	     File[] fileList = dir.listFiles(); // 该文件目录下文件全部放入数组
         String ftpSourceFileName="";
         String ftpServerDIR="";
         

		 //循环读到的文件,转换成unicode格式，保存到 指定目录的unicode子目录下
		 for (int i=0;i< fileList.length-1;i++){
			    if(fileList[i].isDirectory()) { // 判断是文件还是文件夹
			    	continue;
			    }
			    boxFileName=fileList[i].getName();
			    System.out.print((i+1)+"...");
			    System.out.print((Calendar.getInstance().getTimeInMillis()-fileList[i].lastModified())/(3600000*24)-12);
			    ftpSourceFileName=boxPath+"unicode"+pathSeparator+boxFileName.replace("_prepared.",".");
			    if(toUnicode(fileList[i],ftpSourceFileName)==0) {
			    	System.out.println(ftpSourceFileName+"...To UNICODE...OK!");
			    };
		 }
		 
		 System.out.println("----------------------------------------------------------");
		 if(isFtp==1) {
			 ftp(boxPath+"unicode",destPath,pathSeparator,isFtp);
		 }
        
		}
		
		@SuppressWarnings("finally")
		private static int ftp(String boxPath,String destPath,String pathSeparator,int isFtp) throws IOException {
		    
			//取得德文原始properties数据
			Properties propConfig = new Properties();
			try {
				propConfig.load(new FileReader(outBoxMainApp.class.getResource("").getPath()+pathSeparator+"config.properties"));
 
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("配置文件不存在!");
				return 0;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//从配置文件中读取服务器及FTP账号信息
			
			String ftpUserName=propConfig.getProperty("FTP_USERNAME");
			String ftpPassword=propConfig.getProperty("FTP_PASSWORD");
			String ftpBasePath=propConfig.getProperty("FTP_BASE_PATH");
		    String ftpServer=propConfig.getProperty("FTP_SERVER");
			String ftpPortStr=propConfig.getProperty("FTP_PORT");
			String ftpServerOS=propConfig.getProperty("FTP_SERVER_OS");
			
			String  ftpDestFilePath="";
			FileInputStream inputStream =null;
			
			int ftpPort=21;
		    
			try {
				ftpPort=Integer.parseInt(ftpPortStr);
			}catch(Exception e) {
				System.out.println("Ftp port config error! default 21.");
				ftpPort=21;
			}
			
			int destLinux=0;
			if(isFtp==1 && ftpServerOS.toLowerCase().equals("linux")) {
				destLinux=1;
				ftpDestFilePath=destPath.replace("\\",  "/");
				if(!ftpDestFilePath.endsWith(pathSeparator))
					ftpDestFilePath=ftpDestFilePath+"/";
			}
 
 
	        FTPClient ftpClient = new  FTPClient();
	        ftpClient.setControlEncoding("utf-8");
	     

			try {

				System.out.println("connecting...ftp服务器:" + ftpServer + ":" + ftpPort);
			    ftpClient.connect(ftpServer,ftpPort);
				ftpClient.login(ftpUserName, ftpPassword); // 登录ftp服务
				int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器

				if (!FTPReply.isPositiveCompletion(replyCode)) {
					System.out.println("connect failed...ftp服务器:" + ftpServer + ":" + ftpPort);
				}
				System.out.println("connect successfu...ftp服务器:" + ftpServer + ":" + ftpPort);

			
			ftpClient.enterLocalPassiveMode();
	   
 
             String ftpServerDIR="";
        	 //保留从org开始的路径
             
			 File dir = new File(boxPath);
			 if(dir.exists()) {
			
			 
            String unicodeFileName="";
            String unicodeFilePath="";
            File[] fileList = dir.listFiles(); // 该文件目录下文件全部放入数组
            System.out.println("开始上传文件");
			 //循环读到的文件,
			 for (int i=0;i< fileList.length-1;i++){
				    if(fileList[i].isDirectory()) { // 判断是文件还是文件夹
				    	continue;
				    }
				unicodeFilePath=fileList[i].getAbsolutePath();
		     
		        inputStream = new FileInputStream(new File(unicodeFilePath));
		        //以下两行取得FTP 端的目标目录
		        ftpDestFilePath=destPath+fileList[i].getName().replace("_prepared.", ".").replace("~",destLinux==1?"/":"\\");
		    	ftpServerDIR=ftpDestFilePath.substring(0,ftpDestFilePath.lastIndexOf(destLinux==1?"/":"\\"));
		    	unicodeFileName=ftpDestFilePath.substring(ftpDestFilePath.lastIndexOf(destLinux==1?"/":"\\")+1);
		    
		    	
		    	System.out.println(ftpDestFilePath+"..."+ftpServerDIR);
			 
		        ftpClient.changeWorkingDirectory(ftpServerDIR);
		        ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
		        try {
		        ftpClient.storeFile(unicodeFileName, inputStream); 
		        System.out.println("上传文件成功");
				} catch (Exception e) {
					System.out.println("上传文件失败");
					e.printStackTrace();
				} finally {
					

					if (null != inputStream) {

						try {

							inputStream.close();

						} catch (IOException e) {

							e.printStackTrace();

						}

					}
		        }
			 }
		        ftpClient.logout();
			 }else {
				 System.out.println("Unicode file not found! FTP error!");
			 }
			}catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			finally{
				 if (ftpClient.isConnected()) {
						try {
							ftpClient.disconnect();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} 
		        return 0;
		}
		 
		//将每个文件转换成中文，放入box目录
		private static int toUnicode(File boxCNFile,String unionFileName) throws FileNotFoundException, IOException {
			
			System.out.print("..."+unionFileName);

			//取得Unicode文原始properties数据
			Properties propsCn = new Properties();
			try {
					propsCn.load(new FileReader(boxCNFile));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return -1;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return -2;
				}
				
			//
	 
			OutputStream out = new FileOutputStream(unionFileName,false);
			Calendar calendar= Calendar.getInstance();
			out.write(("#Guanhaiqing from China "+calendar.getTime()+"\n").getBytes());
			
			Iterator<Entry<Object, Object>> itCn = propsCn.entrySet().iterator();
	 
			while (itCn.hasNext()) {
				Entry<Object, Object> entry = itCn.next();
				Object key = entry.getKey();
			
				String value = entry.getValue().toString();	   
				out.write((key+"="+transfer.string2Unicode(value)+"\n").getBytes());
			}
			
			out.close();
			System.out.println("...OK!");
			return 0;
		}
		
	}
		
