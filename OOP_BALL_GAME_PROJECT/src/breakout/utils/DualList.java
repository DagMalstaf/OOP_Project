package breakout.utils;

import java.util.ArrayList;
/**
 * 
 *
 * @invar| getList1() != null
 * @invar| getList1().stream().allMatch(e-> e!= null)
 * @invar| getList2() != null
 * @invar| getList2().stream().allMatch(e-> e!= null)
 */
 
public class DualList <T,S> {
	/**
	 * @invar| list1 != null
	 * @invar| list1.stream().allMatch(e-> e!= null)
	 */
	private ArrayList<T> list1;
	/**
	 * @invar| list2 != null
	 * @invar| list2.stream().allMatch(e-> e!= null)
	 */
	private ArrayList<S> list2;
	/**
	 * 
	 * @pre| list1 != null && list2 != null
	 * @pre| list1.stream().allMatch(e-> e!= null)
	 * @pre| list2.stream().allMatch(e-> e!= null)
	 * 
	 */
	public DualList(ArrayList<T> list1, ArrayList<S> list2) {
		this.list1 = list1;
		this.list2 = list2;
	}
	/**
	 * @inspects| this
	 * @return
	 */
	public ArrayList<T> getList1(){
		return this.list1;
	}
	/**
	 * @inspects| this
	 * @return
	 */
	public ArrayList<S> getList2(){
		return this.list2;
	}
	/**
	 * @post| result == 2
	 */
	public int size() {
		return 2;
	}
}

