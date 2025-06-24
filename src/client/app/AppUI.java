package client.app;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import dto.DataDisponibilitaDTO;
import dto.LuogoDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;
import utility.Time;

public class AppUI implements ViewInterface, Observable{ 
	
    private Scanner sc = new Scanner(System.in);
    private List<Observer> observers = new ArrayList<>();
    private Map<String, CompletableFuture<Object>> richiesteInCorso = new HashMap<>();
    
    @Override
    public void mostraMessaggio(String messaggio) {
        if (messaggio != null && !messaggio.isEmpty()) {
            System.out.println(messaggio);
        }
    }
    
    @Override
    public void mostraElencoAzioni(List<String> azioni) {
        mostraMessaggio("Quale operazione desidera (ESC per uscire)?");
        for (int i = 0; i < azioni.size(); i++) {
            System.out.println((i + 1) + ") " + azioni.get(i));
        }
        
        String input = leggiInputDiretto("l'azione da eseguire (da 1 a " + azioni.size() + " o esc)");
        notifyObservers(new UiEvent(UiEventType.ESEGUI_AZIONE, input));
    }
    
    @Override
    public void richiediInput(UiRequest richiesta) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(richiesta.getRichiedenteId(), future);

        Object risultato = gestisciRichiestaInput(richiesta);
        richiestaCompletata(richiesta.getRichiedenteId(), risultato);
    }
    
    private Object gestisciRichiestaInput(UiRequest richiesta) {
        switch (richiesta.getTipo()) {
            case RICHIEDI_INPUT:
                return leggiInputDiretto(richiesta.getMessaggio());
                
            case RICHIEDI_CREDENZIALI:
                return leggiCredenziali();
                
            case RICHIEDI_SI_NO:
                return leggiSiNo(richiesta.getMessaggio());
                
            case RICHIEDI_DATA_VALIDA:
                return leggiDataValida(richiesta.getMessaggio());
                
            case RICHIEDI_INT:
                return leggiInt(richiesta.getMessaggio());
                
            case RICHIEDI_ORA_VALIDA:
                return leggiOraValida(richiesta.getMessaggio());
                
            case RICHIEDI_NUMERO_CON_LIMITE:
                Integer limite = (Integer) richiesta.getParametriAggiuntivi();
                return leggiNumeroConLimite(richiesta.getMessaggio(), limite);
                
            case RICHIEDI_LUOGO:
                return leggiLuogo();
                
            case RICHIEDI_VOLONTARI:
                return leggiVolontari();
                
            case RICHIEDI_GIORNI_PRENOTABILI:
                return leggiGiorniPrenotabili();
                
            default:
                return null;
        }
    }
    
    private String leggiInputDiretto(String messaggio) {
        System.out.println("Inserisci " + messaggio + ":");
        return sc.nextLine();
    }
    
    private Credenziali leggiCredenziali() {
        String username = leggiInputDiretto("username (ESC per tornare indietro)");
        if (username.equalsIgnoreCase("esc")) return null;
        String password = leggiInputDiretto("password");
        if (leggiSiNo("Confermi?")) {
            return new Credenziali(username, password);
        }
        return leggiCredenziali(); 
    }
    
    private boolean leggiSiNo(String messaggio) {
        System.out.println(messaggio);
        do {
            String answer = leggiInputDiretto("si o no");
            switch (answer.toLowerCase()) {
                case "si": return true;
                case "no": return false;
                default: 
                    System.out.println("Formato non valido, inserire si/no");
                    break;
            }
        } while (true);
    }
    
    private String leggiDataValida(String messaggio) {
        String data;
        do {
            data = leggiInputDiretto(messaggio);
            if (!Time.isValidDate(data)) {
                System.out.println("Formato data non valido, deve essere (dd-mm-yyyy)");
            }
        } while (!Time.isValidDate(data));
        return data;
    }
    
    private int leggiInt(String messaggio) {
        System.out.println("Inserisci " + messaggio + ":");
        while (!sc.hasNextInt()) {
            System.out.println("Formato non valido, reinserire.");
            sc.nextLine();
        }
        int result = sc.nextInt();
        sc.nextLine();
        return result;
    }
    
    private String leggiOraValida(String messaggio) {
        String ora;
        do {
            ora = leggiInputDiretto(messaggio);
            if (!Time.isValidHour(ora)) {
                System.out.println("Formato non corretto, inserire tipo 10:30.");
            }
        } while (!Time.isValidHour(ora));
        return ora;
    }
    
    private int leggiNumeroConLimite(String messaggio, int limite) {
        int n;
        do {
            n = leggiInt(messaggio);
            if (n <= limite) {
                System.out.println("Non può essere più piccolo o uguale di " + limite + ".");
            }
        } while (n <= limite);
        return n;
    }
    
    private LuogoDTO leggiLuogo() {
        String tag = leggiInputDiretto("tag del luogo");
        String nome = leggiInputDiretto("nome del luogo");
        String descrizione = leggiInputDiretto("descrizione del luogo");
        String collocazione = leggiInputDiretto("collocazione del luogo");
        return new LuogoDTO(tag, nome, descrizione, collocazione);
    }
    
    private ArrayList<Credenziali> leggiVolontari() {
        ArrayList<Credenziali> volontari = new ArrayList<>();
        boolean continua = true;
        
        do {
            if (leggiSiNo("Vuoi associare un nuovo volontario per questo tipo di visita?")) {
                System.out.println("Inserisci le credenziali del nuovo volontario.");
                Credenziali c = leggiCredenziali();
                if (c != null) {
                    volontari.add(c);
                }
            } else {
                String username = leggiInputDiretto("username del volontario che gestirà la visita");
                volontari.add(new Credenziali(username, null));
            }
            
            continua = (volontari.size() == 0) || leggiSiNo("Vuoi inserire un altro volontario?");
        } while (continua);
        
        return volontari;
    }
    
    private ArrayList<Integer> leggiGiorniPrenotabili() {
        ArrayList<Integer> giorni = new ArrayList<>();
        boolean continua = true;
        
        do {
            int giorno = leggiInt("giorno prenotabile della visita (1-7)");
            if (giorno < 1 || giorno > 7) {
                System.out.println("Numero inserito non valido, deve essere tra 1 e 7.");
            } else if (!giorni.contains(giorno)) {
                giorni.add(giorno);
                continua = leggiSiNo("Vuoi aggiungere un altro giorno prenotabile?");
            } else {
                System.out.println("Giorno già inserito!");
                continua = true;
            }
            if (giorni.size() == 7) continua = false;
        } while (continua);
        
        return giorni;
    }
    
    @Override
    public void richiestaCompletata(String richiedenteId, Object risultato) {
        CompletableFuture<Object> future = richiesteInCorso.remove(richiedenteId);
        if (future != null) {
            future.complete(risultato);
        }
        
        // Notifica il controller del risultato
        notifyObservers(new UiEvent(UiEventType.INPUT_RESPONSE, 
            new AbstractMap.SimpleEntry<>(richiedenteId, risultato)));
    }
    
    @Override
    public void mostraListaLuoghi(List<LuogoDTO> list) {
        for (LuogoDTO luogo : list) {
            if (luogo.getTipiAssociati() == null) {
                System.out.println(luogo.getTitolo());
            } else {
                System.out.println(luogo.getTitolo() + ": " + luogo.getTipiAssociati().toString());
            }
        }
    }
    
    @Override
    public void mostraListaVisite(List<VisitaDTO> list) {
        for (VisitaDTO visita : list) {
            if (!visita.getStato().equals("cancellata")) {
                StringBuilder result = new StringBuilder("-----------");
                result.append("\nTitolo: ").append(visita.getTitolo());
                result.append("\nGiorno: ").append(visita.getGiorno());
                result.append("\nLuogo: ").append(visita.getLuogo());
                result.append("\nStato: ").append(visita.getStato());
                
                if (visita.getDaAcquistare() != null) {
                    result.append("\nDescrizione: ").append(visita.getDescrizione());
                    result.append("\nPunto d'incontro: ").append(visita.getPuntoIncontro());
                    result.append("\nOra d'inizio: ").append(visita.getOraInizio());
                    result.append("\nDa acquistare: ").append(visita.getDaAcquistare());
                    result.append("\nTag: ").append(visita.getTag());
                    
                    if (visita.getPrenotazioni() != null) {
                        for (PrenotazioneDTO p : visita.getPrenotazioni()) {
                            result.append("\nCodice: ").append(p.getCodice());
                            result.append(", n. iscritti: ").append(p.getNum_da_prenotare());
                        }
                    }
                }
                System.out.println(result.toString());
            } else {
                String result = "-----------\nTitolo: " + visita.getTitolo() + 
                              "\nGiorno mancato svolgimento: " + visita.getGiorno() + 
                              "\nStato: " + visita.getStato();
                System.out.println(result);
            }
        }
    }
    
    @Override
    public void mostraListaTipiVisita(List<TipoVisitaDTO> list) {
        for (TipoVisitaDTO tipo : list) {
            System.out.println(tipo.getTag() + " " + tipo.getTitolo());
        }
    }
    
    @Override
    public void mostraListaUtenti(List<UserDTO> list) {
        for (UserDTO user : list) {
            System.out.println(user.toString());
        }
    }
    
    @Override
    public void mostraListaPrenotazioni(List<PrenotazioneDTO> list) {
        for (PrenotazioneDTO prenotazione : list) {
            System.out.println("Prenotazione " + prenotazione.getCodice() + ": " + 
                             prenotazione.getGiorno() + ", " + prenotazione.getTag_visita());
            System.out.println("N. iscritti: " + prenotazione.getNum_da_prenotare());
        }
    }
    
    @Override
    public void mostraListaDateDisponibilita(List<DataDisponibilitaDTO> list) {
        for (DataDisponibilitaDTO data : list) {
            System.out.println("Giorni " + data.getTag() + ": " + data.getGiorni().toString());
        }
    }
    
    // Pattern Observer
    @Override
    public void attach(Observer o) {
        observers.add(o);
    }
    
    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }
    
    @Override
    public void notifyObservers(UiEvent evento) {
        for (Observer o : observers) {
            o.update(evento);
        }
    }
    
    public void start() {
        System.out.println("Benvenuto!");
        String dataApp = leggiDataValida("data di app");
        Time.setActualDate(dataApp);
        
        notifyObservers(new UiEvent(UiEventType.START, null));
        
    }
    
    public void stop() {
        System.out.println("Arrivederci!");
        sc.close();
        notifyObservers(new UiEvent(UiEventType.STOP, null));
        System.exit(0);
    }
}
