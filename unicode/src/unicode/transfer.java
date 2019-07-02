package unicode;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class transfer {

   public static String string2Unicode(String string) {
		if (string.equals("")) {
			return null;
		}
		
		string=string.replace("”","").replace("“","").replace("（",")").replace("）",")");
		
		char[] bytes = string.toCharArray();
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			char c = bytes[i];

			// 标准ASCII范围内的字符，直接输出
			if (c >= 0 && c <= 127) {
				switch(c) {
				case '\n':
					unicode.append('\\');
					unicode.append('n');
					break;
					
				case '\r':
					unicode.append('\\');
					unicode.append('r');
					break;
				default:
					unicode.append(c);
				
				}
				continue;	
			}
			
			String hexString = Integer.toHexString(bytes[i]);

			unicode.append("\\u");
            System.out.print("..."+hexString);
			// 不够四位进行补0操作
			if (hexString.length() < 4) {
				unicode.append("0000".substring(hexString.length(), 4));
			}
			unicode.append(hexString);
		}
		 
		System.out.println(unicode.toString());
		return unicode.toString();
		 
   }
   
 
	/**	 * unicode转字符串	 * 	 * @param unicode	 * @return	 */	
	public static String unicodeToString(String dataStr) {		
		    int start = 0;
	        int end = 0;
	        final StringBuffer buffer = new StringBuffer();
	        while (start > -1) {
	            end = dataStr.indexOf("\\u", start + 2);
	            String charStr = "";
	            if (end == -1) {
	                charStr = dataStr.substring(start + 2, dataStr.length());
	            } else {
	                charStr = dataStr.substring(start + 2, end);
	            }
	            System.out.print(charStr+"..");
	            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
	            buffer.append(new Character(letter).toString());
	            start = end;
	            System.out.println("|");
	        }
	        return buffer.toString();
	    }
	 
 
	private static boolean isChinese(char c) {
		 
		return String.valueOf(c).matches("[\u4e00-\u9fa5]");
		 
	}
	
	    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符
	       
	    /**
	     * @param htmlStr
	     * @return
	     *  删除Html标签
	     */
	    public static String delHTMLTag(String htmlStr) {
	        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
	        Matcher m_script = p_script.matcher(htmlStr);
	        htmlStr = m_script.replaceAll(""); // 过滤script标签
	   
	        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
	        Matcher m_style = p_style.matcher(htmlStr);
	        htmlStr = m_style.replaceAll(""); // 过滤style标签
	   
	        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
	        Matcher m_html = p_html.matcher(htmlStr);
	        htmlStr = m_html.replaceAll(""); // 过滤html标签
	   
	        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
	        Matcher m_space = p_space.matcher(htmlStr);
	        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
	        return htmlStr.trim(); // 返回文本字符串
	    }
	       
	    public static String getTextFromHtml(String htmlStr){
	        htmlStr = delHTMLTag(htmlStr);
	        htmlStr = htmlStr.replaceAll("&nbsp;", "");
//	        htmlStr = htmlStr.substring(0, htmlStr.indexOf("。")+1);
	        return htmlStr;
	    }

/*调用百度翻译接口*/
static String translate(String text,String dstLanguage,String srcLanguage) throws IOException {
	
	final OkHttpClient client = new OkHttpClient();

	String appid="20190417000288719";
 
	// 随机数
    String salt = String.valueOf(System.currentTimeMillis());

	String passwd="EeecolCs6lUz5Iedrp2Y";
	String sign=appid+text+salt+passwd;
	sign=DigestUtils.md5Hex(sign);
	String url="http://api.fanyi.baidu.com/api/trans/vip/translate?q="+encode(text)+"&from="+srcLanguage+"&to="+dstLanguage+"&appid="+appid+"&sign="+sign+"&salt="+salt;

	Request request = new Request.Builder()
	        .url(url)
	        .build();

	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

	    JSONObject object = JSONObject
	    	      .parseObject(response.body().string());
	    if(object.containsKey("error_msg")) {
	    	System.out.println(object.getString("error_code")+"|"+object.getString("error_msg")+"|"+text);
	    	return "";
	    }else {
	    JSONArray result=object.getJSONArray("trans_result");
	    return result.getJSONObject(0).getString("dst");
	    }
	 
	    
}

 /**
  * 对输入的字符串进行URL编码, 即转换为%20这种形式
  * 
  * @param input 原文
  * @return URL编码. 如果编码失败, 则返回原文
  */
 public static String encode(String input) {
     if (input == null) {
         return "";
     }

     try {
         return URLEncoder.encode(input, "utf-8");
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     return input;
 }   
}