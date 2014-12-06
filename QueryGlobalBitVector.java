package edu.asu.mwdb.epidemics.lsh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.asu.mwdb.epidemics.domain.Window;
import edu.asu.mwdb.epidemics.domain.Word;

public class QueryGlobalBitVector {
	
	//Map containing filename as key and binary array of unique window size as value.
	private int[] bitVectorForQueryFile = null;
	//Map containing filename as key and count array of unique window size as value.
	private int[] countVectorForQueryFile = null;
	
	public QueryGlobalBitVector() {}
	
	public QueryGlobalBitVector(Set<Window> uniqueWindows, String queryFileName) {
		init(uniqueWindows,queryFileName);
	}
	
	public int[] getBitVectorForQueryFile() {
		return bitVectorForQueryFile;
	}

	public void setBitVectorForQueryFile(int[] bitVectorForQueryFile) {
		this.bitVectorForQueryFile = bitVectorForQueryFile;
	}

	public int[] getCountVectorForQueryFile() {
		return countVectorForQueryFile;
	}

	public void setCountVectorForQueryFile(int[] countVectorForQueryFile) {
		this.countVectorForQueryFile = countVectorForQueryFile;
	}

	private final void init(Set<Window> uniqueWindows, String queryFileName) {
		String line = "";
		BufferedReader bufReader = null;
		int [] binaryVectorForQuery = new int[uniqueWindows.size()];
		int [] countVectorForQuery = new int[uniqueWindows.size()];
		Map<Window,Integer> wordOccuranceMap = new HashMap<>();
		
		try {
			bufReader = new BufferedReader(new FileReader(new File(queryFileName)));
			//Creating map of unique words present in query word file.
			while((line = bufReader.readLine()) != null) {
				Word word = new Word(line);
			
				if (wordOccuranceMap.containsKey(word.getWindow())) {
					int count = wordOccuranceMap.get(word.getWindow());
					wordOccuranceMap.put(word.getWindow(), count + 1);
				} else {
					wordOccuranceMap.put(word.getWindow(),1);
				}
			}
			bufReader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		int index =0;
		//Creating binary and count vectors of query file in terms of unique window map created with index structure.
		for(Window window: uniqueWindows) {
			if(wordOccuranceMap.containsKey(window)) {
				binaryVectorForQuery[index] = 1;
				countVectorForQuery[index] = wordOccuranceMap.get(window);
			} else {
				binaryVectorForQuery[index] = 0;
				countVectorForQuery[index] = 0;
			}
			index++;
		}

		this.setBitVectorForQueryFile(binaryVectorForQuery);
		this.setCountVectorForQueryFile(countVectorForQuery);
	}

}
