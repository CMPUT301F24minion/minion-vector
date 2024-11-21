package com.example.minion_project;

import com.example.minion_project.events.Event;

public class Lottery {
    private Event event;

    // the lottery has access to the event
    public Lottery(Event event){
        this.event=event;
    }

    public void  poolAplicants(){

    }
}
