package com.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.net.httpserver.HttpHandler;


//import com.sendrequest.*;
/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String USER_AGENT = "Mozilla/5.0";
	private final String CLIENT_ID = "54c66884e1154304a0a7f82ec312c07c";
	private final String CLIENT_SECRET = "91fc22a538e84e2f91ec53fdcdc0324e";
	private final String REDIRECT_URI = "http://localhost";
	private final String ACCESS_TOKEN = "2898235952.5b9e1e6.0ae80b70367c43248d9af8571dccb678";
    /**
     * Default constructor. 
     */
    public MyServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!request.getParameter("username").isEmpty()){
			String username = (String) request.getParameter("username");
		
			String userID = "https://api.instagram.com/v1/users/search?q="+username+"&access_token="+ACCESS_TOKEN;
			String userid = getID(userID, username);
			System.out.println("userID === " + userid);
			if (!(userid==null))
			{
		
				//String token = (String) request.getParameter("token");
				//String userid = (String) request.getParameter("user-id");
				//String code = "https://api.instagram.com/oauth/authorize/?client_id="+CLIENT_ID+"&redirect_uri="+REDIRECT_URI+"&response_type=code&scope=follower_list+comments+likes";
			
				String code = "https://api.instagram.com/v1/users/"+userid+"/?access_token="+ACCESS_TOKEN;
				int followed = getFollowed(code);
				int follows = getFollows(code);
				String name = getName(code);
		
				String reqMedia = "https://api.instagram.com/v1/users/"+userid+"/media/recent/?access_token="+ACCESS_TOKEN+"&count=30";
				int comments30 = getCommentsCount(reqMedia);
				int likes30 = getLikesCount(reqMedia);
				ArrayList<String> commentsTexts30 = getCommentsTexts30(reqMedia);
		
				double score = getScore (followed, follows, likes30);
				Map<String,Integer> wordRepeate = getWordRepeate(commentsTexts30);
		
				writeToDB( name, followed, follows, comments30, likes30,commentsTexts30);
		
				System.out.println("token" + ACCESS_TOKEN +"user-id" + userid);
				System.out.println("Name :---> " +name);
				System.out.println("followed_by :---> " +followed);
				System.out.println("follows :---> " +follows);
				System.out.println("Comment over 30 posts :---> " +comments30);
				System.out.println("Likes over 30 posts :---> " +likes30);
				System.out.println("comments over 30 posts :---> " +commentsTexts30);
				System.out.println("Score :---> " +score);
		
				for(Map.Entry<String, Integer> entry : wordRepeate.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
					System.out.println("Word " +key+" Повторяется " + value+" раз");
				}
		
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><p>" + username);
				response.getWriter().println("<p> name Имя" + name + "<br>");
				response.getWriter().println("followed_by" + followed + "<br>");
				response.getWriter().println("follows" + follows + "<br>");
				response.getWriter().println("likes" + likes30 + "<br>");
				response.getWriter().println("Score:" + score + "<br>");
				response.getWriter().println("RepeateWord:" + wordRepeate + "<br>");
				response.getWriter().println("</p></body></html>");
			}
			else 
			{
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><p>");
				response.getWriter().println("<p>Имя не найдено или доступ к нему закрыт</p>");
				response.getWriter().println("</p></body></html>");
			}
			}
		else
		{
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><p>");
			response.getWriter().println("<p>Вы не ввели имя</p>");
			response.getWriter().println("</p></body></html>");
		}
	}

	private String getID(String searchnameURL, String searchUser) throws JSONException, IOException {
		String resp = null;
		JSONObject jsonComm = new JSONObject(getjson(searchnameURL).toString());
        JSONArray dataComm = jsonComm.getJSONArray("data");
        for (int i=0; i< dataComm.length();i++)
		{
		String username = dataComm.getJSONObject(i).getString("username");
        	if (username.equals(searchUser))
        	{
        		resp = dataComm.getJSONObject(i).getString("id");
        		break;
        	}
		}
		return resp;
	}

	private void writeToDB(String name, int followed, int follows, int comments30, int likes30,
			ArrayList<String> commentsTexts30) {
		
		try {
            DBConnection con = new DBConnection();
            //Connection connection = con();
            //con.insertRow(connection, name , followed, follows, comments30, likes30, commentsTexts30);
        }
        catch (Exception e){
            //e.printStackTrace();
            //out.print(e.toString());
            System.out.println(e.getMessage());
        }
		// TODO Auto-generated method stub
		
	}

	private Map<String, Integer> getWordRepeate(ArrayList<String> commentsTexts30) {
		
		Map<String,Integer> mapWords = new HashMap<String, Integer>();
		ArrayList<String> words = new ArrayList<>();
		for (int i=0; i<commentsTexts30.size();i++)
		{
			String strtrim = commentsTexts30.get(i).trim();
			String[] str = strtrim.split(" ");
			for (int j=0; j<str.length;j++){
				if (!str[j].startsWith("@"))
				{
					String result = str[j].replaceAll("\\!|\\.|\\,|\\?", "").toLowerCase().trim();
					if (!result.equals("?")||!result.equals(""))
					{
					words.add(result);
					}
				}
			}
		}
		for (int i=0; i<words.size();i++)
		{
			int wCount = 0;
			for (int j=0; j<words.size();j++)
			{
				if (words.get(i).equals(words.get(j)))
				{
					wCount ++;
				}
			}
			if (wCount>1){
				mapWords.put(words.get(i), wCount);	
			}
			
		}
		
		
		return mapWords;
	}

	private double getScore(int followed, int follows, int likes30) {
		double score = 1;
		if (followed >500)
		{
			int tempfd = (followed/500);
			if(tempfd >= 5)
			{
				score += 0.5;
			}
			else 
			{
				score += (tempfd/10);
			}			
		}
		else
		{
			if (followed<50)
			{
				score += -0.2;
			}
		}
		System.out.println("Score after fd:---> " +score);
		if (follows>200)
		{
			int fs = (follows/200);
			if (fs>3)
			{
				score -= 0.3;
			}
			else 
			{
				score -= (fs/10);
			}
		}
		System.out.println("Score after fs:---> " +score);
		if (likes30>30)
		{
			int lk = ((likes30-30)/10);
			System.out.println("Score after likes:---> " +score + lk);
			score += (double) lk/10;
			System.out.println("Score after lk/10:---> " +score);
		}
		else
		{
			if (likes30<10)
			{
				score -= 0.3;
			}
			else if (likes30<20)
			{
				score -= 0.2;
			}
			else if (likes30<30)
			{
				score -= 0.1;
			}
		}
		return score;
	}

	private ArrayList<String> getCommentsTexts30(String reqMedia) throws JSONException, IOException {
		ArrayList<String> comments = new ArrayList<String>();
		JSONObject jsonComm = new JSONObject(getjson(reqMedia).toString());
        JSONArray dataComm = jsonComm.getJSONArray("data");
                
		for (int i=0; i< dataComm.length();i++)
		{
		JSONObject countsCommText = dataComm.getJSONObject(i).getJSONObject("comments");
		JSONArray texts = countsCommText.getJSONArray("data");
			for (int j=0; j<texts.length();j++)
			{
				String commTexts = texts.getJSONObject(j).getString("text");
				comments.add(commTexts);
			}
		}
		return comments;
	}

	private int getLikesCount(String reqMedia) throws JSONException, IOException {
		int resp = 0;
		JSONObject jsonComm = new JSONObject(getjson(reqMedia).toString());
        JSONArray dataComm = jsonComm.getJSONArray("data");
        for (int i=0; i< dataComm.length();i++)
		{
		JSONObject countsComm = dataComm.getJSONObject(i).getJSONObject("likes");
				resp += countsComm.getInt("count");}
		return resp;
	}

	private int getCommentsCount(String reqMedia) throws JSONException, IOException {
		
		int resp = 0;
		JSONObject jsonComm = new JSONObject(getjson(reqMedia).toString());
        JSONArray dataComm = jsonComm.getJSONArray("data");
        for (int i=0; i< dataComm.length();i++)
		{
		JSONObject countsComm = dataComm.getJSONObject(i).getJSONObject("comments");
				resp += countsComm.getInt("count");}
		return resp;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public Integer getFollowed(String request) throws JSONException, IOException
	{
		int res = 0;
		JSONObject json = new JSONObject(getjson(request).toString());
        JSONObject data = json.getJSONObject("data");
		JSONObject counts = data.getJSONObject("counts");	
		res = counts.getInt("followed_by");
		return res;
	}
	
	public Integer getFollows(String request) throws JSONException, IOException
	{
		int res = 0;
		JSONObject json = new JSONObject(getjson(request).toString());
        JSONObject data = json.getJSONObject("data");
		JSONObject counts = data.getJSONObject("counts");	
		res = counts.getInt("follows");
		
		return res;
	}
	public String getName(String request) throws JSONException, IOException
	{
		String res = null;
		JSONObject json = new JSONObject(getjson(request).toString());
        JSONObject data = json.getJSONObject("data");
		res = data.getString("full_name");	
				
		return res;
	}
	
	public StringBuffer getjson (String urlj) throws IOException
	{
		URL url = new URL(urlj);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer res = new StringBuffer();
				
		while ((inputLine = in.readLine()) != null) {
				res.append(inputLine);
			}
		in.close();
		return res;
	}
	
}
