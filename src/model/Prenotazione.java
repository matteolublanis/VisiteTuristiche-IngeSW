package model;

import java.util.UUID;

import dto.VisitaDTO;

public class Prenotazione {
    private String id;
    private String user;
    private VisitaDTO visitDetails;

    public Prenotazione(String user, VisitaDTO visitDetails) {
        this.id = UUID.randomUUID().toString(); 
        this.user = user;
        this.visitDetails = visitDetails;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public VisitaDTO getVisitDetails() {
        return visitDetails;
    }
    
}