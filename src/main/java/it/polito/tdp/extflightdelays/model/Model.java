package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<Integer, Airport>();
	}
	
	public void creaGrafo(int x) { //NUMERO DI COMPAGNIE MINIME 
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//AGGIUNGO VERTICI
		dao.loadAllAirports(idMap);
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		//AGGIUNGO GLI ARCHI
		/*METODO PIU' FACILE: SCORRO LE ROTTE -> LA ROTTA r MI DA GLI AEROPORTI A1 E A2
		  								   		 SE C'E' GIA' L'ARCO, AGGIUNGO/ AGGIORNO IL PESO DI QUELLA ROTTA*/
		List<Rotta> rotte = dao.getRotte(idMap);
		for(Rotta r : rotte) {
			if(this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				
				DefaultWeightedEdge edge = this.grafo.getEdge(r.getA1(), r.getA2());
				if(edge == null) {
					Graphs.addEdgeWithVertices(this.grafo, r.getA1(), r.getA2(), r.getnVoli()); // SE GLI AEROPORTI NON ERANO ANCORA COLLEGATI
				}else {
					double pesoVecchio = this.grafo.getEdgeWeight(edge);
					double pesoNuovo = pesoVecchio + r.getnVoli(); 
					this.grafo.setEdgeWeight(edge, pesoNuovo);
				}
			}
		}
		System.out.println("# VERTICI "+ this.grafo.vertexSet().size());
		System.out.println("# ARCHI "+ this.grafo.edgeSet().size());
	}
	
	public List<Airport> getVertici(){
		List<Airport> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}
	
	//ORA DEVO CALCOLARE IL PERCORSO : CERCO ALBERDO DI VISITA -> MI RICAVO DA ESSO IL PERCORSO
	public List<Airport> getPercorso(Airport a1, Airport a2){
		List<Airport> percorso = new ArrayList<>();
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, a1);
		
		//VISITO GRAFO
		boolean trovato = false;
		while(it.hasNext()) {
			Airport visitato = it.next();
			trovato = true;
		}
		
		//OTTENGO IL PERCORSO
		if(trovato) {
			percorso.add(a2);
			Airport step = it.getParent(a2);
			while(!step.equals(a1)) {
				percorso.add(0,step);
				step = it.getParent(step);
			}
			percorso.add(0,a1);
			return percorso;
		}else
			return null;
	}
	
}



















