import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;


public class SpaceGraph<T> extends CS400Graph<T> {
	
	protected Hashtable<T, Planet> allPlanets; // holds graph vertices, key=planetName
	protected Hashtable<T, Planet> unvisitedPlanets;
	protected Hashtable<T, Planet> visitedPlanets;
    public SpaceGraph() { allPlanets = new Hashtable<>(); }
    
	/**
	 * This planet object will store the name of the Planet as
	 * well as a LinkedList of all edges leaving from the 
	 * planet
	 * 
	 * @author tbirk
	 *
	 */
	public class Planet extends Vertex {

        public String planetName; // vertex label or application specific data
        public LinkedList<Edge> edgesLeaving; //list of edges connected to other planet objects
        public boolean isFuelingStation; //true if a refueling station, false otherwise
        public boolean hasDiscoveredFrom; //can only discover from a planet one time

        public Planet(T name) {
        	super(name);
            this.planetName = (String) name;
            this.edgesLeaving = new LinkedList<>();
            Random r = new Random();
            int match = r.nextInt(7);
            if(match == 0)
            	isFuelingStation = true;
            else 
            	isFuelingStation = false;   
        }
	}
	
	/**
	 * The edge object will connect the two planets together, with 
	 * one designated as a source planet and the other a target. Thus, the 
	 * connection will be directed one-way. The edge object also stores the 
	 * distance between the two planets.
	 * @author tbirk
	 *
	 */
	public class Edge {

	       public Planet target;
	        public int distance;

	        public Edge(Planet target, int distance) {
	            this.target = target;
	            this.distance = distance;
	        }	
	}
	
	/**
	 * This spaceship class will be a static object that will travel along the graph.
	 * The fields include fuel capacity as well as current fuel level. The capacity
	 * variable will remain constant but the current fuel level will change as the ship 
	 * travels through the graph.
	 * 
	 * @author tbirk
	 *
	 */
	public static class Spaceship {
		public static double fuelCapacity;
		public static double currentFuel;
		
		public Spaceship(double fuelCapacity) {
			Spaceship.fuelCapacity = fuelCapacity;
			currentFuel = fuelCapacity;
		}
		
		public Spaceship() {
			fuelCapacity = 100; //full fuel tank can travel for 100 lightyears
			currentFuel = 100;
		}
	}

	/**
	 * Returns the current amount of fuel in the spaceship object
	 * @param ship
	 * @return 
	 */
	
	public double getCurrentFuel(Spaceship ship) {
		return Spaceship.currentFuel;
	}
	
	/**
	 * returns whether or not the specified planet is a refueling station
	 * @param planet
	 * @return true if a refueling station, false otherwise
	 */
	public boolean isRefuelingStation(Planet planet) {
		return planet.isFuelingStation;
	}

	/**
	 * Lowers the current fuel of the spaceship object by a designated distance
	 * @param distance
	 */
	public void lowerFuel(double distance) {
		Spaceship.currentFuel -= distance;
	}
	
	/**
	 * Raises the current fuel to maximum capacity
	 */
	public void raiseFuel() {
		Spaceship.currentFuel = Spaceship.fuelCapacity;
	}
	
	//This is just a method to generate random names for planets in the format of (3-5 capital letters) - (3-5 numbers)
	public static String planetNameGenerator() {
	    Random rand = new Random();
	    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String name = "";
	    for (int i = 0; i < (3 + rand.nextInt(3)); i++) {
	      name = name + alphabet.charAt(rand.nextInt(26));
	    }
	    name = name + "-";
	    for (int i = 0; i < (3 + rand.nextInt(3)); i++) {
	      name = name + Integer.toString(rand.nextInt(9));
	    }
	    return name;
	  }
	
	/**
	 * When on a planet a specified planet, a random number of nearby planets can
	 * be discovered, ranging from 0 planets to 3 planets. Once the number of 
	 * planets has been randomly calculated, the distance values from the edges
	 * are calculated using a random generator. Possible distances range from 
	 * 10 lightyears to 36 lightyears. The planet is also randomly generated
	 * via a random name generator. Each of the new planets are then 
	 * connected to the planet that they are currently located on. Planets are
	 * updated onto the hashtables for record keeping.
	 * 
	 * @param currentLocation Planet that player is currently on
	 * @return the number of planets that were discovered
	 */
	public int discoverPlanets(Planet currentLocation) {
		int numPlanets;
		double randomPlanets = Math.random();
		//depending on random value, see how many planets were discovered
		if(randomPlanets < .2)
			numPlanets = 0;
		else if (randomPlanets < .5)
			numPlanets = 1;
		else if (randomPlanets < .8)
			numPlanets = 2;
		else
			numPlanets = 3;
		
		Random r = new Random();
		//for each planet that was found, add a new planet
		for(int i = 0; i < numPlanets; i++) {
			int randomDistance = r.nextInt(26) + 10;
			String newPlanetName = planetNameGenerator();
			Planet discovered = new Planet((T)newPlanetName);
			//create a new edge that connects a new planet with designated distance
			Edge newEdge = new Edge(discovered, randomDistance);
			//connect current location to new location with edge
			currentLocation.edgesLeaving.add(newEdge);
			//add planet to hashtables for record keeping
			unvisitedPlanets.put((T) discovered.planetName, discovered);
			allPlanets.put((T) discovered.planetName, discovered);
		}
		return numPlanets;
	}
	/**
	 * This method goes through the entire hashtable of unvisited planets 
	 * and searches for the shortest path to it. For each path, we check 
	 * for the path's distance and update minDistance if it is the 
	 * shortest up to that point. After looping through all unvisited 
	 * planets, return the unvisited planet that is closest.
	 * 
	 * @param currentLocation
	 * @return the nearest unvisited planet
	 */
	public Planet getNearestUnvisitedPlanet(Planet currentLocation) {
		int numUnvisitedPlanets = unvisitedPlanets.size();
		int minDistance = Integer.MAX_VALUE;
		Path minPath = null;
		//loop through each planet that is unvisited
		for(int i = 0; i < numUnvisitedPlanets; i++) {
			Path current = dijkstrasShortestPath((T) currentLocation, (T)unvisitedPlanets.get(i));
			//check if current distance is less than min distance found so far
			if(current.distance < minDistance) {
				//update minDistance and minPath if this is closest path so far
				minDistance = current.distance;
				minPath = current;
			}
		}
		//look at the minPath to determine the closest unvisited planet
		Planet nearestUnvisited = (Planet) minPath.end;
		
		return nearestUnvisited;
	}
	
	/**
	 * Searches through the hashtable of all visited planets to locate the nearest
	 * planet with a fuel station. Create a path for each planet that has been
	 * visited AND has a fueling station. Compare the distance of these paths
	 * to determine which is the shortest and return the planet that is nearest.
	 * 
	 * @param currentLocation
	 * @return nearets planet with a fuel station
	 */
	public Planet getNearestFuelStation(Planet currentLocation) {
		//check if current planet is already a fuel station
		if(currentLocation.isFuelingStation)
			return currentLocation;
		int numVisitedPlanets = visitedPlanets.size();
		int minDistance = Integer.MAX_VALUE;
		Path minPath = null;
		//loop through all planets on the hashtable
		for(int i = 0; i < numVisitedPlanets; i++ ) {
			//if planet is not a fueling station, don't create a path and skip ahead
			if(!visitedPlanets.get(i).isFuelingStation)
				continue;
			//create a shortest path for each planet that has a fueling station
			Path current = dijkstrasShortestPath((T) currentLocation, (T)visitedPlanets.get(i));
			//update minDistance and minPath if current distance is less than others found so far
			if(current.distance < minDistance) {
				minDistance = current.distance;
				minPath = current;
			}
		}
		Planet nearestFuelStation = (Planet) minPath.end;
		return nearestFuelStation;
	}
	
	/**
	 * To make the list of planets more visible to the user, transfer all planets that
	 * are unvisited onto a linked list and return that linked list
	 * @return
	 */
	public LinkedList<Planet> getUnvisitedPlanets() {
		LinkedList<Planet> unvisited = new LinkedList<Planet>();
		for(int i = 0; i < unvisitedPlanets.size(); i++) {
			unvisited.add(unvisitedPlanets.get(i));
		}
		return unvisited;
	}
}
