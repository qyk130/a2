package problem;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;

import tester.Tester;

public class Quadtree {
	private static final int MAX_LEVEL = 5;
	private static Tester tester = new Tester();
	
	private int level;
	private List<Line2D> links;
	private List<Line2D> chair;
	private List<Rectangle2D> obstacles;
	private boolean full; 
	private Point2D center;
	private Quadtree[] children;
	private double radius;
	private boolean splited;
	
	public Quadtree(int level, Point2D center) {
		this.level = level;
		this.center = center;
		links = new ArrayList<Line2D>();
		chair = new ArrayList<Line2D>();
		obstacles = new ArrayList<Rectangle2D>();
		radius = 1.0 / Math.pow(2, level);
		this.full = false;
		this.splited = false;
	}
	
	public boolean loadArm(ArmConfig arm) {
		clear();
		for(Line2D link: arm.getLinks()) {
			if(!addObject(link, false)) {
				return false;
			}
		}
		for(Line2D link: arm.getChair()) {
			if(!addObject(link, true)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid() {
		if (!splited) {
			for(Line2D link: chair) {
				for(Rectangle2D rect: obstacles) {
					if (rect.intersectsLine(link)) {
						return false;
					}
				}
			}
			for(Line2D link: links) {
				for(Rectangle2D rect: obstacles) {
					if (rect.intersectsLine(link)) {
						return false;
					}
				}
				for(Line2D link2: links) {
					if (!link.equals(link2)) {
						if (!link.getP1().equals(link2.getP1()) && !link.getP1().equals(link2.getP2()) && 
								!link.getP2().equals(link2.getP2()) && !link.getP2().equals(link2.getP1())) {
							if (link.intersectsLine(link2)) {
								return false;
							}
						}
					}
				}
			}
			return true;
		} else {
			return children[0].isValid() && children[1].isValid() && children[2].isValid() && children[3].isValid();
		}
	}
	
	public boolean clear() {
		links = new ArrayList<Line2D>();
		chair = new ArrayList<Line2D>();
		if (!splited) {	
			if (full || !obstacles.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else {
			boolean empty = true;
			empty = children[0].clear() && empty;
			empty = children[1].clear() && empty;
			empty = children[2].clear() && empty;
			empty = children[3].clear() && empty;
			if (empty) {
				children = null;
				splited = false;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean addObject(Line2D link, boolean isChair) {
		if (full) {
			return false;
		}
		if (level == MAX_LEVEL) {
			if (isChair) {
				chair.add(link);
			} else {
				links.add(link);
			}
		} else {
			split();
			if (new Rectangle2D.Double(center.getX(), center.getY(), radius, radius).intersectsLine(link)) {
				if(!children[0].addObject(link, isChair)) {
					return false;
				}
			}
			if (new Rectangle2D.Double(center.getX() - radius, center.getY(), radius, radius).intersectsLine(link)) {
				if(!children[1].addObject(link, isChair)) {
					return false;
				}
			}
			if (new Rectangle2D.Double(center.getX() - radius, center.getY() - radius, radius, radius).intersectsLine(link)) {
				if(!children[2].addObject(link, isChair)) {
					return false;
				}
			}
			if (new Rectangle2D.Double(center.getX(), center.getY() - radius, radius, radius).intersectsLine(link)) {
				if(!children[3].addObject(link, isChair)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void split(){
		if (!splited && level != MAX_LEVEL) {
			splited = true;
			children = new Quadtree[4];
			Point2D newCenter = new Point2D.Double(center.getX() + radius / 2, center.getY() + radius /2);
			children[0] = new Quadtree(level + 1, newCenter);
			newCenter = new Point2D.Double(center.getX() - radius / 2, center.getY() + radius /2);
			children[1] = new Quadtree(level + 1, newCenter);
			newCenter = new Point2D.Double(center.getX() - radius / 2, center.getY() - radius /2);
			children[2] = new Quadtree(level + 1, newCenter);
			newCenter = new Point2D.Double(center.getX() + radius / 2, center.getY() - radius /2);
			children[3] = new Quadtree(level + 1, newCenter);
			if (full) {
				setFull();
			}
		}
	}
	
	private void setFull() {
		if (splited) {
			children[0].setFull();
			children[1].setFull();
			children[2].setFull();
			children[3].setFull();
		} else {
			full = true;
		}
	}
	
	public void addObstacle(Rectangle2D rect) {
		if (level == MAX_LEVEL) {
			obstacles.add(rect);
		}  else {
			split();
			Rectangle2D quadrant[] = new Rectangle2D[4];
			quadrant[0] = new Rectangle2D.Double(center.getX(), center.getY(), radius, radius);
			quadrant[1] = new Rectangle2D.Double(center.getX() - radius, center.getY(), radius, radius);
			quadrant[2] = new Rectangle2D.Double(center.getX() - radius, center.getY() - radius, radius, radius);
			quadrant[3] = new Rectangle2D.Double(center.getX(), center.getY() - radius, radius, radius);
			for (int i = 0; i < 4; i++) {
				if (quadrant[i].intersects(rect)) {
					if (rect.contains(quadrant[i])) {
						children[i].setFull();
					} else {
						children[i].addObstacle(rect);
					}
				}
			}
		}
	}
}
