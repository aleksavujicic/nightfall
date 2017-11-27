package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeeConfig;
import deimophobe.nightfall.bungee.PortReserver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 16/11/17.
 */
public abstract class TemplatedServer extends MinecraftServer {
	//private final ServerType type;
	
	private final int port;
	
	public TemplatedServer(ServerType type) throws IOException {
		this(copyTemplateFolder(type.getSrcFolder(), type.getRunPrefix()), PortReserver.getReserver().findAndReservePort());
	}
	
	private TemplatedServer(PairOfRunFolderAndInternalName pair, int port) throws IOException {
		super(pair.file, pair.name, port);
		this.port = port;
	}
	
	@Override
	public void stop() {
		super.stop();
		PortReserver.getReserver().releasePort(port);
	}
	
	
	
	
	// Staticy goodness cause java sometimes sucks ass
	private static class PairOfRunFolderAndInternalName {
		private final String name;
		private final File file;
		
		private PairOfRunFolderAndInternalName(String name, File file) {
			this.name = name;
			this.file = file;
		}
	}
	
	private static final int MAX_FOLDER_NUMBER = 100;
	private static PairOfRunFolderAndInternalName createNextFreeRunFolder(String prefix) {
		File runningFolder = NightfallBungeeConfig.getNBConfig().getRunningFolder();
		for (int i=0; i<MAX_FOLDER_NUMBER; i++) {
			File testFolder = new File(runningFolder, prefix+i);
			if (!testFolder.exists()) {
				testFolder.mkdir();
				return new PairOfRunFolderAndInternalName(prefix + i, testFolder);
			}
		}
		throw new IllegalStateException("Too many servers with prefix '"+prefix+"' exist");
	}
	private static PairOfRunFolderAndInternalName copyTemplateFolder(File templateFolder, String prefix) throws IOException {
		PairOfRunFolderAndInternalName pair = createNextFreeRunFolder(prefix);
		FileUtils.copyDirectory(templateFolder, pair.file);
		return pair;
	}
}
