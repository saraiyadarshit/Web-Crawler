package webcrawlerv2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Webcrawlerv2 {
    public static void main(String[] args) {
       try {
           
        String username = "001919824";
        String password = "YB18756I";
        String host = "cs5700.ccs.neu.edu";
        String root = "http://cs5700.ccs.neu.edu/accounts/login/";
        int port = 80;
    
        //Opening a Socket Connection
        Socket s ;
        s = new Socket(host, port);

        //Implementing the HTTP GET protocol
        OutputStream out = s.getOutputStream();
        PrintWriter outw = new PrintWriter(out, true);
        //Writing the GET Header to the server
        outw.println("GET " + root + " HTTP/1.1");
        outw.println("Host: cs5700.ccs.neu.edu");
        outw.print("\r\n");
        outw.flush();
        
        InputStream in = s.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(inr);
        String msg;
        String csrftoken= new String();
        String sessionID= new String();
        String r;
        String r1;
        
        //Reading the message sent by the server
        while ((msg = br.readLine()) != null) 
        {

        //Extracting the csrftoken using regular expressions
                r  = "csrftoken=\\w+\\;";
                Pattern p = Pattern.compile(r);
                Matcher m = p.matcher(msg);
		
                while (m.find())
                {
		csrftoken = m.group();
		}
                
       //Extracting the sessionID using regular expressions               
                r1  = "sessionid=\\w+\\;";
                Pattern p2 = Pattern.compile(r1);
                Matcher m2 = p2.matcher(msg);
		
                while (m2.find()) 
                {
		sessionID = m2.group();
		}
        }
                                
        //Extracting the token and SessionID using substring                
        String sendtoken = csrftoken.substring(10, 42);
	String sendsessionId = sessionID.substring(10, 42);
        
        
	String parameters = "csrfmiddlewaretoken=" + sendtoken + 
                            "&username=" + username +
                            "&password=" + password +
                            "&next=/fakebook/";
	       		br.close();
			s.close();
        
                        
       //Implementing HTTP POST method
        Socket s2 = new Socket(host, port);                
                        
        OutputStream out1 = s2.getOutputStream();
        PrintWriter outw1 = new PrintWriter(out1, true);
        //Writing the header for the POST method
        outw1.print("POST " + root + " HTTP/1.0\r\n");
        outw1.print("Host: cs5700.ccs.neu.edu\r\n");
        outw1.print("User-Agent: Mozilla/5.0\r\n");
        outw1.print("Cookie: csrftoken=" + sendtoken + "; sessionid=" + sendsessionId+ "\r\n");
        outw1.print("Content-Type: " + "application/x-www-form-urlencoded \r\n");
        outw1.print("Content-length: " + parameters.length() +"\r\n");
        outw1.print("\r\n");
        outw1.print(parameters + "\r\n");
        outw1.flush();
        
         
        InputStream in1 = s2.getInputStream();
        InputStreamReader inr1 = new InputStreamReader(in1);
        BufferedReader br1 = new BufferedReader(inr1);
        
	String msg2 = new String();
	String postsessionID = new String();
        String seed = new String();
			
       //Reading the message sent by the server
	while ((msg2 = br1.readLine()) != null)
	{
            //System.out.println(msg2);
       //Extracting the sessionID using regular expressions     
            String  r2  = "sessionid=\\w+\\;";
            Pattern p3 = Pattern.compile(r2);
            Matcher m3 = p3.matcher(msg2);
            
            	while (m3.find())
                {
		postsessionID = m3.group();
		}
                
         //Extracting the url in the LOCATION part of the header sent by the server       
            String r3 = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p4 = Pattern.compile(r3);
            Matcher m4 = p4.matcher(msg2);
                       
                while (m4.find())
                {
                seed = m4.group();                    
                }
                                
	}
                //System.out.println(seed);
                
                //Extracting the sessionID using substring   
		String finalsessionID = postsessionID.substring(10, 42);
                
		br1.close();
		s2.close();
                
                //Declaring the variables
                HashSet<String> found = new HashSet<String>();
		HashSet<String> visited = new HashSet<String>();
                String link = new String();
                String first = new String();
                String msg3 = new String();
                String newlinks = new String();
                String addlinks = new String();
                String onlylink = new String();
                String url301 = new String();
                int secret_flags =0;
                
                found.add(seed);
               
                //The Crawler Code
               //If the found hashset is empty it will come out of the while loop 
                while(!found.isEmpty()){
				try {

                                        Socket s3 = new Socket(host, port);
                                        OutputStream out2 = s3.getOutputStream();
                                        PrintWriter outw2 = new PrintWriter(out2, true);
			
					//Storing the next element of found hashset in link 
                                        link = (String) found.iterator().next();
                                        //System.out.println(link);
					//Adding the link as the next element of visited hashset
                                        visited.add(link);
                                        //Removing one element from the found hashset
					found.remove(link);
                                        
                                        //Implementing the HTTP GET method
					outw2.print("GET " + link + " HTTP/1.1\r\n"); 
					outw2.print("Host: cs5700f12.ccs.neu.edu\r\n") ;
					outw2.print("Cookie: csrftoken=" + sendtoken + "; sessionid=" + finalsessionID + "\r\n") ; 
					outw2.print("Connection: close\r\n\r\n");
					outw2.flush();
                                        
					InputStream in2 = s3.getInputStream();
                                        InputStreamReader inr2 = new InputStreamReader(in2);
                                        BufferedReader br2 = new BufferedReader(inr2);
				//Reading the first line of message sent by the server
                                        first = br2.readLine();
					String check [] = first.split(" ");
                                //Checking for error messages from the server
                                //If the server gives the error 403 and 404 stop crawling on the current link, abandon the current link and jump to the next link in the found hashset
					if(check[1].equals("404") || check[2].equals("403"))
					{
						continue;
					}
                               //If the server finds 500 error, store the redirecting link in the found hashset and proceed with the new link         
                                        
					if(check[1].equals("500"))
					{
						found.add(link);
                                                continue;
					}
                                 //       
                                        if(check[1].equals("301"))
					{
                                                                                       
                                         String r5 = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
                                         Pattern p6 = Pattern.compile(r5);
                                         Matcher m6 = p6.matcher(msg2);
                       
                                               while (m6.find())
                                                  {
                                                      url301 = m6.group();                    
                                                  }
                                                  found.add(url301);
                                            }
					
                                //Extracting links from the HTML pages using regular expressions
					while ((msg3 = br2.readLine()) != null){
						String  r4  = "<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]";
						Pattern p5 = Pattern.compile(r4);
						 
                                                 Matcher m5 = p5.matcher(msg3);
                                    //Checking if the link is new and adding new links in found hashset            
                                                while (m5.find()) {
							newlinks = m5.group();
                                                                                        
							addlinks = newlinks.substring(9, (newlinks.length()-1));
							if(newlinks.contains("/fakebook/")) 
							{
								if(!visited.contains(addlinks))
								{
									found.add(addlinks);
								}
							}
						}
                                                
                                                
                                //Extracting the secret_flags from the HTML pages using regular expressions                
						if(msg3.contains("<h2 class='secret_flag'"))
						{
							secret_flags++;
							//flags.add(m);
                                                  String r5 = "FLAG: \\w+\\<";      
                                Pattern p6 = Pattern.compile(r5);
				Matcher matcher = p6.matcher(msg3);
                               //Printing the secret flags using regular expression
				while (matcher.find())
                                {
					String flag = matcher.group();
					System.out.println(flag.substring(6, 70)) ;
				}
						}

						
					}
					s3.close();
                                        //If found five flags break from the loop
					if(secret_flags==5){
						break;
					}
				}

				catch(Exception ex)
				{
					continue;
				}
			}
                }
       catch(Exception e)
       {
           System.out.println(e);
       }
}
}