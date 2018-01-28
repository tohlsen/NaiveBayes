// Do not submit with package statements if you are using eclipse.
// Only use what is provided in the standard libraries.

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class NaiveBayes {
    
    /*
     *  This function reads in a file and returns a 
     *  set of all the tokens. It ignores the subject line
     *  
     *  If the email had the following content:
     *  
     *  Subject: Get rid of your student loans
     *  Hi there ,
     *  If you work for us, we will give you money
     *  to repay your student loans . You will be 
     *  debt free !
     *  FakePerson_22393
     *  
     *  This function would return to you
     *  [hi, be, student, for, your, rid, we, get, of,
     *  free, if, you, us, give, !, repay, will, loans,
     *  work, fakeperson_22393, ,, ., money, there, to, debt]
     */
    
    public static void main(String[] args) throws IOException {
        //test
    	//used to store all words in all emails
    	HashSet<String> words = new HashSet<String>();
    	HashSet<String> spamWords = new HashSet<String>();
    	HashSet<String> hamWords = new HashSet<String>();
    	HashMap<String, Double> spamProbs = new HashMap<String, Double>();
    	HashMap<String, Double> hamProbs = new HashMap<String, Double>();
    	HashMap<String, Integer> spamCounts = new HashMap<String, Integer>();
    	HashMap<String, Integer> hamCounts = new HashMap<String, Integer>();
    	
    	//creating files for ham, spam & test emails
    	File spam = new File("data/train/spam");
    	File ham = new File("data/train/ham");
    	File test = new File("data/test");
    	File[] spamEmails = spam.listFiles();
    	File[] hamEmails = ham.listFiles();
    	File[] testEmails = test.listFiles();
    	
    	//computing P(S):
    	double probSpam = (double) (spamEmails.length) / (spamEmails.length + hamEmails.length);
    	//computing P(H):
    	double probHam = (double) (hamEmails.length) / (spamEmails.length + hamEmails.length);
    	
    	
    	//getting all words in spam emails (and getting the word counts)
    	for (File email : spamEmails) {
    		HashSet<String> spamEmailWords = tokenSet(email);
    		spamWords.addAll(spamEmailWords); //adding all spam words into general words
    		//for each word in the entire words thus far:
    		//if already exists in spamCounts: increase count by 1
    		//else: put a new one, w/ count of 1
    		for (String word : spamWords) {
    			if (spamCounts.containsKey(word)) {
    				int oldCount = spamCounts.get(word);
    				spamCounts.put(word, ++oldCount);
    			}
    			else {
    				spamCounts.put(word, 1);
    			}
    		}
    		
    	}
    	//getting all words in ham emails
    	for (File email : hamEmails) {
    		HashSet<String> hamEmailWords = tokenSet(email);
    		hamWords.addAll(hamEmailWords); //adding all spam words into general words
    		//for each word in the entire words thus far:
    		//if already exists in hamCounts: increase count by 1
    		//else: put a new one, w/ count of 1
    		for (String word : hamWords) {
    			if (hamCounts.containsKey(word)) {
    				int oldCount = hamCounts.get(word);
    				hamCounts.put(word, ++oldCount);
    			}
    			else {
    				hamCounts.put(word, 1);
    			}
    		}
    	}
    	
    	words.addAll(spamWords);
    	words.addAll(hamWords);
    	
    	double spamProb, hamProb;
    	//now that we have counts, find probability of each spam word:
    	for (String word : words) {
    		//calculating the probabilities for each word
    		if (spamCounts.containsKey(word)) {
    			spamProb = (double) (spamCounts.get(word) + 1) / (spamEmails.length + 2);
    		}
    		else {
    			spamProb = (double) 1 / (spamEmails.length + 2);
    		}
    		if (hamCounts.containsKey(word)) {
    			hamProb = (double) (hamCounts.get(word) + 1) / (hamEmails.length + 2);
    		}
    		else {
    			hamProb = (double) 1 / (hamEmails.length + 2);
    		}
    		
    		//adding word and its respective probabilities to both HashMaps
    		spamProbs.put(word, spamProb);
    		hamProbs.put(word, hamProb);
    	}
    		
    	
    	
    	/*
    	 * for each test email:
    	 * 	- for each word in the test email:
    	 * 		- if its an analyzed word, add its probability to the running totals of each probability
    	 * 		- whichever probability is greater: print spam or ham
    	 */
    	//used for 
    	double hamSum, spamSum;
    	
    	for (File email : testEmails) {
    		//resetting their counts after each email
    		hamSum = 0.0;
    		spamSum = 0.0;
    		
    		HashSet<String> testEmailWords = tokenSet(email);
    		for (String word : testEmailWords) {
    			if (words.contains(word)) {
    				spamSum += Math.log(spamProbs.get(word)); 	//adding to the running total of spam probs
    				hamSum += Math.log(hamProbs.get(word));		//adding to the running total of ham probs
    			}
    		}
    		if ((Math.log(probSpam) + spamSum) > (Math.log(probHam) + hamSum)) { //calculation from notes
    			System.out.println(email + " spam");
    		}
    		else {
    			System.out.println(email + " ham");
    		}
    	}
    	
    }
    
    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
