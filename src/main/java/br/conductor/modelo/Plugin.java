package br.conductor.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Plugin.class)
public class Plugin {
	
	private String nome;
	private String description;
	private List<Release> releases;
	private String status;
	

	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public List<Release> getReleases() {
		return releases;
	}


	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "Plugin ["
				+ "nome=" + nome + ", "
				+ "descrição=" + description + ","
				+ "releases=" + releases + ", "
				+ "status=" + status + "]";
	}	

}