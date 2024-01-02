package com.test;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



public class App {

    private static Map<String, Object> dataMap = new ConcurrentHashMap<>();
    private Set<String> keys = dataMap.keySet();







    public static void main(String[] args) {
        App app = new App();

        app.run();
    }

    public void run() {
        setup.setupGame();
        // Value to save gameData Note: will change in settings later
        int save = 60; //Set to 10000 seconds when testing

        // Current update process: Calculate and update information and sleep for 1 second
        while (true){


            update();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Once save counter gets to Zero save data and reset counter.
            save--;
            if(save<=0){
                save = 60;
                //insert save code
                saveData();
            }

        }
    }


    public void update(){
        // I'm leaving this here cause i removed it before anyone got to see it
        //int studQty = student.getQuantity(); // I laugh at the dumbest jokes, it gets worse if I'm tired

        // This will run the current calculations and update any info and visuals needed

        double moneyProduction = 0;
        double unitProduction = 0.0;
        double totalProduction = 0.0;
        double currentCoin = 0;
        double qty = 0;
        double bonusMilestone;
        int cost = 0;




        for(String key : keys){
            if(key.equals("Upgrade")){
                //temp
            }else {
                gameAspects objects = (gameAspects) dataMap.get(key);
                if (key.equals("Money")) {
                    currentCoin = objects.getQuantity();
                } else {
                    // Grab Production values
                    qty = objects.getQuantity();
                    cost = EquationBuilder.main(key);
                    objects.setCost(cost);
                    // Fix logic to handle multiple milestones
                    objects.setBonus();
                    bonusMilestone = objects.getBonus();
                    unitProduction = objects.getProduction() * bonusMilestone;
                    totalProduction = qty * unitProduction;
                    // Testing
//                System.out.println(String.format("Unit: %s\nQty: %s\nProduct/Unit: %s\nTotal: %s",key,qty,unitProduction,totalProduction));

                    moneyProduction += totalProduction;
                }

                if (key.equals("Student")) {
                    setup.studentButton.setText(String.format("Student \n Quantity: %s\n Price: %s", qty, cost));
                } else if (key.equals("Junior")) {
                    setup.juniorButton.setText(String.format("Junior \n Quantity: %s\n Price: %s", qty, cost));
                } else if (key.equals("Senior")) {
                    setup.seniorButton.setText(String.format("Senior \n Quantity: %s\n Price: %s", qty, cost));
                } else if (key.equals("Manager")) {
                    setup.managerButton.setText(String.format("Manager \n Quantity: %s\n Price: %s", qty, cost));
                }
            }
        }

        double newCoin = (currentCoin+moneyProduction);
        gameAspects money = (gameAspects) dataMap.get("Money");
        money.setQuantity(newCoin);
        setup.moneyLabel.setText(String.format("Money: %s",(int)newCoin));


    }


    public static Map<String, Object> getDataMap() {
        return dataMap;
    }

    // Right now the save and load in data is a mess so I'll fix it at somepoint
    private void saveData(){

        Gson gson = new GsonBuilder().serializeNulls()
                .registerTypeAdapter(gameAspects.class, (JsonDeserializer<gameAspects>) (json, typeOfT, context) -> {
                    JsonObject jsonObject = json.getAsJsonObject();
                    String focus = jsonObject.get("focus").getAsString();
                    // Check for specific fields to decide which subclass to instantiate
                    if ("Money".equals(focus)) {
                        // Handle instantiation for Money subclass
                        Double quantity = jsonObject.get("quantity").getAsDouble();
                        // Instantiate Money object
                        return new Money(focus,quantity,null,0.0);
                    } else if ("Student".equals(focus)) {
                        // Handle instantiation for Student subclass
                        Double quantity = jsonObject.get("quantity").getAsDouble();
                        Integer cost = jsonObject.get("cost").getAsInt();
                        // Instantiate Student object
                        return new Student(focus, quantity, cost,0.0);
                    }
                    // Handle other cases or return null/default object
                    return null;
                }).create();
        String json = gson.toJson(dataMap);

        try (FileWriter writer = new FileWriter("data.json")){
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
