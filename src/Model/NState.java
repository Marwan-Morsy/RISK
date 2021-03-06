package Model;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class NState {

    public List<SCountry> allCountries;
    public List<Integer> myCountries, opponentCountris;
    public List<SContinent> continents;
    public Pair<Integer, Integer> place;
    public Pair<Integer, Integer> attack;
    public int damage;
    public int turn = 1;
    public NState parent = null;
    public Pair<Integer, Integer> oppenentPlace;
    public static  NState globalState;
    public NState() {
        allCountries = new ArrayList<>();
        myCountries = new ArrayList<>();
        opponentCountris = new ArrayList<>();
        continents = new ArrayList<>();
    }

    public void performAttack() {

        if (attack == null)
            return;
        SCountry first = allCountries.get(attack.getKey());
        SCountry second = allCountries.get(attack.getValue());
        int remain = first.numberArmies - second.numberArmies;
        first.numberArmies = (int) Math.floor(remain / 2.0);
        second.numberArmies = (int) Math.ceil(remain / 2.0);
        second.owner = first.owner;
        opponentCountris.remove(new Integer(second.id));
        myCountries.add(new Integer(second.id));
        // add opponent bounce
        if(!opponentCountris.isEmpty())
            addOpponentBounce();
    }

    public int getOpponentBounce() {
        int bounse = Math.max(3, opponentCountris.size() / 3);
        for (SContinent sc : continents)
            if (opponentCountris.containsAll(sc.countries))
                bounse += sc.bounse;
        return bounse;
    }

    public int getMyBounce() {
        int bounse = Math.max(3, myCountries.size() / 3);
        if (attack != null)
            bounse += 2;
        for (SContinent sc : continents)
            if (myCountries.containsAll(sc.countries))
                bounse += sc.bounse;
        return bounse;
    }

    private void addOpponentBounce() {
        int leastcontry = opponentCountris.get(0);
        for (int i : opponentCountris)
            if (allCountries.get(i).numberArmies < allCountries.get(leastcontry).numberArmies)
                leastcontry = i;
        oppenentPlace=new Pair<Integer, Integer>(leastcontry, getOpponentBounce());
        allCountries.get(leastcontry).numberArmies += oppenentPlace.getValue();

    }

    public List<NState> getSuccssors() {
        List<NState> successors = new ArrayList<>();
        List<Pair<Integer, Integer>> avalibleAttacks = getAvailableAttacks();

        for (int index : getGoodPlace(myCountries)) {
            SCountry c = allCountries.get(index);
            int bounse = getMyBounce();
            c.numberArmies += bounse;
            Pair<Integer, Integer> tempPlace = new Pair(c.id, bounse);

            for (Pair<Integer, Integer> p : avalibleAttacks) {
                NState s = (NState) this.clone();
                s.attack = p;
                s.place = tempPlace;
                s.parent = this;
                s.performAttack();
                successors.add(s);

            }
            // attacks avalible after place new armies
            boolean canNotAttack = avalibleAttacks.isEmpty();
            for (Pair<Integer, Integer> p : allCountries.get(index).getAvalibleAttacks(allCountries)) {
                boolean done = false;
                for (Pair<Integer, Integer> attack : avalibleAttacks) {
                    done = attack.getKey() == p.getKey() && attack.getValue() == p.getValue();
                    if (done)
                        break;
                }
                if (done)
                    continue;
                NState s = (NState) this.clone();
                s.attack = p;
                s.place = tempPlace;
                s.parent = this;
                s.performAttack();
                successors.add(s);
                canNotAttack = false;
            }
            if (canNotAttack) {
                NState s = (NState) this.clone();
                s.place = tempPlace;
                s.parent = this;
                successors.add(s);
            }
            c.numberArmies -= bounse;

        }
        return successors;

    }

    private List<Integer> getGoodPlace(List<Integer> myCountries) {
        List<Integer> res = new ArrayList<>();
        for (int i : myCountries) {
            SCountry c = allCountries.get(i);
            if (c.getAllAttacks(allCountries).size() != 0)
                res.add(c.id);
        }
        return res;
    }

    protected Object clone() {

        NState s = new NState();
        s.myCountries = clonIntList(myCountries);
        s.opponentCountris = clonIntList(opponentCountris);
        s.allCountries = clonCouList(allCountries);
        s.continents = continents;
        s.parent = this;
        return s;
    }

    protected List<SCountry> clonCouList(List<SCountry> list) {
        List<SCountry> clone = new ArrayList<>();
        for (SCountry c : list)
            clone.add((SCountry) c.clone());

        return clone;
    }

    protected List<Integer> clonIntList(List<Integer> list) {
        List<Integer> clone = new ArrayList<>();
        for (Integer i : list)
            clone.add(i.intValue());
        return clone;
    }

    /*public static void main(String[] args) {

        NState s = new NState();
        List<SCountry> allCountries = new ArrayList<>();
        List<Integer> myCountries = new ArrayList<>(), opponentCountris = new ArrayList<>();
        ;
        List<SContinent> continents = new ArrayList<>();

        SContinent c = new SContinent();
        c.bounse = 5;
        c.countries.add(1);
        c.countries.add(2);
        continents.add(c);
        c = new SContinent();
        c.bounse = 3;
        c.countries.add(3);
        c.countries.add(4);
        continents.add(c);
        for (int i = 0; i < 5; i++) {
            SCountry cc = new SCountry(i);
            cc.numberArmies = i * 2;
            allCountries.add(cc);
        }
        allCountries.get(1).adj.add(2);
        allCountries.get(2).adj.add(3);
        allCountries.get(3).adj.add(4);
        allCountries.get(2).adj.add(1);
        allCountries.get(3).adj.add(2);
        allCountries.get(4).adj.add(3);
        allCountries.get(1).adj.add(3);
        allCountries.get(3).adj.add(1);

        myCountries.add(1);
        myCountries.add(4);
        allCountries.get(1).owner = 1;
        allCountries.get(4).owner = 1;
        opponentCountris.add(2);
        opponentCountris.add(3);
        allCountries.get(2).owner = 2;
        allCountries.get(3).owner = 2;
        s.allCountries = allCountries;
        s.myCountries = opponentCountris;
        s.opponentCountris = myCountries;
        s.continents = continents;

        GreedyAgent g=new GreedyAgent();
        for (NState ss : g.generate_game(s)) {
            System.out.println("-------------------------------\n");
            for (SCountry sc : ss.allCountries)
                System.out.println("country (" + sc.id + ")  owner : " + sc.owner + "number ar  :" + sc.numberArmies);

            System.out.println(" mine : ");
            for (int i : ss.myCountries)
                System.out.print(i + " ,");
            System.out.println("\n oppen : ");
            for (int i : ss.opponentCountris)
                System.out.print(i + " ,");
            if (ss.attack != null)
                System.out.println("\n+" + ss.attack.getKey() + "  attack   " + ss.attack.getValue());
            if (ss.place != null)
                System.out.println("\n" + ss.place.getKey() + " get  " + ss.place.getValue());
        }
        System.out.println("\n       You win");
    }*/

    public List<Pair<Integer, Integer>> getAvailableAttacks() {
        List<Pair<Integer, Integer>> avalibleAttacks = new ArrayList<>();
        for (int index : myCountries) {
            avalibleAttacks.addAll(allCountries.get(index).getAvalibleAttacks(allCountries));
        }
        return avalibleAttacks;
    }
}