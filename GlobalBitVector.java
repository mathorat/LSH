package edu.asu.mwdb.epidemics.lsh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.asu.mwdb.epidemics.domain.Id;
import edu.asu.mwdb.epidemics.domain.Window;
import edu.asu.mwdb.epidemics.domain.Word;

public class GlobalBitVector {
	
	private Set<Window> uniqueWindows = null;
	//Map containing filename as key and binary array of unique window size as value.
	private Map<String, int[]> bitVectorMapForInputFiles = null;
	//Map containing filename as key and count array of unique window size as value.
	private Map<String, int[]> countVectorMapForInputFiles = null;

	public Map<String, int[]> getCountVectorMapForInputFiles() {
		return countVectorMapForInputFiles;
	}

	public void setCountVectorMapForInputFiles(
			Map<String, int[]> countVectorMapForInputFiles) {
		this.countVectorMapForInputFiles = countVectorMapForInputFiles;
	}

	public GlobalBitVector(){}
	
	public GlobalBitVector(String wordFileName) {
		init(wordFileName);
	}

	public Set<Window> getUniqueWindows() {
		return uniqueWindows;
	}

	public void setUniqueWindows(Set<Window> uniqueWindows) {
		this.uniqueWindows = uniqueWindows;
	}

	public Map<String, int[]> getBitVectorMapForInputFiles() {
		return bitVectorMapForInputFiles;
	}

	public void setBitVectorMapForInputFiles(
			Map<String, int[]> bitVectorMapForInputFiles) {
		this.bitVectorMapForInputFiles = bitVectorMapForInputFiles;
	}

	//Method for creating the binary vector for each input file.
	private int[] createBinaryVectorForFile(String fileName,Map<Window,List<Id>> wordOccuranceMap,Set<Window> uniqueWindows) {
		int[] bitVector = new int[uniqueWindows.size()];
		int i = 0;
		for(Window w : uniqueWindows) {
			List<Id> idList = wordOccuranceMap.get(w);
			for(Id id : idList) {
				if(fileName.equals(id.getFileName())) {
					bitVector[i] = 1;
					break;
				}
			}
			i++;
		}
		return bitVector;
	}
	
	//Method for creating the count vector for each input file.
	private int[] createCountVectorForFile(String fileName,Map<Window,List<Id>> wordOccuranceMap,Set<Window> uniqueWindows) {
		int[] bitVector = new int[uniqueWindows.size()];
		int i = 0,count = 1;
		
		for(Window w : uniqueWindows) {
			List<Id> idList = wordOccuranceMap.get(w);
			for(Id id : idList) {
				if(fileName.equals(id.getFileName())) {
					bitVector[i] = count++;
				}
			}
			count = 1;
			i++;
		}
		return bitVector;
	}
	
	//Init method which is called from constructor to populate required data structures. 
	private final void init(String wordFileName) {
		Set<Window> uniqueWindows = new LinkedHashSet<Window>();
		Set<String> uniqueFileNames = new LinkedHashSet<>();
		Map<Window, List<Id>> wordOccuranceMap = new LinkedHashMap<Window, List<Id>>();
		Map<String, int[]> binaryVectorMapForInputFiles = new HashMap<>();
		Map<String, int[]> countVectorMapForInputFiles = new HashMap<>();
		String line = "";
		
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader(new File(wordFileName)));
		
			//creating unique window set and map required for creating bitvector for each file.
			while((line = bufReader.readLine()) != null) {
				Word word = new Word(line);
			
				uniqueWindows.add(word.getWindow());
				uniqueFileNames.add(word.getId().getFileName());
			
				if (wordOccuranceMap.containsKey(word.getWindow())) {
					wordOccuranceMap.get(word.getWindow()).add(word.getId());
				} else {
					List<Id> occuranceList = new ArrayList<Id>();
					occuranceList.add(word.getId());
					wordOccuranceMap.put(word.getWindow(),occuranceList);
				}
			}
			bufReader.close();
			
			//creating bit vector map for input files.
			for(String fileName : uniqueFileNames) {
				int[] binaryVectorForFile = createBinaryVectorForFile(fileName,wordOccuranceMap,uniqueWindows);
				binaryVectorMapForInputFiles.put(fileName, binaryVectorForFile);
				
				int[] countVectorForFile = createCountVectorForFile(fileName,wordOccuranceMap,uniqueWindows);
				countVectorMapForInputFiles.put(fileName, countVectorForFile);
				
			}
		
			//Setting the values for unique window set and vector map for input files.
			this.setUniqueWindows(uniqueWindows);
			this.setBitVectorMapForInputFiles(binaryVectorMapForInputFiles);
			this.setCountVectorMapForInputFiles(countVectorMapForInputFiles);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
