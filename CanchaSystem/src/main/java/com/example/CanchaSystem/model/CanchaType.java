package com.example.CanchaSystem.model;

public enum CanchaType {
    FUTBOL_5(10),
    FUTBOL_7(14),
    FUTBOL_9(18),
    FUTBOL_11(22);

    int totalPlayers;
    CanchaType(int totalPlayers){
        this.totalPlayers=totalPlayers;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }
}

