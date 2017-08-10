package game;

//import static supporto.Costanti.*;
import static supporto.Costanti.TEMPO_RIPOPOLAMENTO_CIBO;
import static supporto.Costanti.TEMPO_RIPOPOLAMENTO_SERPENTI_BASSO;
import static supporto.Costanti.TEMPO_RIPOPOLAMENTO_SERPENTI_ALTO;
import static supporto.Costanti.LIMITE_SERPENTI_BASSO;
import static supporto.Costanti.LIMITE_SERPENTI_ALTO;
import static supporto.Costanti.TEMPO_BASE;

import java.awt.AWTException;

import LukePack.LP;
import audio.GestoreSuoni;
import gestoreComandi.LettoreComandi;
import popolatori.PopolatoreCibo;
import popolatori.PopolatoreSerpenti;
import video.Visualizzatore;
import visualizzatoreClient.VisualizzatoreClient;

public class Main {

	static Partita partita;
	private static Visualizzatore visualizzatore;

	public static void main(String[] args){
		partita = new Partita();
		avviaClient(partita);
	}

	public static void avviaIlGioco() throws AWTException {
		partita.ImpostaPartita();
		visualizzatore = new Visualizzatore(partita);
		LettoreComandi.initControlliDaTastiera(visualizzatore);
		GestoreSuoni.inizzializzaSuoni();

		GestoreSuoni.playMusicaInLoop();
		cominciaIlGioco(partita);
	}

	public static void avviaClient(Partita partita) {
		new VisualizzatoreClient(partita);
		// l'utente inserisce i dati e clicca su Accedi
		// String errNome = null;
		// String errPassword = null;
		// boolean tuttoOk;
		// tuttoOk = ClasseEsterna.metodoHttp(nomeUtente, password, errNome, errPassword);
		// la classe esterna fornisce metodi per leggere il record del profilo ed eventualmente
		// inviare il nuovo
	}



	private static void cominciaIlGioco(Partita partita) throws AWTException {
		PopolatoreSerpenti.creaPopoloIniziale(partita);
		PopolatoreCibo.aggiungiCiboRandom(partita.getMappa());
		visualizzatore.repaint();
		LP.waitFor(1000);
		GestoreSuoni.playSpawnSound();
		int contaCicli=0;

		while(true) {
			// sistema anti-lag
			long tempoInizioAlgoritmo = System.currentTimeMillis(); 

			contaCicli++;

			if((contaCicli%TEMPO_RIPOPOLAMENTO_CIBO)==0){
				PopolatoreCibo.aggiungiCiboRandom(partita.getMappa());
			}
			
			if(partita.getFattorePopolazione()==1){
				if((contaCicli%(TEMPO_RIPOPOLAMENTO_SERPENTI_BASSO)==0) && partita.getNumeroDiSerpenti()<=LIMITE_SERPENTI_BASSO){
					PopolatoreSerpenti.provaAdInserireUnSerpente(partita);
				}
			} else if(partita.getFattorePopolazione()==2){
				if((contaCicli%(TEMPO_RIPOPOLAMENTO_SERPENTI_ALTO)==0) && partita.getNumeroDiSerpenti()<=LIMITE_SERPENTI_ALTO){
					PopolatoreSerpenti.provaAdInserireUnSerpente(partita);
				}
			}


			partita.eseguiTurni();
			visualizzatore.repaint(); // lo metto dopo in modo che il giocatore ha
			//100 ms per reagire

			// sistema anti-lag
			long tempoFineAlgoritmo = System.currentTimeMillis();
			long ritardoAlgoritmo = tempoFineAlgoritmo-tempoInizioAlgoritmo;
			//if(ritardoAlgoritmo>1) System.out.println("ritardo compensato: "+ritardoAlgoritmo+"/"+TEMPO_BASE+"ms \t cpu usage: " + (int)((ritardoAlgoritmo*1.0/TEMPO_BASE*1.0)*100)+"%");
			if(TEMPO_BASE-(ritardoAlgoritmo)>0){
				LP.waitFor(TEMPO_BASE-(ritardoAlgoritmo));
				//System.out.println(TEMPO_BASE-(ritardoAlgoritmo));
			}
		}
	}

}