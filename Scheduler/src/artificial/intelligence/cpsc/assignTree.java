package artificial.intelligence.cpsc;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class assignTree {
	private Parser parser;
	private Map<Classes,TimeSlot> assignNode;
	private static float min = Float.MAX_VALUE;
	private Map<Classes,TimeSlot> result = null;
	private ArrayList<Classes> availableCourses = new ArrayList<Classes>();
	private TimeSlot dollarSign = new TimeSlot();
	
	public assignTree(Parser p, Map<Classes,TimeSlot> aTree)
	{
		dollarSign.setDollarSign(true);
		parser = p;
		assignNode = aTree;
		for(Map.Entry<Classes,TimeSlot> entry: assignNode.entrySet()){
			if(entry.getValue().isDollarSign()){
				availableCourses.add(entry.getKey());
			}
		}
	}
	
	//getter functions for all above
	public Map<Classes,TimeSlot> getAssign(){return assignNode;}
	public float getMin(){return min;}
	public ArrayList<Classes> getCourses(){return availableCourses;}
	
	//setter
	public void setMin(float givenMin){min = givenMin;}
	
	/**
	 * Function that creates an And Tree and returns the best possible solution.
	 * 
	 * @return Map<Classes,TimeSlot> that corresponds to the best solution, or null if no solution was found.
	 */
	public Map<Classes,TimeSlot> createTree()
	{
		new SlotAssign(null,null,this);
		return result;
	}
	
	/**
	 * Function that takes the current assignNode mapping and copies it into result
	 */
	public void copyResult()
	{
		Map<Classes, TimeSlot> copy = new HashMap<Classes, TimeSlot>();
		for(Map.Entry<Classes, TimeSlot> entry : assignNode.entrySet())
		{
			copy.put(entry.getKey(), entry.getValue());
		}
		
		result = copy;
	}
	
	/**
	 * Function that takes in a class and timeslot and evaluates the current assignment
	 * with the added key.
	 * 
	 * @param course: the course being added
	 * @param time: the timeslot of the course being added
	 * @return evaluation, where evaluation < 0 if hard constraints fail or receives a value worse than the min
	 */
	public float evaluateThis(Classes course, TimeSlot time)
	{
		float evaluation = -1;
		assignNode.put(course, time);
		//System.out.println("Evaluating:" +course.toString()+" In Slot: "+time.toString());
		
		legalCheck lc = new legalCheck(assignNode);
		if(!lc.doAllChecks(parser.getCourses(),parser.getNonCompatible(),parser.getUnwanted())){
			//System.out.println("It failed\n");
			assignNode.put(course, dollarSign);
			return -1;
		}
		evaluation = ;
		if(evaluation > min)
		{
			evaluation = -1;
		}
		//System.out.println("It passed\n");
		assignNode.put(course,dollarSign);
		return evaluation;
	}
	
	/**
	 * function that evaluates the finished assignment. only called when tree has assigned all courses
	 * Only soft constraints need to be tested as if a hard constraint was broken, it should have been caught in the
	 * previous node.
	 * 
	 * @return evaluation, should not return -1 when this is called
	 */
	public float evaluateCurr()
	{
		float evaluation = -1;
		//evaluation = 1;
		//return evaluation;
		
		
		
//		float classMins = eval.minCheck(getCourseSlots(), Parser.getLabSlots());
//		float prefPen = eval.preferenceCheck(Parser.getPreferences());
//		float pairPen = eval.pairCheck(Parser.getPairs());
//		float coursePairs = eval.sectionCourseCheck(Parser.getCourseSections());
//		float labPairs = eval.sectionLabCheck(Parser.getLabSections());
//		evaluation = classMins + prefPen + pairPen + coursePairs + labPairs;
		return evaluation;
		 
	}
	
	/**
	 * Assigns a course and a time to the tree
	 * 
	 * @param course: course being assigned
	 * @param time: timeslot of the course being assigned
	 */
	public void assignThis(Classes course, TimeSlot time)
	{
		assignNode.put(course, time);
	}
		
	/**
	 * Removes a course from the tree
	 * 
	 * @param course: course being removed from the mapping
	 */
	public void removeThis(Classes course)
	{
		assignNode.put(course,dollarSign);
	}
	
	/**
	 * Function that takes in a class and returns a list of possible timeslots it can
	 * go into
	 * 
	 * @param aClass: the class we want timeslots for
	 * @return ArrayList<TimeSlot> of times
	 */
	public ArrayList<TimeSlot> getTimes(Classes aClass)
	{
		ArrayList<TimeSlot> times = new ArrayList<TimeSlot>();
		if(aClass instanceof Course)
		{
			ArrayList<CourseSlot> cTimes = parser.getCourseSlots();
			times.addAll(cTimes);
			return times;
		}
		else if(aClass instanceof Lab)
		{
			ArrayList<LabSlot> lTimes = parser.getLabSlots();
			times.addAll(lTimes);
			return times;
		}
		else return null;
	}
}
