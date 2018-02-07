/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * HistoryVector class
 * 		- extension of Vector to make a simple maximum size
 * 		- if max size reached - last element in vector is removed
*/

package Monopoly.dataStrucs;

import java.util.Vector;

public class HistoryVector<T> extends Vector<T> {
	private final int MAX_SIZE;

	public HistoryVector(int size){
		MAX_SIZE = size;
	}

	@Override
	public boolean add(T toAdd){
		if(size() == MAX_SIZE){
			remove(size()-1);
		}
		super.add(0,toAdd);
		return true;
	}
}

