package it.uniroma3.spring.snake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.spring.snake.repository.MatchRepository;
import it.uniroma3.spring.snake.model.Match;
import it.uniroma3.spring.snake.model.User;

@Service
public class MatchService {

	@Autowired
	private MatchRepository matchRepository;
	
	public Iterable<Match> findAll() {
		return this.matchRepository.findAll();
	}
	
	@Transactional
	public void add(final Match match) {
		this.matchRepository.save(match);
	}
	
	public Match findbyId(Long id) {
		return this.matchRepository.findOne(id);
	}
	
	public List<Match> findTopTen() {
		return this.matchRepository.findTop10ByOrderByPositionAsc();
	}
	
	public Match findBestMatchOf(User user) {
		return this.matchRepository.findTopByPlayerOrderByPositionAsc(user);
	}
}
