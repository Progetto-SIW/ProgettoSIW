package serpenti;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import audio.GestoreSuoni;
import popolatori.PopolatoreCibo;
import supporto.Direzione;
import supporto.Posizione;
import terrenoDiGioco.Casella;
import terrenoDiGioco.Stanza;
import static supporto.Costanti.*;

public abstract class Serpente {

	private LinkedList<Casella> caselle;
	private String nome;
	private Direzione direzione;
	private Casella casellaDiTesta;
	private boolean isVivo;	
	private int ciboPreso;
	private int numeroUccisioni;
	private long istanteDiNascita;
	private long tempoSopravvivenza;

	public Serpente(String nome, Stanza stanza) {
		costruisciSerpente(nome, stanza, VITA_SERPENTE_DEFAULT);
	}
	
	public Serpente(String nome, Stanza stanza,int lunghezza) {
		costruisciSerpente(nome, stanza, lunghezza);
	}
	
	private void costruisciSerpente(String nome, Stanza stanza,int lunghezza) {
		this.nome=nome;
		this.isVivo=true;
		this.ciboPreso=0;
		this.numeroUccisioni=0;
		this.istanteDiNascita = System.currentTimeMillis();
		// sempre al centro della stanza (20,20)
		Posizione posizionePrimaCasella = new Posizione(DIMENSIONE_STANZA_DEFAULT/2,DIMENSIONE_STANZA_DEFAULT/2);
		// direzione casuale
		Direzione direzioneSerpente = new Direzione();
		this.setDirezione(direzioneSerpente);
		Direzione direzioneCreazioneCaselle = direzioneSerpente.getInversa();
		// creo la testa del serpente
		this.setCaselle(new LinkedList<Casella>());
		
		// pre: la prima casella � sempre libera!
		// faccio il controllo solo sulle altre caselle
		Casella primaCasella = stanza.getCaselle().get(posizionePrimaCasella);
		this.setCasellaDiTesta(primaCasella);
		primaCasella.setStato(CARATTERE_CASELLA_PLAYER1);
		primaCasella.setSerpente(this);
		int vitaCasella = lunghezza;
		primaCasella.setVita(vitaCasella);
		this.getCaselle().add(primaCasella);

		// creo le altre caselle del serpente

		Casella casellaPrecedente = primaCasella;
		for(int i=0; i<lunghezza-1; i++){
			Casella casella = stanza.getCasellaAdiacente(direzioneCreazioneCaselle, casellaPrecedente);
			
			// controllo casella
			if(casella.isMortale()) return; // serpente pi� corto, pazienza
			
			casella.setStato(CARATTERE_CASELLA_PLAYER1);
			casella.setSerpente(this);
			vitaCasella--;
			casella.setVita(vitaCasella);
			this.getCaselle().add(casella);
			casellaPrecedente = casella;
		}
	}

	public Stanza getStanzaCorrente() {
		return this.caselle.getFirst().getStanza();
	}

	public Casella getCasellaDiTesta(){
		return this.casellaDiTesta;
	}

	public Casella getCasellaDiCoda(){
		return this.caselle.getLast();
	}

	public int getHP(){
		return this.casellaDiTesta.getVita();
	}

	public LinkedList<Casella> getCaselle() {
		return caselle;
	}

	public void setCaselle(LinkedList<Casella> caselle) {
		this.caselle = caselle;
	}

	public Stanza getStanza() {
		return this.getCasellaDiTesta().getStanza();
	}

	public void sposta(Direzione d){
		this.setDirezione(d);
		Casella nuovaCasella = this.getCasellaDiTesta().getStanza().getCasellaAdiacente(d, this.getCasellaDiTesta());

		if(!nuovaCasella.isMortale()){
			if(nuovaCasella.isCibo()){
				if(nuovaCasella.isTestaDiSerpente()){
					this.incrementaVitaSerpente(QTA_CIBO_TESTA_SERPENTE);
				} else {
					this.incrementaVitaSerpente(QTA_CIBO_BASE);
				}
				nuovaCasella.setCasellaOccupataDalSerpente(this,this.getHP()+1,this.getCasellaDiTesta().getStato());
			} else {
				nuovaCasella.setCasellaOccupataDalSerpente(this,this.getHP(),this.getCasellaDiTesta().getStato());
			}
			int vitaSerpente = this.getHP();


			nuovaCasella.setVita(vitaSerpente);
			this.setCasellaDiTesta(nuovaCasella);

			Iterator<Casella> iteratore = this.getCaselle().iterator();
			while(iteratore.hasNext()){
				Casella temp = iteratore.next();
				temp.decrementaVita(); // la testa non si tocca
				if(temp.isMorta()){
					temp.libera();
					iteratore.remove();
				}
			}
			this.caselle.add(nuovaCasella);
		} else { // casella mortale
			if(nuovaCasella.isOccupataDaSerpente()){
				Serpente altroSerpente = nuovaCasella.getSerpente();
				if(altroSerpente.getCasellaDiTesta().getPosizione().equals(nuovaCasella.getPosizione())){
					altroSerpente.muori();
				}
			}
			this.muori();
		}
	}

	public void incrementaVitaSerpente(int qta) {
		this.ciboPreso+=qta;
		for(Casella c : this.getCaselle()){
			if(c.getVita()+qta<=VITA_SERPENTE_MASSIMA){
				c.incrementaVita(qta);
			} else {
				c.setVita(VITA_SERPENTE_MASSIMA);
			}
		}
	}

	public void setCasellaDiTesta(Casella nuovaCasella) {
		this.casellaDiTesta = nuovaCasella;
	}

	public void muori(){
		controllaUccisione();
		rilasciaCibo();
		this.isVivo = false;
	}

	private void controllaUccisione() {
		Serpente serpenteDavanti = this.getCasellaDiTesta().getCasellaAdiacente(this.direzione).getSerpente();
		Serpente serpenteAdestra = this.getCasellaDiTesta().getCasellaAdiacente(this.direzione.getDirezioneDX()).getSerpente();
		Serpente serpenteAsinistra = this.getCasellaDiTesta().getCasellaAdiacente(this.direzione.getDirezioneSX()).getSerpente();
		ArrayList<Serpente> serpentiPerControllo = new ArrayList<Serpente>();
		serpentiPerControllo.add(serpenteDavanti);
		serpentiPerControllo.add(serpenteAdestra);
		serpentiPerControllo.add(serpenteAsinistra);
		
		boolean uccisione = false;
		int i=0;
		while (i<3 && uccisione == false){ // 0,1,2
			Serpente s = serpentiPerControllo.get(i);
			if(s!=null){
				if(!s.getNome().equals(this.nome)){
					s.miHaiUcciso();
					uccisione = true;
				}
			}
			i++;
		}
	}

	protected void rilasciaCibo() {
		PopolatoreCibo.rilasciaCiboNelleCaselle(this.caselle);
		this.caselle.clear();
	}

	abstract public void FaiMossa();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Direzione getDirezione() {
		return direzione;
	}

	public void setDirezione(Direzione direzione) {
		this.direzione = direzione;
	}

	public boolean isMorto() {
		return !this.isVivo;
	}

	public void SetIsVivo(boolean b) {
		this.isVivo = b;
	}

	public int getCiboPreso() {
		return this.ciboPreso;
	}

	public void setCiboPreso(int ciboPreso) {
		this.ciboPreso = ciboPreso;
	}

	@Override
	public String toString(){
		return this.getNome() + " \t " + this.getClass().getSimpleName() + " \t " + (this.getHP()*MOLTIPLICATORE_PUNTEGGIO_CIBO);
	}

	public void miHaiUcciso(){
		this.numeroUccisioni++;
		if(this.getNome().equals(NOME_PLAYER_1)){
			GestoreSuoni.playSlainSound();
		}
	}

	public int getNumeroUccisioni() {
		return numeroUccisioni;
	}

	public void setNumeroUccisioni(int numeroUccisioni) {
		this.numeroUccisioni = numeroUccisioni;
	}

	public double getTempoSopravvissuto() {
		if(this.isVivo){
			this.tempoSopravvivenza = System.currentTimeMillis()-this.istanteDiNascita;
			return tempoSopravvivenza;
		} else {
			return this.tempoSopravvivenza;
		}
	}

	public boolean isTesta(Casella casella){
		if(this.getCasellaDiTesta().equals(casella)) return true;
		return false;
	}
	
	public abstract int getExLunghezza();

}
