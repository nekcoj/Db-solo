package org.app.menu;

import org.fsdb.Input;

import java.util.ArrayList;

public class Menu {
    private StringBuilder menuOutput;

    private String menuTitle;
    private ArrayList<String> menuItems;

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

    public void setPaddings(int rightPad, int leftPad, int topPad, int bottomPad) {
        leftPadding = leftPad;
        rightPadding = rightPad;
        topPadding = topPad;
        bottomPadding = bottomPad;
    }

    public void setMenuTitle(String title) {
        this.menuTitle = title;
    }

    public void addMenuItem(String name) {
        menuItems.add(name);
        if (name.length() > maxItemLength) {
            maxItemLength = name.length();
            topBorderLength = maxItemLength + String.valueOf(menuItems.size()).length() + rightPadding + leftPadding + 5;
        }
    }

    public void show() {
        menuOutput.append("-".repeat(topBorderLength)).append("\n");
        int titleHalfLen = menuTitle.length() / 2;
        int titleRightPad = (topBorderLength / 2) - titleHalfLen - 1;
        int titleLeftPad = (topBorderLength / 2) - (menuTitle.length() - titleHalfLen) - 1;
        menuOutput.append("|").append(" ".repeat(titleLeftPad)).append(menuTitle).append(" ".repeat(titleRightPad)).append("|\n");
        menuOutput.append("-".repeat(topBorderLength)).append("\n");

        addVerticalPadding(topPadding);

        for (int i = 0; i < menuItems.size(); i++)
            menuOutput.append(addLeftBorder(String.format("[%d] %s", i + 1, addRightBorder(menuItems.get(i))))).append("\n");

        addVerticalPadding(bottomPadding);

        menuOutput.append("-".repeat(topBorderLength)).append("\n");
        System.out.print(menuOutput.toString());
    }

    public int prompt(String promptMessage) {
        try {
            System.out.print(promptMessage);
            var index = Input.getInt();
            if (index < 1 || index > menuItems.size()) {
                System.out.printf("Invalid index, try 1-%d\n", menuItems.size());
                index = prompt(promptMessage);
            }
            return index;
        } catch (Exception e) {
            System.out.printf("Invalid input type, try 1-%d\n", menuItems.size());
            return prompt(promptMessage);
        }
    }
}
