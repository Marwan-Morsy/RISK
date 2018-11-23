package Model;

public class NearlyPacifistAgent extends Agent {

    public NearlyPacifistAgent() {
        super(AgentType.Nearly_Pacifist);
    }

    @Override
    public boolean attack() {
        Country to  = getCountriesOwned().get(0).getAdj().get(0);
        Country from  = getCountriesOwned().get(0);
        int maxdiff = 1;
        for (Country c : getCountriesOwned()) {
            for (Country adj : c.getAdj()) {
                if (adj.isBelongAgent(this))
                    continue;
                else if (c.getNumberArmies() - adj.getNumberArmies() > maxdiff) {
                    to = adj;
                    from = c;
                    maxdiff = from.getNumberArmies() - adj.getNumberArmies();
                }
            }
        }
        return attack(from, to);
    }

    @Override
    public boolean move() {
        return false;
    }

    @Override
    public boolean place() {
        Country temp = getCountriesOwned().get(0);
        for (Country c : getCountriesOwned())
            if (c.getNumberArmies() < temp.getNumberArmies())
                temp = c;
        return place(temp);
    }
}
