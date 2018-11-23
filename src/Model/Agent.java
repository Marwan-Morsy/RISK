package Model;

import java.awt.Point;

//import javafx.util.Pair;
//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Agent {

    private int bounceValue;
    private AgentType type;
    private String agentId;
    private AgentState state;
    private int numberOfArmies;
    private List<Country> countriesOwned;
    private static  List<Country> Allcountries;
    private static  List<Point> AllEdges;
    public static  Agent player1;
    public static  Agent player2;


    protected int newCountryBounce = 0;

    public Agent(AgentType agentType){
        this.type = agentType;
        state = AgentState.Alive;
        bounceValue = 2;
        agentId = generateId();
        numberOfArmies = 0;
        countriesOwned = new ArrayList<>();
    }

    public abstract boolean attack();
    public abstract boolean move();
    public abstract boolean place();

    public void addBounce(int bounceValue) {
        this.bounceValue += bounceValue;
    }

    public void subBounce(int bounceValue) {
        this.bounceValue -= bounceValue;
    }
    
    public int getBounceValue() {
		return bounceValue;
	}

    public boolean isAlive() {
        return state == AgentState.Alive;
    }

    public void updateState() {
        if (numberOfArmies == 0)
            this.state = AgentState.Dead;
        else
            this.state = AgentState.Alive;
    }

    public void addArmies(int armies) {
        numberOfArmies += armies;
    }
    public void subArmies(int armies) {
        numberOfArmies -= armies;
    }

    public List<Country> getCountriesOwned() {
        return countriesOwned;
    }

    public void setCountriesOwned(List<Country> countriesOwned) {
        this.countriesOwned = countriesOwned;
        for (Country c : countriesOwned)
            c.setOwner(this);
    }

    public void addCountry(Country country) {
        countriesOwned.add(country);
        country.setOwner(this);
        if (country.getContinentOwned().isSingleOwner(countriesOwned))
            addBounce(country.getContinentOwned().getBounceAdd());
    }

    public void subCountry(Country country) {
        if (country.getContinentOwned().isSingleOwner(countriesOwned))
            subBounce(country.getContinentOwned().getBounceAdd());
        countriesOwned.remove(country);
        country.setOwner(null);
    }

    @Override
    public boolean equals(Object obj) {
        Agent newAgent = (Agent) obj;
        return this.agentId.equals(newAgent.agentId);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

	public static List<Country> getAllcountries() {
		return Allcountries;
	}

	public static void setAllcountries(List<Country> allcountries) {
		Allcountries = allcountries;
	}

	public static List<Point> getAllEdges() {
		return AllEdges;
	}

	public static void setAllEdges(List<Point> allEdges) {
		AllEdges = allEdges;
	}

    protected boolean attack(Country from, Country to) {
        if (from == null || to == null || from.getOwner() == null)
            return false;

        if (!from.getOwner().equals(this) || (to.getOwner() != null && to.getOwner().equals(this)) || !from.isAdj(to))
            return false;

        int remain = from.getNumberArmies() - to.getNumberArmies();
        if (to.getOwner() == null) {
            from.setNumberArmies((int) Math.floor(remain/2));
            to.setNumberArmies((int) Math.ceil(remain / 2));
            addCountry(to);
            newCountryBounce = 2;
            return true;
        }

        if (remain > 1) {
            subArmies(to.getNumberArmies());
            from.setNumberArmies((int) Math.floor(remain/2));
            to.setNumberArmies((int) Math.ceil(remain / 2));
            addCountry(to);
            to.getOwner().subCountry(to);
            newCountryBounce = 2;
        } else {
            subArmies(from.getNumberArmies());
            from.setNumberArmies(0);
            subCountry(from);
        }
        return true;
    }

    protected boolean place(Country to) {
        if (to == null)
            return false;

        if (to.isBelongAgent(this)) {
            to.addArmies(getBounceValue() + newCountryBounce);
            newCountryBounce = 0;
        } else
            return false;
        return true;
    }
}
