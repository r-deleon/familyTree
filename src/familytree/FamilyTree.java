package familytree;

import logic.Controller;

public class FamilyTree {

    public static void main(String[] args) {
        Tree tree = new Tree(new Controller());
        tree.start();
    }
}
