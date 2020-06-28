package it.polito.tdp.extflightdelays.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	ExtFlightDelaysDAO dao;
	Map<Integer,Airport> idMap;
	private Graph<Airport,DefaultWeightedEdge> graph;
	List<Airport> airports;
	
	Double bestOre;
	List<Airport> bestItinerario;
	
	public Model() {
		dao=new ExtFlightDelaysDAO();
		idMap=new HashMap<>();
	}
	
	
	public void creaGrafo(int x) {
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap=new HashMap<>();
		
		dao.getVertici(x,idMap);
		Graphs.addAllVertices(graph, idMap.values());
		
		for(Arco a:dao.getArchi(idMap)) {
			if(!this.graph.containsEdge(a.getA1(), a.getA2())) {
				Graphs.addEdgeWithVertices(graph, a.getA1(), a.getA2(), a.getPeso());
			}
		}
	}
	
	public List<Arco> getConnessi(Airport a){
		List<Arco> archi=new ArrayList<>();
		
		List<Airport> vicini=Graphs.neighborListOf(graph, a);
		for(Airport v :vicini) {
			double peso=this.graph.getEdgeWeight(this.graph.getEdge(a, v));
			archi.add(new Arco(a,v,peso));
		}
		
		Collections.sort(archi);
		return archi;
		
	}
	
	public int nVertici() {
		return this.graph.vertexSet().size();
	}

	public int nArchi() {
		return this.graph.edgeSet().size();
	}
	
	public Collection<Airport> Airport(){
		return idMap.values();
	}
	
	public List<Airport> getCammino(int ore, Airport a){
		this.bestOre=0.0;
		this.bestItinerario=new ArrayList<>();
		
		List<Airport> parziale=new ArrayList<>();
		parziale.add(a);
		ricorsione(parziale,ore,a);
		return bestItinerario;
	}

	private void ricorsione(List<Airport> parziale, int oreMax, Airport a) {

		double ore = this.calcolaOre(parziale);
		if (ore < oreMax) {
			if (ore > bestOre) {
				this.bestOre = ore;
				this.bestItinerario = new ArrayList<>(parziale);
			}
		} else
			return;
		List<Airport> vicini = Graphs.neighborListOf(graph, a);
		for (Airport v : vicini) {
			if (!parziale.contains(v)) {
				parziale.add(v);
				ricorsione(parziale, oreMax, v);
				parziale.remove(parziale.size() - 1);
			}
		}

	}

	
	private double calcolaOre(List<Airport> parziale) {
		double ore=0.0;
		
		for(int i=1; i<parziale.size();i++) {
			if(this.graph.getEdge(parziale.get(i), parziale.get(i-1)) != null) {
				
			Double oreNew=this.graph.getEdgeWeight(this.graph.getEdge(parziale.get(i-1),parziale.get(i)));
			ore+=2*oreNew;
		}
	}
		
		return ore;
	}


	public Double getBestOre() {
		return bestOre;
	}
	
	
	
	
	
}
