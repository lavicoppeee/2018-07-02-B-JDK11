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
	
	
	
}
