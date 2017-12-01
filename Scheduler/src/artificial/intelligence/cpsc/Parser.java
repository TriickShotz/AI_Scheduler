package artificial.intelligence.cpsc;

import java.io.*;
import java.util.ArrayList;

//push Testing...

public class Parser {
	
	String line = null;
	
	private ArrayList<CourseSlot> courseSlots = new ArrayList<CourseSlot>();
	private ArrayList<LabSlot> labSlots = new ArrayList<LabSlot>();
	private ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
	
	private ArrayList<Course> courses = new ArrayList<Course>();
	private ArrayList<Lab> labs = new ArrayList<Lab>();

	private ArrayList<pair<Classes,Classes>> pairs = new ArrayList<pair<Classes,Classes>>();
	private ArrayList<pair<Classes,Classes>> nonCompatible = new ArrayList<pair<Classes,Classes>>();
	private ArrayList<pair<Classes,TimeSlot>> unWanted = new ArrayList<pair<Classes,TimeSlot>>();
	private ArrayList<preferenceTriple> preferences = new ArrayList<preferenceTriple>();
	private ArrayList<pair<Classes,TimeSlot>> partialAssignment = new ArrayList<pair<Classes,TimeSlot>>();
	
	private final String[] headers = {
			"Course slots:\n" + 
			"Lab slots:\n" + 
			"Courses:\n" + 
			"Labs:\n" + 
			"Not compatible:\n" + 
			"Unwanted:\n" + 
			"Preferences:\n" + 
			"Pair:\n" + 
			"Partial assignments:\n"};
	
	public Parser(String inputFileName){
		try{
			//Read Text File
			FileReader fileReader = new FileReader(inputFileName);
			
			//Wrap FileReader in BufferedReader
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			//Print first block of input file, expected to be of the form:
			//Name:
			//EXAMPLENAME
			//
			while(!(line = bufferedReader.readLine()).isEmpty()){
				System.out.println(line);
			}
			
			
			
			//Course Slots
			/**
			 * Parse through the block of text expected to be CourseSlots, dividing each line up into 
			 * a new CourseSlot object
			 */
			//First cut through any empty lines
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			System.out.println("\n" + line);
			
			while(!(line = bufferedReader.readLine()).isEmpty()){
				parseCourseSlot(line);				
			}
			
			
			
			//Lab Slots
			/**
			 * Parse through the block of text expected to be Lab slots, dividing each line up into 
			 * a new LabSlot object
			 */
			//First cut through any empty lines
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			System.out.println("\n" + line);
			
			while(!(line = bufferedReader.readLine()).isEmpty()){
				parseLabSlot(line);
			}
			
			
			timeSlots.addAll(courseSlots);
			timeSlots.addAll(labSlots);
			
			//Courses
			//Much the same as above
			while((line = bufferedReader.readLine()).isEmpty()) {
				System.out.println("EMPTY");
			}
			System.out.println("\n" + line);
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parseCourse(line);
			}
			
			//Labs
			//Rinsing and repeating; nothing new here
			while((line = bufferedReader.readLine()).isEmpty()) {
				System.out.println("EMPTY");
			}
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parseLab(line);
			}
			
			
			//Not Compatible
			//This one necessitates the lookUpLab function too
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parseNonCompatible(line);
			}
			
			
			//Unwanted
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parseUnwanted(line);
			}
			
			//Preference
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parsePreferences(line);
			}
			
			
			//Pair
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			while(!(line = bufferedReader.readLine()).isEmpty()) {
				parsePairs(line);
			}
			
			
			//Partial Assignments
			while((line = bufferedReader.readLine()).isEmpty()){
				System.out.println("EMPTY");
			}
			while((line = bufferedReader.readLine()) != null){
				parsePartialAssignments(line);
			}
			
			
			//Close file
			bufferedReader.close();
			
		} catch (FileNotFoundException e){
			System.out.println("Unable to open file '" + inputFileName + "'");
		} catch (IOException e){
			System.out.println("Error reading file '" + inputFileName + "'");
		}
	}
	/**
	 * TODO generally worried that the pulling of a thing from the ArrayList won't create a true ref to that object, might need to check into more about this and test what the effects of it are. Errors might be hard to find in the general workings of the parser
	 * 
	 * this one functions almost exactly the same as parseUnwanted, save where
	 * it saves the final result. DOes the general song and dance of cleaning up the line,
	 * and looking up the appropriate lecture/lab/slot... with the appropriate function
	 * and created the final product out of the returned results. 
	 * @param line2 Ideally of the form "CLASSINFO, DAY, TIME"
	 */
	private void parsePartialAssignments(String line2) {
		pair<Classes,TimeSlot> partAssignLine;
		Classes partClass;
		TimeSlot partSlot;
		
		
		
		String[] partAssignInfo = line2.split(",\\s*");
		String[] classInfo = partAssignInfo[0].split(" +");
		String[] dayInfo = new String[2];
		
		dayInfo[0] = partAssignInfo[1];
		dayInfo[1] = partAssignInfo[2];
		
		dayInfo = clearWhiteSpace(dayInfo);
		classInfo = clearWhiteSpace(classInfo);
		
		if(classInfo[classInfo.length-1].equals("LEC")){
			System.out.println("This is a lecture");
			//its a lecture
			partClass = lookUpCourse(classInfo);
			partSlot = lookUpCourseSlot(dayInfo);

		}else{
			System.out.println("This is a lab");
			//its a lab
			partClass = lookUpLab(classInfo);
			partSlot = lookUpLabSlot(dayInfo);

		}

		System.out.println(partSlot.toString());
		System.out.println(partClass.toString());
		
		partAssignLine = new pair<Classes,TimeSlot>(partClass,partSlot);
		
		partialAssignment.add(partAssignLine);
	}
	/**
	 * Function to take a string beneath the Pairs: \n header
	 * Splits the string into the two chunks of class1 and class2, and cleans those up
	 * and then looks them up, depending on if they are lectures or labs
	 * @param line2 ideally of the form "CLASSONEINFO,CLASSTWOINFO"
	 */
	private void parsePairs(String line2) {
		pair<Classes,Classes> pairPair; //dumb name
		
		String[] pairInfo = line2.split(",\\s*");
		String[] firstClass = pairInfo[0].split(" +");
		String[] secondClass = pairInfo[1].split(" +");
		
		firstClass = clearWhiteSpace(firstClass);
		secondClass = clearWhiteSpace(secondClass);
		if(firstClass[firstClass.length-2].equals("LEC")){
			if(secondClass[secondClass.length-2].equals("LEC")){
				pairPair = new pair<Classes,Classes>(lookUpCourse(firstClass),lookUpCourse(secondClass));
			}else{
				pairPair = new pair<Classes,Classes>(lookUpCourse(firstClass),lookUpLab(secondClass));
			}
		}else{
			if(secondClass[secondClass.length-2].equals("LEC")){
				pairPair = new pair<Classes,Classes>(lookUpLab(firstClass),lookUpCourse(secondClass));
			}else{
				pairPair = new pair<Classes,Classes>(lookUpLab(firstClass),lookUpLab(secondClass));
			}
		}
		pairs.add(pairPair);
	}
	/**
	 * Function that I found I was using a lot so created method. Iterates through
	 * list of strings, removing any whitespace so they are pure useable information
	 * @param stringArray array of strings from the input or somewhere else
	 * @return the array but any whitespace is removed.
	 * 
	 * e.g. {"Hello   ","  there."} becomes
	 * {"Hello","there."}
	 */
	private String[] clearWhiteSpace(String[] stringArray) {
		for(int i =0;i<stringArray.length;i++){
			stringArray[i] = stringArray[i].replace("\\s+", "");
		}
		return stringArray;
	}
	/**
	 * Function which takes the line under Preference header, ideally of the form:
	 * "DAY,TIME, CLASSINFO, PENALTYNUMBER"
	 * splitting them by those ','s and finding the corresponding lab or course slot
	 * and corresponding lab or course, creating a preferenceTriple with those values
	 * @param line2 line found beneath the preference Header, form found above
	 */
	private void parsePreferences(String line2) {
		preferenceTriple preference;
		String[] preferenceInfo = line2.split(",\\s*");
		
		for(String p: preferenceInfo){
			System.out.println(p);
		}
		
		String[] timeSlotInfo = {preferenceInfo[0].replace("\\s+", ""),preferenceInfo[1].replace("\\s+", "")};
		String[] classInfo = preferenceInfo[2].split(" +");
		String penalty = preferenceInfo[3];
		if(classInfo[classInfo.length-2].equals("LEC")){
			System.out.println("This is a lecture.");
			preference = new preferenceTriple(lookUpCourseSlot(timeSlotInfo),lookUpCourse(classInfo),Float.parseFloat(penalty));
		}else{
			System.out.println("This is a lab/Tut.");
			preference = new preferenceTriple(lookUpLabSlot(timeSlotInfo),lookUpLab(classInfo),Float.parseFloat(penalty));
		}
		if(!preference.hasNullEntries()){
			System.out.println("The preference was made correctly");
			preferences.add(preference);
		}else{
			System.out.println("The preferences are not made correctly: slot or class does not exist");
		}
	}
	/**
	 * parse the unwanted section to the input file, following header Unwanted: \n
	 * Split line into the class info, day and start time, and combine the latter two into timeInfo string
	 * and the former into classInfo, looking up each in the appropriate timeSlot and Classes ArrayList
	 * and add the unwantedPair created from the two into the main List
	 * @param line2 found under the header above, ideally of the form: "LECTUREINFORMATION,DAY,STARTTIME"
	 */
	private void parseUnwanted(String line2) {
		pair<Classes,TimeSlot> unwantedPair;
		Classes unwantedClass;
		TimeSlot unwantedTimeSlot;
		
		String[] unwantedInfo = line2.split(",\\s*");
		String[] classInfo = unwantedInfo[0].split(" +");
		String[] dayInfo = new String[2];
		
		
		dayInfo[0] = unwantedInfo[1];
		dayInfo[1] = unwantedInfo[2];
		
		System.out.println("Day ;"+dayInfo[0]+"Time ;"+dayInfo[1]);

		
		dayInfo = clearWhiteSpace(dayInfo);
		classInfo = clearWhiteSpace(classInfo);
		
		System.out.println("Day ;"+dayInfo[0]+"Time ;"+dayInfo[1]);
		
		if(classInfo[classInfo.length-1].equals("LEC")){
			//its a lecture
			unwantedClass = lookUpCourse(classInfo);
			unwantedTimeSlot = lookUpLabSlot(dayInfo);

		}else{
			//its a lab
			unwantedClass = lookUpLab(classInfo);
			unwantedTimeSlot = lookUpCourseSlot(dayInfo);

		}
		unwantedPair = new pair<Classes,TimeSlot>(unwantedClass,unwantedTimeSlot);
		
		unWanted.add(unwantedPair);
	}
	/**
	 * Specific cases for the lookUpSlot general function
	 * TODO see the lookUpSlot todo entry
	 * @param dayInfo
	 * @return
	 */
	private TimeSlot lookUpCourseSlot(String[] dayInfo) {
		for(int i =0; i < courseSlots.size();i++){
			CourseSlot tempSlot = courseSlots.get(i);
			if(tempSlot.day.equals(dayInfo[0]) && (tempSlot.startTime.equals(dayInfo[1]))){
				return courseSlots.get(i);
			}
		}
		//TODO create a handler for null cases for this function
		return null;
	}
	private TimeSlot lookUpLabSlot(String[] dayInfo) {
		for(int i =0; i < labSlots.size();i++){
			LabSlot tempSlot = labSlots.get(i);
			if(tempSlot.day.equals(dayInfo[0]) && (tempSlot.startTime.equals(dayInfo[1]))){
				return labSlots.get(i);
			}
		}
		//TODO create a handler for null cases for this function
		return null;
	}
	/**
	 * Simple method to parse through the ArrayList of Timeslots given
	 * some information in an array of strings
	 * TODO make this a general function to use instead of checking the type of class everytime. Maybe group this and lookUpCourse/Lab together into one main function, taking both the courseInfo and timeInfo in one method?
	 * @param dayInfo an array where 1-DAYSTRING(e.g. 'MO', 'TU'...) 2-TIMESTRING(e.g. 8:00...)
	 * @return the appropriate day from the ArrayList, if it exists
	 */
	private TimeSlot lookUpTimeSlot(String[] dayInfo) {
		for(int i =0; i < timeSlots.size();i++){
			TimeSlot tempSlot = timeSlots.get(i);
			if(tempSlot.day.equals(dayInfo[0]) && (tempSlot.startTime.equals(dayInfo[1]))){
				return timeSlots.get(i);
			}
		}
		return null;
	}
	/**
	 * Function to do the job of parsing lines identified beneath a NonCompatible: header
	 * Will basically split the function into two halves, as identified by the comma. Each half ideally
	 * describes a course or lab. Branching if tree to identify the type of each (assuming the second last 
	 * word in each line is either LEC or LAB or TUT)
	 * Then simply look up the lab or course's object in the appropriate ArrayList, and create a new pair
	 * using those found objects
	 * Doesn't handle the null exceptions yet TODO 
	 * 
	 * @param line2 Line of the form "String of Class","String of second Class" under header NonCompatible:\n
	 */
	private void parseNonCompatible(String line2) {
		String[] pairString;
		pair<Classes,Classes> nonCompat;
		pairString = line2.split(",\\s*");
		String[] firstArg = pairString[0].split(" +");
		String[] secondArg = pairString[1].split(" +");
		
		firstArg = clearWhiteSpace(firstArg);
		
		secondArg = clearWhiteSpace(secondArg);

		if(firstArg[firstArg.length-2].equals("LEC")){
			Course left = lookUpCourse(firstArg);
			if(secondArg[secondArg.length-2].equals("LEC")){
				Course right = lookUpCourse(secondArg);
				nonCompat = new pair<Classes,Classes>(left,right);
			}else{
				Lab right = lookUpLab(secondArg);
				nonCompat = new pair<Classes,Classes>(left,right);
			}
		}else{
			Lab left = lookUpLab(firstArg);
			if(secondArg[secondArg.length-2].equals("LEC")){
				Course right = lookUpCourse(secondArg);
				nonCompat = new pair<Classes,Classes>(left,right);
			}else{
				Lab right = lookUpLab(secondArg);
				nonCompat = new pair<Classes,Classes>(left,right);
			}
		}
		nonCompatible.add(nonCompat);
	}
	
	/**
	 * Function that takes a line that comes from the Labs: block and parses it
	 * into relevant Lab info, like Department, ClassNumber, Section, and 
	 * its parent Course which is looked up with the BASIC FOR NOW lookupCourse function
	 * Will also add the relevant lab to the children of the looked up Course
	 * @param line2 as recognized under the header
	 */
	private void parseLab(String line2) {
		String[] info;
		Lab l;
		
		info = line2.split(" +");
		int numTerms = info.length;
		info = clearWhiteSpace(info);
				
		l = new Lab(info[0],
				info[1],
				info[info.length - 1]);
		String[] courseInfo = new String[4];
		if(numTerms ==4){
			for(int i=0;i<2;i++){
				courseInfo[i] = info[i];
			}
			courseInfo[2] = "LEC";
			courseInfo[3] = "01";
		}else{
			for(int i = 0;i<4;i++){
				courseInfo[i] = info[i];
			}
		}
		if(lookUpCourse(courseInfo) == null){
			System.out.println("Something bad happened: the requested Course described by the lab does not exist, or Rhys is bad at coding.");
		}
		l.setBelongsTo(lookUpCourse(courseInfo));
		lookUpCourse(courseInfo).addLab(l);
		labs.add(l);		
	}
	
	/**
	 * Function iterates through the lab list, and finds class that matches the string of info
	 * given
	 * @param labInfo 0-Department, 1-CourseNumber, 3-CourseSection,5-LabSection
	 * @return
	 */
	private Lab lookUpLab(String[] labInfo){
		String[] workingLabInfo = new String[6];
		if(labInfo.length == 4){
			
			workingLabInfo[0] = labInfo[0];
			workingLabInfo[1] = labInfo[1];
			workingLabInfo[2] = "LEC";
			workingLabInfo[3] = "01";
			workingLabInfo[4] = labInfo[2];
			workingLabInfo[5] = labInfo[3];
		}else {
			workingLabInfo = labInfo;
		}
		
		
		for(int i =0; i < labs.size();i++){
			Lab tempLab = labs.get(i);
			if((tempLab.getDepartment().equals(workingLabInfo[0]))&&
				(tempLab.getClassNumber().equals(workingLabInfo[1]) &&
				(tempLab.getBelongsTo().getSection().equals(workingLabInfo[3])) &&
				(tempLab.getSection().equals(workingLabInfo[5])))){
				return labs.get(i);
			}
			
		}
		//@TODO TODO
		System.out.println("This should never happen, Lab not found. THROW EXCEPTION HERE");
		return null;
	}
	
	/**
	 * Simple function iterates through the course list and finds the class that
	 * matches all the info given to it in courseInfo. 
	 * @param courseInfo Array of size 4, first element is DEPARTMENT, second is NUMBER, third is always LEC, and fourth is SECTION
	 * @return a course from the ArrayList which matches the given course
	 */
	private Course lookUpCourse(String[] courseInfo) {
		for(int i =0; i < courses.size();i++){
			Course tempCourse = courses.get(i);
			if(tempCourse.getClassNumber().equals(courseInfo[1]) &&
			   tempCourse.getDepartment().equals(courseInfo[0])  &&
			   tempCourse.getSection().equals(courseInfo[3])){
				return courses.get(i);
			}
		}
		//@TODO TODO
		System.out.println("This should never happen, Course not found. THROW EXCEPTION HERE");

		return null;
	}
	/**
	 * Function that splits an identified Course information input line
	 * into its appropriate variable
	 * @param line2: read from the file, course information following Course: \n Header
	 */
	private void parseCourse(String line2) {
		String[] info = new String[4];
		info = line2.split(" +");
		Course c = new Course(info[0].replaceAll("\\s+", ""), 
								info[1].replaceAll("\\s+", ""), 
								info[3].replaceAll("\\s+", ""));
		courses.add(c);		
	}

	/**
	 * Function which will perform the functionality of splitting an identified LabSlot input line
	 * into an appropriate variable
	 * @param line2: read from the file, following the header Lab slots: \n
	 */
	private void parseLabSlot(String line2) {
		String[] info = new String[4];
		info = line2.split(",\\s*");
		LabSlot ls = new LabSlot(info[0].replaceAll("\\s+", ""), 
										info[1].replaceAll("\\s+", ""), 
										Integer.parseInt(info[2].replaceAll("\\s+", "")), 
										Integer.parseInt(info[3].replaceAll("\\s+", "")));
		labSlots.add(ls);		
	}


	/**
	 * Function which will perform the functionality of splitting an identified CourseSlot input line
	 * into an appropriate variable
	 * @param line2: read from the file, following the header Course slots: \n
	 */
	private void parseCourseSlot(String line2) {
		String[] info = new String[4];
		info = line2.split(",\\s*");
		CourseSlot cs = new CourseSlot(info[0].replaceAll("\\s+", ""), 
										info[1].replaceAll("\\s+", ""), 
										Integer.parseInt(info[2].replaceAll("\\s+", "")), 
										Integer.parseInt(info[3].replaceAll("\\s+", "")));
		courseSlots.add(cs);		
	}


	/**
	 * Simple function to check if a String representing a read line
	 * from the file is equal to a header of the input file, and if so, 
	 * what one via its place in the headers list
	 * Returns -1 if its not a header
	 * TODO add this functionality to check each header before enacting whatever function fits. General decision on what to parse based off return from this function
	 */
	private int equalHeader(String line2) {
		for(int i = 0; i < headers.length;i++) {
			if(line2.equals(headers[i])) {
				return i;
			}
		}
		return -1;
	}
	
	
	public ArrayList<pair<Classes,Classes>> getPairs() {
		return this.pairs;
	}
	public ArrayList<pair<Classes,Classes>> getNonCompatible() {
		return this.nonCompatible;
	}
	public ArrayList<pair<Classes,TimeSlot>> getUnwanted() {
		return this.unWanted;
	}
	public ArrayList<preferenceTriple> getPreferences() {
		return this.preferences;
	}
	
	public ArrayList<pair<Classes,TimeSlot>> getPartialAssignments() {
		return this.partialAssignment;
	}
	
	public ArrayList<CourseSlot> getCourseSlots(){
		return this.courseSlots;
	}
	
	public ArrayList<LabSlot> getLabSlots() {
		return this.labSlots;
	}
	
	public ArrayList<Course> getCourses() {
		return this.courses;
	}
	
	public ArrayList<Lab> getLabs() {
		return this.labs;
	}
	
	//Creates a list of a list of courses for evalCheck.
	private ArrayList<ArrayList<Course>> getCourseSections() {
		ArrayList<ArrayList<Course>> sectionList = new ArrayList<ArrayList<Course>>();
		boolean foundCourse = false;
		for(Course nextCourse : courses) {
			foundCourse = false;
			for(ArrayList<Course> secSquared : sectionList) {
				if((nextCourse.getDepartment() == secSquared.get(0).getDepartment()) && (nextCourse.getClassNumber() == secSquared.get(0).getClassNumber())){
					secSquared.add(nextCourse);
					foundCourse = true;
					break;
				}
			}
			if(!foundCourse) {
				ArrayList<Course> temp = new ArrayList<Course>();
				temp.add(nextCourse);
				sectionList.add(temp);
			}
		}
		return sectionList;
	}
	
	//Creates a list of a list of labs for evalCheck.
	private ArrayList<ArrayList<Lab>> getLabSections() {
		ArrayList<ArrayList<Lab>> sectionList = new ArrayList<ArrayList<Lab>>();
		boolean foundLab = false;
		for(Lab nextLab : labs) {
			foundLab = false;
			for(ArrayList<Lab> secSquared : sectionList) {
				if((nextLab.getDepartment() == secSquared.get(0).getDepartment()) && (nextLab.getClassNumber() == secSquared.get(0).getClassNumber())){
					secSquared.add(nextLab);
					foundLab = true;
					break;
				}
			}
			if(!foundLab) {
				ArrayList<Lab> temp = new ArrayList<Lab>();
				temp.add(nextLab);
				sectionList.add(temp);
			}
		}
		return sectionList;
	}
}



//TODO: Things Parser should catch before passing things to the AI:
//Stop if more courses than total courseMax/labs than total labMax
//Some combination of Lectures and labs such that there must be overlap (Highly improbable, but could be an edge case)
//partAssign: if no Course, Lab or TimeSlot exist(i.e., no putting courses on a Saturday)
//unwanted: if a course has no slot that isn't unwanted (Probably rare)
//Stop if more evening classes than Max slots, as above
//More 500-level courses than timeSlots
//partAssign for TUE 11:00
//
