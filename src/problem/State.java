package problem;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	private ArmConfig arm;
	private Map<State, ArrayList<ArmConfig>> pathTo;
	
	public State(ArmConfig arm) {
		this.arm = arm;
		pathTo = new HashMap<State,ArrayList<ArmConfig>>();
	}
	
	public int stepsTo(State state) {
		int maxSteps = (int) Math.ceil(this.arm.maxAngleDiff(state.arm) / 0.0017453292222222);
		int temp = (int) Math.ceil(this.arm.maxDistance(state.arm)/0.001);
		if (temp > maxSteps) {
			maxSteps = temp;
		}
		return maxSteps;
	}
	
	public void connectTo(State state, int steps, Quadtree root) {
		 Point2D base;
		 ArrayList<Double> jointAngles;
		 
	}
}
