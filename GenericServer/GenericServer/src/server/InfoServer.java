/**
 * 
 */
package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Aaron
 *
 */
public class InfoServer implements Runnable {
	private Connection conn = null;
	private Statement st = null;
	
	private int userID;
	private Socket sock;
	private int screen = 0;
	private int activeJob;
	//commands
    private final String SVRCMD = "#@!?Server:";
    private final String PAGECMD = "GOTOSCREEN:";
    private final String GETFEEDS = "GETFEED";
    private final String CLIENTCMD = "#@!?Client:";
    private final String JOBCMD = "SETJOB:";
	/**
	 * 0 = Home screen
	 * 1 = Contacts Search Screen
	 * 2 = Staff Page
	 * 3 = Job Search
	 * 4 = Inbox
	 * 5 = Job Page
	 * 6 = Ticket page
	 * 7 = Employee Page
	 * 8 = Contacts Screen
	 */
	//server commands start with "#@!?Server:"
	private InputStream is;
	private OutputStream os;
	private BufferedReader br;
	
	public InfoServer(int USR_ID, Socket SOCKET){
		userID = USR_ID;
		sock = SOCKET;
		connect();
		
	}
	
	private void connect(){
		try{
			//connect to database
			conn = DriverManager.getConnection("jdbc:ucanaccess://serverDB.accdb");
			//test connection?
			st = conn.createStatement();
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}
	
	@Override
	public void run() {
		System.out.println("running");
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
			InputStreamReader isr = new InputStreamReader(is);
			PrintWriter pw = new PrintWriter(os);
			br = new BufferedReader(isr);
			String message ="";
			while((message = br.readLine())!= null){ //While more to read
				System.out.println("loop");
				if(message.startsWith(SVRCMD)){ //if it is a server command
					try{
						//put server command code here
						String serverMessage = message.replace(SVRCMD, "").trim();
						if(serverMessage.startsWith(PAGECMD)){
							screen = Integer.parseInt(serverMessage.replace(PAGECMD, "").trim());
						}
						if(serverMessage.startsWith(JOBCMD)){
							activeJob = Integer.parseInt(serverMessage.replace(JOBCMD,""));
						}
						if(serverMessage.contentEquals(GETFEEDS)){
							String sendback =" ";
							sendback = CLIENTCMD + getFeed();
							pw.println(sendback);
							pw.flush();
						}
						
					}
					catch(NumberFormatException nfe){
						//then it wasn't an page command, maybe more elegant way of doing this
					}
				}
				else{
					System.out.println("error");
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private String getFeed() {
		// TODO Auto-generated method stub
		String feed="";
		switch(screen){
		case 0://i lied not home screen
			break;
		case 1:
			feed = getHomePageFeed();
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8: 
			break;
		default:
			break;
		}
		System.out.println(feed);
		return feed;
	}
	
	//Homepage Feed
	private String getHomePageFeed(){
		String feed = "";
		try{
			PreparedStatement prepStatementJobs = null;
			PreparedStatement prepStatementTickets = null;
			PreparedStatement prepStatementEmployeeName = null;
			ResultSet resultsJobQuery = null;
			ResultSet resultsTicketQuery = null;
			ResultSet resultsEmployeeQuery = null;
			// SQL code for jobs
			String sql="select * from jobs_tbl where  userIDs=?";
			//sql command for jobs
			prepStatementJobs = conn.prepareStatement(sql);
			//sql cmd pass the statement the user ID as a filter
			prepStatementJobs.setInt(1,userID);
			//sql cmd resultsJobQuery is jobs for that user
			resultsJobQuery = prepStatementJobs.executeQuery();
			
			
			boolean first=true;
			//Array list of the companiesID so can itterate thru them later in binary search
			ArrayList<Integer> companiesID = new ArrayList<Integer>(0);
			ArrayList<String> companiesName = new ArrayList<String>(0);
			
			//more sql code but for tickets
			sql = "select * from tickets_tbl where company =?";
			//while querry has another job...
			while(resultsJobQuery.next()){
				//get CompanyID and CompanyName
				feed+="header."+resultsJobQuery.getString(1)+"~"+resultsJobQuery.getString(3)+"~%";
				//add which company it is
				companiesID.add(resultsJobQuery.getInt(1));
				companiesName.add(resultsJobQuery.getString(3));
				if(!first){
					//if ! first then add more "or companies" to list
					sql+=" or company =?";
				}
				first = false;
			}
			//more sql cmds..prepStatementJobs is tickets now
			prepStatementTickets = conn.prepareStatement(sql);
			//start at 1 because SQL is weird and starts at 1 not 0
			int company = 1;
			//for each company...
			for(int companyID:companiesID){
				//set each "or company" criteria to a company
				prepStatementTickets.setInt(company , companyID);
				company++;
			}
			resultsTicketQuery = prepStatementTickets.executeQuery();//run query of tickets...resultsJobQuery is tickets now
			while(resultsTicketQuery.next()){//while ticket query has more records
				sql = "select firstname, lastname from employees_tbl where employeeID =?";//sql cmd to pull up the employees name
				prepStatementEmployeeName = conn.prepareStatement(sql);
				prepStatementEmployeeName.setInt(1, resultsTicketQuery.getInt(3));//set employee ID form resultsTicketQuery record to first filter
				resultsEmployeeQuery = prepStatementEmployeeName.executeQuery();
				resultsEmployeeQuery.next();
				String employeeName = resultsEmployeeQuery.getString(1)+" "+resultsEmployeeQuery.getString(2);
				/**
				 * the following two areas do the exact same thing but I'm not sure which will be more efficient...will test
				 * 
				 * Have tested and the first one is somewhere between 2x - 5x faster with 4 records //results might not be right on this test
				 * 													  10x - infinitely faster with 2 records
				 * 													  4x - infinitely faster with 8 records
				*/
				String companyName = binarySearch(resultsTicketQuery.getInt(2),companiesID, companiesName);
				//System.out.println(System.currentTimeMillis()-startTime);
				//end area 1
				/** //area 2
				System.out.println("idea 2");
				startTime = System.currentTimeMillis();//start time
				sql="select Company from jobs_tbl where  companyID=?";// SQL code for jobs
				prepStatementEmployeeName = conn.prepareStatement(sql);//sql command for jobs
				prepStatementEmployeeName.setInt(1,resultsTicketQuery.getInt(2));//sql cmd pass the statement the user ID as a filter
				resultsEmployeeQuery = prepStatementEmployeeName.executeQuery();//sql cmd resultsJobQuery is jobs for that user
				resultsEmployeeQuery.next();
				companyName = resultsEmployeeQuery.getString(1);
				System.out.println(System.currentTimeMillis()-startTime);
				//end area 2
				*/ //see above: to be deleted after all parties have accepted first method is faster
				feed+= "child."+companyName+"~"+employeeName+"~"+resultsTicketQuery.getDate(4)+"~"+resultsTicketQuery.getTime(4)+"~%";
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return feed;
		
	}
	
	private String binarySearch(int targetID, ArrayList<Integer> id, ArrayList<String> name){
		ArrayList<Integer> idClone = (ArrayList<Integer>) id.clone();//clone so don't change the reference Array
		ArrayList<String> nameClone = (ArrayList<String>) name.clone();//clone so don't change the reference Array
		
		int halfway = idClone.size()/2;//binary Search
		
		if( targetID == idClone.get(halfway)){//if it found it
			return nameClone.get(halfway);//return it
		}
		/**
		 * Side note: if you don't have it remove the halfway point it enters an infinite recursive loop and spits out a empty string
		 */
		else if(targetID < idClone.get(halfway)){//if it is less than what halfway is
			//then for everything above or equal to halfway...remove it
			for(int i = halfway; i<=idClone.size()-1;i++){
				idClone.remove(halfway);
				nameClone.remove(halfway);
				return binarySearch(targetID, idClone,nameClone); //Recursion
			}
		}
		
		else{//else it is over halfway
			for(int i = 0; i <= halfway;i++){//and remove everything under or equal to the halfway mark
				idClone.remove(0);
				nameClone.remove(0);
				return binarySearch(targetID, idClone,nameClone);
			}
		}
		//never gets here, but eclipse is dumb and doesn't realize that it can't reach here
		return "";
	}

}