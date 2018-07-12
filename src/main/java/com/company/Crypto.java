package com.company;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.*;


public class Crypto {
    Client client = ClientBuilder.newClient();
    private String firstLine;
    String ipAddress = InetAddress.getLocalHost().toString();
    String networkFinal ;
    String[] userHash;

    public Crypto() throws UnknownHostException {
        System.out.println("CryptoPi Ver 1.0.0");
        checkNetwork();
        Scanner s = new Scanner(System.in);
        firstLine = s.nextLine();
        firstLine = firstLine.toUpperCase();
        String secondLine = s.nextLine();
        checkLine2(secondLine);
        checkSP(firstLine);
    }

    private void checkNetwork() {
        if(ipAddress.contains("169.254"))
            networkFinal = "192.168.254.199";
        else if(ipAddress.contains("192.168"))
            networkFinal = "192.168.254.199";
        else
            networkFinal = "47.148.230.167";
    }


    public void checkSP(String s) {

        if(s.contains("-P"))
        {
            //checkValidInput(s);
            if(s.contains("BTC"))
                reqPrice("BTC");
            if(s.contains("ETH"))
                reqPrice("ETH");
            if(s.contains("XRP"))
                reqPrice("XRP");
            if(s.contains("ADA"))
                reqPrice("ADA");
            if(s.contains("ZEC"))
                reqPrice("ZEC");
            if(s.contains("LTC"))
                reqPrice("LTC");
            if(s.contains("NEO"))
                reqPrice("NEO");
            if(s.contains("XLM"))
                reqPrice("XLM");
        }
        if(s.contains("-HELP"))
        {
            printHelp();
        }
        if(s.contains("-SYNOPSIS"))
        {
            System.out.println("Cryptocurrency Trakcer / Adblocker Interface. (v1.0.0)");
        }
        if(s.contains("-bat"))
            saveHash(s);

        if(s.contains("-PIHOLE"))
        {
            //checkValidInput(s);
            String[] str = s.split("E ");
            s = str[1];
            if(s.contains("-S"))
                requestPiHoleStats();
            if(s.contains("-E"))
            {
                checkNetwork();
                piOn();
            }
            if(s.contains("-D"))
            {
                checkNetwork();
                str = s.split("D ");
                Integer disableTime = Integer.parseInt(str[1]);
                piOff(disableTime);
            }
            if(s.contains("-TOP"))
            {
                topItems();
            }
        }
    }

    private void printHelp() {
        System.out.println("CryptoPi [-P ] Ticker | [-PIHOLE} s | e | d (seconds) | top [--bat=token] | -help | -synopsis");
        System.out.println(" ");
        System.out.println("CryptoPi returns the current market price of a specified cryptocurrency.\n" +
                "The tool can also return the top 10 Ads blocks/DNS Queries\n" +
                "Can also Enable/Disable the local PiHole device");
        System.out.println(" ");
        System.out.println("Version \t : 1.0.0 \n" +
                "Dependencies : Jersey 2.27, Json, Jackson 2.9.5 \n" +
                "Author \t\t : Gil, Robert. Gil, Albert\n" +
                "Contact \t : rgil@cpp.edu OR argil@cpp.edu \n"
        );
    }

    public void saveHash(String s)
    {
        this.userHash = s.split("= "); //This is to be ignored
        //System.out.println(userHash[1]);
    }

    public void checkLine2(String s)
    {
        if(!s.contains(".eot"))
        {
            stateInvalidInput();
        }
    }
    public void checkValidInput(String str)
    {
        if(str.contains("-P") && !str.contains("BTC")
                || !str.contains("ETH")
                || !str.contains("XRP")
                || !str.contains("ADA")
                || !str.contains("ZEC")
                || !str.contains("LTC")
                || !str.contains("NEO")
                || !str.contains("XLM"))
        {
            stateInvalidInput();
        }

        if(str.contains("-PIHOLE") && !str.contains("S")
                || !str.contains("E")
                || !str.contains("D")
                || !str.contains("TOP"))
        {
            stateInvalidInput();
        }
    }
    public void stateInvalidInput()
    {
        System.out.println("Invaid Input!");
        printHelp();
    }
    //////////////////////CRYPTOCOMPARE//////////////////////
    public void reqPrice(String s)
    {
        WebTarget target = client.target("https://min-api.cryptocompare.com/data/price?fsym=" + s + "&tsyms=USD");
        //        //Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        String str = target.request(MediaType.TEXT_XML).get(String.class);
        System.out.print(s.toUpperCase() + " Current Market Price: $");
        JSONObject obj = new JSONObject(str);
        double currentPrice = obj.getInt("USD");
        System.out.print(currentPrice);
    }
    //////////////////////PIHOLE//////////////////////
    private void piOn() {
        String auth = "32ec97f47624dd732948a778cd78df18673ebd196a87d2b32c09fc973a4f1239";
        WebTarget target = client.target("http://"+networkFinal+"/admin/api.php?enable&auth=" + auth);
        String result = target.request(MediaType.TEXT_XML).get(String.class);
        System.out.println(result);
    }
    private void piOff(int time)
    {
        String auth = "32ec97f47624dd732948a778cd78df18673ebd196a87d2b32c09fc973a4f1239";
        WebTarget target = client.target("http://"+networkFinal+"/admin/api.php?disable=" + time + "&auth=" + auth);
        String result = target.request(MediaType.TEXT_XML).get(String.class);
        System.out.println(result + " for " + time + " Seconds");
    }
    public void requestPiHoleStats()
    {
        WebTarget target = client.target("http://"+networkFinal+"/admin/api.php");
        String result = target.request(MediaType.TEXT_XML).get(String.class);
        JSONObject obj = new JSONObject(result);
        int domains_being_blocked = obj.getInt("domains_being_blocked");
        int dns_queries_today = obj.getInt("dns_queries_today");
        int ads_blocked_today = obj.getInt("ads_blocked_today");
        float ads_percentage_today = obj.getInt("ads_percentage_today");
        int unique_domains = obj.getInt("unique_domains");
        int queries_forwarded = obj.getInt("queries_forwarded");
        int queries_cached = obj.getInt("queries_cached");
        int clients_ever_seen = obj.getInt("clients_ever_seen");
        int unique_clients = obj.getInt("unique_clients");
        String status = obj.getString("status");
        System.out.println("Domains Being Blocked: " + domains_being_blocked);
        System.out.println("DNS Queries Today: " + dns_queries_today);
        System.out.println("Ads Blocked Today: " + ads_blocked_today);
        System.out.println("Ads Percentage Today: " + ads_percentage_today);
        System.out.println("Unique Domains: " + unique_domains);
        System.out.println("Queries Forwarded: " + queries_forwarded);
        System.out.println("Quieries Cached: " + queries_cached);
        System.out.println("Clients Ever Seen: " + clients_ever_seen);
        System.out.println("Unique Clients: " + unique_clients);
        System.out.println("PiHole Status: " + status);
    }
    private void topItems()
    {
        String auth = "32ec97f47624dd732948a778cd78df18673ebd196a87d2b32c09fc973a4f1239";
        WebTarget target = client.target("http://"+networkFinal+"/admin/api.php?topItems=10&auth=" + auth);
        String result = target.request(MediaType.TEXT_XML).get(String.class);
        String[] items = result.split(",");
        for(int i = 0; i< items.length;i++)
        {
            System.out.println(items[i]);
        }
    }
    //////////////////////NANOPOOL////////////////////// API GETTING 404
    private void approxEarn(Integer hashRate) {
        WebTarget target = client.target("https://api.nanopool.org/v1/eth/approximated_earnings/" + hashRate);
        String result = target.request(MediaType.TEXT_XML).get(String.class);
        System.out.println(result);
    }
}
