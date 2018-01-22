package eu.ensg.tsi.azarzelli.gama.io;

import eu.ensg.tsi.azarzelli.gama.domain.Terrain;

public interface IWriter {
	public void write(Terrain terrain, String filename);
}
