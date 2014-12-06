package edu.asu.mwdb.epidemics.lsh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Layer {
	
	private List<Integer> indexPositionList;
	private Map<String,List<String>> similarFileBucketMap;
	
	public Layer() {}
	
	public Layer(List<Integer> positionList, Map<String, int[]> globalBitVectorMap) {
		createHashFamilies(positionList, globalBitVectorMap);
	}
	
	public List<Integer> getIndexPosition() {
		return indexPositionList;
	}
	public void setIndexPositionList(List<Integer> indexPosition) {
		this.indexPositionList = indexPosition;
	}
	public Map<String, List<String>> getSimilarFileBucketMap() {
		return similarFileBucketMap;
	}
	public void setSimilarFileBucketMap(
			Map<String, List<String>> similarFileBucketMap) {
		this.similarFileBucketMap = similarFileBucketMap;
	}
	
	//Function to get size of layer considering the integer of 2 bytes and char of 1 byte.
	public long getSize(){
		long layerSize = 0;
		layerSize += this.indexPositionList.size() * 2;
		layerSize += this.getSimilarFileBucketMap().keySet().size() * this.getIndexPosition().size();
		Collection<List<String>> valueList = this.getSimilarFileBucketMap().values();
		for(List<String> l: valueList) {
			layerSize += l.size() * 10;
		}
		return layerSize;
	}
	
	private void createHashFamilies(List<Integer> positionList, Map<String, int[]> globalBitVectorMap) {
		this.setIndexPositionList(positionList);
		
		Map<String,List<String>> bucketMap  = new HashMap<>();
		StringBuffer hashCode = new StringBuffer();
		
		for(Entry<String, int[]> entry : globalBitVectorMap.entrySet()) {
			String fileName = entry.getKey();
			int[] bitVector = entry.getValue();
			
			for(int pos : positionList) {
				hashCode.append(bitVector[pos]);
			}
			
			if(bucketMap.containsKey(hashCode.toString())){
				bucketMap.get(hashCode.toString()).add(fileName);
			} else {
				List<String> fileNameList = new ArrayList<>();
				fileNameList.add(fileName);
				bucketMap.put(hashCode.toString(), fileNameList);
			}
			hashCode.setLength(0);
		}
		
		this.setSimilarFileBucketMap(bucketMap);
	}
	
	private boolean compareHashCode(String hCode1, String hCode2, int threshold) {		
		int i,hammingDistance = 0;
		
		for(i=0 ; i<hCode1.length(); i++) {
			if(hCode1.charAt(i) != hCode2.charAt(i)) {
				hammingDistance++;
				if(hammingDistance > threshold) {
					return false;
				}
			}
		}
		return true;
	}

	public int getSimilarFilesNames(int[] queryBitVector, int t, Set<String> similarFileResultSet,int threshold, int accessedIndex, List<String> results) {
		List<Integer> indexPosition = this.getIndexPosition();
		Map<String,List<String>> similarFileBucketMap = this.getSimilarFileBucketMap();
		
		StringBuffer queryHashCode = new StringBuffer();
		
		for(int index : indexPosition) {
			queryHashCode.append(queryBitVector[index]);
		}
		
		if(threshold == 0) {
			if(similarFileBucketMap.containsKey(queryHashCode.toString())) {
				for(String str : similarFileBucketMap.get(queryHashCode.toString())) {
					if(similarFileResultSet.size() < t) {
						results.add(str);
						similarFileResultSet.add(str);
					} else {
						accessedIndex++;
						break;
					}
				}
			}
		} else {
			for(Entry<String, List<String>> entry : similarFileBucketMap.entrySet()) {
				String key = entry.getKey();
				if(compareHashCode(key, queryHashCode.toString(), threshold)) {
					accessedIndex++;
					for(String str : entry.getValue()) {
						if(similarFileResultSet.size() < t) {
							results.add(str);
							similarFileResultSet.add(str);
						} else {
							break;
						}
					}
				}
			}
		}
		return accessedIndex;
	}
}
