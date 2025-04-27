package de.thedead2.minecraft_tales;

import de.thedead2.minecraft_tales.platform.Services;
import de.thedead2.minecraft_tales.api.services.IPlatformHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class MTGlobalConstants {

	public static final String MOD_ID = "minecraft_tales";
	public static final String MOD_NAME = "Minecraft Tales";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static final IPlatformHelper PLATFORM = Services.load(IPlatformHelper.class);

	public static final Path SAVE_DIR = PLATFORM.getGameDirectory().resolve(MOD_ID);
	public static final Path JOURNAL_DIR = SAVE_DIR.resolve("journals");
	public static final Path QUESTS_DIR = SAVE_DIR.resolve("quests");
	public static final Path STORY_DIR = SAVE_DIR.resolve("story");

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);


}
