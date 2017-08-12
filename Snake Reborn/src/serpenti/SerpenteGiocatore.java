package serpenti;

import static supporto.Costanti.*;

import audio.GestoreSuoni;
import gestorePunteggi.GestorePunteggi;
import terrenoDiGioco.Casella;
import terrenoDiGioco.Stanza;

public class SerpenteGiocatore extends Serpente {
	
	private int exLunghezza;

	public SerpenteGiocatore(String nome, Stanza stanza) {
		super(nome, stanza);
		for(Casella c:this.getCaselle()){
			c.setStato(CARATTERE_CASELLA_PLAYER1);		
		}
	}
	
	public SerpenteGiocatore(String nome, Stanza stanza, int lunghezza) {
		super(nome, stanza, lunghezza);
		for(Casella c:this.getCaselle()){
			c.setStato(CARATTERE_CASELLA_PLAYER1);		
		}
	}

	@Override
	public void FaiMossa() {
		if(!super.isMorto()){
			super.sposta(this.getDirezione());
		}
	}
	
	public String getNomeGiocatore() {
		return super.getNome();
	}

	public void setNomeGiocatore(String nomeGiocatore) {
		super.setNome(nomeGiocatore);
	}
	
	@Override
	public void incrementaVitaSerpente(int qta) {
		GestoreSuoni.playTakeSound();
		super.incrementaVitaSerpente(qta);
	}
	
	public void muori(){
		GestoreSuoni.playExplodeSound();
		GestorePunteggi.inviaPunteggio();
		this.exLunghezza=this.getHP();
		super.rilasciaCibo();
		super.getCaselle().clear();
		super.SetIsVivo(false);
	}
	
	public int getExLunghezza(){
		return this.exLunghezza;
	}

}
