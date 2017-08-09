package gestorePunteggi;

import game.Partita;
import server.model.Match;

public class MatchFactory {
	
	public Match buildMatch(Partita partita){
		Match m = new Match(); 
		m.setScore(partita.getPunteggioPlayer1());
		m.setTime((long)partita.getSerpentePlayer1().getTempoSopravvissuto()/1000);
	    m.setKills(partita.getSerpentePlayer1().getNumeroUccisioni());
	    return m;
	}
	
}
