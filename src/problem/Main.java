package problem;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;


public class Main {
	private static ProblemSpec ps = new ProblemSpec();
	private static Quadtree root;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ps.loadProblem(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root = new Quadtree(1, new Point2D.Double(0.5, 0.5));
		for(Obstacle obstacle: ps.getObstacles()) {
			root.addObstacle(obstacle.getRect());
		}
		
		for(Line2D link: ps.getInitialState().getLinks()) {
			if(!root.addObject(link, false)) {
				System.out.println("collision detected!");
			}
		}
		for(Line2D link: ps.getInitialState().getChair()) {
			if(!root.addObject(link, true)) {
				System.out.println("collision detected!");
			}
		}
		root.clear();
		if (!root.isValid()) {
			System.out.println("not valid!");
		}
		
	}
	

}
