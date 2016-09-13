package problem;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;


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
		int stateCount = 0;
	    while(stateCount < 1000) {
	    	ArmConfig randomArm;
	    	randomArm = ArmConfig.randomArm(ps.getJointCount(), ps.getInitialState().hasGripper(), 0, 0, 1, 1);
	    	if (root.loadArm(randomArm)) {
	    		if (root.isValid()) {
	    			states.add(new State(randomArm));
	    			stateCount++;
	    		}
	    	}
	    }
	}
	

}
