/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minetrack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import org.jsoup.Jsoup;

/**
 *
 * @author Tim
 */
public class Main {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
    private static Double zecBal;
    private static String zecAddress;
    private static Double xmrBal;
    private static String xmrAddress;
    private static Scanner kb = new Scanner(System.in);
    private static org.jsoup.nodes.Document doc;
    private static String command = "";
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static Date date;
    private static Date yesterdayDate;
    private static final String CmcXmr = "https://api.coinmarketcap.com/v1/ticker/monero/";
    private static final String CmcZec = "https://api.coinmarketcap.com/v1/ticker/zcash/";
    private static final String xmrPoolURL = "https://api.nanopool.org/v1/xmr/balance/";
    private static ArrayList logStuff;
    private static Double zecMinedMonth;
    private static Double zecMDollarMonth;
    private static Double zecMinedYear;
    private static Double zecDollarYear;
    private static Double xmrMinedMonth;
    private static Double xmrMDollarMonth;
    private static Double xmrMinedYear;
    private static Double xmrDollarYear;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        date = new Date();
        yesterdayDate = new Date();
        System.out.println(dateFormat.format(date));
        logStuff = new ArrayList();
        zecMinedMonth = new Double(0.0);
        zecMDollarMonth = new Double(0.0);
        zecMinedYear = new Double(0.0);
        zecDollarYear = new Double(0.0);
        xmrMinedMonth = new Double(0.0);
        xmrMDollarMonth = new Double(0.0);
        xmrMinedYear = new Double(0.0);
        xmrDollarYear = new Double(0.0);
        try{
            System.out.println("Will try to load past balances.");
            // Open file to read from, named SavedObj.sav.
            FileInputStream saveFile = new FileInputStream("SaveObj.sav");
            // Create an ObjectInputStream to get objects from save file.
            ObjectInputStream save = new ObjectInputStream(saveFile);
            // Now we do the restore.
            zecBal = (Double) save.readObject();
            zecAddress = (String) save.readObject();
            xmrBal = (Double) save.readObject();
            xmrAddress = (String) save.readObject();
            zecMinedMonth = (Double) save.readObject();
            zecMDollarMonth = (Double) save.readObject();
            zecMinedYear = (Double) save.readObject();
            zecDollarYear = (Double) save.readObject();
            xmrMinedMonth = (Double) save.readObject();
            xmrMDollarMonth = (Double) save.readObject();
            xmrMinedYear = (Double) save.readObject();
            xmrDollarYear = (Double) save.readObject();
            yesterdayDate = (Date) save.readObject();
            // Close the file.
            save.close(); // This also closes saveFile.
            System.out.println("Zcash address: " + zecAddress + " with balance: " + zecBal);
            System.out.println("XMR address: " + xmrAddress + " with balance: " + xmrBal);
            System.out.println("zecMinedMonth: " + zecMinedMonth);
            System.out.println("zecMDollarMonth: " +zecMDollarMonth);
            System.out.println("zecMinedYear: " +zecMinedYear);
            System.out.println("zecDollarYear: "+zecDollarYear);
            System.out.println("xmrMinedMonth: "+xmrMinedMonth);
            System.out.println("xmrMDollarMonth: "+xmrMDollarMonth);
            System.out.println("xmrMinedYear: "+xmrMinedYear);
            System.out.println("xmrDollarYear: "+xmrDollarYear);
            System.out.println("Data loaded!");
            }catch(Exception exc){
                //exc.printStackTrace(); // If there was an error, print the info.
                System.out.println("Some or none of the data loaded - it may not have been available or some unknown error was thrown. Proceed.");
            }
        while(!command.equalsIgnoreCase("quit")){
            System.out.println();
            System.out.println("Please enter a command.");
            command = kb.nextLine();
            System.out.println();
            try{
                if(command.equalsIgnoreCase("save")){
                    save();
                }
                if(command.equalsIgnoreCase("zec")){
                   zec();
                }
                if(command.equalsIgnoreCase("xmr")){
                   xmr();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        
        }
        String goodBye ="Program is closing. Thank you :)";
        System.out.println(goodBye);
    }
    
    
    
    
    private static void xmr() throws IOException{
        date = new Date();
                System.out.println(dateFormat.format(date));
                if(xmrAddress==null){
                    System.out.println("Please enter your XMR address: ");
                    xmrAddress=kb.nextLine();
                }
                if(xmrBal==null){
                    System.out.println("Please Enter The XMR Pool Pending Balance from yesterday: ");
                    String xmrBals = kb.nextLine();
                    System.out.println("You Entered: " + xmrBals + ", this will be entered as yesterdays balance\n     and will be used to calculate today's xmr mined.");
                    xmrBal = Double.parseDouble(xmrBals);
                }
                
                Double xmrBalCurr = sendGetXmr(xmrPoolURL, xmrAddress);
                System.out.println("Pending Balance: " + xmrBalCurr);
                Double currentXmr = CmcSendGet(CmcXmr);
                Double currentVal = xmrBalCurr*currentXmr;
                //System.out.println("Current Value of Zcash on pool: $" + currentVal);
                currentVal = Math.round(currentVal*100.0)/100.0;
                Double balDiff = xmrBalCurr-xmrBal;
                Double dayVal = balDiff*currentXmr;
                balDiff = Math.round(balDiff*100000000.0)/100000000.0;
                dayVal = Math.round(dayVal*100.0)/100.0;
                //System.out.println("Current Value of XMR on pool: $" + currentVal);
                //System.out.printf("dexp: %f\n", balDiff);
                System.out.printf("The amount of xmr mined today: %f  \n     and it's value: $" + dayVal + "\n", balDiff);
                xmrBal = xmrBalCurr;
                logStuff.add("XMR");
                logStuff.add(balDiff);
                logStuff.add(dayVal);
                xmrMinedMonth = xmrMinedMonth + balDiff; 
                xmrMDollarMonth = xmrMDollarMonth + dayVal;
                xmrMinedYear = xmrMinedYear + balDiff;
                xmrDollarYear = xmrDollarYear + dayVal;
                System.out.println("Would you like to save now?");
                String c2=kb.nextLine();
                    if(c2.equalsIgnoreCase("yes")){
                        // Create some data objects for us to save.
                        save();
                    }else{
                        System.out.println("Okay we won't save, but don't forget to save before closing the program if you would \n     like the program to remember your addresses and current balances.");
                    }
    }
    
    private static void zec() throws IOException{
                String zecPoolURL = "https://api-zcash.flypool.org/miner/:"+ zecAddress + "/currentStats";
                date = new Date();
                System.out.println(dateFormat.format(date));
                if(zecAddress==null){
                    System.out.println("Please enter your Zcash address: ");
                    zecAddress=kb.nextLine();
                }
                if(zecBal==null){
                    System.out.println("Please Enter The Zec Pool Pending Balance from yesterday: ");
                    String zecBals = kb.nextLine();
                    System.out.println("You Entered: " + zecBals + ", this will be entered as yesterdays balance\n     and will be used to calculate today's zcash mined.");
                    zecBal = Double.parseDouble(zecBals);
                }
                //This gets the balance from the zcash.flypool pool.
                Double pendBalance = zecSendGet(zecPoolURL);
                System.out.println("Pending Balance: " + pendBalance);
                Double currentZec = CmcSendGet(CmcZec);
                Double currentVal = pendBalance*currentZec;
                //System.out.println("Current Value of Zcash on pool: $" + currentVal);
                currentVal = Math.round(currentVal*100.0)/100.0;
                Double balDiff = pendBalance-zecBal;
                Double dayVal = balDiff*currentZec;
                balDiff = Math.round(balDiff*100000000.0)/100000000.0;
                dayVal = Math.round(dayVal*100.0)/100.0;
                //System.out.println("Current Value of Zcash on pool: $" + currentVal);
                System.out.printf("The amount of zec mined today: %f  \n     and it's value: $" + dayVal + "\n", balDiff);
                zecBal = pendBalance;
                logStuff.add("ZEC");
                logStuff.add(balDiff);
                logStuff.add(dayVal);
                zecMinedMonth = zecMinedMonth + balDiff; 
                zecMDollarMonth = zecMDollarMonth + dayVal;
                zecMinedYear = zecMinedYear + balDiff;
                zecDollarYear = zecDollarYear + dayVal;
                System.out.println("Would you like to save now?");
                String c2=kb.nextLine();
                    if(c2.equalsIgnoreCase("yes")){
                        // Create some data objects for us to save.
                        save();
                    }else{
                        System.out.println("Okay we won't save, but don't forget to save before closing the program if you would \n     like the program to remember your addresses and current balances.");
                    }
    }
    
    private static double zecSendGet(String GET_URL) throws IOException {
		URL obj = new URL(GET_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
                        String resp=response.toString();
			//System.out.println(resp);
                        String [] respParts = resp.split(":");
                        //System.out.println(respParts[12]);
                        String [] xmrBalInPoold= respParts[12].split(",");
                        String xmrBalInPool = xmrBalInPoold[0];
                        Double pendBalance = Double.parseDouble(xmrBalInPool);
                        pendBalance=pendBalance/100000000;
                        //System.out.println(pendBalance);     
                        return pendBalance;
		} else {
			System.out.println("GET request didn't work");
                        return 0.0;
                }

	}
    
    
    private static void save() throws IOException{
        try{  // Catch errors in I/O if necessary.
                // Open a file to write to, named SavedObj.sav.
                FileOutputStream saveFile=new FileOutputStream("SaveObj.sav");
                // Create an ObjectOutputStream to put objects into save file.
                ObjectOutputStream save = new ObjectOutputStream(saveFile);
                // Now we do the save.
                save.writeObject(zecBal);
                save.writeObject(zecAddress);
                save.writeObject(xmrBal);
                save.writeObject(xmrAddress);
                save.writeObject(zecMinedMonth);
                save.writeObject(zecMDollarMonth); 
                save.writeObject(zecMinedYear); 
                save.writeObject(zecDollarYear);
                save.writeObject(xmrMinedMonth);
                save.writeObject(xmrMDollarMonth); 
                save.writeObject(xmrMinedYear);
                save.writeObject(xmrDollarYear);
                save.writeObject(yesterdayDate);
                //Close the file.
                save.close(); // This also closes saveFile.
                PrintWriter out = new PrintWriter(new FileWriter("MiningLog.txt", true));
                for (int i = 0; i < logStuff.size(); i=i+3) {
                    out.printf("%-22s%-22s%-22f$%-22f", dateFormat.format(date), logStuff.get(i),logStuff.get(i+1), logStuff.get(i+2));
                    out.println();
                    //System.out.println("at " + i + ": " + logStuff.get(i) + " next: " + logStuff.get(i+1) + " and: " + logStuff.get(i+2));
                }
                logStuff.clear();
                out.close();
                System.out.println("Saved!");
        }catch(Exception exc){
            exc.printStackTrace(); // If there was an error, print the info.
        }
    }
    
    private static double sendGetXmr(String URL, String myAddress) throws IOException {
		URL obj = new URL(URL + myAddress);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
                        String resp=response.toString();
			//System.out.println(resp);
                        String [] respParts = resp.split(":");
                        //System.out.println(respParts[2]);
                        String xmrBalInPool= respParts[2].substring(0, respParts[2].length()-1);
                        xmrBalInPool = xmrBalInPool.trim();
                        Double toRet = Double.parseDouble(xmrBalInPool);
                        //System.out.println(xmrBalInPool);
                        return toRet;
		} else {
			System.out.println("GET request didn't work");
                        return 0.0;
                }

	}
    
    private static double CmcSendGet(String GET_URL) throws IOException {
		URL obj = new URL(GET_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
                        String resp = response.toString();
			//System.out.println(resp);
                        String [] respParts = resp.split(":");
                        String [] resParts = respParts[5].split("\"");
                        String res = resParts[1];
                        res = res.trim();
                        Double price = Double.parseDouble(res);
                        //System.out.println(price);
                        return price;
               	} else {
			System.out.println("GET request not worked");
                        return 0.0;
		}

	}
}
