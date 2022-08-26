package com.example.mvvm_example.repositories;

import android.view.View;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRepository {

    String[] drinksListRemote = {"Coffee", "Slushy", "Smoothie", "Juice"};

    public  void suggestNewDrink(IDrinkCallback drinkCallback){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        //Before executing background task

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000); //Mimic server request / long execution

                    String drinkName = drinksListRemote[new Random()
                            .nextInt(drinksListRemote.length)];
                    drinkCallback.onDrinkSuggested(drinkName);
                } catch (InterruptedException e) {
                    drinkCallback.onErrorOccurred();
                    e.printStackTrace();
                } catch (Exception e){
                    drinkCallback.onErrorOccurred();
                    e.printStackTrace();
                }
            }
        });
    }

    public interface IDrinkCallback {
        void onDrinkSuggested(String drinkName);
        void onErrorOccurred();
    }
}
