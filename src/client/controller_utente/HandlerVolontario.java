package client.controller_utente;

import java.util.List;
import archivio.AppManager;
import archivio.ArchivioFactory;
import archivio.UserInfoManager;
import client.app.ViewInterface;
import client.log_events.AppEvent;
import dto.DataDisponibilitaDTO;
import dto.VisitaDTO;
import utility.MethodName;

public class HandlerVolontario extends ControllerUtente {
    
    private AppManager appPlan;
    private UserInfoManager userInfo;
    
    public HandlerVolontario(ViewInterface view, String connectionCode, int tipoApp) {
        super(tipoApp, view);
        this.connectionCode = connectionCode;
        userInfo = ArchivioFactory.createUserInfoManager(tipoApp);
        appPlan = ArchivioFactory.createAppManager(tipoApp);
    }
    
    @MethodName("Visualizza i tipi di visita a cui sei collegato")
    public void visualizzaTipiVisita() {
        view.mostraListaTipiVisita(userInfo.getElencoTipiVisiteVolontario(connectionCode));
    }
    
    private void visualListVisitDTO(List<VisitaDTO> visite) {
        if (visite.size() != 0) {
            view.mostraListaVisite(visite);
        } else {
            catchEvent(AppEvent.NO_CONFIRMED_VISIT);
        }
    }
    
    @MethodName("Visualizza le visite confermate che gestirai")
    public void vediVisiteConfermate() {
        List<VisitaDTO> visite = userInfo.visiteConfermateVolontario(connectionCode);
        visualListVisitDTO(visite);
    }
    
    @MethodName("Comunica le tue prossime disponibilità")
    public void comunicaDisponibilita() {
        if (appPlan.getPossibilitaDareDisponibilita()) {
            List<DataDisponibilitaDTO> dateDisponibilita = userInfo.getDatePerDisponibilita(connectionCode);
            
            if (dateDisponibilita == null) {
                catchEvent(AppEvent.NO_AVAILABILITY);
            } else {
                visualDateDisponibilita(dateDisponibilita);
                do {
                    String data = richiediDataValida("data in cui dai disponibilità");
                    if (userInfo.inserisciDisponibilita(connectionCode, data)) {
                        catchEvent(AppEvent.INSERTED_DISPONIBILITY);
                    } else {
                        catchEvent(AppEvent.NOT_INSERTED_DISPONIBILITY);
                    }
                } while (chiediSiNo("Vuoi aggiungere altre disponibilità?"));
            }
        } else {
            catchEvent(AppEvent.CANT_ADD_DISPONIBILITY);
        }
    }
    
    private void visualDateDisponibilita(List<DataDisponibilitaDTO> dateDisponibilita) {
        view.mostraListaDateDisponibilita(dateDisponibilita);
    }
}