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
    	HashMap<String, Double> spamProbs = new HashMap<String, Double>();
    	HashMap<String, Double> hamProbs = new HashMap<String, Double>();
    	
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
    	
    	//getting all words in spam emails
    	for (File email : spamEmails) {
    		words.addAll(tokenSet(email)); //adding words from spam emails into HashSet
    	}
    	//getting all words in ham emails
    	for (File email : hamEmails) {
    		words.addAll(tokenSet(email)); //adding words from ham emails into HashSet
    	}

    	//try to fit the lower part of this up ^^^^^^^^^ here!
    	/*
    	 * --computing P(w | S) & P(w | H)--
    	 * for each word:
    	 * 	-count the number of spam/ham emails that word is seen in
    	 *	-calculate that individual P(w | S) & P(w | H)
    	 *	-add it to both hashmaps that keep all probabilities
    	 *
    	 *NOTE : REEEEAAAAAAAAAAAAAAAAAAAAAALLLLLLLLLLYYYYYYYYYYYYYYYYYYY   SSSSSSSLLLLLLLLOOOOOOOOOOOWWWWWWWWWWWWWWWW
    	 */
    	
    	//used to keep track of the number of emails each word is seen in (for spam & ham emails)
    	int spamCount, hamCount;
    	
    	for (String word : words) {		
    		//resetting counts to 0
    		spamCount = 0;
    		hamCount = 0;
    		//getting # of spam emails this word is seen in
    		for (File email : spamEmails) {
    			HashSet<String> spamEmailWords = tokenSet(email); //potentially unnecessary line (just put in if statement)
    			if (spamEmailWords.contains(word)) {
    				spamCount++;
    			}
    		}
    		//getting # of ham emails this word is seen in
    		for (File email : hamEmails) {
    			HashSet<String> hamEmailWords = tokenSet(email); //potentially unnecessary line (just put in if statement)
    			if (hamEmailWords.contains(word)) {
    				hamCount++;
    			}
    		}
    		System.out.println("spam count:" + spamCount);
    		System.out.println("ham count:" + hamCount);
    		//calculating individual word probability, adding to total set
    		double spamProb = (double) (spamCount + 1) / (spamEmails.length + 2);
    		double hamProb = (double) (hamCount + 1) / (hamEmails.length + 2);
    		
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
