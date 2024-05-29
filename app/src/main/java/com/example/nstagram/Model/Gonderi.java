package com.example.nstagram.Model;

public class Gonderi {
    private String gonderiId;
    private String gonderen;
    private String gonderiResmi;
    private String gonderiHakkinda;

    public Gonderi() {
    }

    public Gonderi(String gonderiId, String gonderen, String gonderiResmi, String gonderiHakkinda) {
        this.gonderiId = gonderiId;
        this.gonderen = gonderen;
        this.gonderiResmi = gonderiResmi;
        this.gonderiHakkinda = gonderiHakkinda;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public String getGonderen() {
        return gonderen;
    }

    public String getGonderiResmi() {
        return gonderiResmi;
    }

    public String getGonderiHakkinda() {
        return gonderiHakkinda;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public void setGonderiResmi(String gonderiResmi) {
        this.gonderiResmi = gonderiResmi;
    }

    public void setGonderiHakkinda(String gonderiHakkinda) {
        this.gonderiHakkinda = gonderiHakkinda;
    }
}
