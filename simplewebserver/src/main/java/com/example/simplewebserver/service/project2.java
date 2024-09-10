package com.example.simplewebserver.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.example.simplewebserver.data.Create_db;

import java.util.*;
import java.net.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.*;
import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class project2 {
	private String word;
	private final Socket sock;
	
	public project2(String keyword, Socket sock) {
		this.word = keyword;
		this.sock = sock;
	}
	
	public void sendData(OutputStream os, String sen) {
		PrintWriter out = new PrintWriter(os);
		out.println(sen);
		out.flush();
	}
	
	public void sendDataI(OutputStream os, String i) {
		PrintWriter out = new PrintWriter(os);
		out.println(i);
		out.flush();
	}

	public void kewordPro(){
			String pm="";	
			Create_db db = new Create_db();
			OutputStream pos = null;
			BufferedReader br = null;
			try {
				pos = sock.getOutputStream();
				br = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			try {
				System.out.println("-- 키워드 입력 --");
	
				word = URLDecoder.decode(word, "UTF-8");
				System.out.println(word);
				
				if(db.isWord(word)) {
					System.out.println("-- 입력했던 적이 있는 키워드 --");
						
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
					
					
					String sendStr = ""; 
					String str = "";
					for (String url : topUrls) {
					    try {
					    	
					    	String decodeUrl = URLDecoder.decode(url, "UTF-8");
					    	db.WUinsert(word, url);
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
						if(idx % 5 == 0) {
							sendStr += testStr[idx] + ". \n";
							continue;
						}
						sendStr += testStr[idx] + ". ";
					}
					
					String[] sendList = sendStr.split("\n"); // \n단위로 split
					
					String sLl = String.valueOf(sendList.length);
					
					sendDataI(pos, sLl);
					
					
					for(int i=0;i<sendList.length; i++) {
						
						if(i == 0) {
							sendData(pos, sendList[i]);
							pm = br.readLine();
							if(pm == null) {
								pm = br.readLine();
							}
						}
						
						else if(i > 0 && pm.equals("more")) {
							sendData(pos, sendList[i]);	
							pm = br.readLine();
						}			
					}
					
					System.out.println("----------------------");
	
					String pymsg = br.readLine();  
					System.out.println(pymsg);
					String[] qp = pymsg.split("\\^");
					
					System.out.println(qp[1]);
					db.wgptinsert(word, qp[0]);
					
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
		            
		            String gp2 = qp[1];

		            String regexx = "\\d+%";
		            		          
		            Pattern pattern = Pattern.compile(regexx);
		            Matcher matcher = pattern.matcher(gp2);
		            
		            List<String> percentages = new ArrayList<>();
		            
		            // 매칭되는 모든 부분을 찾아 리스트에 추가
		            while (matcher.find()) {
		                percentages.add(matcher.group());
		            }
		            String as1 = percentages.get(0);
		            String as2 = percentages.get(1);
		 
		            as1 = as1.replace("%", "");
		            as2 = as2.replace("%", "");
		            System.out.println(as1 + as2);
		            db.sentiInsert(word, as1, as2);

				}
				
			}catch (IOException e) {
				e.printStackTrace();
			}
	
					
			System.out.println("--완료--");
		}
	
}

