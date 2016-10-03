package problem;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	public ArmConfig arm;
	public Map<State, List<ArmConfig>> pathTo;
	public State parent;
	
	public State(ArmConfig arm) {
		this.arm = arm;
		pathTo = new HashMap<State,List<ArmConfig>>();
	}
	
	public int stepsTo(State state) {
		int maxSteps = (int) Math.ceil(this.arm.maxAngleDiff(state.arm) / 0.0017453292222222);
		int temp = (int) Math.ceil(this.arm.getBaseCenter().distance(state.arm.getBaseCenter())/0.001);
		if (temp > maxSteps) {
			maxSteps = temp;
		}
		return maxSteps;
	}
	
	public int linkCount() {
		return pathTo.size();
	}
	
	public boolean connectTo(State state, int steps, Quadtree root) {
		if (pathTo.get(state) != null) {
			return true;
		}
		Point2D base;
		List<Double> jointAngles = new ArrayList<Double>(this.arm.getJointAngles());
		Point2D baseStep = new Point2D.Double();
		List<Double> angleStep = new ArrayList<Double>();
		ArmConfig armStep;
		List<ArmConfig> path = new ArrayList<ArmConfig>();
		base = (Point2D)this.arm.getBaseCenter().clone();
		baseStep.setLocation((state.arm.getBaseCenter().getX() - base.getX()) / steps, 
				(state.arm.getBaseCenter().getY() - base.getY()) / steps);
		for(int i = 0; i < jointAngles.size(); i++) {
		    angleStep.add((state.arm.getJointAngles().get(i) - jointAngles.get(i)) / steps);
		}
		
		for(int i = 1; i < steps; i++) {
			base.setLocation(base.getX() + baseStep.getX(), base.getY() + baseStep.getY());
			for (int j = 0; j < jointAngles.size(); j++) {
				jointAngles.set(j, jointAngles.get(j) + angleStep.get(j));
			}
			if (state.arm.hasGripper()) {
				List<Double> gripperLengths = new ArrayList<Double>();
				gripperLengths.add(0.03);
				gripperLengths.add(0.03);
				gripperLengths.add(0.03);
				gripperLengths.add(0.03);
				armStep = new ArmConfig(base, jointAngles, gripperLengths);
			} else {
				armStep = new ArmConfig(base, jointAngles);
			}
			if (root.loadArm(armStep) && armStep.isValid()) {
				path.add(armStep);
			} else {
				return false;
			}
		}
		List<ArmConfig> reversedPath = new ArrayList<ArmConfig>();
		for (int i = path.size() - 1; i >= 0; i--) {
			reversedPath.add(path.get(i));
		}
		path.add(state.arm);
		reversedPath.add(this.arm);
		pathTo.put(state, path);
		state.pathTo.put(this, reversedPath);
		return true;
	}
}
