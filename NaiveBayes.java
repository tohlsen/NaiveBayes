// Do not submit with package statements if you are using eclipse.
// Only use what is provided in the standard libraries.

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class NaiveBayes {
    
    /*	****NAIVE BAYES CLASSIFIER****
     * CSE 312
     * Winter 2018
     * Tyler Ohlsen (tlohlsen)
     * Last Edited: 01.28.2018
     * 
     * Program description:
     *  Takes in a large database of training files,
     *  and, using the Naive Baye's Classifier Algorithm, learns 
     *  whether a certain email is ham or spam base on the words contained
     *  in the email
     */
    
    public static void main(String[] args) throws IOException {
    	//getting arg from command line:
    	String dir = args[0];

    	//used to store all words in all emails
    	HashSet<String> words = new HashSet<String>();
    	
    	//used to store the words for spam & ham emails
    	HashSet<String> spamWords = new HashSet<String>();
    	HashSet<String> hamWords = new HashSet<String>();
    	
    	//used to store the counts of all spam & ham words
    	HashMap<String, Integer> spamCounts = new HashMap<String, Integer>();
    	HashMap<String, Integer> hamCounts = new HashMap<String, Integer>();
    	
    	//used to store the probabilities of all spam & ham words
    	HashMap<String, Double> spamProbs = new HashMap<String, Double>();
    	HashMap<String, Double> hamProbs = new HashMap<String, Double>();
    	
    	//creating files for ham, spam & test emails (and an array of the files found in their directories)
    	File spam = new File(dir + "\train\spam");
    	File ham = new File(dir + "\train\ham");
    	File test = new File(dir + "\test");
    	File[] spamEmails = spam.listFiles();
    	File[] hamEmails = ham.listFiles();
    	File[] testEmails = test.listFiles();
    	
    	//computing P(S):
    	double probSpam = (double) (spamEmails.length) / (spamEmails.length + hamEmails.length);
    	//computing P(H):
    	double probHam = (double) (hamEmails.length) / (spamEmails.length + hamEmails.length);
    	
    	/*
    	 * For each spam email:
    	 * 	- collect all the words within it
    	 * 	- for each word:
    	 * 		- increase the count of that word being found in a spam email,
    	 * 		  or add it & give it a count of 1
    	 */
    	for (File email : spamEmails) {
    		HashSet<String> spamEmailWords = tokenSet(email);
    		spamWords.addAll(spamEmailWords);
    		
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
    	/*
    	 * For each ham email:
    	 * 	- collect all the words within it
    	 * 	- for each word:
    	 * 		- increase the count of that word being found in a ham email,
    	 * 		  or add it & give it a count of 1
    	 */
    	for (File email : hamEmails) {
    		HashSet<String> hamEmailWords = tokenSet(email);
    		hamWords.addAll(hamEmailWords);
    		
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
    	
    	//adding both subsets of words to all the words
    	words.addAll(spamWords);
    	words.addAll(hamWords);
    	
    	//used to hold the probabilities of each word being in a spam or ham email
    	double spamProb, hamProb;
    	/*
    	 * for each word in all of the emails:
    	 * 	- calculate the probability of it being found in a spam or ham email
    	 * 	- if there's no count of that word: set count to 0
    	 * 	- add the word and its corresponding ham & spam probabilities to the hashmaps
    	 */
    	for (String word : words) {
    		
    		//searching for word in spamCounts:
    		if (spamCounts.containsKey(word)) {
    			spamProb = (double) (spamCounts.get(word) + 1) / (spamEmails.length + 2);
    		}
    		//if not found: set count to 0
    		else {
    			spamProb = (double) 1 / (spamEmails.length + 2);
    		}
    		
    		//searching for word in hamCounts:
    		if (hamCounts.containsKey(word)) {
    			hamProb = (double) (hamCounts.get(word) + 1) / (hamEmails.length + 2);
    		}
    		//if not found: set count to 0
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
    	 * NOTE: we use log in these calculations to prevent floating point underflow
    	 */
    	//used for keeping the running total of probabilities of each word
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
    		if ((Math.log(probSpam) + spamSum) > (Math.log(probHam) + hamSum)) { //calculation from notes on Naive Baye's algorithm
    			System.out.println(email.getName() + " spam");	//printing spam if p(S | x_1...x_n) > 0.5
    		}
    		else {
    			System.out.println(email.getName() + " ham");	//printing ham if p(H | x_1...x_n) > 0.5
    		}
    	}
    	
    }
    
    //given
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
