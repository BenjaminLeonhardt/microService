package MSBenchmark.microServices;

import java.util.ArrayList;

import grpcServices.StapelMsg;

public class FortschrittgRPC extends Fortschritt{
	private StapelMsg stapelGRPC;
	private StapelMsg stapelGRPCgedreht;
	private int batchAnzahl;
	private int aktuellerBatch;

	
	public FortschrittgRPC(){
		super();
	}
	
	public FortschrittgRPC(Fortschritt f){
		super(f);
	}
	
	public FortschrittgRPC(int parameter, int von, int bis, long startZeit, String titel, int proto, int stapelgroesse) {
		super(parameter, von, bis, startZeit, titel, proto, stapelgroesse);
	}

	public StapelMsg getStapelGRPC() {
		return stapelGRPC;
	}

	public void setStapelGRPC(StapelMsg stapelGRPC) {
		this.stapelGRPC = stapelGRPC;
	}

	public StapelMsg getStapelGRPCgedreht() {
		return stapelGRPCgedreht;
	}

	public void setStapelGRPCgedreht(StapelMsg stapelGRPCgedreht) {
		this.stapelGRPCgedreht = stapelGRPCgedreht;
	}

	public int getBatchAnzahl() {
		return batchAnzahl;
	}

	public void setBatchAnzahl(int batchAnzahl) {
		this.batchAnzahl = batchAnzahl;
	}

	public int getAktuellerBatch() {
		return aktuellerBatch;
	}

	public void setAktuellerBatch(int aktuellerBatch) {
		this.aktuellerBatch = aktuellerBatch;
	}



}
