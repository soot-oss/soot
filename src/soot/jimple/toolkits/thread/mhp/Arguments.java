package soot.jimple.toolkits.thread.mhp;

import java.util.*;
import soot.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.spark.pag.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class Arguments{
	public static Set threadAllocSites;
	public static Hierarchy hierarchy;
	public static CallGraph callGraph;
	public static PAG pag;
	public static ArrayList inlineSites;
	public static Set methodsNeedingInlining;
	public static Set canNotBeCompacted;
	public static Set specialJoin;
	public static Map allocNodeToThread;
	public static Map allocNodeToObj;
	public static Map startToThread;
	public static Map notifyAll;
	public static Map unitToSuccs;
	public static Map unitToPreds;
	public static Map synchObj;
	public static Map joinStmtToThread;
	public static Set allocNodes;
	public static Set multiRunAllocNodes;
/*	Arguments(){
		threadAllocSites = new HashSet();
		allocNodeToThread = new HashMap();
		canNotBeCompacted = new HashSet();
		startToThread = new HashMap();
		methodsNeedingInlining = new HashSet();
		notifyAll = new HashMap();
		unitToSuccs = new HashMap();
		unitToPreds = new HashMap();
		joinStmtToThread = new HashMap();
		System.out.println("==after init argu: allocNodes: "+allocNodes);
	}
*/
	public static void setSynchObj(Map so){
		synchObj = so;
	}
	public static Map getSynchObj(){
		return (Map)synchObj;
	}
	public static Set getMultiRunAllocNodes(){
		return (Set)multiRunAllocNodes;
	}
	public static void setMultiRunAllocNodes(Set moan){
		multiRunAllocNodes = moan;
		
	}
	public static Set getAllocNodes(){
		return (Set)allocNodes;
	}
	public static void setAllocNodes(Set an){
		allocNodes = an;
	}
	protected static Set getSpecialJoin(){
		return (Set)specialJoin;
	}
	protected static void setSpecialJoin(Set sj){
		specialJoin = sj;
	}
	protected static Set getCanNotBeCompacted(){
		return (Set)canNotBeCompacted;
	}
	protected static void setCanNotBeCompacted(Set cnbct){
		canNotBeCompacted = cnbct;
	}
	protected static Set getThreadAllocSites(){
		return (Set)threadAllocSites;
	}
	protected static Hierarchy getHierarchy(){
		return hierarchy;   
	}
	public  static void setHierarchy(Hierarchy hie){
		hierarchy = hie;
	}
	public static CallGraph getCallGraph(){
		return callGraph;
	}
	public static  void setCallGraph(CallGraph cg){
		callGraph = cg;
	}
	public static PAG getPag(){
		return pag;
	}
	public static  void setPag(PAG pa){
		pag = pa;
	}
	public static ArrayList getInlineSites(){
		return (ArrayList)inlineSites;
	}
	public static  void setInlineSites(ArrayList is){
		inlineSites = is;
	}
	protected static Set getMethodsNeedingInlining(){
		return (Set)methodsNeedingInlining;
	}
	public static  void setMethodsNeedingInlining(Set mne){
		methodsNeedingInlining = mne;
	}
	protected static Map getAllocNodeToThread(){
		return (Map)allocNodeToThread;
	}
	protected static  void setAllocNodeToThread(Map antt){
		allocNodeToThread = antt;
	}
	protected static Map getAllocNodeToObj(){
		return (Map)allocNodeToObj;
	}
	public static   void setAllocNodeToObj(Map anto){
		allocNodeToObj = anto;
	}
	protected static Map getStartToThread(){
		return (Map)startToThread;
	}
	protected static  void setStartToThread(Map stt){
		startToThread = stt;
	}
	protected static Map getNotifyAll(){
		return (Map)notifyAll;
	}
	protected static  void setNotifyAll(Map na){
		notifyAll = na;
	}
	protected static Map getUnitToSuccs(){
		return (Map)unitToSuccs;
	}
	protected static  void setUnitToSuccs(Map uts){
		unitToSuccs = uts;
	}
	protected static Map getUnitToPreds(){
		return (Map)unitToPreds;
	}
	protected static  void setUnitToPreds(Map utp){
		unitToPreds= utp;
	}
	
	protected static Map getJoinStmtToThread(){
		return (Map)unitToPreds;
	}
	protected static  void setJoinStmtToThread(Map utp){
		joinStmtToThread = utp;
	}
}
