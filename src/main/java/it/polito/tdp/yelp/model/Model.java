package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<Business, Double> mediaLocale;

	// lista che mi serve per il calcolo del percorso migliore
	private List<Business> percorsoBest;
	
	public Model() {
		dao = new YelpDao();
		mediaLocale = new HashMap<>();
	}
	
	public void creaGrafo(String citta, int anno) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, this.dao.getAllBusinessWithCityAndYear(citta, anno));
		
		
		for (Business b : this.grafo.vertexSet()) {
			double media = this.dao.calcolaMediaRecensioniWithLocaleAndAnno(b, anno);
			mediaLocale.put(b, media);
		}
		
		// Aggiunta degli archi 
		for (Business b1 : this.grafo.vertexSet()) {
			for (Business b2 : this.grafo.vertexSet()) {
				if ((mediaLocale.get(b1) - mediaLocale.get(b2)) == 0) {
					// non faccio nulla
				}
				if ((mediaLocale.get(b1) - mediaLocale.get(b2)) > 0) {
					Graphs.addEdge(this.grafo, b2, b1, (mediaLocale.get(b1) - mediaLocale.get(b2)));
				} else if ((mediaLocale.get(b1) - mediaLocale.get(b2)) < 0) {
					Graphs.addEdge(this.grafo, b1, b2, (mediaLocale.get(b2) - mediaLocale.get(b1)));
				}
			}
		}
	}
	
	public Business calcolaLocaleMigliore() {
		Business localeMigliore = null;
		
		double max = 0.0;
		
		for (Business b : this.grafo.vertexSet()) {
			
			double sommaEntranti = 0.0;
			for (DefaultWeightedEdge e : this.grafo.incomingEdgesOf(b)) {
				sommaEntranti += this.grafo.getEdgeWeight(e);
			}
			
			double sommaUscenti = 0.0;
			for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(b)) {
				sommaUscenti += this.grafo.getEdgeWeight(e);
			}
			// System.out.println("Business " + b + " (" + (sommaEntranti - sommaUscenti) +")\n");
			if ((sommaEntranti - sommaUscenti) > max) {
				max = sommaEntranti - sommaUscenti;
				localeMigliore = b;
			}
		}
	
		return localeMigliore;
	}
	
	// Metodo che prepara la ricorsione
	public List<Business> calcolaPercorsoMigliore(Business partenza, Business arrivo, double soglia){
		this.percorsoBest = null;
		
		List<Business> parziale = new ArrayList<Business>(); // questa è la lista che andremo a costruire man mano
		parziale.add(partenza); // aggiungo il locale di partenza perchè rappresenta il punto da cui ha inizio la ricorsione
		
		// Adesso possiamo far partire la ricorsione
		cerca(parziale, 1, arrivo, soglia);
		
		return this.percorsoBest;
	}
	
	// ALGORITMO VERAMENTE RICORSIVO
	public void cerca(List<Business> parziale, int livello, Business arrivo, double soglia) {
		Business ultimo = parziale.get(parziale.size()-1);
		// condizione di terminazione (ho trovato il locale di destinazione)
		if (ultimo.equals(arrivo)) {
			
			if(this.percorsoBest == null) {
				this.percorsoBest = new LinkedList<>(parziale);
				return;
			} else if (parziale.size() < this.percorsoBest.size()) {
				this.percorsoBest = new LinkedList<>(parziale);
				return;
			} else {
				// in questo caso la dimensione di parziale è maggiore o uguale della dimensione della soluzione migliore corrente
				// e quindi NON facciamo nulla
				return;
			}
			
		}
		
		// generazione dei vari percorsi cercando di aggiungere i vertici successori 
		// dell'ultimo vertice inserito : cioè cerco di uscire dall'ultimo vertice spostandomi
		// verso uno dei vertici ad esso collegati attraversando degli archi uscenti il cui
		// peso è maggiore della soglia prefissata
		
		for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(ultimo)) {
			if (this.grafo.getEdgeWeight(e) > soglia) {
				Business prossimo = Graphs.getOppositeVertex(this.grafo, e, ultimo);
				if (!parziale.contains(prossimo)) {
					parziale.add(prossimo);
					cerca(parziale, livello+1, arrivo, soglia);
					parziale.remove(parziale.size()-1);
				}
						
			}
		}
		
	}
	
	public Set<Business> getAllVertici(){
		return this.grafo.vertexSet();
	}
	
	public List<String> getAllCitta(){
		return this.dao.getAllCitta();
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
}
