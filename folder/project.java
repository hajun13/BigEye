package jsp;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import http.*;
import sql.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.*;
import java.net.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.*;



public class Project extends JspService{
private String rev;	//최종메시지


private String word;

String summer = "";
String urll;


private String head = "<head>"
		+ "<title>키워드 분석</title>"
		//style
		+ "<style>"
		
		+ "header {"
		+ "background: #D8D8D8"
		+ "}"
		+ "main {"
	
		+ "}"
		+ "</style>"
		+ "</head>";

private String main1 = "<!DOCTYPE html>"
		+ "<html>"
		//head//
		+ "<head>"
		+ "<title>키워드 분석</title>"
		
		+ "<style>"
		+ "header {"
		+ "background: #D8D8D8"
		+ "}"

		+ "</style>"
		+ "</head>"	
		+ "<body>"
		+ "<header>";



private String makeHtml(String url, String a, String b) {
	String result = "";
	
	result += main1
			+ "<p style=\"font-weight:bold;\"> 입력 url : <a href= \"" + url + "\" target=\"_blank\"> "+ url + "</a>"
			+ "</p><hr>"
			+ "</header>"
			+ "<main>"
			+ "<br>"
			+ "<big><strong>요약: </big></strong><br><br>"
			+ "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>"
			+ a
			+ "</main>"
			+ "<br><hr>"
			+ "<footer>"
			+ b
			+ "</footer>"
			+ "</body>"
			+ "</html>";

	return result;
}

private String startHtml() {
	String result = "";
	result += main1;
	
	return result;
}
// startHtml <p style ~ url 담는부분 전까지 만들어놓음 -> url을 차례대로 1, 2, 3 추가함. 그리고 요약내용은 어떻게 할지 생각해보기


private String addUrl(String url) {
	String result="";
	
	result += "<p style=\"font-weight:bold;\"> 참조 url : <a href= \"" + url + "\" target=\"_blank\"> "+ url + "</a>";
	return result;
}

private String endHtml (String a, String b) {
	String result = "";
	result += "</p><hr>"
			+ "</header>"
			+ "<main>"
			+ "<br>"
			+ "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>"
			+ "<big><strong>요약: </big></strong><br><br>"
			+ a
			+ "</main>"
			+ "<br><hr>"
			+ "<footer>"
			+ b
			+ "</footer>"
			+ "</body>"
			+ "</html>";
	
	return result;
}


public String getHtml(Socket sock, Session ses, String host, String[] params) throws IOException{

		
		Create_db db = new Create_db();
		Socket s = ses.pget("py"); // 파이썬 클라이언트 소켓	
		OutputStream pos = s.getOutputStream();
		
		urll = getParamValue(params, "url"); // 사용자가 입력한 url 추출
		System.out.println("original url : " + urll);
		if(urll == null) {
			try {
				System.out.println("-- 키워드 입력 --");
				word = getParamValue(params, "keyword");
				System.out.println(word);
				word = URLDecoder.decode(word, "UTF-8");
				System.out.println(word);
				
				if(db.isWord(word)) {
					System.out.println("-- 입력했던 적이 있는 키워드 --");
					rev = startHtml();
					
					String iu = db.WUselWORD(word);
					String[] iuList = iu.split("\t");
					
					for(int i=0; i<3; i++) {
						String ul = iuList[i];
						System.out.println(ul);
						rev += addUrl(ul);	
					}
					
					String msg = db.wgptselURL(word);
					String ks = db.wordselURL(word);
					rev += endHtml(msg, ks);
						
				}
				
				else if(!db.isWord(word)) {
					System.out.println("-- 새로 입력한 키워드 -- ");
					String encodedWord = URLEncoder.encode(word, "UTF-8");
					
					String googleSearchURL = "https://www.google.com/search?q=" + encodedWord;
					
					Document doc = Jsoup.connect(googleSearchURL)
			                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
			                .get();
					
					Elements links = doc.select("a[jsname=UWckNb]");
	
					ArrayList<String> topUrls = new ArrayList<>();
	
					for (Element link : links) {
						String url = link.attr("href");
						System.out.println("orijinal url : " + url);
					    if (url.startsWith("http://") || url.startsWith("https://")) {

					    	topUrls.add(url);
					    }
					    if (topUrls.size() >= 3) break;
					}
					
					rev = startHtml();
					
					String sendStr = ""; 
					String str = "";
					for (String url : topUrls) {
					    try {
					    	
					    	String decodeUrl = URLDecoder.decode(url, "UTF-8");
					    	db.WUinsert(word, url);
					    	System.out.println("josup 연결하기 전 url : " + decodeUrl);
					    	rev += addUrl(decodeUrl);
					        Document pageDoc = Jsoup.connect(decodeUrl).get();
					        Elements ex = pageDoc.select("body");
					        String txt = ex.text();
					        System.out.println(txt);
					        str += txt + "() ";
					        ///////////
				
					    } catch (IOException e) {
					        e.printStackTrace();
					    }
					}
							
					String[] testStr = str.split("[.]");
					
						//변수idx가 for문 바깥에서 선언함. 이유는 파이썬에 보내기 전에 url 3개에 들어있는 내용을 모두 담아서 전달할 것이기 때문
					for(int idx=0; idx<testStr.length; idx++) {
						System.out.println("testStr[ +" + idx + "]: " + testStr[idx]);
						if(idx % 5 == 0) {
							sendStr += testStr[idx] + ". \n";
							continue;
						}
						sendStr += testStr[idx] + ". ";
					}
					
					String[] sendList = sendStr.split("\n"); // \n단위로 split
					
					String sLl = String.valueOf(sendList.length);
					
					
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
					InputStream in = s.getInputStream();
					
					sendDataI(pos, sLl);
					
					String pm = null;
					
					for(int i=0;i<sendList.length; i++) {
						
						System.out.println(i);
		
						if(i == 0 || pm == null) {
							sendData(pos, sendList[i]);
							pm = br.readLine();
						}
						
						else if(i > 0 && pm.equals("more")) {
							sendData(pos, sendList[i]);	
							pm = br.readLine();
						}			
					}
	
					String pymsg = br.readLine();  
					summer = pymsg;
					
					db.Wgptinsert(word, pymsg);
					
					String line;
					
		            while ((line = br.readLine()) != null) {
		            	
		            	if (line.equals("END")) {  // 종료 신호를 받으면 루프 종료
		                    break;
		                }
		            	
		            	String[] parts = line.split("\t");
		            	if (parts.length == 2) {
		            		
		                    String kword = parts[0];
		                    double score = Double.parseDouble(parts[1]);
		                    String Sscore = String.format("%.4f", score);
		                    
		                    db.wordinsert(word, kword, Sscore);
		                }
		            }
		            String[] pns = pymsg.split("<span>");
		            String ps = pns[1];
		            String regexx = "\\d+%";
		            		          
		            Pattern pattern = Pattern.compile(regexx);
		            Matcher matcher = pattern.matcher(ps);
		            
		            List<String> percentages = new ArrayList<>();
		            
		            // 매칭되는 모든 부분을 찾아 리스트에 추가
		            while (matcher.find()) {
		                percentages.add(matcher.group());
		            }
		            
		            String as1 = percentages.get(0);
		            String as2 = percentages.get(1);
		            
		            System.out.println(as1 + as2);
		            String wordAll = db.wordselURL(word);
		            String wgptAll = db.wgptselURL(word);
		            
		            rev += endHtml(wgptAll, wordAll); 
				
				}
				
			}catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		
		else if(urll != null) {
			System.out.println("-- url 입력 --");
	 		urll = URLDecoder.decode(urll, "UTF-8");
			
			
			if(db.isURL(urll)) {
				System.out.println("-- 입력했던 적이 있는 url --");
				
				String keyAll = db.keyselURL(urll); //keyAll 키워드 : 빈도수 저장
				String gptAll = db.gptselURL(urll);
				
					
				rev = makeHtml(urll, gptAll, keyAll);
				
				System.out.println(rev);

			}
			
			else if(!db.isURL(urll)){
				System.out.println("새로운 url");
				
				System.out.println("jsoup 연결하기 전 url:  " + urll);
				Document doc = Jsoup.connect(urll).get();
				
	
				Elements ex = doc.select("body");
				String str = ex.text();
				System.out.println(str);
	
				
				str = str.toLowerCase();
				String regex = "[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s\\.]"; 
				
				str = str.replaceAll(regex, "");
				
				
				String[] splitStr = str.split(" ");
				
				System.out.println(str);////
				String[] testStr = str.split("[.]");
				String sendStr = "";
				
				
				for(int i=0; i<testStr.length; i++) {
					System.out.println("testStr[ +" + i + "]: " + testStr[i]);
					if(i % 5 == 0) {
						sendStr += testStr[i] + ". \n";
						continue;
					}
					sendStr += testStr[i] + ". ";
				}
				
			
				
				String[] sendList = sendStr.split("\n"); // \n단위로 split	
				/////
			
				HashMap<String, Integer> hs = new HashMap<>();
				
				
				
				for(int i=0; i<splitStr.length; i++) {			
					if(hs.containsKey(splitStr[i])) {
						hs.put(splitStr[i], hs.get(splitStr[i]) + 1);
						
					}else {
						
						hs.put(splitStr[i], 1);
					}
				}
	
				List<String> keySet = new ArrayList<>(hs.keySet());
				
				
				//value 값으로 내림차순 정렬
				keySet.sort((o1, o2) -> hs.get(o2).compareTo(hs.get(o1)));

				db.keydelCON("");
			
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
				String sLl = String.valueOf(sendList.length);
				sendDataI(pos, sLl);
				
				String pm = null;
				
				for(int i=0;i<sendList.length; i++) {
					
					System.out.println(i);
	
					if(i == 0 || pm == null) {
						sendData(pos, sendList[i]);
						pm = br.readLine();
					}
					
					else if(i > 0 && pm.equals("more")) {
						sendData(pos, sendList[i]);	
						pm = br.readLine();
					}			
				}
				
				System.out.println("클라이언트로 데이터 전송 ... \n");
				
				
				System.out.println("클라이언트 데이터 입력 대기중 ... ");
				String pymsg = br.readLine();  
				summer = pymsg;
				System.out.println("데이터 수신 완료 ");
				
				db.gptinsert(urll, pymsg);
				
				String line;
	
	            while ((line = br.readLine()) != null) {
	            	
	            	if (line.equals("END")) {  // 종료 신호를 받으면 루프 종료
	                    break;
	                }
	            	
	            	String[] parts = line.split("\t");
	            	if (parts.length == 2) {
	            		
	                    String word = parts[0];
	                    double score = Double.parseDouble(parts[1]);
	                    String Sscore = String.format("%.4f", score);
	                    System.out.printf("%s: %.4f%n", word, score);
	                    db.keyinsert(urll, word, Sscore);
	                }
	            }
				//body
				String keyAll = db.keyselURL(urll); //keyAll 키워드 : 빈도수 저장
				String gptAll = db.gptselURL(urll);
	            rev = makeHtml(urll, gptAll, keyAll);
	            
	            System.out.println(keyAll);
	            
	        }
		}
		
		System.out.println("--완료--");
		return "<html><meta charset='utf-8'>" + rev + "</html>";
	}
}

