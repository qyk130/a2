package problem;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;


public class Main {
	private static ProblemSpec ps = new ProblemSpec();
	private static Quadtree root;
	private static ArrayList<State> states;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ps.loadProblem(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		states = new ArrayList<State>();
 		root = new Quadtree(1, new Point2D.Double(0.5, 0.5));
		for(Obstacle obstacle: ps.getObstacles()) {
			root.addObstacle(obstacle.getRect());
		}
		
		ArmConfig initSmallGripper;
		ArmConfig goalSmallGripper;
		if (ps.getInitialState().hasGripper()) {
			List<Double> gripperLengths = new ArrayList<Double>();
			gripperLengths.add(0.03);
			gripperLengths.add(0.03);
			gripperLengths.add(0.03);
			gripperLengths.add(0.03);
			initSmallGripper = new ArmConfig(ps.getInitialState().getBaseCenter(), 
					ps.getInitialState().getJointAngles(), gripperLengths);
			goalSmallGripper = new ArmConfig(ps.getGoalState().getBaseCenter(),
					ps.getInitialState().getJointAngles(), gripperLengths);
		} else {
			initSmallGripper = new ArmConfig(ps.getInitialState().getBaseCenter(), 
					ps.getInitialState().getJointAngles());
			goalSmallGripper = new ArmConfig(ps.getGoalState().getBaseCenter(),
					ps.getInitialState().getJointAngles());
		}
		states.add(new State(initSmallGripper));
		boolean noPath = true;
		int maxState = 150 + ps.getJointCount() * 50;
		int stateCount = 0;
		HashMap<Obstacle, int[]>  trials = new HashMap<Obstacle, int[]>();
		HashMap<Obstacle, int[]>  success = new HashMap<Obstacle, int[]>();
		for (Obstacle obst: ps.getObstacles()) {
			int[] array = new int[4];
			trials.put(obst, array);
			array = new int[4];
			success.put(obst, array);
		}
		State goal =new State(goalSmallGripper);
		while (noPath) {	
		    while(stateCount < maxState - ps.getJointCount() * 50) {
		    	ArmConfig randomArm;
		    	randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 0, 0, 1, 1);
		    	if (root.loadArm(randomArm)) {
		    		if (root.isValid()) {
		    			states.add(new State(randomArm));
		    			stateCount++;
		    		}
		    	}
		    }
		    while(stateCount < maxState) {
		    	if (ps.getObstacles().size() == 0) {
		    		break;
		    	}
		    	for (Obstacle obst: ps.getObstacles()) {
		    		ArmConfig randomArm;
		    		int times = (trials.get(obst)[0]+ 1) / (success.get(obst)[0] + 1);
		    		if (times > 50) { times = 0;}
		    		for (int i = 0; i < times; i++) {
		    			trials.get(obst)[0]++;		    		
		    			randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 
		    					obst.getRect().getMinX() - 0.20, 
		    					obst.getRect().getMinY() - 0.20,
		    					obst.getRect().getMinX() - 0.05,
		    					obst.getRect().getMaxY() + 0.20);
		    			if (randomArm != null && root.loadArm(randomArm)) {
				    			states.add(new State(randomArm));
				    			stateCount++;
				    			success.get(obst)[0]++;
				    	}
		    		}
		    		times = (trials.get(obst)[1]+ 1) / (success.get(obst)[1] + 1);
		    		if (times > 50) { times = 0;}
		    		for (int i = 0; i < times; i++) {
			    		trials.get(obst)[1]++;	
		    			randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 
		    					obst.getRect().getMinX() - 0.20, 
		    					obst.getRect().getMaxY() + 0.05,
		    					obst.getRect().getMaxX() + 0.20,
		    					obst.getRect().getMaxY() + 0.20);
		    			if (randomArm != null && root.loadArm(randomArm)) {
				    			states.add(new State(randomArm));
				    			stateCount++;
				    			success.get(obst)[1]++;
				    	}
		    		}
		    		times = (trials.get(obst)[2]+ 1) / (success.get(obst)[2] + 1);
		    		if (times > 50) { times = 0;}
		    		for (int i = 0; i < times; i++) {
		    			trials.get(obst)[2]++;	
		    			randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 
		    					obst.getRect().getMaxX() + 0.05, 
		    					obst.getRect().getMinY() - 0.20,
		    					obst.getRect().getMaxX() + 0.20,
		    					obst.getRect().getMaxY() + 0.20);
		    			if (randomArm != null && root.loadArm(randomArm)) {
				    			states.add(new State(randomArm));
				    			stateCount++;
				    			success.get(obst)[2]++;
				    	}
		    		}
		    		times = (trials.get(obst)[3]+ 1) / (success.get(obst)[3] + 1);
		    		if (times > 50) { times = 0;}
		    		for (int i = 0; i < times; i++) {
		    			trials.get(obst)[3]++;	
		    			randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 
		    					obst.getRect().getMinX() - 0.20, 
		    					obst.getRect().getMinY() - 0.20,
		    					obst.getRect().getMaxX() + 0.20,
		    					obst.getRect().getMinY() - 0.05);
		    			if (randomArm != null && root.loadArm(randomArm)) {
				    			states.add(new State(randomArm));
				    			stateCount++;
				    			success.get(obst)[3]++;
				    	}
		    		}
		    	}
		    }
		    states.add(goal);
		    for(int i = 0; i < states.size(); i++) {
		    	HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		    	for (int j = i + 1; j < states.size(); j++) {
		    		map.put(j, states.get(i).arm.getBaseCenter().distance(states.get(j).arm.getBaseCenter()));
		    		//map.put(j, 1.0 * states.get(i).stepsTo(states.get(j)));
		    	}
		    	while (states.get(i).linkCount() < Math.log(stateCount) - 2 && map.size() > 0) {
		    		int min = -1;
		    		double minDistance = 99999;
		    		for (Entry<Integer, Double> entry : map.entrySet()) {
		    			if (entry.getValue() < minDistance) {
		    				minDistance = entry.getValue();
		    				min = entry.getKey();
		    			}
		    		}
		    		states.get(i).connectTo(states.get(min), states.get(i).stepsTo(states.get(min)), root);
	
		    	    map.remove(min);
		    	}	   	
		    }
		    int i = 0;
		    if (states.get(states.size() - 1).pathTo.size() == 0) {
		    	System.out.println("goal not connected");
		    }
		    Set<State> close = new HashSet<State>();
		    Set<State> open = new HashSet<State>();
		    Map<State, Integer> cost = new HashMap<State, Integer>();
		    open.add(states.get(0));
		    cost.put(states.get(0), 99999);
		    while (goal.parent == null && open.size() != 0) {
		    	State min = states.get(0);
		    	for(State candidate: open) {
		    		if (cost.get(candidate) + candidate.stepsTo(goal) < cost.get(min) + min.stepsTo(goal)) {
		    			min = candidate;
		    		}
		    	}
		    	close.add(min);
		    	open.remove(min);
		    	for(Entry<State, List<ArmConfig>> entry : min.pathTo.entrySet()) {
		    		if (!close.contains(entry.getKey()) && 
		    				(!cost.containsKey(entry.getKey()) || cost.get(entry.getKey()) > entry.getValue().size())) {
		    			entry.getKey().parent = min;
		    			cost.put(entry.getKey(), entry.getValue().size());
		    			open.add(entry.getKey());
		    		}
		    	}
		    }
			
		    if (goal.parent == null) {
		    	System.out.println("path not found");
		    	maxState += 50 + ps.getJointCount() * 10;
		    	states.remove(goal);
		    } else {
		    	noPath = false;
		    }
		}
		
	    	String s = new String();
	    	State state = states.get(states.size() - 1);
	    	int primStep = 0;
	    	while (state.parent != null) {
	    		State prev = state.parent;
	    		String ts = new String();
	    		primStep += prev.pathTo.get(state).size();
	    		for (ArmConfig arm: prev.pathTo.get(state)) {
	    			ts = ts + arm.toString() + '\n';
	    		}
	    		s = ts + s;
	    		state = prev;
	    	}
	    	if (ps.getInitialState().hasGripper()) {
		    	int gripStep =(int) Math.ceil(ps.getInitialState().maxGripperDiff(initSmallGripper) * 1000);
		    	ArmConfig current = ps.getInitialState();
		    	List<Double> gripStepSize = new ArrayList<Double>();
		    	gripStepSize.add((initSmallGripper.getGripperLengths().get(0) - current.getGripperLengths().get(0)) / gripStep);
		    	gripStepSize.add((initSmallGripper.getGripperLengths().get(1) - current.getGripperLengths().get(1)) / gripStep);
		    	gripStepSize.add((initSmallGripper.getGripperLengths().get(2) - current.getGripperLengths().get(2)) / gripStep);
		    	gripStepSize.add((initSmallGripper.getGripperLengths().get(3) - current.getGripperLengths().get(3)) / gripStep);
		    	String ts = new String();
		    	ts = current.toString() + '\n';
		    	primStep += gripStep - 1;
		    	for (int c = 0; c < gripStep - 1; c++) {
		    		for (int j =0; j < 4; j++) {
		    			current.gripperLengths.set(j, current.gripperLengths.get(j) + gripStepSize.get(j));
		    		}
		    		ts = ts + current.toString() + '\n';
		    	}
		    	s = ts + s;
		    	current = goalSmallGripper;
		    	gripStep =(int) Math.ceil(current.maxGripperDiff(ps.getGoalState()) * 1000);
		    	gripStepSize = new ArrayList<Double>();
		    	gripStepSize.add((ps.getGoalState().getGripperLengths().get(0) - current.getGripperLengths().get(0)) / gripStep);
		    	gripStepSize.add((ps.getGoalState().getGripperLengths().get(1) - current.getGripperLengths().get(1)) / gripStep);
		    	gripStepSize.add((ps.getGoalState().getGripperLengths().get(2) - current.getGripperLengths().get(2)) / gripStep);
		    	gripStepSize.add((ps.getGoalState().getGripperLengths().get(3) - current.getGripperLengths().get(3)) / gripStep);
		    	ts = new String();
		    	primStep += gripStep - 1;
		    	for (int c = 0; c < gripStep - 1; c++) {
		    		for (int j =0; j < 4; j++) {
		    			current.gripperLengths.set(j, current.gripperLengths.get(j) + gripStepSize.get(j));
		    		}
		    		ts = ts + current.toString() + '\n';
		    	}
		    	s = s + ts;
		    	s = Integer.toString(primStep) + '\n' + s + ps.getGoalState().toString();
	    	} else {
	    		s =Integer.toString(primStep) + '\n' + ps.getInitialState().toString() + '\n' + s;
	    	}
	    	try {
				ps.saveSolution(args[1], s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	}
	

}
