package org.app.menu;

import org.fsdb.Input;

import java.util.ArrayList;

public class Menu {
    private StringBuilder menuOutput;

    private String menuTitle;
    private ArrayList<MenuChoice> menuItems;

    private int leftPadding = 4;
    private int rightPadding = 4;
    private int topPadding = 1;
    private int bottomPadding = 1;

    private int maxItemLength = 0;
    private int topBorderLength = 0;

    public Menu() {
        menuOutput = new StringBuilder();
        menuItems = new ArrayList<>();
    }

    private String addLeftBorder(String text) {
        return "|" + " ".repeat(leftPadding) + text;
    }

    private String addRightBorder(String text) {
        int rightPadAdd = maxItemLength - text.length() + rightPadding;
        return text + " ".repeat(rightPadAdd) + "|";
    }

    private void addVerticalPadding(int amount) {
        for (int i = 0; i < amount; i++)
            menuOutput.append(String.format("|%s|\n", " ".repeat(topBorderLength - 2)));
    }

    public ArrayList<MenuChoice> getMenuItems() {
        return menuItems;
    }

    public Menu setPaddings(int rightPad, int leftPad, int topPad, int bottomPad) {
        leftPadding = leftPad;
        rightPadding = rightPad;
        topPadding = topPad;
        bottomPadding = bottomPad;
        return this;
    }

    public Menu setMenuTitle(String title) {
        this.menuTitle = title;
        return this;
    }

    public Menu addMenuItem(String name, String key) {
        menuItems.add(new MenuChoice(name, menuItems.size() + 1, key));
        if (name.length() > maxItemLength) {
            maxItemLength = name.length();
            topBorderLength = maxItemLength + String.valueOf(menuItems.size()).length() + rightPadding + leftPadding + 5;
        }
        return this;
    }

    public Menu addMenuItem(String name) {
        addMenuItem(name, name.toLowerCase().charAt(0) + String.valueOf(menuItems.size() + 1));
        return this;
    }

    public Menu show() {
        menuOutput.append("-".repeat(topBorderLength)).append("\n");
        int titleHalfLen = menuTitle.length() / 2;
        int titleRightPad = (topBorderLength / 2) - titleHalfLen - 1;
        int titleLeftPad = (topBorderLength / 2) - (menuTitle.length() - titleHalfLen) - 1;
        menuOutput.append("|").append(" ".repeat(titleLeftPad)).append(menuTitle).append(" ".repeat(titleRightPad)).append("|\n");
        menuOutput.append("-".repeat(topBorderLength)).append("\n");

        addVerticalPadding(topPadding);

        for (int i = 0; i < menuItems.size(); i++)
            menuOutput.append(addLeftBorder(String.format("[%d] %s", i + 1, addRightBorder(menuItems.get(i).text)))).append("\n");

        addVerticalPadding(bottomPadding);

        menuOutput.append("-".repeat(topBorderLength)).append("\n");
        System.out.print(menuOutput.toString());

        return this;
    }

    public MenuChoice prompt(String promptMessage) {
        try {
            System.out.print(promptMessage);
            var index = Input.getInt();
            if (index < 1 || index > menuItems.size()) {
                System.out.printf("Invalid index, try 1-%d\n", menuItems.size());
                index = prompt(promptMessage).index;
            }
            return menuItems.get(index - 1);
        } catch (Exception e) {
            System.out.printf("Invalid input type, try 1-%d\n", menuItems.size());
            return prompt(promptMessage);
        }
    }
}
